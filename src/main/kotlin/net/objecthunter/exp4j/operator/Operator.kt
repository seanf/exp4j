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
package net.objecthunter.exp4j.operator

/**
 * Class representing operators that can be used in an expression
 */
abstract class Operator
/**
 * Create a new operator for use in expressions
 * @param symbol the symbol of the operator
 * @param numberOfOperands the number of operands the operator takes (1 or 2)
 * @param leftAssociative set to true if the operator is left associative, false if it is right associative
 * @param precedence the precedence value of the operator
 */
(
        /**
         * Get the operator symbol
         * @return the symbol
         */
        val symbol: String,
        /**
         * Get the number of operands
         * @return the number of operands
         */
        val numOperands: Int,
        /**
         * Check if the operator is left associative
         * @return true os the operator is left associative, false otherwise
         */
        val isLeftAssociative: Boolean,
        /**
         * Check the precedence value for the operator
         * @return the precedence value
         */
        val precedence: Int) {

    /**
     * Apply the operation on the given operands
     * @param args the operands for the operation
     * @return the calculated result of the operation
     */
    abstract fun apply(vararg args: Double): Double

    companion object {
        /**
         * The precedence value for the addition operation
         */
        val PRECEDENCE_ADDITION = 500
        /**
         * The precedence value for the subtraction operation
         */
        val PRECEDENCE_SUBTRACTION = PRECEDENCE_ADDITION
        /**
         * The precedence value for the multiplication operation
         */
        val PRECEDENCE_MULTIPLICATION = 1000
        /**
         * The precedence value for the division operation
         */
        val PRECEDENCE_DIVISION = PRECEDENCE_MULTIPLICATION
        /**
         * The precedence value for the modulo operation
         */
        val PRECEDENCE_MODULO = PRECEDENCE_DIVISION
        /**
         * The precedence value for the power operation
         */
        val PRECEDENCE_POWER = 10000
        /**
         * The precedence value for the unary minus operation
         */
        val PRECEDENCE_UNARY_MINUS = 5000
        /**
         * The precedence value for the unary plus operation
         */
        val PRECEDENCE_UNARY_PLUS = PRECEDENCE_UNARY_MINUS

        /**
         * The set of allowed operator chars
         */
        val ALLOWED_OPERATOR_CHARS = charArrayOf('+', '-', '*', '/', '%', '^', '!', '#', '§', '$', '&', ';', ':', '~', '<', '>', '|', '=')

        /**
         * Check if a character is an allowed operator char
         * @param ch the char to check
         * @return true if the char is allowed an an operator symbol, false otherwise
         */
        fun isAllowedOperatorChar(ch: Char): Boolean {
            for (allowed in ALLOWED_OPERATOR_CHARS) {
                if (ch == allowed) {
                    return true
                }
            }
            return false
        }
    }
}
