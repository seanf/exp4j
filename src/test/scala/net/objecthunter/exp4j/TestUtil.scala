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
import net.objecthunter.exp4j.tokenizer._
import org.junit.Assert

object TestUtil {
  def assertVariableToken(token: Nothing, name: String): Unit = {
    assertEquals(Token.TOKEN_VARIABLE, token.getType)
    Assert.assertEquals(name, token.asInstanceOf[Nothing].getName)
  }

  def assertOpenParenthesesToken(token: Nothing): Unit = assertEquals(Token.TOKEN_PARENTHESES_OPEN, token.getType)

  def assertCloseParenthesesToken(token: Nothing): Unit = assertEquals(Token.TOKEN_PARENTHESES_CLOSE, token.getType)

  def assertFunctionToken(token: Nothing, name: String, i: Int): Unit = {
    assertEquals(token.getType, Token.TOKEN_FUNCTION)
    val f = token.asInstanceOf[Nothing]
    assertEquals(i, f.getFunction.getNumArguments)
    assertEquals(name, f.getFunction.getName)
  }

  def assertOperatorToken(tok: Nothing, symbol: String, numArgs: Int, precedence: Int): Unit = {
    assertEquals(tok.getType, Token.TOKEN_OPERATOR)
    Assert.assertEquals(numArgs, tok.asInstanceOf[Nothing].getOperator.getNumOperands)
    assertEquals(symbol, tok.asInstanceOf[Nothing].getOperator.getSymbol)
    assertEquals(precedence, tok.asInstanceOf[Nothing].getOperator.getPrecedence)
  }

  def assertNumberToken(tok: Nothing, v: Double): Unit = {
    assertEquals(tok.getType, Token.TOKEN_NUMBER)
    Assert.assertEquals(v, tok.asInstanceOf[Nothing].getValue, 0d)
  }

  def assertFunctionSeparatorToken(t: Nothing): Unit = assertEquals(t.getType, Token.TOKEN_SEPARATOR)
}
