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
*//*
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
import net.objecthunter.exp4j.tokenizer._
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ExpressionTest {
  @Test
  @throws[Exception]
  def testExpression1(): Unit = {
    val tokens = Array[Token](new NumberToken(3d), new NumberToken(2d), new OperatorToken(Operators.getBuiltinOperator('+', 2)))
    val exp = new Expression(tokens)
    assertEquals(5d, exp.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression2(): Unit = {
    val tokens = Array[Token](new NumberToken(1d), new FunctionToken(Functions.getBuiltinFunction("log")))
    val exp = new Expression(tokens)
    assertEquals(0d, exp.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testGetVariableNames1(): Unit = {
    val tokens = Array[Token](new VariableToken("a"), new VariableToken("b"), new OperatorToken(Operators.getBuiltinOperator('+', 2)))
    val exp = new Expression(tokens)
    assertEquals(2, exp.getVariableNames.size)
  }

  @Test
  @throws[Exception]
  def testFactorial(): Unit = {
    val factorial = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1d
        for (i <- 1 to arg) {
          result *= i
        }
        result
      }
    }
    var e = new ExpressionBuilder("2!+3!").operator(factorial).build
    assertEquals(8d, e.evaluate, 0d)
    e = new ExpressionBuilder("3!-2!").operator(factorial).build
    assertEquals(4d, e.evaluate, 0d)
    e = new ExpressionBuilder("3!").operator(factorial).build
    assertEquals(6, e.evaluate, 0)
    e = new ExpressionBuilder("3!!").operator(factorial).build
    assertEquals(720, e.evaluate, 0)
    e = new ExpressionBuilder("4 + 3!").operator(factorial).build
    assertEquals(10, e.evaluate, 0)
    e = new ExpressionBuilder("3! * 2").operator(factorial).build
    assertEquals(12, e.evaluate, 0)
    e = new ExpressionBuilder("3!").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(6, e.evaluate, 0)
    e = new ExpressionBuilder("3!!").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(720, e.evaluate, 0)
    e = new ExpressionBuilder("4 + 3!").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(10, e.evaluate, 0)
    e = new ExpressionBuilder("3! * 2").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(12, e.evaluate, 0)
    e = new ExpressionBuilder("2 * 3!").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(12, e.evaluate, 0)
    e = new ExpressionBuilder("4 + (3!)").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(10, e.evaluate, 0)
    e = new ExpressionBuilder("4 + 3! + 2 * 6").operator(factorial).build
    assertTrue(e.validate.isValid)
    assertEquals(22, e.evaluate, 0)
  }

  @Test def testCotangent1(): Unit = {
    val e = new ExpressionBuilder("cot(1)").build
    assertEquals(1 / Math.tan(1), e.evaluate, 0d)
  }

  @Test(expected = classOf[ArithmeticException]) def testInvalidCotangent1(): Unit = {
    val e = new ExpressionBuilder("cot(0)").build
    e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testOperatorFactorial2(): Unit = {
    val factorial = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1d
        for (i <- 1 to arg) {
          result *= i
        }
        result
      }
    }
    val e = new ExpressionBuilder("!3").build
    assertFalse(e.validate.isValid)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testInvalidFactorial2(): Unit = {
    val factorial = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1d
        for (i <- 1 to arg) {
          result *= i
        }
        result
      }
    }
    val e = new ExpressionBuilder("!!3").build
    assertFalse(e.validate.isValid)
  }

  @Test
  @Ignore
  @throws[Exception]
  // If Expression should be threads safe this test must pass
  def evaluateFamily(): Unit = {
    val e = new ExpressionBuilder("sin(x)").variable("x").build
    val executor = Executors.newFixedThreadPool(100)
    for (i <- 0 until 100000) {
      executor.execute(() => {
        val x = Math.random
        e.setVariable("x", x)
        try Thread.sleep(100)
        catch {
          case e1: InterruptedException =>
            e1.printStackTrace()
        }
        assertEquals(Math.sin(x), e.evaluate, 0f)
      })
    }
  }
}
