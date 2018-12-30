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

import net.objecthunter.exp4j.multiplatform.cbrt
import kotlin.math.*

/**
 * Class representing the builtin functions available for use in expressions
 */
object Functions {
    private val INDEX_SIN = 0
    private val INDEX_COS = 1
    private val INDEX_TAN = 2
    private val INDEX_COT = 3
    private val INDEX_LOG = 4
    private val INDEX_LOG1P = 5
    private val INDEX_ABS = 6
    private val INDEX_ACOS = 7
    private val INDEX_ASIN = 8
    private val INDEX_ATAN = 9
    private val INDEX_CBRT = 10
    private val INDEX_CEIL = 11
    private val INDEX_FLOOR = 12
    private val INDEX_SINH = 13
    private val INDEX_SQRT = 14
    private val INDEX_TANH = 15
    private val INDEX_COSH = 16
    private val INDEX_POW = 17
    private val INDEX_EXP = 18
    private val INDEX_EXPM1 = 19
    private val INDEX_LOG10 = 20
    private val INDEX_LOG2 = 21
    private val INDEX_SGN = 22

    private val builtinFunctions = arrayOfNulls<Function>(23)

    init {
        builtinFunctions[INDEX_SIN] = object : Function("sin") {
            override fun apply(vararg args: Double): Double {
                return sin(args[0])
            }
        }
        builtinFunctions[INDEX_COS] = object : Function("cos") {
            override fun apply(vararg args: Double): Double {
                return cos(args[0])
            }
        }
        builtinFunctions[INDEX_TAN] = object : Function("tan") {
            override fun apply(vararg args: Double): Double {
                return tan(args[0])
            }
        }
        builtinFunctions[INDEX_COT] = object : Function("cot") {
            override fun apply(vararg args: Double): Double {
                val tan = tan(args[0])
                if (tan == 0.0) {
                    throw ArithmeticException("Division by zero in cotangent!")
                }
                return 1.0 / tan(args[0])
            }
        }
        builtinFunctions[INDEX_LOG] = object : Function("log") {
            override fun apply(vararg args: Double): Double {
                return ln(args[0])
            }
        }
        builtinFunctions[INDEX_LOG2] = object : Function("log2") {
            override fun apply(vararg args: Double): Double {
                return ln(args[0]) / ln(2.0)
            }
        }
        builtinFunctions[INDEX_LOG10] = object : Function("log10") {
            override fun apply(vararg args: Double): Double {
                return log10(args[0])
            }
        }
        builtinFunctions[INDEX_LOG1P] = object : Function("log1p") {
            override fun apply(vararg args: Double): Double {
                return ln1p(args[0])
            }
        }
        builtinFunctions[INDEX_ABS] = object : Function("abs") {
            override fun apply(vararg args: Double): Double {
                return abs(args[0])
            }
        }
        builtinFunctions[INDEX_ACOS] = object : Function("acos") {
            override fun apply(vararg args: Double): Double {
                return acos(args[0])
            }
        }
        builtinFunctions[INDEX_ASIN] = object : Function("asin") {
            override fun apply(vararg args: Double): Double {
                return asin(args[0])
            }
        }
        builtinFunctions[INDEX_ATAN] = object : Function("atan") {
            override fun apply(vararg args: Double): Double {
                return atan(args[0])
            }
        }
        builtinFunctions[INDEX_CBRT] = object : Function("cbrt") {
            override fun apply(vararg args: Double): Double {
                return args[0].cbrt()
            }
        }
        builtinFunctions[INDEX_FLOOR] = object : Function("floor") {
            override fun apply(vararg args: Double): Double {
                return floor(args[0])
            }
        }
        builtinFunctions[INDEX_SINH] = object : Function("sinh") {
            override fun apply(vararg args: Double): Double {
                return sinh(args[0])
            }
        }
        builtinFunctions[INDEX_SQRT] = object : Function("sqrt") {
            override fun apply(vararg args: Double): Double {
                return sqrt(args[0])
            }
        }
        builtinFunctions[INDEX_TANH] = object : Function("tanh") {
            override fun apply(vararg args: Double): Double {
                return tanh(args[0])
            }
        }
        builtinFunctions[INDEX_COSH] = object : Function("cosh") {
            override fun apply(vararg args: Double): Double {
                return cosh(args[0])
            }
        }
        builtinFunctions[INDEX_CEIL] = object : Function("ceil") {
            override fun apply(vararg args: Double): Double {
                return ceil(args[0])
            }
        }
        builtinFunctions[INDEX_POW] = object : Function("pow", 2) {
            override fun apply(vararg args: Double): Double {
                return args[0].pow(args[1])
            }
        }
        builtinFunctions[INDEX_EXP] = object : Function("exp", 1) {
            override fun apply(vararg args: Double): Double {
                return exp(args[0])
            }
        }
        builtinFunctions[INDEX_EXPM1] = object : Function("expm1", 1) {
            override fun apply(vararg args: Double): Double {
                return expm1(args[0])
            }
        }
        builtinFunctions[INDEX_SGN] = object : Function("signum", 1) {
            override fun apply(vararg args: Double): Double {
                return if (args[0] > 0) {
                    1.0
                } else if (args[0] < 0) {
                    -1.0
                } else {
                    0.0
                }
            }
        }
    }

    /**
     * Get the builtin function for a given name
     * @param name te name of the function
     * @return a Function instance
     */
    fun getBuiltinFunction(name: String): Function? {

        return if (name == "sin") {
            builtinFunctions[INDEX_SIN]
        } else if (name == "cos") {
            builtinFunctions[INDEX_COS]
        } else if (name == "tan") {
            builtinFunctions[INDEX_TAN]
        } else if (name == "cot") {
            builtinFunctions[INDEX_COT]
        } else if (name == "asin") {
            builtinFunctions[INDEX_ASIN]
        } else if (name == "acos") {
            builtinFunctions[INDEX_ACOS]
        } else if (name == "atan") {
            builtinFunctions[INDEX_ATAN]
        } else if (name == "sinh") {
            builtinFunctions[INDEX_SINH]
        } else if (name == "cosh") {
            builtinFunctions[INDEX_COSH]
        } else if (name == "tanh") {
            builtinFunctions[INDEX_TANH]
        } else if (name == "abs") {
            builtinFunctions[INDEX_ABS]
        } else if (name == "log") {
            builtinFunctions[INDEX_LOG]
        } else if (name == "log10") {
            builtinFunctions[INDEX_LOG10]
        } else if (name == "log2") {
            builtinFunctions[INDEX_LOG2]
        } else if (name == "log1p") {
            builtinFunctions[INDEX_LOG1P]
        } else if (name == "ceil") {
            builtinFunctions[INDEX_CEIL]
        } else if (name == "floor") {
            builtinFunctions[INDEX_FLOOR]
        } else if (name == "sqrt") {
            builtinFunctions[INDEX_SQRT]
        } else if (name == "cbrt") {
            builtinFunctions[INDEX_CBRT]
        } else if (name == "pow") {
            builtinFunctions[INDEX_POW]
        } else if (name == "exp") {
            builtinFunctions[INDEX_EXP]
        } else if (name == "expm1") {
            builtinFunctions[INDEX_EXPM1]
        } else if (name == "signum") {
            builtinFunctions[INDEX_SGN]
        } else {
            null
        }
    }

}

