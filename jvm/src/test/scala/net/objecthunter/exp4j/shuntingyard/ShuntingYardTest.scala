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
package net.objecthunter.exp4j.shuntingyard

import net.objecthunter.exp4j.TestUtil._
import java.util
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.tokenizer.Token
import org.junit.Test

class ShuntingYardTest {
  @Test
  @throws[Exception]
  def testShuntingYard1(): Unit = {
    val expression = "2+3"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), null, true)
    assertNumberToken(tokens(0), 2d)
    assertNumberToken(tokens(1), 3d)
    assertOperatorToken(tokens(2), "+", 2, Operator.PRECEDENCE_ADDITION)
  }

  @Test
  @throws[Exception]
  def testShuntingYard2(): Unit = {
    val expression = "3*x"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), Set("x"), true)
    assertNumberToken(tokens(0), 3d)
    assertVariableToken(tokens(1), "x")
    assertOperatorToken(tokens(2), "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
  }

  @Test
  @throws[Exception]
  def testShuntingYard3(): Unit = {
    val expression = "-3"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), null, true)
    assertNumberToken(tokens(0), 3d)
    assertOperatorToken(tokens(1), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
  }

  @Test
  @throws[Exception]
  def testShuntingYard4(): Unit = {
    val expression = "-2^2"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), null, true)
    assertNumberToken(tokens(0), 2d)
    assertNumberToken(tokens(1), 2d)
    assertOperatorToken(tokens(2), "^", 2, Operator.PRECEDENCE_POWER)
    assertOperatorToken(tokens(3), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
  }

  @Test
  @throws[Exception]
  def testShuntingYard5(): Unit = {
    val expression = "2^-2"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), null, true)
    assertNumberToken(tokens(0), 2d)
    assertNumberToken(tokens(1), 2d)
    assertOperatorToken(tokens(2), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertOperatorToken(tokens(3), "^", 2, Operator.PRECEDENCE_POWER)
  }

  @Test
  @throws[Exception]
  def testShuntingYard6(): Unit = {
    val expression = "2^---+2"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), null, true)
    assertNumberToken(tokens(0), 2d)
    assertNumberToken(tokens(1), 2d)
    assertOperatorToken(tokens(2), "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertOperatorToken(tokens(3), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertOperatorToken(tokens(4), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertOperatorToken(tokens(5), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertOperatorToken(tokens(6), "^", 2, Operator.PRECEDENCE_POWER)
  }

  @Test
  @throws[Exception]
  def testShuntingYard7(): Unit = {
    val expression = "2^-2!"
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
    val userOperators = Map("!" -> factorial)
    val tokens = ShuntingYard.convertToRPN(expression, Map(), userOperators, null, true)
    assertNumberToken(tokens(0), 2d)
    assertNumberToken(tokens(1), 2d)
    assertOperatorToken(tokens(2), "!", 1, Operator.PRECEDENCE_POWER + 1)
    assertOperatorToken(tokens(3), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertOperatorToken(tokens(4), "^", 2, Operator.PRECEDENCE_POWER)
  }

  @Test
  @throws[Exception]
  def testShuntingYard8(): Unit = {
    val expression = "-3^2"
    val tokens = ShuntingYard.convertToRPN(expression, Map(), Map(), null, true)
    assertNumberToken(tokens(0), 3d)
    assertNumberToken(tokens(1), 2d)
    assertOperatorToken(tokens(2), "^", 2, Operator.PRECEDENCE_POWER)
    assertOperatorToken(tokens(3), "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
  }

  @Test
  @throws[Exception]
  def testShuntingYard9(): Unit = {
    val reciprocal = new Operator("$", 1, true, Operator.PRECEDENCE_DIVISION) {
      def apply(args: Double*): Double = {
        if (args(0) == 0d) throw new ArithmeticException("Division by zero!")
        1d / args(0)
      }
    }
    val userOperators = Map("$" -> reciprocal)
    val tokens = ShuntingYard.convertToRPN("1$", Map(), userOperators, null, true)
    assertNumberToken(tokens(0), 1d)
    assertOperatorToken(tokens(1), "$", 1, Operator.PRECEDENCE_DIVISION)
  }
}
