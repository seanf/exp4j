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

import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.function.Functions
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.shuntingyard.ShuntingYard

/**
 * Factory class for [Expression] instances. This class is the main API entrypoint. Users should create new
 * [Expression] instances using this factory class.
 */
class ExpressionBuilder
/**
 * Create a new ExpressionBuilder instance and initialize it with a given expression string.
 * @param expression the expression to be parsed
 */
(private val expression: String) {

    private val userFunctions: MutableMap<String, Function>

    private val userOperators: MutableMap<String, Operator>

    private val variableNames: MutableSet<String>

    private var implicitMultiplication = true

    init {
        if (expression.trim { it <= ' ' }.isEmpty()) {
            throw IllegalArgumentException("Expression can not be empty")
        }
        this.userOperators = HashMap(4)
        this.userFunctions = HashMap(4)
        this.variableNames = HashSet(4)
    }

    /**
     * Add a [net.objecthunter.exp4j.function.Function] implementation available for use in the expression
     * @param function the custom [net.objecthunter.exp4j.function.Function] implementation that should be available for use in the expression.
     * @return the ExpressionBuilder instance
     */
    fun function(function: Function): ExpressionBuilder {
        this.userFunctions[function.name] = function
        return this
    }

    /**
     * Add multiple [net.objecthunter.exp4j.function.Function] implementations available for use in the expression
     * @param functions the custom [net.objecthunter.exp4j.function.Function] implementations
     * @return the ExpressionBuilder instance
     */
    fun functions(vararg functions: Function): ExpressionBuilder {
        for (f in functions) {
            this.userFunctions[f.name] = f
        }
        return this
    }

    /**
     * Add multiple [net.objecthunter.exp4j.function.Function] implementations available for use in the expression
     * @param functions A [List] of custom [net.objecthunter.exp4j.function.Function] implementations
     * @return the ExpressionBuilder instance
     */
    fun functions(functions: List<Function>): ExpressionBuilder {
        for (f in functions) {
            this.userFunctions[f.name] = f
        }
        return this
    }

    /**
     * Declare variable names used in the expression
     * @param variableNames the variables used in the expression
     * @return the ExpressionBuilder instance
     */
    fun variables(variableNames: Set<String>): ExpressionBuilder {
        this.variableNames.addAll(variableNames)
        return this
    }

    /**
     * Declare variable names used in the expression
     * @param variableNames the variables used in the expression
     * @return the ExpressionBuilder instance
     */
    fun variables(vararg variableNames: String): ExpressionBuilder {
        this.variableNames.addAll(variableNames)
        return this
    }

    /**
     * Declare a variable used in the expression
     * @param variableName the variable used in the expression
     * @return the ExpressionBuilder instance
     */
    fun variable(variableName: String): ExpressionBuilder {
        this.variableNames.add(variableName)
        return this
    }

    fun implicitMultiplication(enabled: Boolean): ExpressionBuilder {
        this.implicitMultiplication = enabled
        return this
    }

    /**
     * Add an [net.objecthunter.exp4j.operator.Operator] which should be available for use in the expression
     * @param operator the custom [net.objecthunter.exp4j.operator.Operator] to add
     * @return the ExpressionBuilder instance
     */
    fun operator(operator: Operator): ExpressionBuilder {
        this.checkOperatorSymbol(operator)
        this.userOperators[operator.symbol] = operator
        return this
    }

    private fun checkOperatorSymbol(op: Operator) {
        val name = op.symbol
        for (ch in name.toCharArray()) {
            if (!Operator.isAllowedOperatorChar(ch)) {
                throw IllegalArgumentException("The operator symbol '$name' is invalid")
            }
        }
    }

    /**
     * Add multiple [net.objecthunter.exp4j.operator.Operator] implementations which should be available for use in the expression
     * @param operators the set of custom [net.objecthunter.exp4j.operator.Operator] implementations to add
     * @return the ExpressionBuilder instance
     */
    fun operator(vararg operators: Operator): ExpressionBuilder {
        for (o in operators) {
            this.operator(o)
        }
        return this
    }

    /**
     * Add multiple [net.objecthunter.exp4j.operator.Operator] implementations which should be available for use in the expression
     * @param operators the [List] of custom [net.objecthunter.exp4j.operator.Operator] implementations to add
     * @return the ExpressionBuilder instance
     */
    fun operator(operators: List<Operator>): ExpressionBuilder {
        for (o in operators) {
            this.operator(o)
        }
        return this
    }

    /**
     * Build the [Expression] instance using the custom operators and functions set.
     * @return an [Expression] instance which can be used to evaluate the result of the expression
     */
    fun build(): Expression {
        /* set the constants' variable names */
        variableNames.add("pi")
        variableNames.add("π")
        variableNames.add("e")
        variableNames.add("φ")
        /* Check if there are duplicate vars/functions */
        for (`var` in variableNames) {
            if (Functions.getBuiltinFunction(`var`) != null || userFunctions.containsKey(`var`)) {
                throw IllegalArgumentException("A variable can not have the same name as a function [$`var`]")
            }
        }
        return Expression(ShuntingYard.convertToRPN(this.expression, this.userFunctions, this.userOperators,
                this.variableNames, this.implicitMultiplication), this.userFunctions.keys)
    }

}
