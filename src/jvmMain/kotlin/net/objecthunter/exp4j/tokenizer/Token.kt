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
 * Abstract class for tokens used by exp4j to tokenize expressions
 */
abstract class Token internal constructor(val type: Int) {
    companion object {
        val TOKEN_NUMBER: Int = 1
        val TOKEN_OPERATOR: Int = 2
        val TOKEN_FUNCTION: Int = 3
        val TOKEN_PARENTHESES_OPEN: Int = 4
        val TOKEN_PARENTHESES_CLOSE: Int = 5
        val TOKEN_VARIABLE: Int = 6
        val TOKEN_SEPARATOR: Int = 7
    }

}
