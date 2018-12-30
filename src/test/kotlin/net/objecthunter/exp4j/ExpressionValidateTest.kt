/*
 * Copyright 2014 Bartosz Firyn
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

import net.objecthunter.exp4j.function.Function

import org.junit.Assert
import org.junit.Test

import java.util.Date

import org.junit.Assert.assertTrue


class ExpressionValidateTest {

    /**
     * Dummy function with 2 arguments.
     */
    internal var beta: Function = object : Function("beta", 2) {

        override fun apply(vararg args: Double): Double {
            return args[1] - args[0]
        }
    }

    /**
     * Dummy function with 3 arguments.
     */
    internal var gamma: Function = object : Function("gamma", 3) {

        override fun apply(vararg args: Double): Double {
            return args[0] * args[1] / args[2]
        }
    }

    /**
     * Dummy function with 7 arguments.
     */
    internal var eta: Function = object : Function("eta", 7) {

        override fun apply(vararg args: Double): Double {
            var eta = 0.0
            for (a in args) {
                eta += a
            }
            return eta
        }
    }

    // valid scenarios

    @Test
    @Throws(Exception::class)
    fun testValidateNumber() {
        val exp = ExpressionBuilder("1")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateNumberPositive() {
        val exp = ExpressionBuilder("+1")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateNumberNegative() {
        val exp = ExpressionBuilder("-1")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateOperator() {
        val exp = ExpressionBuilder("x + 1 + 2")
                .variable("x")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunction() {
        val exp = ExpressionBuilder("sin(x)")
                .variable("x")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionPositive() {
        val exp = ExpressionBuilder("+sin(x)")
                .variable("x")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionNegative() {
        val exp = ExpressionBuilder("-sin(x)")
                .variable("x")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionAndOperator() {
        val exp = ExpressionBuilder("sin(x + 1 + 2)")
                .variable("x")
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithTwoArguments() {
        val exp = ExpressionBuilder("beta(x, y)")
                .variables("x", "y")
                .functions(beta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithTwoArgumentsAndOperator() {
        val exp = ExpressionBuilder("beta(x, y + 1)")
                .variables("x", "y")
                .functions(beta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithThreeArguments() {
        val exp = ExpressionBuilder("gamma(x, y, z)")
                .variables("x", "y", "z")
                .functions(gamma)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithThreeArgumentsAndOperator() {
        val exp = ExpressionBuilder("gamma(x, y, z + 1)")
                .variables("x", "y", "z")
                .functions(gamma)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithTwoAndThreeArguments() {
        val exp = ExpressionBuilder("gamma(x, beta(y, h), z)")
                .variables("x", "y", "z", "h")
                .functions(gamma, beta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithTwoAndThreeArgumentsAndOperator() {
        val exp = ExpressionBuilder("gamma(x, beta(y, h), z + 1)")
                .variables("x", "y", "z", "h")
                .functions(gamma, beta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithTwoAndThreeArgumentsAndMultipleOperator() {
        val exp = ExpressionBuilder("gamma(x * 2 / 4, beta(y, h + 1 + 2), z + 1 + 2 + 3 + 4)")
                .variables("x", "y", "z", "h")
                .functions(gamma, beta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithSevenArguments() {
        val exp = ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7)")
                .functions(eta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateFunctionWithSevenArgumentsAndoperator() {
        val exp = ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7) * 2 * 3 * 4")
                .functions(eta)
                .build()
        val result = exp.validate(false)
        Assert.assertTrue(result.isValid)
    }

    // invalid scenarios

    @Test
    @Throws(Exception::class)
    fun testValidateInvalidFunction() {
        val exp = ExpressionBuilder("sin()")
                .build()
        val result = exp.validate(false)
        Assert.assertFalse(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateInvalidOperand() {
        val exp = ExpressionBuilder("1 + ")
                .build()
        val result = exp.validate(false)
        Assert.assertFalse(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateInvalidFunctionWithTooFewArguments() {
        val exp = ExpressionBuilder("beta(1)")
                .functions(beta)
                .build()
        val result = exp.validate(false)
        Assert.assertFalse(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateInvalidFunctionWithTooFewArgumentsAndOperands() {
        val exp = ExpressionBuilder("beta(1 + )")
                .functions(beta)
                .build()
        val result = exp.validate(false)
        Assert.assertFalse(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateInvalidFunctionWithManyArguments() {
        val exp = ExpressionBuilder("beta(1, 2, 3)")
                .functions(beta)
                .build()
        val result = exp.validate(false)
        Assert.assertFalse(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun testValidateInvalidOperator() {
        val exp = ExpressionBuilder("+")
                .build()
        val result = exp.validate(false)
        Assert.assertFalse(result.isValid)
    }

    // Thanks go out to werwiesel for reporting the issue
    // https://github.com/fasseg/exp4j/issues/59
    @Test
    @Throws(Exception::class)
    fun testNoArgFunctionValidation() {
        val now = object : Function("now", 0) {
            override fun apply(vararg args: Double): Double {
                return Date().time.toDouble()
            }
        }
        var e = ExpressionBuilder("14*now()")
                .function(now)
                .build()
        assertTrue(e.validate().isValid)

        e = ExpressionBuilder("now()")
                .function(now)
                .build()
        assertTrue(e.validate().isValid)

        e = ExpressionBuilder("sin(now())")
                .function(now)
                .build()
        assertTrue(e.validate().isValid)

        e = ExpressionBuilder("sin(now()) % 14")
                .function(now)
                .build()
        assertTrue(e.validate().isValid)
    }

}
