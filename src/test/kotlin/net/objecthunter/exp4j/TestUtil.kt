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

package net.objecthunter.exp4j

import org.junit.Assert.assertEquals

import net.objecthunter.exp4j.tokenizer.*

import org.junit.Assert

object TestUtil {

    fun assertVariableToken(token: Token, name: String) {
        assertEquals(Token.TOKEN_VARIABLE.toLong(), token.type.toLong())
        Assert.assertEquals(name, (token as VariableToken).name)
    }

    fun assertOpenParenthesesToken(token: Token) {
        assertEquals(Token.TOKEN_PARENTHESES_OPEN.toLong(), token.type.toLong())
    }

    fun assertCloseParenthesesToken(token: Token) {
        assertEquals(Token.TOKEN_PARENTHESES_CLOSE.toLong(), token.type.toLong())
    }

    fun assertFunctionToken(token: Token, name: String, i: Int) {
        assertEquals(token.type.toLong(), Token.TOKEN_FUNCTION.toLong())
        val f = token as FunctionToken
        assertEquals(i.toLong(), f.function.numArguments.toLong())
        assertEquals(name, f.function.name)
    }

    fun assertOperatorToken(tok: Token, symbol: String, numArgs: Int, precedence: Int) {
        assertEquals(tok.type.toLong(), Token.TOKEN_OPERATOR.toLong())
        Assert.assertEquals(numArgs.toLong(), (tok as OperatorToken).operator.numOperands.toLong())
        assertEquals(symbol, tok.operator.symbol)
        assertEquals(precedence.toLong(), tok.operator.precedence.toLong())
    }

    fun assertNumberToken(tok: Token, v: Double) {
        assertEquals(tok.type.toLong(), Token.TOKEN_NUMBER.toLong())
        Assert.assertEquals(v, (tok as NumberToken).value, 0.0)
    }

    fun assertFunctionSeparatorToken(t: Token) {
        assertEquals(t.type.toLong(), Token.TOKEN_SEPARATOR.toLong())
    }
}
