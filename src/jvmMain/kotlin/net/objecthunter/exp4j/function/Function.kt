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

package net.objecthunter.exp4j.function

/**
 * A class representing a Function which can be used in an expression
 */
abstract class Function
/**
 * Create a new Function with a given name and number of arguments
 *
 * @param name the name of the Function
 * @param numArguments the number of arguments the function takes
 */
@JvmOverloads constructor(
        /**
         * Get the name of the Function
         *
         * @return the name
         */
        val name: String,
        /**
         * Get the number of arguments for this function
         *
         * @return the number of arguments
         */
        val numArguments: Int = 1) {

    init {
        if (numArguments < 0) {
            throw IllegalArgumentException("The number of function arguments can not be less than 0 for '" +
                    name + "'")
        }
        if (!isValidFunctionName(name)) {
            throw IllegalArgumentException("The function name '$name' is invalid")
        }

    }

    /**
     * Method that does the actual calculation of the function value given the arguments
     *
     * @param args the set of arguments used for calculating the function
     * @return the result of the function evaluation
     */
    abstract fun apply(vararg args: Double): Double

    companion object {

        /**
         * Get the set of characters which are allowed for use in Function names.
         *
         * @return the set of characters allowed
         */
        val allowedFunctionCharacters: CharArray
            @Deprecated("since 0.4.5 All unicode letters are allowed to be used in function names since 0.4.3. This API\n" +
                    "                  Function can be safely ignored. Checks for function name validity can be done using Character.isLetter() et al.")
            get() {
                val chars = CharArray(53)
                var count = 0
                for (i in 65..90) {
                    chars[count++] = i.toChar()
                }
                for (i in 97..122) {
                    chars[count++] = i.toChar()
                }
                chars[count] = '_'
                return chars
            }

        fun isValidFunctionName(name: String?): Boolean {
            if (name == null) {
                return false
            }

            val size = name.length

            if (size == 0) {
                return false
            }

            for (i in 0 until size) {
                val c = name[i]
                if (Character.isLetter(c) || c == '_') {
                    continue
                } else if (Character.isDigit(c) && i > 0) {
                    continue
                }
                return false
            }
            return true
        }
    }
}
/**
 * Create a new Function with a given name that takes a single argument
 *
 * @param name the name of the Function
 */
