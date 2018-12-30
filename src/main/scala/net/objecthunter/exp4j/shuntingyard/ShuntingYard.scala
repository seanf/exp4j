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

import java.util._
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.tokenizer.OperatorToken
import net.objecthunter.exp4j.tokenizer.Token
import net.objecthunter.exp4j.tokenizer.Tokenizer

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
    def convertToRPN(expression: String, userFunctions: util.Map[String, Function], userOperators: util.Map[String, Operator], variableNames: util.Set[String], implicitMultiplication: Boolean): Array[Token] = {
      val stack = new util.Stack[Token]
      val output = new util.ArrayList[Token]
      val tokenizer = new Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication)
      while ( {
        tokenizer.hasNext
      }) {
        val token = tokenizer.nextToken
        token.getType match {
          case Token.TOKEN_NUMBER =>
          case Token.TOKEN_VARIABLE =>
            output.add(token)
            break //todo: break is not supported
          case Token.TOKEN_FUNCTION =>
            stack.add(token)
            break //todo: break is not supported
          case Token.TOKEN_SEPARATOR =>
            while ( {
              !stack.empty && stack.peek.getType != Token.TOKEN_PARENTHESES_OPEN
            }) output.add(stack.pop)
            if (stack.empty || stack.peek.getType != Token.TOKEN_PARENTHESES_OPEN) throw new IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses")
            break //todo: break is not supported
          case Token.TOKEN_OPERATOR =>
            while ( {
              !stack.empty && stack.peek.getType == Token.TOKEN_OPERATOR
            }) {
              val o1 = token.asInstanceOf[OperatorToken]
              val o2 = stack.peek.asInstanceOf[OperatorToken]
              if (o1.getOperator.getNumOperands == 1 && o2.getOperator.getNumOperands == 2) break //todo: break is not supported
              else if ((o1.getOperator.isLeftAssociative && o1.getOperator.getPrecedence <= o2.getOperator.getPrecedence) || (o1.getOperator.getPrecedence < o2.getOperator.getPrecedence)) output.add(stack.pop)
              else break //todo: break is not supported
            }
            stack.push(token)
            break //todo: break is not supported
          case Token.TOKEN_PARENTHESES_OPEN =>
            stack.push(token)
            break //todo: break is not supported
          case Token.TOKEN_PARENTHESES_CLOSE =>
            while ( {
              stack.peek.getType != Token.TOKEN_PARENTHESES_OPEN
            }) output.add(stack.pop)
            stack.pop
            if (!stack.isEmpty && stack.peek.getType == Token.TOKEN_FUNCTION) output.add(stack.pop)
            break //todo: break is not supported
          case _ =>
            throw new IllegalArgumentException("Unknown Token type encountered. This should not happen")
        }
      }
      while ( {
        !stack.empty
      }) {
        val t = stack.pop
        if (t.getType == Token.TOKEN_PARENTHESES_CLOSE || t.getType == Token.TOKEN_PARENTHESES_OPEN) throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression")
        else output.add(t)
      }
      output.toArray(new Array[Token](output.size)).asInstanceOf[Array[Token]]
    }
}
