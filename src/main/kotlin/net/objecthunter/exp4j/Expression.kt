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
import net.objecthunter.exp4j.tokenizer.FunctionToken
import net.objecthunter.exp4j.tokenizer.NumberToken
import net.objecthunter.exp4j.tokenizer.OperatorToken
import net.objecthunter.exp4j.tokenizer.Token
import net.objecthunter.exp4j.tokenizer.VariableToken

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.TreeSet
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class Expression {

    private val tokens: Array<Token>

    private val variables: MutableMap<String, Double>

    private val userFunctionNames: Set<String>

    val variableNames: Set<String>
        get() {
            val variables = HashSet<String>()
            for (t in tokens) {
                if (t.type == Token.TOKEN_VARIABLE.toInt())
                    variables.add((t as VariableToken).name)
            }
            return variables
        }

    private fun createDefaultVariables(): MutableMap<String, Double> {
        val vars = HashMap<String, Double>(4)
        vars["pi"] = Math.PI
        vars["π"] = Math.PI
        vars["φ"] = 1.61803398874
        vars["e"] = Math.E
        return vars
    }

    /**
     * Creates a new expression that is a copy of the existing one.
     *
     * @param existing the expression to copy
     */
    constructor(existing: Expression) {
        this.tokens = Arrays.copyOf(existing.tokens, existing.tokens.size)
        this.variables = HashMap()
        this.variables.putAll(existing.variables)
        this.userFunctionNames = HashSet(existing.userFunctionNames)
    }

    internal constructor(tokens: Array<Token>) {
        this.tokens = tokens
        this.variables = createDefaultVariables()
        this.userFunctionNames = emptySet()
    }

    internal constructor(tokens: Array<Token>, userFunctionNames: Set<String>) {
        this.tokens = tokens
        this.variables = createDefaultVariables()
        this.userFunctionNames = userFunctionNames
    }

    fun setVariable(name: String, value: Double): Expression {
        this.checkVariableName(name)
        this.variables[name] = java.lang.Double.valueOf(value)
        return this
    }

    private fun checkVariableName(name: String) {
        if (this.userFunctionNames.contains(name) || Functions.getBuiltinFunction(name) != null) {
            throw IllegalArgumentException("The variable name '$name' is invalid. Since there exists a function with the same name")
        }
    }

    fun setVariables(variables: Map<String, Double>): Expression {
        for ((key, value) in variables) {
            this.setVariable(key, value)
        }
        return this
    }

    @JvmOverloads
    fun validate(checkVariablesSet: Boolean = true): ValidationResult {
        val errors = ArrayList<String>(0)
        if (checkVariablesSet) {
            /* check that all vars have a value set */
            for (t in this.tokens) {
                if (t.type == Token.TOKEN_VARIABLE.toInt()) {
                    val `var` = (t as VariableToken).name
                    if (!variables.containsKey(`var`)) {
                        errors.add("The setVariable '$`var`' has not been set")
                    }
                }
            }
        }

        /* Check if the number of operands, functions and operators match.
           The idea is to increment a counter for operands and decrease it for operators.
           When a function occurs the number of available arguments has to be greater
           than or equals to the function's expected number of arguments.
           The count has to be larger than 1 at all times and exactly 1 after all tokens
           have been processed */
        var count = 0
        for (tok in this.tokens) {
            when (tok.type) {
                Token.TOKEN_NUMBER, Token.TOKEN_VARIABLE -> count++
                Token.TOKEN_FUNCTION -> {
                    val func = (tok as FunctionToken).function
                    val argsNum = func.numArguments
                    if (argsNum > count) {
                        errors.add("Not enough arguments for '" + func.name + "'")
                    }
                    if (argsNum > 1) {
                        count -= argsNum - 1
                    } else if (argsNum == 0) {
                        // see https://github.com/fasseg/exp4j/issues/59
                        count++
                    }
                }
                Token.TOKEN_OPERATOR -> {
                    val op = (tok as OperatorToken).operator
                    if (op.numOperands == 2) {
                        count--
                    }
                }
            }
            if (count < 1) {
                errors.add("Too many operators")
                return ValidationResult(false, errors)
            }
        }
        if (count > 1) {
            errors.add("Too many operands")
        }
        return if (errors.size == 0) ValidationResult.SUCCESS else ValidationResult(false, errors)

    }

    fun evaluateAsync(executor: ExecutorService): Future<Double> {
        return executor.submit(Callable { evaluate() })
    }

    fun evaluate(): Double {
        val output = ArrayStack()
        for (t in tokens) {
            if (t.type == Token.TOKEN_NUMBER.toInt()) {
                output.push((t as NumberToken).value)
            } else if (t.type == Token.TOKEN_VARIABLE.toInt()) {
                val name = (t as VariableToken).name
                val value = this.variables[name]
                        ?: throw IllegalArgumentException(
                                "No value has been set for the setVariable '" +
                                        name + "'.")
                output.push(value)
            } else if (t.type == Token.TOKEN_OPERATOR.toInt()) {
                val op = t as OperatorToken
                if (output.size() < op.operator.numOperands) {
                    throw IllegalArgumentException(
                            "Invalid number of operands available for '" +
                                    op.operator.symbol +
                                    "' operator")
                }
                if (op.operator.numOperands == 2) {
                    /* pop the operands and push the result of the operation */
                    val rightArg = output.pop()
                    val leftArg = output.pop()
                    output.push(op.operator.apply(leftArg, rightArg))
                } else if (op.operator.numOperands == 1) {
                    /* pop the operand and push the result of the operation */
                    val arg = output.pop()
                    output.push(op.operator.apply(arg))
                }
            } else if (t.type == Token.TOKEN_FUNCTION.toInt()) {
                val func = t as FunctionToken
                val numArguments = func.function.numArguments
                if (output.size() < numArguments) {
                    throw IllegalArgumentException(
                            "Invalid number of arguments available for '" +
                                    func.function.name +
                                    "' function")
                }
                /* collect the arguments from the stack */
                val args = DoubleArray(numArguments)
                for (j in numArguments - 1 downTo 0) {
                    args[j] = output.pop()
                }
                output.push(func.function.apply(*args))
            }
        }
        if (output.size() > 1) {
            throw IllegalArgumentException("Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function.")
        }
        return output.pop()
    }
}
