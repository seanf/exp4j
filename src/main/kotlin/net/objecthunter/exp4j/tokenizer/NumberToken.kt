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

/**
 * Represents a number in the expression
 */
class NumberToken
/**
 * Create a new instance
 * @param value the value of the number
 */
(
        /**
         * Get the value of the number
         * @return the value
         */
        val value: Double) : Token(Token.TOKEN_NUMBER) {

    internal constructor(expression: CharArray, offset: Int, len: Int) : this(String(expression, offset, len).toDouble()) {}
}
