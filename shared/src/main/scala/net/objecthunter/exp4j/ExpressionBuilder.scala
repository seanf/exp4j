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

import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.function.Functions
import net.objecthunter.exp4j.operator.Operator
import net.objecthunter.exp4j.shuntingyard.ShuntingYard

import scala.collection.mutable
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
  * Factory class for {@link Expression} instances. This class is the main API entrypoint. Users should create new
  * {@link Expression} instances using this factory class.
  *
  * @constructor
  * Create a new ExpressionBuilder instance and initialize it with a given expression string.
  *
  * @param expression the expression to be parsed
  */
@JSExportTopLevel("ExpressionBuilder")
@JSExportAll
class ExpressionBuilder(val expression: String) {
  if (expression == null || expression.trim.length == 0) throw new IllegalArgumentException("Expression can not be empty")

  final private val userOperators = new mutable.HashMap[String, Operator]
  final private val userFunctions = new mutable.HashMap[String, Function]
  final private val variableNames = new mutable.HashSet[String]
  private var implicitMultiplication = true

  /**
    * Add a {@link net.objecthunter.exp4j.function.Function} implementation available for use in the expression
    *
    * @param function the custom { @link net.objecthunter.exp4j.function.Function} implementation that should be available for use in the expression.
    * @return the ExpressionBuilder instance
    */
  def function(function: Function): ExpressionBuilder = {
    this.userFunctions.put(function.getName, function)
    this
  }

  /**
    * Add multiple {@link net.objecthunter.exp4j.function.Function} implementations available for use in the expression
    *
    * @param functions the custom { @link net.objecthunter.exp4j.function.Function} implementations
    * @return the ExpressionBuilder instance
    */
  def functions(functions: Function*): ExpressionBuilder = {
    for (f <- functions) {
      this.userFunctions.put(f.getName, f)
    }
    this
  }

  /**
    * Add multiple {@link net.objecthunter.exp4j.function.Function} implementations available for use in the expression
    *
    * @param functions A { @link List} of custom { @link net.objecthunter.exp4j.function.Function} implementations
    * @return the ExpressionBuilder instance
    */
  def functions(functions: List[Function]): ExpressionBuilder = {
    for (f <- functions) {
      this.userFunctions.put(f.getName, f)
    }
    this
  }

  /**
    * Declare variable names used in the expression
    *
    * @param variableNames the variables used in the expression
    * @return the ExpressionBuilder instance
    */
  def variables(variableNames: Set[String]): ExpressionBuilder = {
    this.variableNames ++= variableNames
    this
  }

  def variables(variableNames: String*): ExpressionBuilder = {
    this.variableNames ++= variableNames
    this
  }

  /**
    * Declare a variable used in the expression
    *
    * @param variableName the variable used in the expression
    * @return the ExpressionBuilder instance
    */
  def variable(variableName: String): ExpressionBuilder = {
    this.variableNames.add(variableName)
    this
  }

  def implicitMultiplication(enabled: Boolean): ExpressionBuilder = {
    this.implicitMultiplication = enabled
    this
  }

  /**
    * Add an {@link net.objecthunter.exp4j.operator.Operator} which should be available for use in the expression
    *
    * @param operator the custom { @link net.objecthunter.exp4j.operator.Operator} to add
    * @return the ExpressionBuilder instance
    */
  private def operator(operator: Operator): ExpressionBuilder = {
    this.checkOperatorSymbol(operator)
    this.userOperators.put(operator.getSymbol, operator)
    this
  }

  private def checkOperatorSymbol(op: Operator) = {
    val name = op.getSymbol
    for (ch <- name.toCharArray) {
      if (!Operator.isAllowedOperatorChar(ch)) throw new IllegalArgumentException("The operator symbol '" + name + "' is invalid")
    }
  }

  /**
    * Add multiple {@link net.objecthunter.exp4j.operator.Operator} implementations which should be available for use in the expression
    *
    * @param operators the set of custom { @link net.objecthunter.exp4j.operator.Operator} implementations to add
    * @return the ExpressionBuilder instance
    */
  def operator(operators: Operator*): ExpressionBuilder = {
    for (o <- operators) {
      this.operator(o)
    }
    this
  }

  /**
    * Add multiple {@link net.objecthunter.exp4j.operator.Operator} implementations which should be available for use in the expression
    *
    * @param operators the { @link List} of custom { @link net.objecthunter.exp4j.operator.Operator} implementations to add
    * @return the ExpressionBuilder instance
    */
  def operator(operators: List[Operator]): ExpressionBuilder = {
    for (o <- operators) {
      this.operator(o)
    }
    this
  }

  /**
    * Build the {@link Expression} instance using the custom operators and functions set.
    *
    * @return an { @link Expression} instance which can be used to evaluate the result of the expression
    */
  def build: Expression = {
    if (expression.length == 0) throw new IllegalArgumentException("The expression can not be empty")
    /* set the constants' variable names */ variableNames.add("pi")
    variableNames.add("π")
    variableNames.add("e")
    variableNames.add("φ")
    /* Check if there are duplicate vars/functions */
    for (v <- variableNames) {
      if (Functions.getBuiltinFunction(v) != null || userFunctions.contains(v)) throw new IllegalArgumentException("A variable can not have the same name as a function [" + v + "]")
    }
    new Expression(tokens = ShuntingYard.convertToRPN(this.expression, this.userFunctions.toMap, this.userOperators.toMap, this.variableNames.toSet, this.implicitMultiplication), userFunctionNames = this.userFunctions.keySet.toSet)
  }
}
