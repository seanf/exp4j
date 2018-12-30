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
 *//*
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

import org.junit.Assert
import org.junit.Test
import java.util.Date
import org.junit.Assert.assertTrue

class ExpressionValidateTest {
  /**
    * Dummy function with 2 arguments.
    */
    private[exp4j] val beta = new function.Function("beta", 2) {
      def apply(args: Double*): Double = args(1) - args(0)
    }
  /**
    * Dummy function with 3 arguments.
    */
  private[exp4j] val gamma = new function.Function("gamma", 3) {
    def apply(args: Double*): Double = args(0) * args(1) / args(2)
  }
  /**
    * Dummy function with 7 arguments.
    */
  private[exp4j] val eta = new function.Function("eta", 7) {
    def apply(args: Double*): Double = {
      var eta: Double = 0
      for (a <- args) {
        eta += a
      }
      eta
    }
  }

  // valid scenarios
  @Test
  @throws[Exception]
  def testValidateNumber(): Unit = {
    val exp = new ExpressionBuilder("1").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateNumberPositive(): Unit = {
    val exp = new ExpressionBuilder("+1").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateNumberNegative(): Unit = {
    val exp = new ExpressionBuilder("-1").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateOperator(): Unit = {
    val exp = new ExpressionBuilder("x + 1 + 2").variable("x").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunction(): Unit = {
    val exp = new ExpressionBuilder("sin(x)").variable("x").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionPositive(): Unit = {
    val exp = new ExpressionBuilder("+sin(x)").variable("x").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionNegative(): Unit = {
    val exp = new ExpressionBuilder("-sin(x)").variable("x").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionAndOperator(): Unit = {
    val exp = new ExpressionBuilder("sin(x + 1 + 2)").variable("x").build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithTwoArguments(): Unit = {
    val exp = new ExpressionBuilder("beta(x, y)").variables("x", "y").functions(beta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithTwoArgumentsAndOperator(): Unit = {
    val exp = new ExpressionBuilder("beta(x, y + 1)").variables("x", "y").functions(beta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithThreeArguments(): Unit = {
    val exp = new ExpressionBuilder("gamma(x, y, z)").variables("x", "y", "z").functions(gamma).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithThreeArgumentsAndOperator(): Unit = {
    val exp = new ExpressionBuilder("gamma(x, y, z + 1)").variables("x", "y", "z").functions(gamma).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithTwoAndThreeArguments(): Unit = {
    val exp = new ExpressionBuilder("gamma(x, beta(y, h), z)").variables("x", "y", "z", "h").functions(gamma, beta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithTwoAndThreeArgumentsAndOperator(): Unit = {
    val exp = new ExpressionBuilder("gamma(x, beta(y, h), z + 1)").variables("x", "y", "z", "h").functions(gamma, beta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithTwoAndThreeArgumentsAndMultipleOperator(): Unit = {
    val exp = new ExpressionBuilder("gamma(x * 2 / 4, beta(y, h + 1 + 2), z + 1 + 2 + 3 + 4)").variables("x", "y", "z", "h").functions(gamma, beta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithSevenArguments(): Unit = {
    val exp = new ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7)").functions(eta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateFunctionWithSevenArgumentsAndoperator(): Unit = {
    val exp = new ExpressionBuilder("eta(1, 2, 3, 4, 5, 6, 7) * 2 * 3 * 4").functions(eta).build
    val result = exp.validate(false)
    Assert.assertTrue(result.isValid)
  }

  // invalid scenarios
  @Test
  @throws[Exception]
  def testValidateInvalidFunction(): Unit = {
    val exp = new ExpressionBuilder("sin()").build
    val result = exp.validate(false)
    Assert.assertFalse(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateInvalidOperand(): Unit = {
    val exp = new ExpressionBuilder("1 + ").build
    val result = exp.validate(false)
    Assert.assertFalse(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateInvalidFunctionWithTooFewArguments(): Unit = {
    val exp = new ExpressionBuilder("beta(1)").functions(beta).build
    val result = exp.validate(false)
    Assert.assertFalse(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateInvalidFunctionWithTooFewArgumentsAndOperands(): Unit = {
    val exp = new ExpressionBuilder("beta(1 + )").functions(beta).build
    val result = exp.validate(false)
    Assert.assertFalse(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateInvalidFunctionWithManyArguments(): Unit = {
    val exp = new ExpressionBuilder("beta(1, 2, 3)").functions(beta).build
    val result = exp.validate(false)
    Assert.assertFalse(result.isValid)
  }

  @Test
  @throws[Exception]
  def testValidateInvalidOperator(): Unit = {
    val exp = new ExpressionBuilder("+").build
    val result = exp.validate(false)
    Assert.assertFalse(result.isValid)
  }

  // Thanks go out to werwiesel for reporting the issue
  // https://github.com/fasseg/exp4j/issues/59
  @Test
  @throws[Exception]
  def testNoArgFunctionValidation(): Unit = {
    val now = new function.Function("now", 0) {
      def apply(args: Double*): Double = new Date().getTime.toDouble
    }
    var e = new ExpressionBuilder("14*now()").function(now).build
    assertTrue(e.validate.isValid)
    e = new ExpressionBuilder("now()").function(now).build
    assertTrue(e.validate.isValid)
    e = new ExpressionBuilder("sin(now())").function(now).build
    assertTrue(e.validate.isValid)
    e = new ExpressionBuilder("sin(now()) % 14").function(now).build
    assertTrue(e.validate.isValid)
  }
}
