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
     * @param expression the expression to convert
     * @param userFunctions the custom functions used
     * @param userOperators the custom operators used
     * @param variableNames the variable names used in the expression
     * @param implicitMultiplication set to fasle to turn off implicit multiplication
     * @return a [net.objecthunter.exp4j.tokenizer.Token] array containing the result
     */
    fun convertToRPN(expression: String, userFunctions: Map<String, Function>?,
            userOperators: Map<String, Operator>?, variableNames: Set<String>?, implicitMultiplication: Boolean): Array<Token> {
        val stack = Stack<Token>()
        val output = ArrayList<Token>()

        val tokenizer = Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication)
        while (tokenizer.hasNext()) {
            val token = tokenizer.nextToken()
            when (token.type) {
                Token.TOKEN_NUMBER, Token.TOKEN_VARIABLE -> output.add(token)
                Token.TOKEN_FUNCTION -> stack.push(token)
                Token.TOKEN_SEPARATOR -> {
                    while (!stack.empty() && stack.peek().type != Token.TOKEN_PARENTHESES_OPEN.toInt()) {
                        output.add(stack.pop())
                    }
                    if (stack.empty() || stack.peek().type != Token.TOKEN_PARENTHESES_OPEN.toInt()) {
                        throw IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses")
                    }
                }
                Token.TOKEN_OPERATOR -> {
                    while (!stack.empty() && stack.peek().type == Token.TOKEN_OPERATOR.toInt()) {
                        val o1 = token as OperatorToken
                        val o2 = stack.peek() as OperatorToken
                        if (o1.operator.numOperands == 1 && o2.operator.numOperands == 2) {
                            break
                        } else if (o1.operator.isLeftAssociative && o1.operator.precedence <= o2.operator.precedence || o1.operator.precedence < o2.operator.precedence) {
                            output.add(stack.pop())
                        } else {
                            break
                        }
                    }
                    stack.push(token)
                }
                Token.TOKEN_PARENTHESES_OPEN -> stack.push(token)
                Token.TOKEN_PARENTHESES_CLOSE -> {
                    while (stack.peek().type != Token.TOKEN_PARENTHESES_OPEN.toInt()) {
                        output.add(stack.pop())
                    }
                    stack.pop()
                    if (!stack.empty() && stack.peek().type == Token.TOKEN_FUNCTION.toInt()) {
                        output.add(stack.pop())
                    }
                }
                else -> throw IllegalArgumentException("Unknown Token type encountered. This should not happen")
            }
        }
        while (!stack.empty()) {
            val t = stack.pop()
            if (t.type == Token.TOKEN_PARENTHESES_CLOSE.toInt() || t.type == Token.TOKEN_PARENTHESES_OPEN.toInt()) {
                throw IllegalArgumentException("Mismatched parentheses detected. Please check the expression")
            } else {
                output.add(t)
            }
        }
        return output.toTypedArray()
    }
}
