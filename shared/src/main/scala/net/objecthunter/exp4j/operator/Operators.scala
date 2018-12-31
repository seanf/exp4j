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
package net.objecthunter.exp4j.operator

object Operators {
  private val INDEX_ADDITION = 0
  private val INDEX_SUBTRACTION = 1
  private val INDEX_MUTLIPLICATION = 2
  private val INDEX_DIVISION = 3
  private val INDEX_POWER = 4
  private val INDEX_MODULO = 5
  private val INDEX_UNARYMINUS = 6
  private val INDEX_UNARYPLUS = 7
  private val builtinOperators = new Array[Operator](8)

  def getBuiltinOperator(symbol: Char, numArguments: Int): Operator = symbol match {
    case '+' =>
      if (numArguments != 1) builtinOperators(INDEX_ADDITION)
      else builtinOperators(INDEX_UNARYPLUS)
    case '-' =>
      if (numArguments != 1) builtinOperators(INDEX_SUBTRACTION)
      else builtinOperators(INDEX_UNARYMINUS)
    case '*' =>
      builtinOperators(INDEX_MUTLIPLICATION)
    case '/' =>
      builtinOperators(INDEX_DIVISION)
    case '^' =>
      builtinOperators(INDEX_POWER)
    case '%' =>
      builtinOperators(INDEX_MODULO)
    case _ =>
      null
  }

  builtinOperators(INDEX_ADDITION) = new Operator("+", 2, true, Operator.PRECEDENCE_ADDITION) {
    override def apply(args: Double*): Double = args(0) + args(1)
  }
  builtinOperators(INDEX_SUBTRACTION) = new Operator("-", 2, true, Operator.PRECEDENCE_ADDITION) {
    override def apply(args: Double*): Double = args(0) - args(1)
  }
  builtinOperators(INDEX_UNARYMINUS) = new Operator("-", 1, false, Operator.PRECEDENCE_UNARY_MINUS) {
    override def apply(args: Double*): Double = -args(0)
  }
  builtinOperators(INDEX_UNARYPLUS) = new Operator("+", 1, false, Operator.PRECEDENCE_UNARY_PLUS) {
    override def apply(args: Double*): Double = args(0)
  }
  builtinOperators(INDEX_MUTLIPLICATION) = new Operator("*", 2, true, Operator.PRECEDENCE_MULTIPLICATION) {
    override def apply(args: Double*): Double = args(0) * args(1)
  }
  builtinOperators(INDEX_DIVISION) = new Operator("/", 2, true, Operator.PRECEDENCE_DIVISION) {
    override def apply(args: Double*): Double = {
      if (args(1) == 0d) throw new ArithmeticException("Division by zero!")
      args(0) / args(1)
    }
  }
  builtinOperators(INDEX_POWER) = new Operator("^", 2, false, Operator.PRECEDENCE_POWER) {
    override def apply(args: Double*): Double = Math.pow(args(0), args(1))
  }
  builtinOperators(INDEX_MODULO) = new Operator("%", 2, true, Operator.PRECEDENCE_MODULO) {
    override def apply(args: Double*): Double = {
      if (args(1) == 0d) throw new ArithmeticException("Division by zero!")
      args(0) % args(1)
    }
  }
}
