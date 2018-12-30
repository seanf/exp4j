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
package net.objecthunter.exp4j

import net.objecthunter.exp4j.function.Functions
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.tokenizer.FunctionToken
import net.objecthunter.exp4j.tokenizer.NumberToken
import net.objecthunter.exp4j.tokenizer.OperatorToken
import net.objecthunter.exp4j.tokenizer.Token
import net.objecthunter.exp4j.tokenizer.VariableToken
import java.util
import java.util._
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

object Expression {
  private def createDefaultVariables = {
    val vars = new HashMap[String, java.lang.Double](4)
    vars.put("pi", Math.PI)
    vars.put("π", Math.PI)
    vars.put("φ", 1.61803398874d)
    vars.put("e", Math.E)
    vars
  }
}

class Expression {
  final private var tokens: Array[Token] = null
  final private var variables: Map[String, java.lang.Double] = null
  final private var userFunctionNames: Set[String] = null

  /**
    * Creates a new expression that is a copy of the existing one.
    *
    * @param existing the expression to copy
    */
  def this(existing: Expression) {
    this()
    this.tokens = Arrays.copyOf(existing.tokens, existing.tokens.length)
    this.variables = new HashMap[String, java.lang.Double]
    this.variables.putAll(existing.variables)
    this.userFunctionNames = new util.HashSet[String](existing.userFunctionNames)
  }

  def this(tokens: Array[Token], userFunctionNames: Set[String]) {
    this()
    this.tokens = tokens
    this.variables = Expression.createDefaultVariables
    this.userFunctionNames = userFunctionNames
  }

  def this(tokens: Array[Token]) {
    this(tokens, Collections.emptySet[String])
  }

  def setVariable(name: String, value: Double): Expression = {
    this.checkVariableName(name)
    this.variables.put(name, java.lang.Double.valueOf(value))
    this
  }

  private def checkVariableName(name: String) = if (this.userFunctionNames.contains(name) || Functions.getBuiltinFunction(name) != null) throw new IllegalArgumentException("The variable name '" + name + "' is invalid. Since there exists a function with the same name")

  def setVariables(variables: util.Map[String, java.lang.Double]): Expression = {
    import scala.collection.JavaConverters._
    for ((k, v) <- variables.asScala) {
      this.setVariable(k, v)
    }
    this
  }

  def getVariableNames: util.Set[String] = {
    val variables = new util.HashSet[String]
    for (t <- tokens) {
      if (t.getType == Token.TOKEN_VARIABLE) variables.add(t.asInstanceOf[VariableToken].getName)
    }
    variables
  }

  def validate(checkVariablesSet: Boolean): ValidationResult = {
    val errors = new util.ArrayList[String](0)
    if (checkVariablesSet) /* check that all vars have a value set */ for (t <- this.tokens) {
      if (t.getType == Token.TOKEN_VARIABLE) {
        val `var` = t.asInstanceOf[VariableToken].getName
        if (!variables.containsKey(`var`)) errors.add("The setVariable '" + `var` + "' has not been set")
      }
    }
    /* Check if the number of operands, functions and operators match.
               The idea is to increment a counter for operands and decrease it for operators.
               When a function occurs the number of available arguments has to be greater
               than or equals to the function's expected number of arguments.
               The count has to be larger than 1 at all times and exactly 1 after all tokens
               have been processed */ var count = 0
    for (tok <- this.tokens) {
      tok.getType match {
        case Token.TOKEN_NUMBER | Token.TOKEN_VARIABLE =>
          count += 1
        case Token.TOKEN_FUNCTION =>
          val func: function.Function = tok.asInstanceOf[FunctionToken].getFunction
          val argsNum = func.getNumArguments
          if (argsNum > count) errors.add("Not enough arguments for '" + func.getName + "'")
          if (argsNum > 1) count -= argsNum - 1
          else if (argsNum == 0) { // see https://github.com/fasseg/exp4j/issues/59
            count += 1
          }
        case Token.TOKEN_OPERATOR =>
          val op: Operator = tok.asInstanceOf[OperatorToken].getOperator
          if (op.getNumOperands == 2) count -= 1
      }
      if (count < 1) {
        errors.add("Too many operators")
        return new ValidationResult(false, errors)
      }
    }
    if (count > 1) errors.add("Too many operands")
    if (errors.size == 0) ValidationResult.SUCCESS
    else new ValidationResult(false, errors)
  }

  def validate: ValidationResult = validate(true)

  def evaluateAsync(executor: ExecutorService): Future[java.lang.Double] = executor.submit(new Callable[java.lang.Double]() {
    @throws[Exception]
    override def call: java.lang.Double = return evaluate
  })

  def evaluate: Double = {
    val output = new ArrayStack
    var i = 0
    for (t <- tokens) {
      if (t.getType == Token.TOKEN_NUMBER) output.push(t.asInstanceOf[NumberToken].getValue)
      else if (t.getType == Token.TOKEN_VARIABLE) {
        val name = t.asInstanceOf[VariableToken].getName
        val value = this.variables.get(name)
        if (value == null) throw new IllegalArgumentException("No value has been set for the setVariable '" + name + "'.")
        output.push(value)
      }
      else if (t.getType == Token.TOKEN_OPERATOR) {
        val op = t.asInstanceOf[OperatorToken]
        if (output.size < op.getOperator.getNumOperands) throw new IllegalArgumentException("Invalid number of operands available for '" + op.getOperator.getSymbol + "' operator")
        if (op.getOperator.getNumOperands == 2) {
          /* pop the operands and push the result of the operation */ val rightArg = output.pop
          val leftArg = output.pop
          output.push(op.getOperator.apply(leftArg, rightArg))
        }
        else if (op.getOperator.getNumOperands == 1) {
          /* pop the operand and push the result of the operation */ val arg = output.pop
          output.push(op.getOperator.apply(arg))
        }
      }
      else if (t.getType == Token.TOKEN_FUNCTION) {
        val func = t.asInstanceOf[FunctionToken]
        val numArguments = func.getFunction.getNumArguments
        if (output.size < numArguments) throw new IllegalArgumentException("Invalid number of arguments available for '" + func.getFunction.getName + "' function")
        /* collect the arguments from the stack */
        val args = new Array[Double](numArguments)
        for (j <- numArguments - 1 to 0 by -1) args(j) = output.pop

        output.push(func.getFunction.apply(args:_*))
      }
    }
    if (output.size > 1) throw new IllegalArgumentException("Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function.")
    output.pop
  }
}
