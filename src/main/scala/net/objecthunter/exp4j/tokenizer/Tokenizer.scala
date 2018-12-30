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

import java.util
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.function.Functions
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.operator.Operators

object Tokenizer {
  private def isNumeric(ch: Char, lastCharE: Boolean) = Character.isDigit(ch) || ch == '.' || ch == 'e' || ch == 'E' || (lastCharE && (ch == '-' || ch == '+'))

  def isAlphabetic(codePoint: Int): Boolean = Character.isLetter(codePoint)

  def isVariableOrFunctionCharacter(codePoint: Int): Boolean = isAlphabetic(codePoint) || Character.isDigit(codePoint) || codePoint == '_' || codePoint == '.'
}

class Tokenizer {
  final private var expression = null
  final private var expressionLength = 0
  final private var userFunctions = null
  final private var userOperators = null
  final private var variableNames = null
  final private var implicitMultiplication = false
  private var pos = 0
  private var lastToken = null

  def this(expression: String, userFunctions: util.Map[String, Function], userOperators: util.Map[String, Operator], variableNames: util.Set[String], implicitMultiplication: Boolean) {
    this()
    this.expression = expression.trim.toCharArray
    this.expressionLength = this.expression.length
    this.userFunctions = userFunctions
    this.userOperators = userOperators
    this.variableNames = variableNames
    this.implicitMultiplication = implicitMultiplication
  }

  def this(expression: String, userFunctions: util.Map[String, Function], userOperators: util.Map[String, Operator], variableNames: util.Set[String]) {
    this()
    this.expression = expression.trim.toCharArray
    this.expressionLength = this.expression.length
    this.userFunctions = userFunctions
    this.userOperators = userOperators
    this.variableNames = variableNames
    this.implicitMultiplication = true
  }

  def hasNext: Boolean = this.expression.length > pos

  def nextToken: Token = {
    var ch = expression(pos)
    while ( {
      Character.isWhitespace(ch)
    }) ch = expression({
      pos += 1; pos
    })
    if (Character.isDigit(ch) || ch == '.') {
      if (lastToken != null) if (lastToken.getType == Token.TOKEN_NUMBER) throw new IllegalArgumentException("Unable to parse char '" + ch + "' (Code:" + ch.toInt + ") at [" + pos + "]")
      else if (implicitMultiplication && (lastToken.getType != Token.TOKEN_OPERATOR && lastToken.getType != Token.TOKEN_PARENTHESES_OPEN && lastToken.getType != Token.TOKEN_FUNCTION && lastToken.getType != Token.TOKEN_SEPARATOR)) { // insert an implicit multiplication token
        lastToken = new OperatorToken(Operators.getBuiltinOperator('*', 2))
        return lastToken
      }
      return parseNumberToken(ch)
    }
    else if (isArgumentSeparator(ch)) return parseArgumentSeparatorToken(ch)
    else if (isOpenParentheses(ch)) {
      if (lastToken != null && implicitMultiplication && (lastToken.getType != Token.TOKEN_OPERATOR && lastToken.getType != Token.TOKEN_PARENTHESES_OPEN && lastToken.getType != Token.TOKEN_FUNCTION && lastToken.getType != Token.TOKEN_SEPARATOR)) {
        lastToken = new OperatorToken(Operators.getBuiltinOperator('*', 2))
        return lastToken
      }
      return parseParentheses(true)
    }
    else if (isCloseParentheses(ch)) return parseParentheses(false)
    else if (Operator.isAllowedOperatorChar(ch)) return parseOperatorToken(ch)
    else if (Tokenizer.isAlphabetic(ch) || ch == '_') { // parse the name which can be a setVariable or a function
      if (lastToken != null && implicitMultiplication && (lastToken.getType != Token.TOKEN_OPERATOR && lastToken.getType != Token.TOKEN_PARENTHESES_OPEN && lastToken.getType != Token.TOKEN_FUNCTION && lastToken.getType != Token.TOKEN_SEPARATOR)) {
        lastToken = new OperatorToken(Operators.getBuiltinOperator('*', 2))
        return lastToken
      }
      return parseFunctionOrVariable
    }
    throw new IllegalArgumentException("Unable to parse char '" + ch + "' (Code:" + ch.toInt + ") at [" + pos + "]")
  }

