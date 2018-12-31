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
  def assertVariableToken(token: Token, name: String): Unit = {
    assertEquals(Token.TOKEN_VARIABLE, token.getType)
    Assert.assertEquals(name, token.asInstanceOf[VariableToken].getName)
  }

  def assertOpenParenthesesToken(token: Token): Unit = assertEquals(Token.TOKEN_PARENTHESES_OPEN, token.getType)

  def assertCloseParenthesesToken(token: Token): Unit = assertEquals(Token.TOKEN_PARENTHESES_CLOSE, token.getType)

  def assertFunctionToken(token: Token, name: String, i: Int): Unit = {
    assertEquals(token.getType, Token.TOKEN_FUNCTION)
    val f = token.asInstanceOf[FunctionToken]
    assertEquals(i, f.getFunction.getNumArguments)
    assertEquals(name, f.getFunction.getName)
  }

  def assertOperatorToken(tok: Token, expectSymbol: String, expectNumOperands: Int, expectPrecedence: Int): Unit = {
    assertEquals("token type", Token.TOKEN_OPERATOR, tok.getType)
    assertEquals("numOperands", expectNumOperands, tok.asInstanceOf[OperatorToken].getOperator.getNumOperands)
    assertEquals("symbol", expectSymbol, tok.asInstanceOf[OperatorToken].getOperator.getSymbol)
    assertEquals("precedence", expectPrecedence, tok.asInstanceOf[OperatorToken].getOperator.getPrecedence)
  }

  def assertNumberToken(tok: Token, v: Double): Unit = {
    assertEquals(tok.getType, Token.TOKEN_NUMBER)
    Assert.assertEquals(v, tok.asInstanceOf[NumberToken].getValue, 0d)
  }

  def assertFunctionSeparatorToken(t: Token): Unit = assertEquals(t.getType, Token.TOKEN_SEPARATOR)
}
