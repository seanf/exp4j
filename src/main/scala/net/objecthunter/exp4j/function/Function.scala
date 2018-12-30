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
package net.objecthunter.exp4j.function

/**
  * A class representing a Function which can be used in an expression
  */
object Function {
  /**
    * Get the set of characters which are allowed for use in Function names.
    *
    * @return the set of characters allowed
    * @deprecated since 0.4.5 All unicode letters are allowed to be used in function names since 0.4.3. This API
    *             Function can be safely ignored. Checks for function name validity can be done using Character.isLetter() et al.
    */
    def getAllowedFunctionCharacters: Array[Char] = {
      val chars = new Array[Char](53)
      var count = 0
      var i = 65
      while ( {
        i < 91
      }) {
        chars({
          count += 1; count - 1
        }) = i.toChar
        {
          i += 1; i - 1
        }
      }
      var i = 97
      while ( {
        i < 123
      }) {
        chars({
          count += 1; count - 1
        }) = i.toChar
        {
          i += 1; i - 1
        }
      }
      chars(count) = '_'
      chars
    }

  def isValidFunctionName(name: String): Boolean = {
    if (name == null) return false
    val size = name.length
    if (size == 0) return false
    var i = 0
    while ( {
      i < size
    }) {
      val c = name.charAt(i)
      if (Character.isLetter(c) || c == '_') continue //todo: continue is not supported
      else if (Character.isDigit(c) && i > 0) continue //todo: continue is not supported
      return false
      {
        i += 1; i - 1
      }
    }
    true
  }
}

abstract class Function(val name: String, val numArguments: Int)

/**
  * Create a new Function with a given name and number of arguments
  *
  * @param name         the name of the Function
  * @param numArguments the number of arguments the function takes
  */ {
  if (numArguments < 0) throw new IllegalArgumentException("The number of function arguments can not be less than 0 for '" + name + "'")
  if (!Function.isValidFunctionName(name)) throw new IllegalArgumentException("The function name '" + name + "' is invalid")

  /**
    * Create a new Function with a given name that takes a single argument
    *
    * @param name the name of the Function
    */
  def this(name: String) {
    this(name, 1)
  }

  /**
    * Get the name of the Function
    *
    * @return the name
    */
  def getName: String = name

  /**
    * Get the number of arguments for this function
    *
    * @return the number of arguments
    */
  def getNumArguments: Int = numArguments

  /**
    * Method that does the actual calculation of the function value given the arguments
    *
    * @param args the set of arguments used for calculating the function
    * @return the result of the function evaluation
    */
  def apply(args: Double*): Double
}
