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

/**
 * Contains the validation result for a given [Expression]
 */
class ValidationResult
/**
 * Create a new instance
 * @param valid Whether the validation of the expression was successful
 * @param errors The list of errors returned if the validation was unsuccessful
 */
(
        /**
         * Check if an expression has been validated successfully
         * @return true if the validation was successful, false otherwise
         */
        val isValid: Boolean,
        /**
         * Get the list of errors describing the issues while validating the expression
         * @return The List of errors
         */
        val errors: List<String>?) {
    companion object {

        /**
         * A static class representing a successful validation result
         */
        val SUCCESS = ValidationResult(true, null)
    }
}
