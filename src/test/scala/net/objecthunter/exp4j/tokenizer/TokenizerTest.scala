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
package net.objecthunter.exp4j.tokenizer

import net.objecthunter.exp4j.TestUtil._
import org.junit.Assert._
import java.util
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import org.junit.Test

class TokenizerTest {
  @Test
  @throws[Exception]
  def testTokenization1(): Unit = {
    val tokenizer = new Nothing("1.222331", null, null, null)
    assertNumberToken(tokenizer.nextToken, 1.222331d)
  }

  @Test
  @throws[Exception]
  def testTokenization2(): Unit = {
    val tokenizer = new Nothing(".222331", null, null, null)
    assertNumberToken(tokenizer.nextToken, .222331d)
  }

  @Test
  @throws[Exception]
  def testTokenization3(): Unit = {
    val tokenizer = new Nothing("3e2", null, null, null)
    assertNumberToken(tokenizer.nextToken, 300d)
  }

  @Test
  @throws[Exception]
  def testTokenization4(): Unit = {
    val tokenizer = new Nothing("3+1", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 1d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization5(): Unit = {
    val tokenizer = new Nothing("+3", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization6(): Unit = {
    val tokenizer = new Nothing("-3", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization7(): Unit = {
    val tokenizer = new Nothing("---++-3", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization8(): Unit = {
    val tokenizer = new Nothing("---++-3.004", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3.004d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization9(): Unit = {
    val tokenizer = new Nothing("3+-1", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 1d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization10(): Unit = {
    val tokenizer = new Nothing("3+-1-.32++2", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 1d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 2, Operator.PRECEDENCE_SUBTRACTION)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 0.32d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 1, Operator.PRECEDENCE_UNARY_PLUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization11(): Unit = {
    val tokenizer = new Nothing("2+", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization12(): Unit = {
    val tokenizer = new Nothing("log(1)", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 1d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization13(): Unit = {
    val tokenizer = new Nothing("x", null, null, new util.HashSet[String](util.Arrays.asList("x")))
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "x")
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization14(): Unit = {
    val tokenizer = new Nothing("2*x-log(3)", null, null, new util.HashSet[String](util.Arrays.asList("x")))
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "x")
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 2, Operator.PRECEDENCE_SUBTRACTION)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization15(): Unit = {
    val tokenizer = new Nothing("2*xlog+log(3)", null, null, new util.HashSet[String](util.Arrays.asList("xlog")))
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "xlog")
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization16(): Unit = {
    val tokenizer = new Nothing("2*x+-log(3)", null, null, new util.HashSet[String](util.Arrays.asList("x")))
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "x")
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization17(): Unit = {
    val tokenizer = new Nothing("2 * x + -log(3)", null, null, new util.HashSet[String](util.Arrays.asList("x")))
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "x")
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "+", 2, Operator.PRECEDENCE_ADDITION)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 3d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization18(): Unit = {
    val log2 = new Nothing("log2") {
      def apply(args: Double*): Double = Math.log(args(0)) / Math.log(2d)
    }
    val funcs = new util.HashMap[String, Nothing](1)
    funcs.put(log2.getName, log2)
    val tokenizer = new Nothing("log2(4)", funcs, null, null)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log2", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 4d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization19(): Unit = {
    val avg = new Nothing(("avg", 2)) {
      def apply(args: Double*): Double = {
        var sum = 0
        for (arg <- args) {
          sum += arg
        }
        sum / args.length
      }
    }
    val funcs = new util.HashMap[String, Nothing](1)
    funcs.put(avg.getName, avg)
    val tokenizer = new Nothing("avg(1,2)", funcs, null, null)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "avg", 2)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 1d)
    assertTrue(tokenizer.hasNext)
    assertFunctionSeparatorToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization20(): Unit = {
    val factorial = new Nothing(("!", 1, true, Operator.PRECEDENCE_POWER + 1)) {
      def apply(args: Double*) = 0d
    }
    val operators = new util.HashMap[String, Nothing](1)
    operators.put(factorial.getSymbol, factorial)
    val tokenizer = new Nothing("2!", null, operators, null)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "!", factorial.getNumOperands, factorial.getPrecedence)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization21(): Unit = {
    val tokenizer = new Nothing("log(x) - y * (sqrt(x^cos(y)))", null, null, new util.HashSet[String](util.Arrays.asList("x", "y")))
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "log", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "x")
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 2, Operator.PRECEDENCE_SUBTRACTION)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "y")
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "sqrt", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "x")
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "^", 2, Operator.PRECEDENCE_POWER)
    assertTrue(tokenizer.hasNext)
    assertFunctionToken(tokenizer.nextToken, "cos", 1)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertVariableToken(tokenizer.nextToken, "y")
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }

  @Test
  @throws[Exception]
  def testTokenization22(): Unit = {
    val tokenizer = new Nothing("--2 * (-14)", null, null, null)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 2d)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "*", 2, Operator.PRECEDENCE_MULTIPLICATION)
    assertTrue(tokenizer.hasNext)
    assertOpenParenthesesToken(tokenizer.nextToken)
    assertTrue(tokenizer.hasNext)
    assertOperatorToken(tokenizer.nextToken, "-", 1, Operator.PRECEDENCE_UNARY_MINUS)
    assertTrue(tokenizer.hasNext)
    assertNumberToken(tokenizer.nextToken, 14d)
    assertTrue(tokenizer.hasNext)
    assertCloseParenthesesToken(tokenizer.nextToken)
    assertFalse(tokenizer.hasNext)
  }
}
