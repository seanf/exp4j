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
package net.objecthunter.exp4j.tokenizer

import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.function.Functions
import net.objecthunter.exp4j.multiplatform.isDigit
import net.objecthunter.exp4j.multiplatform.isLetter
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.operator.Operators

class Tokenizer(expression: String, userFunctions: Map<String, Function>?, userOperators: Map<String, Operator>?, variableNames: Set<String>?, private val implicitMultiplication: Boolean = true) {

    private val expression: String = expression.trim { it <= ' ' }

    private val expressionLength: Int

    private val userFunctions: Map<String, Function>

    private val userOperators: Map<String, Operator>

    private val variableNames: Set<String>

    private var pos = 0

    private var lastToken: Token? = null


    init {
        this.expressionLength = this.expression.length
        this.userFunctions = userFunctions ?: mapOf()
        this.userOperators = userOperators ?: mapOf()
        this.variableNames = variableNames ?: setOf()
    }

    operator fun hasNext(): Boolean {
        return this.expression.length > pos
    }

    fun nextToken(): Token {
        var ch = expression[pos]
        while (ch.isWhitespace()) {
            ch = expression[++pos]
        }
        if (ch.isDigit() || ch == '.') {
            lastToken?.let { lt ->
                if (lt.type == Token.TOKEN_NUMBER) {
                    throw IllegalArgumentException("Unable to parse char '" + ch + "' (Code:" + ch.toInt() + ") at [" + pos + "]")
                } else if (implicitMultiplication && (lt.type != Token.TOKEN_OPERATOR
                                && lt.type != Token.TOKEN_PARENTHESES_OPEN
                                && lt.type != Token.TOKEN_FUNCTION
                                && lt.type != Token.TOKEN_SEPARATOR)) {
                    // insert an implicit multiplication token
                    lastToken = OperatorToken(Operators.getBuiltinOperator('*', 2)!!)
                    return lastToken!!
                }
            }
            return parseNumberToken(ch)
        } else if (isArgumentSeparator(ch)) {
            return parseArgumentSeparatorToken(ch)
        } else if (isOpenParentheses(ch)) {
            lastToken?.let { lt ->
                if (implicitMultiplication &&
                        (lt.type != Token.TOKEN_OPERATOR
                                && lt.type != Token.TOKEN_PARENTHESES_OPEN
                                && lt.type != Token.TOKEN_FUNCTION
                                && lt.type != Token.TOKEN_SEPARATOR)) {
                    // insert an implicit multiplication token
                    lastToken = OperatorToken(Operators.getBuiltinOperator('*', 2)!!)
                    return lastToken!!
                }
            }
            return parseParentheses(true)
        } else if (isCloseParentheses(ch)) {
            return parseParentheses(false)
        } else if (Operator.isAllowedOperatorChar(ch)) {
            return parseOperatorToken(ch)
        } else if (isAlphabetic(ch) || ch == '_') {
            // parse the name which can be a setVariable or a function
            lastToken?.let { lt ->
                if (implicitMultiplication &&
                        (lt.type != Token.TOKEN_OPERATOR
                                && lt.type != Token.TOKEN_PARENTHESES_OPEN
                                && lt.type != Token.TOKEN_FUNCTION
                                && lt.type != Token.TOKEN_SEPARATOR)) {
                    // insert an implicit multiplication token
                    lastToken = OperatorToken(Operators.getBuiltinOperator('*', 2)!!)
                    return lastToken!!
                }
            }
            return parseFunctionOrVariable()

        }
        throw IllegalArgumentException("Unable to parse char '" + ch + "' (Code:" + ch.toInt() + ") at [" + pos + "]")
    }

    private fun parseArgumentSeparatorToken(@Suppress("UNUSED_PARAMETER") ch: Char): Token {
        this.pos++
        this.lastToken = ArgumentSeparatorToken()
        return lastToken!!
    }

    private fun isArgumentSeparator(ch: Char): Boolean {
        return ch == ','
    }

    private fun parseParentheses(open: Boolean): Token {
        if (open) {
            this.lastToken = OpenParenthesesToken()
        } else {
            this.lastToken = CloseParenthesesToken()
        }
        this.pos++
        return lastToken!!
    }

    private fun isOpenParentheses(ch: Char): Boolean {
        return ch == '(' || ch == '{' || ch == '['
    }

