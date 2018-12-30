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

/**
  * Abstract class for tokens used by exp4j to tokenize expressions
  */
object Token {
  val TOKEN_NUMBER = 1
  val TOKEN_OPERATOR = 2
  val TOKEN_FUNCTION = 3
  val TOKEN_PARENTHESES_OPEN = 4
  val TOKEN_PARENTHESES_CLOSE = 5
  val TOKEN_VARIABLE = 6
  val TOKEN_SEPARATOR = 7
}

abstract class Token private[tokenizer](val `type`: Int) {
  def getType: Int = `type`
}
