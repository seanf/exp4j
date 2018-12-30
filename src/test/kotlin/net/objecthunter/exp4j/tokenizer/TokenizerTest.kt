/*
 * Copyright 2014 Frank Asseg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.objecthunter.exp4j.tokenizer

import net.objecthunter.exp4j.TestUtil.assertCloseParenthesesToken
import net.objecthunter.exp4j.TestUtil.assertFunctionSeparatorToken
import net.objecthunter.exp4j.TestUtil.assertFunctionToken
import net.objecthunter.exp4j.TestUtil.assertNumberToken
import net.objecthunter.exp4j.TestUtil.assertOpenParenthesesToken
import net.objecthunter.exp4j.TestUtil.assertOperatorToken
import net.objecthunter.exp4j.TestUtil.assertVariableToken
import org.junit.Assert.*

import java.util.Arrays
import java.util.HashMap
import java.util.HashSet

import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator

import org.junit.Test

class TokenizerTest {

    @Test
    @Throws(Exception::class)
    fun testTokenization1() {
        val tokenizer = Tokenizer("1.222331", null, null, null)
        assertNumberToken(tokenizer.nextToken(), 1.222331)
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization2() {
        val tokenizer = Tokenizer(".222331", null, null, null)
        assertNumberToken(tokenizer.nextToken(), .222331)
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization3() {
        val tokenizer = Tokenizer("3e2", null, null, null)
        assertNumberToken(tokenizer.nextToken(), 300.0)
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization4() {
        val tokenizer = Tokenizer("3+1", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 1.0)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization5() {
        val tokenizer = Tokenizer("+3", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization6() {
        val tokenizer = Tokenizer("-3", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization7() {
        val tokenizer = Tokenizer("---++-3", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization8() {
        val tokenizer = Tokenizer("---++-3.004", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.004)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization9() {
        val tokenizer = Tokenizer("3+-1", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 1.0)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization10() {
        val tokenizer = Tokenizer("3+-1-.32++2", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 1.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 2, Operator.PRECEDENCE_SUBTRACTION)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 0.32)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization11() {
        val tokenizer = Tokenizer("2+", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization12() {
        val tokenizer = Tokenizer("log(1)", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 1.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization13() {
        val tokenizer = Tokenizer("x", null, null, HashSet(Arrays.asList("x")))

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "x")

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization14() {
        val tokenizer = Tokenizer("2*x-log(3)", null, null, HashSet(Arrays.asList("x")))

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "x")

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 2, Operator.PRECEDENCE_SUBTRACTION)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization15() {
        val tokenizer = Tokenizer("2*xlog+log(3)", null, null, HashSet(Arrays.asList("xlog")))

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "xlog")

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization16() {
        val tokenizer = Tokenizer("2*x+-log(3)", null, null, HashSet(Arrays.asList("x")))

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "x")

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization17() {
        val tokenizer = Tokenizer("2 * x + -log(3)", null, null, HashSet(Arrays.asList("x")))

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "x")

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "+", 2, Operator.PRECEDENCE_ADDITION)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 3.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization18() {
        val log2 = object : Function("log2") {

            override fun apply(vararg args: Double): Double {
                return Math.log(args[0]) / Math.log(2.0)
            }
        }

        val funcs = HashMap<String, Function>(1)
        funcs[log2.name] = log2
        val tokenizer = Tokenizer("log2(4)", funcs, null, null)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log2", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 4.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization19() {
        val avg = object : Function("avg", 2) {

            override fun apply(vararg args: Double): Double {
                var sum = 0.0
                for (arg in args) {
                    sum += arg
                }
                return sum / args.size
            }
        }
        val funcs = HashMap<String, Function>(1)
        funcs[avg.name] = avg
        val tokenizer = Tokenizer("avg(1,2)", funcs, null, null)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "avg", 2)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 1.0)

        assertTrue(tokenizer.hasNext())
        assertFunctionSeparatorToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization20() {
        val factorial = object : Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
        val operators = HashMap<String, Operator>(1)
        operators[factorial.symbol] = factorial

        val tokenizer = Tokenizer("2!", null, operators, null)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "!", factorial.numOperands, factorial.precedence)

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization21() {
        val tokenizer = Tokenizer("log(x) - y * (sqrt(x^cos(y)))", null, null, HashSet(Arrays.asList("x", "y")))

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "log", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "x")

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 2, Operator.PRECEDENCE_SUBTRACTION)

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "y")

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "sqrt", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "x")

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "^", 2, Operator.PRECEDENCE_POWER)

        assertTrue(tokenizer.hasNext())
        assertFunctionToken(tokenizer.nextToken(), "cos", 1)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertVariableToken(tokenizer.nextToken(), "y")

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }

    @Test
    @Throws(Exception::class)
    fun testTokenization22() {
        val tokenizer = Tokenizer("--2 * (-14)", null, null, null)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 2.0)

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)

        assertTrue(tokenizer.hasNext())
        assertOpenParenthesesToken(tokenizer.nextToken())

        assertTrue(tokenizer.hasNext())
        assertOperatorToken(tokenizer.nextToken(), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)

        assertTrue(tokenizer.hasNext())
        assertNumberToken(tokenizer.nextToken(), 14.0)

        assertTrue(tokenizer.hasNext())
        assertCloseParenthesesToken(tokenizer.nextToken())

        assertFalse(tokenizer.hasNext())
    }
}
