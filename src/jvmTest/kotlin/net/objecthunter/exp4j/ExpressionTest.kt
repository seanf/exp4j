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
package net.objecthunter.exp4j

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

import net.objecthunter.exp4j.function.Functions
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.operator.Operators
import net.objecthunter.exp4j.tokenizer.*

import org.junit.Ignore
import org.junit.Test

import java.util.concurrent.Executors


class ExpressionTest {
    @Test
    @Throws(Exception::class)
    fun testExpression1() {
        val tokens = arrayOf(NumberToken(3.0), NumberToken(2.0), OperatorToken(Operators.getBuiltinOperator('+', 2)!!))
        val exp = Expression(tokens)
        assertEquals(5.0, exp.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression2() {
        val tokens = arrayOf(NumberToken(1.0), FunctionToken(Functions.getBuiltinFunction("log")!!))
        val exp = Expression(tokens)
        assertEquals(0.0, exp.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testGetVariableNames1() {
        val tokens = arrayOf(VariableToken("a"), VariableToken("b"), OperatorToken(Operators.getBuiltinOperator('+', 2)!!))
        val exp = Expression(tokens)

        assertEquals(2, exp.variableNames.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun testFactorial() {
        val factorial = object : Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {

            override fun apply(vararg args: Double): Double {
                val arg = args[0].toInt()
                if (arg.toDouble() != args[0]) {
                    throw IllegalArgumentException("Operand for factorial has to be an integer")
                }
                if (arg < 0) {
                    throw IllegalArgumentException("The operand of the factorial can not be less than zero")
                }
                
                val result = 1.rangeTo(arg)
                        .map(Int::toDouble)
                        .fold(1.0) { a, b -> a * b }
                return result
            }
        }

        var e = ExpressionBuilder("2!+3!")
                .operator(factorial)
                .build()
        assertEquals(8.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3!-2!")
                .operator(factorial)
                .build()
        assertEquals(4.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3!")
                .operator(factorial)
                .build()
        assertEquals(6.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3!!")
                .operator(factorial)
                .build()
        assertEquals(720.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("4 + 3!")
                .operator(factorial)
                .build()
        assertEquals(10.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3! * 2")
                .operator(factorial)
                .build()
        assertEquals(12.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3!")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(6.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3!!")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(720.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("4 + 3!")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(10.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("3! * 2")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(12.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("2 * 3!")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(12.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("4 + (3!)")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(10.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("4 + 3! + 2 * 6")
                .operator(factorial)
                .build()
        assertTrue(e.validate().isValid)
        assertEquals(22.0, e.evaluate(), 0.0)
    }

    @Test
    fun testCotangent1() {
        val e = ExpressionBuilder("cot(1)")
                .build()
        assertEquals(1 / Math.tan(1.0), e.evaluate(), 0.0)

    }

    @Test(expected = ArithmeticException::class)
    fun testInvalidCotangent1() {
        val e = ExpressionBuilder("cot(0)")
                .build()
        e.evaluate()

    }

    @Test(expected = NullPointerException::class)
    @Throws(Exception::class)
    fun testOperatorFactorial2() {
        val factorial = object : Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {

            override fun apply(vararg args: Double): Double {
                val arg = args[0].toInt()
                if (arg.toDouble() != args[0]) {
                    throw IllegalArgumentException("Operand for factorial has to be an integer")
                }
                if (arg < 0) {
                    throw IllegalArgumentException("The operand of the factorial can not be less than zero")
                }
                val result = 1.rangeTo(arg).map(Int::toDouble)
                        .fold(1.0) { a, b -> a * b }
                return result
            }
        }

        val e = ExpressionBuilder("!3").build()
        assertFalse(e.validate().isValid)
    }

    @Test(expected = NullPointerException::class)
    fun testInvalidFactorial2() {
        val factorial = object : Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {

            override fun apply(vararg args: Double): Double {
                val arg = args[0].toInt()
                if (arg.toDouble() != args[0]) {
                    throw IllegalArgumentException("Operand for factorial has to be an integer")
                }
                if (arg < 0) {
                    throw IllegalArgumentException("The operand of the factorial can not be less than zero")
                }
                val result = 1.rangeTo(arg).map(Int::toDouble)
                        .fold(1.0) { a, b -> a * b }
                return result
            }
        }

        val e = ExpressionBuilder("!!3").build()
        assertFalse(e.validate().isValid)
    }

    @Test
    @Ignore
    @Throws(Exception::class)
    // If Expression should be threads safe this test must pass
    fun evaluateFamily() {
        val e = ExpressionBuilder("sin(x)")
                .variable("x")
                .build()
        val executor = Executors.newFixedThreadPool(100)
        for (i in 0..99999) {
            executor.execute {
                val x = Math.random()
                e.setVariable("x", x)
                try {
                    Thread.sleep(100)
                } catch (e1: InterruptedException) {
                    e1.printStackTrace()
                }

                assertEquals(Math.sin(x), e.evaluate(), 0.0)
            }
        }
    }
}

