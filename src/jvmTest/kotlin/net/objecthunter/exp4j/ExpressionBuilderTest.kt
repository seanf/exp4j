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

import org.junit.Assert.*

import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator

import org.junit.Test
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.ln1p
import kotlin.math.sin
import kotlin.math.sqrt

class ExpressionBuilderTest {

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder1() {
        val result = ExpressionBuilder("2+1")
                .build()
                .evaluate()
        assertEquals(3.0, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder2() {
        val result = ExpressionBuilder("cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.PI)
                .evaluate()
        val expected = cos(Math.PI)
        assertEquals(-1.0, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder3() {
        val x = Math.PI
        val result = ExpressionBuilder("sin(x)-log(3*x/4)")
                .variables("x")
                .build()
                .setVariable("x", x)
                .evaluate()

        val expected = sin(x) - ln(3 * x / 4)
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder4() {
        val log2 = object : Function("log2", 1) {

            override fun apply(vararg args: Double): Double {
                return Math.log(args[0]) / Math.log(2.0)
            }
        }
        val result = ExpressionBuilder("log2(4)")
                .function(log2)
                .build()
                .evaluate()

        val expected = 2.0
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder5() {
        val avg = object : Function("avg", 4) {

            override fun apply(vararg args: Double): Double {
                var sum = 0.0
                for (arg in args) {
                    sum += arg
                }
                return sum / args.size
            }
        }
        val result = ExpressionBuilder("avg(1,2,3,4)")
                .function(avg)
                .build()
                .evaluate()

        val expected = 2.5
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder6() {
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

        val result = ExpressionBuilder("3!")
                .operator(factorial)
                .build()
                .evaluate()

        val expected = 6.0
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder7() {
        val res = ExpressionBuilder("x")
                .variables("x")
                .build()
                .validate()
        assertFalse(res.isValid)
        assertEquals(res.errors!!.size.toLong(), 1)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder8() {
        val res = ExpressionBuilder("x*y*z")
                .variables("x", "y", "z")
                .build()
                .validate()
        assertFalse(res.isValid)
        assertEquals(res.errors!!.size.toLong(), 3)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder9() {
        val res = ExpressionBuilder("x")
                .variables("x")
                .build()
                .setVariable("x", 1.0)
                .validate()
        assertTrue(res.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidationDocExample() {
        val e = ExpressionBuilder("x")
                .variables("x")
                .build()
        var res = e.validate()
        assertFalse(res.isValid)
        assertEquals(1, res.errors!!.size.toLong())

        e.setVariable("x", 1.0)
        res = e.validate()
        assertTrue(res.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder10() {
        val result = ExpressionBuilder("1e1")
                .build()
                .evaluate()
        assertEquals(10.0, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder11() {
        val result = ExpressionBuilder("1.11e-1")
                .build()
                .evaluate()
        assertEquals(0.111, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder12() {
        val result = ExpressionBuilder("1.11e+1")
                .build()
                .evaluate()
        assertEquals(11.1, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder13() {
        val result = ExpressionBuilder("-3^2")
                .build()
                .evaluate()
        assertEquals(-9.0, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder14() {
        val result = ExpressionBuilder("(-3)^2")
                .build()
                .evaluate()
        assertEquals(9.0, result, 0.0)
    }

    @Test(expected = ArithmeticException::class)
    @Throws(Exception::class)
    fun testExpressionBuilder15() {
        val result = ExpressionBuilder("-3/0")
                .build()
                .evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder16() {
        val result = ExpressionBuilder("log(x) - y * (sqrt(x^cos(y)))")
                .variables("x", "y")
                .build()
                .setVariable("x", 1.0)
                .setVariable("y", 2.0)
                .evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder17() {
        val e = ExpressionBuilder("x-y*")
                .variables("x", "y")
                .build()
        val res = e.validate(false)
        assertFalse(res.isValid)
        assertEquals(1, res.errors!!.size.toLong())
        assertEquals("Too many operators", res.errors!![0])
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder18() {
        val e = ExpressionBuilder("log(x) - y *")
                .variables("x", "y")
                .build()
        val res = e.validate(false)
        assertFalse(res.isValid)
        assertEquals(1, res.errors!!.size.toLong())
        assertEquals("Too many operators", res.errors!![0])
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder19() {
        val e = ExpressionBuilder("x - y *")
                .variables("x", "y")
                .build()
        val res = e.validate(false)
        assertFalse(res.isValid)
        assertEquals(1, res.errors!!.size.toLong())
        assertEquals("Too many operators", res.errors!![0])
    }

    /* legacy tests from earlier exp4j versions */

    @Test
    @Throws(Exception::class)
    fun testFunction1() {
        val custom = object : Function("timespi") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.PI
            }
        }
        val e = ExpressionBuilder("timespi(x)")
                .function(custom)
                .variables("x")
                .build()
                .setVariable("x", 1.0)
        val result = e.evaluate()
        assertTrue(result == Math.PI)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction2() {
        val custom = object : Function("loglog") {

            override fun apply(vararg values: Double): Double {
                return Math.log(Math.log(values[0]))
            }
        }
        val e = ExpressionBuilder("loglog(x)")
                .variables("x")
                .function(custom)
                .build()
                .setVariable("x", 1.0)
        val result = e.evaluate()
        assertTrue(result == Math.log(Math.log(1.0)))
    }

    @Test
    @Throws(Exception::class)
    fun testFunction3() {
        val custom1 = object : Function("foo") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.E
            }
        }
        val custom2 = object : Function("bar") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.PI
            }
        }
        val e = ExpressionBuilder("foo(bar(x))")
                .function(custom1)
                .function(custom2)
                .variables("x")
                .build()
                .setVariable("x", 1.0)
        val result = e.evaluate()
        assertTrue(result == 1.0 * Math.E * Math.PI)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction4() {
        val custom1 = object : Function("foo") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.E
            }
        }
        val varX = 32.24979131
        val e = ExpressionBuilder("foo(log(x))")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX)
        val result = e.evaluate()
        assertTrue(result == Math.log(varX) * Math.E)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction5() {
        val custom1 = object : Function("foo") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.E
            }
        }
        val custom2 = object : Function("bar") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.PI
            }
        }
        val varX = 32.24979131
        val e = ExpressionBuilder("bar(foo(log(x)))")
                .variables("x")
                .function(custom1)
                .function(custom2)
                .build()
                .setVariable("x", varX)
        val result = e.evaluate()
        assertTrue(result == Math.log(varX) * Math.E * Math.PI)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction6() {
        val custom1 = object : Function("foo") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.E
            }
        }
        val custom2 = object : Function("bar") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.PI
            }
        }
        val varX = 32.24979131
        val e = ExpressionBuilder("bar(foo(log(x)))")
                .variables("x")
                .functions(custom1, custom2)
                .build()
                .setVariable("x", varX)
        val result = e.evaluate()
        assertTrue(result == Math.log(varX) * Math.E * Math.PI)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction7() {
        val custom1 = object : Function("half") {

            override fun apply(vararg values: Double): Double {
                return values[0] / 2
            }
        }
        val e = ExpressionBuilder("half(x)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", 1.0)
        assertTrue(0.5 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testFunction10() {
        val custom1 = object : Function("max", 2) {

            override fun apply(vararg values: Double): Double {
                return if (values[0] < values[1]) values[1] else values[0]
            }
        }
        val e = ExpressionBuilder("max(x,y)")
                .variables("x", "y")
                .function(custom1)
                .build()
                .setVariable("x", 1.0)
                .setVariable("y", 2.0)
        assertTrue(2.0 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testFunction11() {
        val custom1 = object : Function("power", 2) {

            override fun apply(vararg values: Double): Double {
                return Math.pow(values[0], values[1])
            }
        }
        val e = ExpressionBuilder("power(x,y)")
                .variables("x", "y")
                .function(custom1)
                .build()
                .setVariable("x", 2.0)
                .setVariable("y",
                        4.0)
        assertTrue(Math.pow(2.0, 4.0) == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testFunction12() {
        val custom1 = object : Function("max", 5) {

            override fun apply(vararg values: Double): Double {
                var max = values[0]
                for (i in 1 until numArguments) {
                    if (values[i] > max) {
                        max = values[i]
                    }
                }
                return max
            }
        }
        val e = ExpressionBuilder("max(1,2.43311,51.13,43,12)")
                .function(custom1)
                .build()
        assertTrue(51.13 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testFunction13() {
        val custom1 = object : Function("max", 3) {

            override fun apply(vararg values: Double): Double {
                var max = values[0]
                for (i in 1 until numArguments) {
                    if (values[i] > max) {
                        max = values[i]
                    }
                }
                return max
            }
        }
        val varX = Math.E
        val e = ExpressionBuilder("max(log(x),sin(x),x)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX)
        assertTrue(varX == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testFunction14() {
        val custom1 = object : Function("multiply", 2) {

            override fun apply(vararg values: Double): Double {
                return values[0] * values[1]
            }
        }
        val varX = 1.0
        val e = ExpressionBuilder("multiply(sin(x),x+1)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX)
        val expected = Math.sin(varX) * (varX + 1)
        val actual = e.evaluate()
        assertTrue(expected == actual)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction15() {
        val custom1 = object : Function("timesPi") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.PI
            }
        }
        val varX = 1.0
        val e = ExpressionBuilder("timesPi(x^2)")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX)
        val expected = varX * Math.PI
        val actual = e.evaluate()
        assertTrue(expected == actual)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction16() {
        val custom1 = object : Function("multiply", 3) {

            override fun apply(vararg values: Double): Double {
                return values[0] * values[1] * values[2]
            }
        }
        val varX = 1.0
        val e = ExpressionBuilder("multiply(sin(x),x+1^(-2),log(x))")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX)
        val expected = Math.sin(varX) * Math.pow(varX + 1, -2.0) * Math.log(varX)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testFunction17() {
        val custom1 = object : Function("timesPi") {

            override fun apply(vararg values: Double): Double {
                return values[0] * Math.PI
            }
        }
        val varX = Math.E
        val e = ExpressionBuilder("timesPi(log(x^(2+1)))")
                .variables("x")
                .function(custom1)
                .build()
                .setVariable("x", varX)
        val expected = Math.log(Math.pow(varX, 3.0)) * Math.PI
        assertTrue(expected == e.evaluate())
    }

    // thanks to Marcin Domanski who issued
    // http://jira.congrace.de/jira/browse/EXP-11
    // i have this test, which fails in 0.2.9
    @Test
    @Throws(Exception::class)
    fun testFunction18() {
        val minFunction = object : Function("min", 2) {

            override fun apply(values: DoubleArray): Double {
                var currentMin = Double.POSITIVE_INFINITY
                for (value in values) {
                    currentMin = Math.min(currentMin, value)
                }
                return currentMin
            }
        }
        val b = ExpressionBuilder("-min(5, 0) + 10")
                .function(minFunction)
        val calculated = b.build().evaluate()
        assertTrue(calculated == 10.0)
    }

    // thanks to Sylvain Machefert who issued
    // http://jira.congrace.de/jira/browse/EXP-11
    // i have this test, which fails in 0.3.2
    @Test
    @Throws(Exception::class)
    fun testFunction19() {
        val minFunction = object : Function("power", 2) {

            override fun apply(values: DoubleArray): Double {
                return Math.pow(values[0], values[1])
            }
        }
        val b = ExpressionBuilder("power(2,3)")
                .function(minFunction)
        val calculated = b.build().evaluate()
        assertEquals(Math.pow(2.0, 3.0), calculated, 0.0)
    }

    // thanks to Narendra Harmwal who noticed that getArgumentCount was not
    // implemented
    // this test has been added in 0.3.5
    @Test
    @Throws(Exception::class)
    fun testFunction20() {
        val maxFunction = object : Function("max", 3) {

            override fun apply(vararg values: Double): Double {
                var max = values[0]
                for (i in 1 until numArguments) {
                    if (values[i] > max) {
                        max = values[i]
                    }
                }
                return max
            }
        }
        val b = ExpressionBuilder("max(1,2,3)")
                .function(maxFunction)
        val calculated = b.build().evaluate()
        assertTrue(maxFunction.numArguments == 3)
        assertTrue(calculated == 3.0)
    }

    @Test
    @Throws(Exception::class)
    fun testOperators1() {
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

        var e = ExpressionBuilder("1!").operator(factorial)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("2!").operator(factorial)
                .build()
        assertTrue(2.0 == e.evaluate())
        e = ExpressionBuilder("3!").operator(factorial)
                .build()
        assertTrue(6.0 == e.evaluate())
        e = ExpressionBuilder("4!").operator(factorial)
                .build()
        assertTrue(24.0 == e.evaluate())
        e = ExpressionBuilder("5!").operator(factorial)
                .build()
        assertTrue(120.0 == e.evaluate())
        e = ExpressionBuilder("11!").operator(factorial)
                .build()
        assertTrue(39916800.0 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testOperators2() {
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
        var e = ExpressionBuilder("2^3!").operator(factorial)
                .build()
        assertEquals(64.0, e.evaluate(), 0.0)
        e = ExpressionBuilder("3!^2").operator(factorial)
                .build()
        assertTrue(36.0 == e.evaluate())
        e = ExpressionBuilder("-(3!)^-1").operator(factorial)
                .build()
        val actual = e.evaluate()
        assertEquals(Math.pow(-6.0, -1.0), actual, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testOperators3() {
        val gteq = object : Operator(">=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {

            override fun apply(values: DoubleArray): Double {
                return if (values[0] >= values[1]) {
                    1.0
                } else {
                    0.0
                }
            }
        }
        var e = ExpressionBuilder("1>=2").operator(gteq)
                .build()
        assertTrue(0.0 == e.evaluate())
        e = ExpressionBuilder("2>=1").operator(gteq)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("-2>=1").operator(gteq)
                .build()
        assertTrue(0.0 == e.evaluate())
        e = ExpressionBuilder("-2>=-1").operator(gteq)
                .build()
        assertTrue(0.0 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testModulo1() {
        val result = ExpressionBuilder("33%(20/2)%2")
                .build().evaluate()
        assertTrue(result == 1.0)
    }

    @Test
    @Throws(Exception::class)
    fun testOperators4() {
        val greaterEq = object : Operator(">=", 2, true, 4) {

            override fun apply(values: DoubleArray): Double {
                return if (values[0] >= values[1]) {
                    1.0
                } else {
                    0.0
                }
            }
        }
        val greater = object : Operator(">", 2, true, 4) {

            override fun apply(values: DoubleArray): Double {
                return if (values[0] > values[1]) {
                    1.0
                } else {
                    0.0
                }
            }
        }
        val newPlus = object : Operator(">=>", 2, true, 4) {

            override fun apply(values: DoubleArray): Double {
                return values[0] + values[1]
            }
        }
        var e = ExpressionBuilder("1>2").operator(greater)
                .build()
        assertTrue(0.0 == e.evaluate())
        e = ExpressionBuilder("2>=2").operator(greaterEq)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("1>=>2").operator(newPlus)
                .build()
        assertTrue(3.0 == e.evaluate())
        e = ExpressionBuilder("1>=>2>2").operator(greater).operator(newPlus)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("1>=>2>2>=1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("1 >=> 2 > 2 >= 1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("1 >=> 2 >= 2 > 1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build()
        assertTrue(0.0 == e.evaluate())
        e = ExpressionBuilder("1 >=> 2 >= 2 > 0").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build()
        assertTrue(1.0 == e.evaluate())
        e = ExpressionBuilder("1 >=> 2 >= 2 >= 1").operator(greater).operator(newPlus)
                .operator(greaterEq)
                .build()
        assertTrue(1.0 == e.evaluate())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidOperator1() {
        val fail = object : Operator("2", 2, true, 1) {

            override fun apply(values: DoubleArray): Double {
                return 0.0
            }
        }
        ExpressionBuilder("1").operator(fail)
                .build()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidFunction1() {
        val func = object : Function("1gd") {

            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidFunction2() {
        val func = object : Function("+1gd") {

            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder01() {
        val e = ExpressionBuilder("7*x + 3*y")
                .variables("x", "y")
                .build()
                .setVariable("x", 1.0)
                .setVariable("y", 2.0)
        val result = e.evaluate()
        assertTrue(result == 13.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder02() {
        val e = ExpressionBuilder("7*x + 3*y")
                .variables("x", "y")
                .build()
                .setVariable("x", 1.0)
                .setVariable("y", 2.0)
        val result = e.evaluate()
        assertTrue(result == 13.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder03() {
        val varX = 1.3
        val varY = 4.22
        val e = ExpressionBuilder("7*x + 3*y - log(y/x*12)^y")
                .variables("x", "y")
                .build()
                .setVariable("x", varX)
                .setVariable("y",
                        varY)
        val result = e.evaluate()
        assertTrue(result == 7 * varX + 3 * varY - Math.pow(Math.log(varY / varX * 12), varY))
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder04() {
        var varX = 1.3
        var varY = 4.22
        val e = ExpressionBuilder("7*x + 3*y - log(y/x*12)^y")
                .variables("x", "y")
                .build()
                .setVariable("x", varX)
                .setVariable("y", varY)
        var result = e.evaluate()
        assertTrue(result == 7 * varX + 3 * varY - Math.pow(Math.log(varY / varX * 12), varY))
        varX = 1.79854
        varY = 9281.123
        e.setVariable("x", varX)
        e.setVariable("y", varY)
        result = e.evaluate()
        assertTrue(result == 7 * varX + 3 * varY - Math.pow(Math.log(varY / varX * 12), varY))
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder05() {
        val varX = 1.3
        val varY = 4.22
        val e = ExpressionBuilder("3*y")
                .variables("y")
                .build()
                .setVariable("x", varX)
                .setVariable("y", varY)
        val result = e.evaluate()
        assertTrue(result == 3 * varY)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder06() {
        val varX = 1.3
        val varY = 4.22
        val varZ = 4.22
        val e = ExpressionBuilder("x * y * z")
                .variables("x", "y", "z")
                .build()
        e.setVariable("x", varX)
        e.setVariable("y", varY)
        e.setVariable("z", varZ)
        val result = e.evaluate()
        assertTrue(result == varX * varY * varZ)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder07() {
        val varX = 1.3
        val e = ExpressionBuilder("log(sin(x))")
                .variables("x")
                .build()
                .setVariable("x", varX)
        val result = e.evaluate()
        assertTrue(result == Math.log(Math.sin(varX)))
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionBuilder08() {
        val varX = 1.3
        val e = ExpressionBuilder("log(sin(x))")
                .variables("x")
                .build()
                .setVariable("x", varX)
        val result = e.evaluate()
        assertTrue(result == Math.log(Math.sin(varX)))
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testSameName() {
        val custom = object : Function("bar") {

            override fun apply(vararg values: Double): Double {
                return values[0] / 2
            }
        }
        val varBar = 1.3
        val e = ExpressionBuilder("bar(bar)")
                .variables("bar")
                .function(custom)
                .build()
                .setVariable("bar", varBar)
        val res = e.validate()
        assertFalse(res.isValid)
        assertEquals(1, res.errors!!.size.toLong())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidFunction() {
        val varY = 4.22
        val e = ExpressionBuilder("3*invalid_function(y)")
                .variables("<")
                .build()
                .setVariable("y", varY)
        e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testMissingVar() {
        val varY = 4.22
        val e = ExpressionBuilder("3*y*z")
                .variables("y", "z")
                .build()
                .setVariable("y", varY)
        e.evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testUnaryMinusPowerPrecedence() {
        val e = ExpressionBuilder("-1^2")
                .build()
        assertEquals(-1.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnaryMinus() {
        val e = ExpressionBuilder("-1")
                .build()
        assertEquals(-1.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression1() {
        val expr: String
        val expected: Double
        expr = "2 + 4"
        expected = 6.0
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression10() {
        val expr: String
        val expected: Double
        expr = "1 * 1.5 + 1"
        expected = 1 * 1.5 + 1
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression11() {
        val x = 1.0
        val y = 2.0
        val expr = "log(x) ^ sin(y)"
        val expected = Math.pow(Math.log(x), Math.sin(y))
        val e = ExpressionBuilder(expr)
                .variables("x", "y")
                .build()
                .setVariable("x", x)
                .setVariable("y", y)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression12() {
        val expr = "log(2.5333333333)^(0-1)"
        val expected = Math.pow(Math.log(2.5333333333), -1.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression13() {
        val expr = "2.5333333333^(0-1)"
        val expected = Math.pow(2.5333333333, -1.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression14() {
        val expr = "2 * 17.41 + (12*2)^(0-1)"
        val expected = 2 * 17.41 + Math.pow((12 * 2).toDouble(), -1.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression15() {
        val expr = "2.5333333333 * 17.41 + (12*2)^log(2.764)"
        val expected = 2.5333333333 * 17.41 + Math.pow((12 * 2).toDouble(), Math.log(2.764))
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression16() {
        val expr = "2.5333333333/2 * 17.41 + (12*2)^(log(2.764) - sin(5.6664))"
        val expected = 2.5333333333 / 2 * 17.41 + Math.pow((12 * 2).toDouble(), Math.log(2.764) - Math.sin(5.6664))
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression17() {
        val expr = "x^2 - 2 * y"
        val x = Math.E
        val y = Math.PI
        val expected = x * x - 2 * y
        val e = ExpressionBuilder(expr)
                .variables("x", "y")
                .build()
                .setVariable("x", x)
                .setVariable("y", y)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression18() {
        val expr = "-3"
        val expected = -3.0
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression19() {
        val expr = "-3 * -24.23"
        val expected = -3 * -24.23
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression2() {
        val expr: String
        val expected: Double
        expr = "2+3*4-12"
        expected = (2 + 3 * 4 - 12).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression20() {
        val expr = "-2 * 24/log(2) -2"
        val expected = -2 * 24 / Math.log(2.0) - 2
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression21() {
        val expr = "-2 *33.34/log(x)^-2 + 14 *6"
        val x = 1.334
        val expected = -2 * 33.34 / Math.pow(Math.log(x), -2.0) + 14 * 6
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x)
        assertEquals(expected, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionPower() {
        val expr = "2^-2"
        val expected = Math.pow(2.0, -2.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertEquals(expected, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpressionMultiplication() {
        val expr = "2*-2"
        val expected = -4.0
        val e = ExpressionBuilder(expr)
                .build()
        assertEquals(expected, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression22() {
        val expr = "-2 *33.34/log(x)^-2 + 14 *6"
        val x = 1.334
        val expected = -2 * 33.34 / Math.pow(Math.log(x), -2.0) + 14 * 6
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression23() {
        val expr = "-2 *33.34/(log(foo)^-2 + 14 *6) - sin(foo)"
        val x = 1.334
        val expected = -2 * 33.34 / (Math.pow(Math.log(x), -2.0) + 14 * 6) - Math.sin(x)
        val e = ExpressionBuilder(expr)
                .variables("foo")
                .build()
                .setVariable("foo", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression24() {
        val expr = "3+4-log(23.2)^(2-1) * -1"
        val expected = 3 + 4 - Math.pow(Math.log(23.2), (2 - 1).toDouble()) * -1
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression25() {
        val expr = "+3+4-+log(23.2)^(2-1) * + 1"
        val expected = 3 + 4 - Math.log(23.2)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression26() {
        val expr = "14 + -(1 / 2.22^3)"
        val expected = 14 + -(1.0 / Math.pow(2.22, 3.0))
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression27() {
        val expr = "12^-+-+-+-+-+-+---2"
        val expected = Math.pow(12.0, -2.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression28() {
        val expr = "12^-+-+-+-+-+-+---2 * (-14) / 2 ^ -log(2.22323) "
        val expected = Math.pow(12.0, -2.0) * -14 / Math.pow(2.0, -Math.log(2.22323))
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression29() {
        val expr = "24.3343 % 3"
        val expected = 24.3343 % 3
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testVarname1() {
        val expr = "12.23 * foo.bar"
        val e = ExpressionBuilder(expr)
                .variables("foo.bar")
                .build()
                .setVariable("foo.bar", 1.0)
        assertTrue(12.23 == e.evaluate())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testMisplacedSeparator() {
        val expr = "12.23 * ,foo"
        val e = ExpressionBuilder(expr)
                .build()
                .setVariable(",foo", 1.0)
        assertTrue(12.23 == e.evaluate())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidVarname() {
        val expr = "12.23 * @foo"
        val e = ExpressionBuilder(expr)
                .build()
                .setVariable("@foo", 1.0)
        assertTrue(12.23 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testVarMap() {
        val expr = "12.23 * foo - bar"
        val variables = HashMap<String, Double>()
        variables["foo"] = 2.0
        variables["bar"] = 3.3
        val e = ExpressionBuilder(expr)
                .variables(variables.keys)
                .build()
                .setVariables(variables)
        assertTrue(12.23 * 2.0 - 3.3 == e.evaluate())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidNumberofArguments1() {
        val expr = "log(2,2)"
        val e = ExpressionBuilder(expr)
                .build()
        e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testInvalidNumberofArguments2() {
        val avg = object : Function("avg", 4) {

            override fun apply(vararg args: Double): Double {
                var sum = 0.0
                for (arg in args) {
                    sum += arg
                }
                return sum / args.size
            }
        }
        val expr = "avg(2,2)"
        val e = ExpressionBuilder(expr)
                .build()
        e.evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testExpression3() {
        val expr: String
        val expected: Double
        expr = "2+4*5"
        expected = (2 + 4 * 5).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression30() {
        val expr = "24.3343 % 3 * 20 ^ -(2.334 % log(2 / 14))"
        val expected = 24.3343 % 3 * Math.pow(20.0, -(2.334 % Math.log(2.0 / 14.0)))
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression31() {
        val expr = "-2 *33.34/log(y_x)^-2 + 14 *6"
        val x = 1.334
        val expected = -2 * 33.34 / Math.pow(Math.log(x), -2.0) + 14 * 6
        val e = ExpressionBuilder(expr)
                .variables("y_x")
                .build()
                .setVariable("y_x", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression32() {
        val expr = "-2 *33.34/log(y_2x)^-2 + 14 *6"
        val x = 1.334
        val expected = -2 * 33.34 / Math.pow(Math.log(x), -2.0) + 14 * 6
        val e = ExpressionBuilder(expr)
                .variables("y_2x")
                .build()
                .setVariable("y_2x", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression33() {
        val expr = "-2 *33.34/log(_y)^-2 + 14 *6"
        val x = 1.334
        val expected = -2 * 33.34 / Math.pow(Math.log(x), -2.0) + 14 * 6
        val e = ExpressionBuilder(expr)
                .variables("_y")
                .build()
                .setVariable("_y", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression34() {
        val expr = "-2 + + (+4) +(4)"
        val expected = (-2 + 4 + 4).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression40() {
        val expr = "1e1"
        val expected = 10.0
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression41() {
        val expr = "1e-1"
        val expected = 0.1
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    /*
     * Added tests for expressions with scientific notation see http://jira.congrace.de/jira/browse/EXP-17
     */
    @Test
    @Throws(Exception::class)
    fun testExpression42() {
        val expr = "7.2973525698e-3"
        val expected = 7.2973525698e-3
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression43() {
        val expr = "6.02214E23"
        val expected = 6.02214e23
        val e = ExpressionBuilder(expr)
                .build()
        val result = e.evaluate()
        assertTrue(expected == result)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression44() {
        val expr = "6.02214E23"
        val expected = 6.02214e23
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test(expected = NumberFormatException::class)
    @Throws(Exception::class)
    fun testExpression45() {
        val expr = "6.02214E2E3"
        ExpressionBuilder(expr)
                .build()
    }

    @Test(expected = NumberFormatException::class)
    @Throws(Exception::class)
    fun testExpression46() {
        val expr = "6.02214e2E3"
        ExpressionBuilder(expr)
                .build()
    }

    // tests for EXP-20: No exception is thrown for unmatched parenthesis in
    // build
    // Thanks go out to maheshkurmi for reporting
    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression48() {
        val expr = "(1*2"
        val e = ExpressionBuilder(expr)
                .build()
        val result = e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression49() {
        val expr = "{1*2"
        val e = ExpressionBuilder(expr)
                .build()
        val result = e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression50() {
        val expr = "[1*2"
        val e = ExpressionBuilder(expr)
                .build()
        val result = e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression51() {
        val expr = "(1*{2+[3}"
        val e = ExpressionBuilder(expr)
                .build()
        val result = e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression52() {
        val expr = "(1*(2+(3"
        val e = ExpressionBuilder(expr)
                .build()
        val result = e.evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testExpression53() {
        val expr = "14 * 2x"
        val exp = ExpressionBuilder(expr)
                .variables("x")
                .build()
        exp.setVariable("x", 1.5)
        assertTrue(exp.validate().isValid)
        assertEquals(14.0 * 2.0 * 1.5, exp.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression54() {
        val expr = "2 ((-(x)))"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        e.setVariable("x", 1.5)
        assertEquals(-3.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression55() {
        val expr = "2 sin(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        e.setVariable("x", 2.0)
        assertTrue(Math.sin(2.0) * 2 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression56() {
        val expr = "2 sin(3x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        e.setVariable("x", 2.0)
        assertTrue(Math.sin(6.0) * 2.0 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample1() {
        val e = ExpressionBuilder("3 * sin(y) - 2 / (x - 2)")
                .variables("x", "y")
                .build()
                .setVariable("x", 2.3)
                .setVariable("y", 3.14)
        val result = e.evaluate()
        val expected = 3 * Math.sin(3.14) - 2.0 / (2.3 - 2.0)
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample3() {
        val result = ExpressionBuilder("2cos(xy)")
                .variables("x", "y")
                .build()
                .setVariable("x", 0.5)
                .setVariable("y", 0.25)
                .evaluate()
        assertEquals(2.0 * Math.cos(0.5 * 0.25), result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample4() {
        val expr = "pi+π+e+φ"
        val expected = 2 * Math.PI + Math.E + 1.61803398874
        val e = ExpressionBuilder(expr).build()
        assertEquals(expected, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample5() {
        val expr = "7.2973525698e-3"
        val expected = expr.toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertEquals(expected, e.evaluate(), 0.0)
    }


    @Test
    @Throws(Exception::class)
    fun testDocumentationExample6() {
        val logb = object : Function("logb", 2) {
            override fun apply(vararg args: Double): Double {
                return Math.log(args[0]) / Math.log(args[1])
            }
        }
        val result = ExpressionBuilder("logb(8, 2)")
                .function(logb)
                .build()
                .evaluate()
        val expected = 3.0
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample7() {
        val avg = object : Function("avg", 4) {

            override fun apply(vararg args: Double): Double {
                var sum = 0.0
                for (arg in args) {
                    sum += arg
                }
                return sum / args.size
            }
        }
        val result = ExpressionBuilder("avg(1,2,3,4)")
                .function(avg)
                .build()
                .evaluate()

        val expected = 2.5
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample8() {
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

        val result = ExpressionBuilder("3!")
                .operator(factorial)
                .build()
                .evaluate()

        val expected = 6.0
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample9() {
        val gteq = object : Operator(">=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {

            override fun apply(values: DoubleArray): Double {
                return if (values[0] >= values[1]) {
                    1.0
                } else {
                    0.0
                }
            }
        }

        var e = ExpressionBuilder("1>=2").operator(gteq)
                .build()
        assertTrue(0.0 == e.evaluate())
        e = ExpressionBuilder("2>=1").operator(gteq)
                .build()
        assertTrue(1.0 == e.evaluate())
    }

    @Test(expected = ArithmeticException::class)
    @Throws(Exception::class)
    fun testDocumentationExample10() {
        val reciprocal = object : Operator("$", 1, true, Operator.PRECEDENCE_DIVISION) {
            override fun apply(vararg args: Double): Double {
                if (args[0] == 0.0) {
                    throw ArithmeticException("Division by zero!")
                }
                return 1.0 / args[0]
            }
        }
        val e = ExpressionBuilder("0$").operator(reciprocal).build()
        e.evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample11() {
        val e = ExpressionBuilder("x")
                .variable("x")
                .build()

        var res = e.validate()
        assertFalse(res.isValid)
        assertEquals(1, res.errors!!.size.toLong())

        e.setVariable("x", 1.0)
        res = e.validate()
        assertTrue(res.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testDocumentationExample12() {
        val e = ExpressionBuilder("x")
                .variable("x")
                .build()

        val res = e.validate(false)
        assertTrue(res.isValid)
        assertNull(res.errors)
    }

    // Thanks go out to Johan Björk for reporting the division by zero problem EXP-22
    // https://www.objecthunter.net/jira/browse/EXP-22
    @Test(expected = ArithmeticException::class)
    @Throws(Exception::class)
    fun testExpression57() {
        val expr = "1 / 0"
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(Double.POSITIVE_INFINITY == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression58() {
        val expr = "17 * sqrt(-1) * 12"
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(e.evaluate().isNaN())
    }

    // Thanks go out to Alex Dolinsky for reporting the missing exception when an empty
    // expression is passed as in new ExpressionBuilder("")
    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression59() {
        val e = ExpressionBuilder("")
                .build()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testExpression60() {
        val e = ExpressionBuilder("   ")
                .build()
        e.evaluate()
    }

    @Test(expected = ArithmeticException::class)
    @Throws(Exception::class)
    fun testExpression61() {
        val e = ExpressionBuilder("14 % 0")
                .build()
        e.evaluate()
    }

    // https://www.objecthunter.net/jira/browse/EXP-24
    // thanks go out to Rémi for the issue report
    @Test
    @Throws(Exception::class)
    fun testExpression62() {
        val e = ExpressionBuilder("x*1.0e5+5")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertTrue(Math.E * 1.0 * Math.pow(10.0, 5.0) + 5 == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression63() {
        val e = ExpressionBuilder("log10(5)")
                .build()
        assertEquals(Math.log10(5.0), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression64() {
        val e = ExpressionBuilder("log2(5)")
                .build()
        assertEquals(Math.log(5.0) / Math.log(2.0), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression65() {
        val e = ExpressionBuilder("2log(e)")
                .variables("e")
                .build()
                .setVariable("e", Math.E)

        assertEquals(2.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression66() {
        val e = ExpressionBuilder("log(e)2")
                .variables("e")
                .build()
                .setVariable("e", Math.E)

        assertEquals(2.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression67() {
        val e = ExpressionBuilder("2esin(pi/2)")
                .variables("e", "pi")
                .build()
                .setVariable("e", Math.E)
                .setVariable("pi", Math.PI)

        assertEquals(2.0 * Math.E * Math.sin(Math.PI / 2.0), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression68() {
        val e = ExpressionBuilder("2x")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(2 * Math.E, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression69() {
        val e = ExpressionBuilder("2x2")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(4 * Math.E, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression70() {
        val e = ExpressionBuilder("2xx")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(2.0 * Math.E * Math.E, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression71() {
        val e = ExpressionBuilder("x2x")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(2.0 * Math.E * Math.E, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression72() {
        val e = ExpressionBuilder("2cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(2 * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression73() {
        val e = ExpressionBuilder("cos(x)2")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(2 * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression74() {
        val e = ExpressionBuilder("cos(x)(-2)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(-2.0 * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression75() {
        val e = ExpressionBuilder("(-2)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(-2.0 * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression76() {
        val e = ExpressionBuilder("(-x)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(-E * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression77() {
        val e = ExpressionBuilder("(-xx)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(-E * E * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression78() {
        val e = ExpressionBuilder("(xx)cos(x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(E * E * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression79() {
        val e = ExpressionBuilder("cos(x)(xx)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(E * E * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression80() {
        val e = ExpressionBuilder("cos(x)(xy)")
                .variables("x", "y")
                .build()
                .setVariable("x", Math.E)
                .setVariable("y", Math.sqrt(2.0))
        assertEquals(sqrt(2.0) * E * Math.cos(Math.E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression81() {
        val e = ExpressionBuilder("cos(xy)")
                .variables("x", "y")
                .build()
                .setVariable("x", Math.E)
                .setVariable("y", Math.sqrt(2.0))
        assertEquals(cos(sqrt(2.0) * E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression82() {
        val e = ExpressionBuilder("cos(2x)")
                .variables("x")
                .build()
                .setVariable("x", Math.E)
        assertEquals(cos(2 * E), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression83() {
        val e = ExpressionBuilder("cos(xlog(xy))")
                .variables("x", "y")
                .build()
                .setVariable("x", Math.E)
                .setVariable("y", Math.sqrt(2.0))
        assertEquals(cos(E * ln(E * sqrt(2.0))), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression84() {
        val e = ExpressionBuilder("3x_1")
                .variables("x_1")
                .build()
                .setVariable("x_1", Math.E)
        assertEquals(3.0 * E, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression85() {
        val e = ExpressionBuilder("1/2x")
                .variables("x")
                .build()
                .setVariable("x", 6.0)
        assertEquals(3.0, e.evaluate(), 0.0)
    }

    // thanks got out to David Sills
    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testSpaceBetweenNumbers() {
        val e = ExpressionBuilder("1 1")
                .build()
    }

    // thanks go out to Janny for providing the tests and the bug report
    @Test
    @Throws(Exception::class)
    fun testUnaryMinusInParenthesisSpace() {
        val b = ExpressionBuilder("( -1)^2")
        val calculated = b.build().evaluate()
        assertTrue(calculated == 1.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnaryMinusSpace() {
        val b = ExpressionBuilder(" -1 + 2")
        val calculated = b.build().evaluate()
        assertTrue(calculated == 1.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnaryMinusSpaces() {
        val b = ExpressionBuilder(" -1 + + 2 +   -   1")
        val calculated = b.build().evaluate()
        assertTrue(calculated == 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnaryMinusSpace1() {
        val b = ExpressionBuilder("-1")
        val calculated = b.build().evaluate()
        assertTrue(calculated == -1.0)
    }

    @Test
    @Throws(Exception::class)
    fun testExpression4() {
        val expr: String
        val expected: Double
        expr = "2+4 * 5"
        expected = (2 + 4 * 5).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression5() {
        val expr: String
        val expected: Double
        expr = "(2+4)*5"
        expected = ((2 + 4) * 5).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression6() {
        val expr: String
        val expected: Double
        expr = "(2+4)*5 + 2.5*2"
        expected = (2 + 4) * 5 + 2.5 * 2
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression7() {
        val expr: String
        val expected: Double
        expr = "(2+4)*5 + 10/2"
        expected = ((2 + 4) * 5 + 10 / 2).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression8() {
        val expr: String
        val expected: Double
        expr = "(2 * 3 +4)*5 + 10/2"
        expected = ((2 * 3 + 4) * 5 + 10 / 2).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testExpression9() {
        val expr: String
        val expected: Double
        expr = "(2 * 3 +4)*5 +4 + 10/2"
        expected = ((2 * 3 + 4) * 5 + 4 + 10 / 2).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testFailUnknownFunction1() {
        val expr: String
        expr = "lig(1)"
        val e = ExpressionBuilder(expr)
                .build()
        e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testFailUnknownFunction2() {
        val expr: String
        expr = "galength(1)"
        ExpressionBuilder(expr)
                .build().evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testFailUnknownFunction3() {
        val expr: String
        expr = "tcos(1)"
        val exp = ExpressionBuilder(expr)
                .build()
        val result = exp.evaluate()
        println(result)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction22() {
        val expr: String
        expr = "cos(cos_1)"
        val e = ExpressionBuilder(expr)
                .variables("cos_1")
                .build()
                .setVariable("cos_1", 1.0)
        assertTrue(e.evaluate() == Math.cos(1.0))
    }

    @Test
    @Throws(Exception::class)
    fun testFunction23() {
        val expr: String
        expr = "log1p(1)"
        val e = ExpressionBuilder(expr)
                .build()
        assertEquals(ln1p(1.0), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testFunction24() {
        val expr: String
        expr = "pow(3,3)"
        val e = ExpressionBuilder(expr)
                .build()
        assertEquals(27.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testPostfix1() {
        val expr: String
        val expected: Double
        expr = "2.2232^0.1"
        expected = Math.pow(2.2232, 0.1)
        val actual = ExpressionBuilder(expr)
                .build().evaluate()
        assertTrue(expected == actual)
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixEverything() {
        val expr: String
        val expected: Double
        expr = "(sin(12) + log(34)) * 3.42 - cos(2.234-log(2))"
        expected = (Math.sin(12.0) + Math.log(34.0)) * 3.42 - Math.cos(2.234 - Math.log(2.0))
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixExponentation1() {
        val expr: String
        val expected: Double
        expr = "2^3"
        expected = Math.pow(2.0, 3.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixExponentation2() {
        val expr: String
        val expected: Double
        expr = "24 + 4 * 2^3"
        expected = 24 + 4 * Math.pow(2.0, 3.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixExponentation3() {
        val expr: String
        val expected: Double
        val x = 4.334
        expr = "24 + 4 * 2^x"
        expected = 24 + 4 * Math.pow(2.0, x)
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixExponentation4() {
        val expr: String
        val expected: Double
        val x = 4.334
        expr = "(24 + 4) * 2^log(x)"
        expected = (24 + 4) * Math.pow(2.0, Math.log(x))
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
                .setVariable("x", x)
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction1() {
        val expr: String
        val expected: Double
        expr = "log(1) * sin(0)"
        expected = Math.log(1.0) * Math.sin(0.0)
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction10() {
        val expr: String
        var expected: Double
        expr = "cbrt(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.cbrt(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction11() {
        val expr: String
        var expected: Double
        expr = "cos(x) - (1/cbrt(x))"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            if (x == 0.0) {
                x = x + 0.5
                continue
            }
            expected = Math.cos(x) - 1 / Math.cbrt(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction12() {
        val expr: String
        var expected: Double
        expr = "acos(x) * expm1(asin(x)) - exp(atan(x)) + floor(x) + cosh(x) - sinh(cbrt(x))"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.acos(x) * Math.expm1(Math.asin(x)) - Math.exp(Math.atan(x)) + Math.floor(x) + Math.cosh(x) - Math.sinh(Math.cbrt(x))
            if (expected.isNaN()) {
                assertTrue(e.setVariable("x", x).evaluate().isNaN())
            } else {
                assertTrue(expected == e.setVariable("x", x).evaluate())
            }
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction13() {
        val expr: String
        var expected: Double
        expr = "acos(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.acos(x)
            if (expected.isNaN()) {
                assertTrue(e.setVariable("x", x).evaluate().isNaN())
            } else {
                assertTrue(expected == e.setVariable("x", x).evaluate())
            }
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction14() {
        val expr: String
        var expected: Double
        expr = " expm1(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.expm1(x)
            if (expected.isNaN()) {
                assertTrue(e.setVariable("x", x).evaluate().isNaN())
            } else {
                assertTrue(expected == e.setVariable("x", x).evaluate())
            }
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction15() {
        val expr: String
        var expected: Double
        expr = "asin(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.asin(x)
            if (expected.isNaN()) {
                assertTrue(e.setVariable("x", x).evaluate().isNaN())
            } else {
                assertTrue(expected == e.setVariable("x", x).evaluate())
            }
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction16() {
        val expr: String
        var expected: Double
        expr = " exp(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.exp(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction17() {
        val expr: String
        var expected: Double
        expr = "floor(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.floor(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction18() {
        val expr: String
        var expected: Double
        expr = " cosh(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.cosh(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction19() {
        val expr: String
        var expected: Double
        expr = "sinh(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.sinh(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction20() {
        val expr: String
        var expected: Double
        expr = "cbrt(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.cbrt(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction21() {
        val expr: String
        var expected: Double
        expr = "tanh(x)"
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        var x = -10.0
        while (x < 10) {
            expected = Math.tanh(x)
            assertTrue(expected == e.setVariable("x", x).evaluate())
            x = x + 0.5
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction2() {
        val expr: String
        val expected: Double
        expr = "log(1)"
        expected = 0.0
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction3() {
        val expr: String
        val expected: Double
        expr = "sin(0)"
        expected = 0.0
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction5() {
        val expr: String
        val expected: Double
        expr = "ceil(2.3) +1"
        expected = Math.ceil(2.3) + 1
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction6() {
        val expr: String
        val expected: Double
        val x = 1.565
        val y = 2.1323
        expr = "ceil(x) + 1 / y * abs(1.4)"
        expected = Math.ceil(x) + 1 / y * Math.abs(1.4)
        val e = ExpressionBuilder(expr)
                .variables("x", "y")
                .build()
        assertTrue(expected == e.setVariable("x", x)
                .setVariable("y", y).evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction7() {
        val expr: String
        val expected: Double
        val x = Math.E
        expr = "tan(x)"
        expected = Math.tan(x)
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        assertTrue(expected == e.setVariable("x", x).evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction8() {
        val expr: String
        val expected: Double
        val varE = Math.E
        expr = "2^3.4223232 + tan(e)"
        expected = Math.pow(2.0, 3.4223232) + Math.tan(Math.E)
        val e = ExpressionBuilder(expr)
                .variables("e")
                .build()
        assertTrue(expected == e.setVariable("e", varE).evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixFunction9() {
        val expr: String
        val expected: Double
        val x = Math.E
        expr = "cbrt(x)"
        expected = Math.cbrt(x)
        val e = ExpressionBuilder(expr)
                .variables("x")
                .build()
        assertTrue(expected == e.setVariable("x", x).evaluate())
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testPostfixInvalidVariableName() {
        val expr: String
        val expected: Double
        val x = 4.5334332
        val log = Math.PI
        expr = "x * pi"
        expected = x * log
        val e = ExpressionBuilder(expr)
                .variables("x", "pi")
                .build()
        assertTrue(expected == e.setVariable("x", x)
                .setVariable("log", log).evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixParanthesis() {
        val expr: String
        val expected: Double
        expr = "(3 + 3 * 14) * (2 * (24-17) - 14)/((34) -2)"
        expected = ((3 + 3 * 14) * (2 * (24 - 17) - 14) / (34 - 2)).toDouble()
        val e = ExpressionBuilder(expr)
                .build()
        assertTrue(expected == e.evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testPostfixVariables() {
        val expr: String
        val expected: Double
        val x = 4.5334332
        val pi = Math.PI
        expr = "x * pi"
        expected = x * pi
        val e = ExpressionBuilder(expr)
                .variables("x", "pi")
                .build()
        assertTrue(expected == e.setVariable("x", x)
                .setVariable("pi", pi).evaluate())
    }

    @Test
    @Throws(Exception::class)
    fun testUnicodeVariable1() {
        val e = ExpressionBuilder("λ")
                .variable("λ")
                .build()
                .setVariable("λ", E)
        assertEquals(E, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnicodeVariable2() {
        val e = ExpressionBuilder("log(3ε+1)")
                .variable("ε")
                .build()
                .setVariable("ε", E)
        assertEquals(ln(3 * E + 1), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnicodeVariable3() {
        val log = object : Function("λωγ", 1) {

            override fun apply(vararg args: Double): Double {
                return ln(args[0])
            }
        }

        val e = ExpressionBuilder("λωγ(π)")
                .variable("π")
                .function(log)
                .build()
                .setVariable("π", PI)
        assertEquals(ln(PI), e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testUnicodeVariable4() {
        val log = object : Function("λ_ωγ", 1) {

            override fun apply(vararg args: Double): Double {
                return ln(args[0])
            }
        }

        val e = ExpressionBuilder("3λ_ωγ(πε6)")
                .variables("π", "ε")
                .function(log)
                .build()
                .setVariable("π", PI)
                .setVariable("ε", E)
        assertEquals(3 * ln(PI * E * 6.0), e.evaluate(), 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testImplicitMulitplicationOffNumber() {
        val e = ExpressionBuilder("var_12")
                .variable("var_1")
                .implicitMultiplication(false)
                .build()
        e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testImplicitMulitplicationOffVariable() {
        val e = ExpressionBuilder("var_1var_1")
                .variable("var_1")
                .implicitMultiplication(false)
                .build()
        e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testImplicitMulitplicationOffParantheses() {
        val e = ExpressionBuilder("var_1(2)")
                .variable("var_1")
                .implicitMultiplication(false)
                .build()
        e.evaluate()
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testImplicitMulitplicationOffFunction() {
        val e = ExpressionBuilder("var_1log(2)")
                .variable("var_1")
                .implicitMultiplication(false)
                .build()
                .setVariable("var_1", 2.0)
        e.evaluate()
    }

    @Test
    @Throws(Exception::class)
    fun testImplicitMulitplicationOnNumber() {
        val e = ExpressionBuilder("var_12")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2.0)
        assertEquals(4.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testImplicitMulitplicationOnVariable() {
        val e = ExpressionBuilder("var_1var_1")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2.0)
        assertEquals(4.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testImplicitMulitplicationOnParantheses() {
        val e = ExpressionBuilder("var_1(2)")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2.0)
        assertEquals(4.0, e.evaluate(), 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testImplicitMulitplicationOnFunction() {
        val e = ExpressionBuilder("var_1log(2)")
                .variable("var_1")
                .build()
                .setVariable("var_1", 2.0)
        assertEquals(2 * ln(2.0), e.evaluate(), 0.0)
    }

    // thanks go out to vandanagopal for reporting the issue
    // https://github.com/fasseg/exp4j/issues/23
    @Test
    @Throws(Exception::class)
    fun testSecondArgumentNegative() {
        val round = object : Function("MULTIPLY", 2) {
            override fun apply(vararg args: Double): Double {
                return Math.round(args[0] * args[1]).toDouble()
            }
        }
        val result = ExpressionBuilder("MULTIPLY(2,-1)")
                .function(round)
                .build()
                .evaluate()
        assertEquals(-2.0, result, 0.0)
    }

    // Test for https://github.com/fasseg/exp4j/issues/65
    @Test
    @Throws(Exception::class)
    fun testVariableWithDot() {
        val result = ExpressionBuilder("2*SALARY.Basic")
                .variable("SALARY.Basic")
                .build()
                .setVariable("SALARY.Basic", 1.5)
                .evaluate()
        assertEquals(3.0, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testTwoAdjacentOperators() {
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

        val result = ExpressionBuilder("3!+2")
                .operator(factorial)
                .build()
                .evaluate()

        val expected = 8.0
        assertEquals(expected, result, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun testGetVariableNames1() {
        val e = ExpressionBuilder("b*a-9.24c")
                .variables("b", "a", "c")
                .build()
        val variableNames = e.variableNames
        assertTrue(variableNames.contains("a"))
        assertTrue(variableNames.contains("b"))
        assertTrue(variableNames.contains("c"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetVariableNames2() {
        val e = ExpressionBuilder("log(bar)-FOO.s/9.24c")
                .variables("bar", "FOO.s", "c")
                .build()
        val variableNames = e.variableNames
        assertTrue(variableNames.contains("bar"))
        assertTrue(variableNames.contains("FOO.s"))
        assertTrue(variableNames.contains("c"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSameVariableAndBuiltinFunctionName() {
        val e = ExpressionBuilder("log10(log10)")
                .variables("log10")
                .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSameVariableAndUserFunctionName() {
        val e = ExpressionBuilder("2*tr+tr(2)")
                .variables("tr")
                .function(object : Function("tr") {
                    override fun apply(vararg args: Double): Double {
                        return 0.0
                    }
                })
                .build()
    }

    @Test
    fun testSignum() {
        var e = ExpressionBuilder("signum(1)")
                .build()
        assertEquals(1.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("signum(-1)")
                .build()
        assertEquals(-1.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("signum(--1)")
                .build()
        assertEquals(1.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("signum(+-1)")
                .build()
        assertEquals(-1.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("-+1")
                .build()
        assertEquals(-1.0, e.evaluate(), 0.0)

        e = ExpressionBuilder("signum(-+1)")
                .build()
        assertEquals(-1.0, e.evaluate(), 0.0)
    }

    @Test
    fun testCustomPercent() {
        val percentage = object : Function("percentage", 2) {
            override fun apply(vararg args: Double): Double {
                val `val` = args[0]
                val percent = args[1]
                return if (percent < 0) {
                    `val` - `val` * Math.abs(percent) / 100.0
                } else {
                    `val` - `val` * percent / 100.0
                }
            }
        }

        var e = ExpressionBuilder("percentage(1000,-10)")
                .function(percentage)
                .build()
        assertEquals(0.0, 900.0, e.evaluate())

        e = ExpressionBuilder("percentage(1000,12)")
                .function(percentage)
                .build()
        assertEquals(0.0, 1000.0 * 0.12, e.evaluate())
    }
}
