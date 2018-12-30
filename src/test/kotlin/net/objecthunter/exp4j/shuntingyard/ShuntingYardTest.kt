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
package net.objecthunter.exp4j.shuntingyard

import net.objecthunter.exp4j.TestUtil.assertNumberToken
import net.objecthunter.exp4j.TestUtil.assertOperatorToken
import net.objecthunter.exp4j.TestUtil.assertVariableToken

import java.util.stream.IntStream

import net.objecthunter.exp4j.operator.Operator

import org.junit.Test

class ShuntingYardTest {

    @Test
    @Throws(Exception::class)
    fun testShuntingYard1() {
        val expression = "2+3"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, null, true)
        assertNumberToken(tokens[0], 2.0)
        assertNumberToken(tokens[1], 3.0)
        assertOperatorToken(tokens[2], "+", 2, Operator.PRECEDENCE_ADDITION)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard2() {
        val expression = "3*x"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, setOf("x"), true)
        assertNumberToken(tokens[0], 3.0)
        assertVariableToken(tokens[1], "x")
        assertOperatorToken(tokens[2], "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard3() {
        val expression = "-3"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, null, true)
        assertNumberToken(tokens[0], 3.0)
        assertOperatorToken(tokens[1], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard4() {
        val expression = "-2^2"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, null, true)
        assertNumberToken(tokens[0], 2.0)
        assertNumberToken(tokens[1], 2.0)
        assertOperatorToken(tokens[2], "^", 2, Operator.PRECEDENCE_POWER)
        assertOperatorToken(tokens[3], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard5() {
        val expression = "2^-2"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, null, true)
        assertNumberToken(tokens[0], 2.0)
        assertNumberToken(tokens[1], 2.0)
        assertOperatorToken(tokens[2], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
        assertOperatorToken(tokens[3], "^", 2, Operator.PRECEDENCE_POWER)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard6() {
        val expression = "2^---+2"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, null, true)
        assertNumberToken(tokens[0], 2.0)
        assertNumberToken(tokens[1], 2.0)
        assertOperatorToken(tokens[2], "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
        assertOperatorToken(tokens[3], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
        assertOperatorToken(tokens[4], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
        assertOperatorToken(tokens[5], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
        assertOperatorToken(tokens[6], "^", 2, Operator.PRECEDENCE_POWER)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard7() {
        val expression = "2^-2!"
        val factorial = object : Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {

            override fun apply(vararg args: Double): Double {
                val arg = args[0].toInt()
                if (arg.toDouble() != args[0]) {
                    throw IllegalArgumentException("Operand for factorial has to be an integer")
                }
                if (arg < 0) {
                    throw IllegalArgumentException("The operand of the factorial can not be less than zero")
                }
                val result = IntStream.rangeClosed(1, arg).asDoubleStream()
                        .reduce(1.0) { a, b -> a * b }
                return result
            }
        }
        val userOperators = HashMap<String, Operator>()
        userOperators["!"] = factorial
        val tokens = ShuntingYard.convertToRPN(expression, null, userOperators, null, true)
        assertNumberToken(tokens[0], 2.0)
        assertNumberToken(tokens[1], 2.0)
        assertOperatorToken(tokens[2], "!", 1, Operator.PRECEDENCE_POWER + 1)
        assertOperatorToken(tokens[3], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
        assertOperatorToken(tokens[4], "^", 2, Operator.PRECEDENCE_POWER)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard8() {
        val expression = "-3^2"
        val tokens = ShuntingYard.convertToRPN(expression, null, null, null, true)
        assertNumberToken(tokens[0], 3.0)
        assertNumberToken(tokens[1], 2.0)
        assertOperatorToken(tokens[2], "^", 2, Operator.PRECEDENCE_POWER)
        assertOperatorToken(tokens[3], "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    }

    @Test
    @Throws(Exception::class)
    fun testShuntingYard9() {
        val reciprocal = object : Operator("$", 1, true, Operator.PRECEDENCE_DIVISION) {
            override fun apply(vararg args: Double): Double {
                if (args[0] == 0.0) {
                    throw ArithmeticException("Division by zero!")
                }
                return 1.0 / args[0]
            }
        }
        val userOperators = HashMap<String, Operator>()
        userOperators["$"] = reciprocal
        val tokens = ShuntingYard.convertToRPN("1$", null, userOperators, null, true)
        assertNumberToken(tokens[0], 1.0)
        assertOperatorToken(tokens[1], "$", 1, Operator.PRECEDENCE_DIVISION)
    }

}