    private fun isCloseParentheses(ch: Char): Boolean {
        return ch == ')' || ch == '}' || ch == ']'
    }

    private fun parseFunctionOrVariable(): Token {
        val offset = this.pos
        var testPos: Int
        var lastValidLen = 1
        var lastValidToken: Token? = null
        var len = 1
        if (isEndOfExpression(offset)) {
            this.pos++
        }
        testPos = offset + len - 1
        while (!isEndOfExpression(testPos) && isVariableOrFunctionCharacter(expression[testPos])) {
            val name = expression.substring(offset, offset + len)
            if (variableNames.contains(name)) {
                lastValidLen = len
                lastValidToken = VariableToken(name)
            } else {
                val f = getFunction(name)
                if (f != null) {
                    lastValidLen = len
                    lastValidToken = FunctionToken(f)
                }
            }
            len++
            testPos = offset + len - 1
        }
        if (lastValidToken == null) {
            throw UnknownFunctionOrVariableException(expression, pos, len)
        }
        pos += lastValidLen
        lastToken = lastValidToken
        return lastToken!!
    }

    private fun getFunction(name: String): Function? {
        return userFunctions[name] ?: Functions.getBuiltinFunction(name)
    }

    private fun parseOperatorToken(firstChar: Char): Token {
        val offset = this.pos
        var len = 1
        var symbol = StringBuilder()
        var lastValid: Operator? = null
        symbol.append(firstChar)

        while (!isEndOfExpression(offset + len) && Operator.isAllowedOperatorChar(expression[offset + len])) {
            symbol.append(expression[offset + len++])
        }

        while (symbol.isNotEmpty()) {
            val op = this.getOperator(symbol.toString())
            if (op == null) {
                // remove last char
//                symbol.setLength(symbol.length - 1)
                symbol = StringBuilder(symbol.subSequence(0, symbol.length - 1))
            } else {
                lastValid = op
                break
            }
        }

        pos += symbol.length
        lastToken = OperatorToken(lastValid!!)
        return lastToken!!
    }

    private fun getOperator(symbol: String): Operator? {
        var op: Operator? = this.userOperators[symbol]
        if (op == null && symbol.length == 1) {
            var argc = 2
            if (lastToken == null) {
                argc = 1
            } else {
                val lt = lastToken!!
                val lastTokenType = lt.type
                if (lastTokenType == Token.TOKEN_PARENTHESES_OPEN || lastTokenType == Token.TOKEN_SEPARATOR) {
                    argc = 1
                } else if (lastTokenType == Token.TOKEN_OPERATOR) {
                    val lastOp = (lt as OperatorToken).operator
                    if (lastOp.numOperands == 2 || lastOp.numOperands == 1 && !lastOp.isLeftAssociative) {
                        argc = 1
                    }
                }

            }
            op = Operators.getBuiltinOperator(symbol[0], argc)
        }
        return op
    }

    private fun parseNumberToken(firstChar: Char): Token {
        val offset = this.pos
        var len = 1
        this.pos++
        if (isEndOfExpression(offset + len)) {
            lastToken = NumberToken(firstChar.toString().toDouble())
            return lastToken!!
        }
        while (!isEndOfExpression(offset + len) && isNumeric(expression[offset + len], expression[offset + len - 1] == 'e' || expression[offset + len - 1] == 'E')) {
            len++
            this.pos++
        }
        // check if the e is at the end
        if (expression[offset + len - 1] == 'e' || expression[offset + len - 1] == 'E') {
            // since the e is at the end it's not part of the number and a rollback is necessary
            len--
            pos--
        }
        lastToken = NumberToken(expression, offset, len)
        return lastToken!!
    }

    private fun isEndOfExpression(offset: Int): Boolean {
        return this.expressionLength <= offset
    }

    companion object {

        private fun isNumeric(ch: Char, lastCharE: Boolean): Boolean {
            return ch.isDigit() || ch == '.' || ch == 'e' || ch == 'E' ||
                    lastCharE && (ch == '-' || ch == '+')
        }

        fun isAlphabetic(codePoint: Char): Boolean {
            return codePoint.isLetter()
        }

        fun isVariableOrFunctionCharacter(codePoint: Char): Boolean {
            return isAlphabetic(codePoint) ||
                    codePoint.isDigit() ||
                    codePoint == '_' ||
                    codePoint == '.'
        }
    }
}