  private def parseArgumentSeparatorToken(ch: Char) = {
    this.pos += 1
    this.lastToken = new ArgumentSeparatorToken
    lastToken
  }

  private def isArgumentSeparator(ch: Char) = ch == ','

  private def parseParentheses(open: Boolean) = {
    if (open) this.lastToken = new OpenParenthesesToken
    else this.lastToken = new CloseParenthesesToken
    this.pos += 1
    lastToken
  }

  private def isOpenParentheses(ch: Char) = ch == '(' || ch == '{' || ch == '['

  private def isCloseParentheses(ch: Char) = ch == ')' || ch == '}' || ch == ']'

  private def parseFunctionOrVariable = {
    val offset = this.pos
    var testPos = 0
    var lastValidLen = 1
    var lastValidToken = null
    var len = 1
    if (isEndOfExpression(offset)) this.pos += 1
    testPos = offset + len - 1
    while ( {
      !isEndOfExpression(testPos) && Tokenizer.isVariableOrFunctionCharacter(expression(testPos))
    }) {
      val name = new String(expression, offset, len)
      if (variableNames != null && variableNames.contains(name)) {
        lastValidLen = len
        lastValidToken = new VariableToken(name)
      }
      else {
        val f = getFunction(name)
        if (f != null) {
          lastValidLen = len
          lastValidToken = new FunctionToken(f)
        }
      }
      len += 1
      testPos = offset + len - 1
    }
    if (lastValidToken == null) throw new UnknownFunctionOrVariableException(new String(expression), pos, len)
    pos += lastValidLen
    lastToken = lastValidToken
    lastToken
  }

  private def getFunction(name: String) = {
    var f = null
    if (this.userFunctions != null) f = this.userFunctions.get(name)
    if (f == null) f = Functions.getBuiltinFunction(name)
    f
  }

  private def parseOperatorToken(firstChar: Char) = {
    val offset = this.pos
    var len = 1
    val symbol = new StringBuilder
    var lastValid = null
    symbol.append(firstChar)
    while ( {
      !isEndOfExpression(offset + len) && Operator.isAllowedOperatorChar(expression(offset + len))
    }) symbol.append(expression(offset + {
      len += 1; len - 1
    }))
    while ( {
      symbol.length > 0
    }) {
      val op = this.getOperator(symbol.toString)
      if (op == null) symbol.setLength(symbol.length - 1)
      else {
        lastValid = op
        break //todo: break is not supported
      }
    }
    pos += symbol.length
    lastToken = new OperatorToken(lastValid)
    lastToken
  }

  private def getOperator(symbol: String) = {
    var op = null
    if (this.userOperators != null) op = this.userOperators.get(symbol)
    if (op == null && symbol.length == 1) {
      var argc = 2
      if (lastToken == null) argc = 1
      else {
        val lastTokenType = lastToken.getType
        if (lastTokenType == Token.TOKEN_PARENTHESES_OPEN || lastTokenType == Token.TOKEN_SEPARATOR) argc = 1
        else if (lastTokenType == Token.TOKEN_OPERATOR) {
          val lastOp = lastToken.asInstanceOf[OperatorToken].getOperator
          if (lastOp.getNumOperands == 2 || (lastOp.getNumOperands == 1 && !lastOp.isLeftAssociative)) argc = 1
        }
      }
      op = Operators.getBuiltinOperator(symbol.charAt(0), argc)
    }
    op
  }

  private def parseNumberToken(firstChar: Char): Token = {
    val offset = this.pos
    var len = 1
    this.pos += 1
    if (isEndOfExpression(offset + len)) {
      lastToken = new NumberToken(String.valueOf(firstChar).toDouble)
      return lastToken
    }
    while ( {
      !isEndOfExpression(offset + len) && Tokenizer.isNumeric(expression(offset + len), expression(offset + len - 1) == 'e' || expression(offset + len - 1) == 'E')
    }) {
      len += 1
      this.pos += 1
    }
    // check if the e is at the end
    if (expression(offset + len - 1) == 'e' || expression(offset + len - 1) == 'E') { // since the e is at the end it's not part of the number and a rollback is necessary
      len -= 1
      pos -= 1
    }
    lastToken = new NumberToken(expression, offset, len)
    lastToken
  }

  private def isEndOfExpression(offset: Int) = this.expressionLength <= offset
}
