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

  def isValidFunctionName(name: String): Boolean = {
    if (name == null || name.isEmpty) return false
    if (name.charAt(0).isDigit) return false
    name.forall(c => c.isLetterOrDigit || c == '_')
  }
}

/**
  * @constructor
  * Create a new Function with a given name and number of arguments
  *
  * @param name         the name of the Function
  * @param numArguments the number of arguments the function takes
  */
abstract class Function(val name: String, val numArguments: Int) {
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
