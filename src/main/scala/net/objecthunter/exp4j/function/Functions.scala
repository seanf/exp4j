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
  private val builtinFunctions = new Array[Function](23)

  /**
    * Get the builtin function for a given name
    *
    * @param name te name of the function
    * @return a Function instance
    */
  def getBuiltinFunction(name: String): Function = if (name == "sin") builtinFunctions(INDEX_SIN)
  else if (name == "cos") builtinFunctions(INDEX_COS)
  else if (name == "tan") builtinFunctions(INDEX_TAN)
  else if (name == "cot") builtinFunctions(INDEX_COT)
  else if (name == "asin") builtinFunctions(INDEX_ASIN)
  else if (name == "acos") builtinFunctions(INDEX_ACOS)
  else if (name == "atan") builtinFunctions(INDEX_ATAN)
  else if (name == "sinh") builtinFunctions(INDEX_SINH)
  else if (name == "cosh") builtinFunctions(INDEX_COSH)
  else if (name == "tanh") builtinFunctions(INDEX_TANH)
  else if (name == "abs") builtinFunctions(INDEX_ABS)
  else if (name == "log") builtinFunctions(INDEX_LOG)
  else if (name == "log10") builtinFunctions(INDEX_LOG10)
  else if (name == "log2") builtinFunctions(INDEX_LOG2)
  else if (name == "log1p") builtinFunctions(INDEX_LOG1P)
  else if (name == "ceil") builtinFunctions(INDEX_CEIL)
  else if (name == "floor") builtinFunctions(INDEX_FLOOR)
  else if (name == "sqrt") builtinFunctions(INDEX_SQRT)
  else if (name == "cbrt") builtinFunctions(INDEX_CBRT)
  else if (name == "pow") builtinFunctions(INDEX_POW)
  else if (name == "exp") builtinFunctions(INDEX_EXP)
  else if (name == "expm1") builtinFunctions(INDEX_EXPM1)
  else if (name == "signum") builtinFunctions(INDEX_SGN)
  else null

  try builtinFunctions(INDEX_SIN) = new Function("sin") {
    override def apply(args: Double*): Double = Math.sin(args(0))
  }
  builtinFunctions(INDEX_COS) = new Function("cos") {
    override def apply(args: Double*): Double = Math.cos(args(0))
  }
  builtinFunctions(INDEX_TAN) = new Function("tan") {
    override def apply(args: Double*): Double = Math.tan(args(0))
  }
  builtinFunctions(INDEX_COT) = new Function("cot") {
    override def apply(args: Double*): Double = {
      val tan = Math.tan(args(0))
      if (tan == 0d) throw new ArithmeticException("Division by zero in cotangent!")
      1d / Math.tan(args(0))
    }
  }
  builtinFunctions(INDEX_LOG) = new Function("log") {
    override def apply(args: Double*): Double = Math.log(args(0))
  }
  builtinFunctions(INDEX_LOG2) = new Function("log2") {
    override def apply(args: Double*): Double = Math.log(args(0)) / Math.log(2d)
  }
  builtinFunctions(INDEX_LOG10) = new Function("log10") {
    override def apply(args: Double*): Double = Math.log10(args(0))
  }
  builtinFunctions(INDEX_LOG1P) = new Function("log1p") {
    override def apply(args: Double*): Double = Math.log1p(args(0))
  }
  builtinFunctions(INDEX_ABS) = new Function("abs") {
    override def apply(args: Double*): Double = Math.abs(args(0))
  }
  builtinFunctions(INDEX_ACOS) = new Function("acos") {
    override def apply(args: Double*): Double = Math.acos(args(0))
  }
  builtinFunctions(INDEX_ASIN) = new Function("asin") {
    override def apply(args: Double*): Double = Math.asin(args(0))
  }
  builtinFunctions(INDEX_ATAN) = new Function("atan") {
    override def apply(args: Double*): Double = Math.atan(args(0))
  }
  builtinFunctions(INDEX_CBRT) = new Function("cbrt") {
    override def apply(args: Double*): Double = Math.cbrt(args(0))
  }
  builtinFunctions(INDEX_FLOOR) = new Function("floor") {
    override def apply(args: Double*): Double = Math.floor(args(0))
  }
  builtinFunctions(INDEX_SINH) = new Function("sinh") {
    override def apply(args: Double*): Double = Math.sinh(args(0))
  }
  builtinFunctions(INDEX_SQRT) = new Function("sqrt") {
    override def apply(args: Double*): Double = Math.sqrt(args(0))
  }
  builtinFunctions(INDEX_TANH) = new Function("tanh") {
    override def apply(args: Double*): Double = Math.tanh(args(0))
  }
  builtinFunctions(INDEX_COSH) = new Function("cosh") {
    override def apply(args: Double*): Double = Math.cosh(args(0))
  }
  builtinFunctions(INDEX_CEIL) = new Function("ceil") {
    override def apply(args: Double*): Double = Math.ceil(args(0))
  }
  builtinFunctions(INDEX_POW) = new Function(("pow", 2)) {
    override def apply(args: Double*): Double = Math.pow(args(0), args(1))
  }
  builtinFunctions(INDEX_EXP) = new Function(("exp", 1)) {
    override def apply(args: Double*): Double = Math.exp(args(0))
  }
  builtinFunctions(INDEX_EXPM1) = new Function(("expm1", 1)) {
    override def apply(args: Double*): Double = Math.expm1(args(0))
  }
  builtinFunctions(INDEX_SGN) = new Function(("signum", 1)) {
    override def apply(args: Double*): Double = if (args(0) > 0) 1
    else if (args(0) < 0) -1
    else 0
  }
}
