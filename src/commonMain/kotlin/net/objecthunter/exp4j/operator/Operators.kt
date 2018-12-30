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

import kotlin.math.pow

object Operators {
    private val INDEX_ADDITION = 0
    private val INDEX_SUBTRACTION = 1
    private val INDEX_MUTLIPLICATION = 2
    private val INDEX_DIVISION = 3
    private val INDEX_POWER = 4
    private val INDEX_MODULO = 5
    private val INDEX_UNARYMINUS = 6
    private val INDEX_UNARYPLUS = 7

    private val builtinOperators = arrayOfNulls<Operator>(8) as Array<Operator>

    init {
        builtinOperators[INDEX_ADDITION] = object : Operator("+", 2, true, Operator.PRECEDENCE_ADDITION) {
            override fun apply(vararg args: Double): Double {
                return args[0] + args[1]
            }
        }
        builtinOperators[INDEX_SUBTRACTION] = object : Operator("-", 2, true, Operator.PRECEDENCE_ADDITION) {
            override fun apply(vararg args: Double): Double {
                return args[0] - args[1]
            }
        }
        builtinOperators[INDEX_UNARYMINUS] = object : Operator("-", 1, false, Operator.PRECEDENCE_UNARY_MINUS) {
            override fun apply(vararg args: Double): Double {
                return -args[0]
            }
        }
        builtinOperators[INDEX_UNARYPLUS] = object : Operator("+", 1, false, Operator.PRECEDENCE_UNARY_PLUS) {
            override fun apply(vararg args: Double): Double {
                return args[0]
            }
        }
        builtinOperators[INDEX_MUTLIPLICATION] = object : Operator("*", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
            override fun apply(vararg args: Double): Double {
                return args[0] * args[1]
            }
        }
        builtinOperators[INDEX_DIVISION] = object : Operator("/", 2, true, Operator.PRECEDENCE_DIVISION) {
            override fun apply(vararg args: Double): Double {
                if (args[1] == 0.0) {
                    throw ArithmeticException("Division by zero!")
                }
                return args[0] / args[1]
            }
        }
        builtinOperators[INDEX_POWER] = object : Operator("^", 2, false, Operator.PRECEDENCE_POWER) {
            override fun apply(vararg args: Double): Double {
                return args[0].pow(args[1])
            }
        }
        builtinOperators[INDEX_MODULO] = object : Operator("%", 2, true, Operator.PRECEDENCE_MODULO) {
            override fun apply(vararg args: Double): Double {
                if (args[1] == 0.0) {
                    throw ArithmeticException("Division by zero!")
                }
                return args[0] % args[1]
            }
        }
    }

    fun getBuiltinOperator(symbol: Char, numArguments: Int): Operator? {
        when (symbol) {
            '+' -> return if (numArguments != 1) {
                builtinOperators[INDEX_ADDITION]
            } else {
                builtinOperators[INDEX_UNARYPLUS]
            }
            '-' -> return if (numArguments != 1) {
                builtinOperators[INDEX_SUBTRACTION]
            } else {
                builtinOperators[INDEX_UNARYMINUS]
            }
            '*' -> return builtinOperators[INDEX_MUTLIPLICATION]
            '/' -> return builtinOperators[INDEX_DIVISION]
            '^' -> return builtinOperators[INDEX_POWER]
            '%' -> return builtinOperators[INDEX_MODULO]
            else -> return null
        }
    }

}
