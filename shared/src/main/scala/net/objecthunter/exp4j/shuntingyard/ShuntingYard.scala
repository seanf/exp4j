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

import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.tokenizer.OperatorToken
import net.objecthunter.exp4j.tokenizer.Token
import net.objecthunter.exp4j.tokenizer.Tokenizer

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * Shunting yard implementation to convert infix to reverse polish notation
  */
object ShuntingYard {
  /**
    * Convert a Set of tokens from infix to reverse polish notation
    *
    * @param expression             the expression to convert
    * @param userFunctions          the custom functions used
    * @param userOperators          the custom operators used
    * @param variableNames          the variable names used in the expression
    * @param implicitMultiplication set to fasle to turn off implicit multiplication
    * @return a { @link net.objecthunter.exp4j.tokenizer.Token} array containing the result
    */
    def convertToRPN(expression: String, userFunctions: Map[String, Function], userOperators: Map[String, Operator], variableNames: Set[String], implicitMultiplication: Boolean): Array[Token] = {
      val stack = new mutable.Stack[Token]
      val output = new ArrayBuffer[Token]
      val tokenizer = new Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication)
      while ( {
        tokenizer.hasNext
      }) {
        val token = tokenizer.nextToken
        token.getType match {
          case Token.TOKEN_NUMBER | Token.TOKEN_VARIABLE =>
            output.+=(token)
          case Token.TOKEN_FUNCTION =>
            stack.push(token)
          case Token.TOKEN_SEPARATOR =>
            while ( {
              stack.nonEmpty && stack.top.getType != Token.TOKEN_PARENTHESES_OPEN
            }) output.+=(stack.pop)
            if (stack.isEmpty || stack.top.getType != Token.TOKEN_PARENTHESES_OPEN) throw new IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses")
          case Token.TOKEN_OPERATOR =>
            // TODO eliminate break
            breakable {
              while ( {
                stack.nonEmpty && stack.top.getType == Token.TOKEN_OPERATOR
              }) {
                val o1 = token.asInstanceOf[OperatorToken]
                val o2 = stack.top.asInstanceOf[OperatorToken]
                if (o1.getOperator.getNumOperands == 1 && o2.getOperator.getNumOperands == 2)
                  break
                else if ((o1.getOperator.isLeftAssociative && o1.getOperator.getPrecedence <= o2.getOperator.getPrecedence) || (o1.getOperator.getPrecedence < o2.getOperator.getPrecedence))
                  output.+=(stack.pop)
                else
                  break
              }
            }
            stack.push(token)
          case Token.TOKEN_PARENTHESES_OPEN =>
            stack.push(token)
          case Token.TOKEN_PARENTHESES_CLOSE =>
            while ( {
              stack.top.getType != Token.TOKEN_PARENTHESES_OPEN
            }) output.+=(stack.pop)
            stack.pop
            if (stack.nonEmpty && stack.top.getType == Token.TOKEN_FUNCTION) output.+=(stack.pop)
          case _ =>
            throw new IllegalArgumentException("Unknown Token type encountered. This should not happen")
        }
      }
      while (stack.nonEmpty) {
        val t = stack.pop
        if (t.getType == Token.TOKEN_PARENTHESES_CLOSE || t.getType == Token.TOKEN_PARENTHESES_OPEN) throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression")
        else output.+=(t)
      }
      output.toArray
    }
}
