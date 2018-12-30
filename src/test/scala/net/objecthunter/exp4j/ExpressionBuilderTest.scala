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

import java.lang.Math._
import org.junit.Assert._
import java.util
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import org.junit.Test

class ExpressionBuilderTest {
  @Test
  @throws[Exception]
  def testExpressionBuilder1(): Unit = {
    val result = new Nothing("2+1").build.evaluate
    assertEquals(3d, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder2(): Unit = {
    val result = new Nothing("cos(x)").variables("x").build.setVariable("x", Math.PI).evaluate
    val expected = cos(Math.PI)
    assertEquals(-1d, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder3(): Unit = {
    val x = Math.PI
    val result = new Nothing("sin(x)-log(3*x/4)").variables("x").build.setVariable("x", x).evaluate
    val expected = sin(x) - log(3 * x / 4)
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder4(): Unit = {
    val log2 = new Nothing(("log2", 1)) {
      def apply(args: Double*): Double = Math.log(args(0)) / Math.log(2)
    }
    val result = new Nothing("log2(4)").function(log2).build.evaluate
    val expected = 2
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder5(): Unit = {
    val avg = new Nothing(("avg", 4)) {
      def apply(args: Double*): Double = {
        var sum = 0
        for (arg <- args) {
          sum += arg
        }
        sum / args.length
      }
    }
    val result = new Nothing("avg(1,2,3,4)").function(avg).build.evaluate
    val expected = 2.5d
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder6(): Unit = {
    val factorial = new Nothing(("!", 1, true, Operator.PRECEDENCE_POWER + 1)) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1
        var i = 1
        while ( {
          i <= arg
        }) {
          result *= i
          {
            i += 1; i - 1
          }
        }
        result
      }
    }
    val result = new Nothing("3!").operator(factorial).build.evaluate
    val expected = 6d
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder7(): Unit = {
    val res = new Nothing("x").variables("x").build.validate
    assertFalse(res.isValid)
    assertEquals(res.getErrors.size, 1)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder8(): Unit = {
    val res = new Nothing("x*y*z").variables("x", "y", "z").build.validate
    assertFalse(res.isValid)
    assertEquals(res.getErrors.size, 3)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder9(): Unit = {
    val res = new Nothing("x").variables("x").build.setVariable("x", 1d).validate
    assertTrue(res.isValid)
  }

  @Test
  @throws[Exception]
  def testValidationDocExample(): Unit = {
    val e = new Nothing("x").variables("x").build
    var res = e.validate
    assertFalse(res.isValid)
    assertEquals(1, res.getErrors.size)
    e.setVariable("x", 1d)
    res = e.validate
    assertTrue(res.isValid)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder10(): Unit = {
    val result = new Nothing("1e1").build.evaluate
    assertEquals(10d, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder11(): Unit = {
    val result = new Nothing("1.11e-1").build.evaluate
    assertEquals(0.111d, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder12(): Unit = {
    val result = new Nothing("1.11e+1").build.evaluate
    assertEquals(11.1d, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder13(): Unit = {
    val result = new Nothing("-3^2").build.evaluate
    assertEquals(-9d, result, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder14(): Unit = {
    val result = new Nothing("(-3)^2").build.evaluate
    assertEquals(9d, result, 0d)
  }

  @Test(expected = classOf[ArithmeticException])
  @throws[Exception]
  def testExpressionBuilder15(): Unit = {
    val result = new Nothing("-3/0").build.evaluate
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder16(): Unit = {
    val result = new Nothing("log(x) - y * (sqrt(x^cos(y)))").variables("x", "y").build.setVariable("x", 1d).setVariable("y", 2d).evaluate
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder17(): Unit = {
    val e = new Nothing("x-y*").variables("x", "y").build
    val res = e.validate(false)
    assertFalse(res.isValid)
    assertEquals(1, res.getErrors.size)
    assertEquals("Too many operators", res.getErrors.get(0))
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder18(): Unit = {
    val e = new Nothing("log(x) - y *").variables("x", "y").build
    val res = e.validate(false)
    assertFalse(res.isValid)
    assertEquals(1, res.getErrors.size)
    assertEquals("Too many operators", res.getErrors.get(0))
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder19(): Unit = {
    val e = new Nothing("x - y *").variables("x", "y").build
    val res = e.validate(false)
    assertFalse(res.isValid)
    assertEquals(1, res.getErrors.size)
    assertEquals("Too many operators", res.getErrors.get(0))
  }

  /* legacy tests from earlier exp4j versions */ @Test
  @throws[Exception]
  def testFunction1(): Unit = {
    val custom = new Nothing("timespi") {
      def apply(values: Double*): Double = values(0) * Math.PI
    }
    val e = new Nothing("timespi(x)").function(custom).variables("x").build.setVariable("x", 1)
    val result = e.evaluate
    assertTrue(result == Math.PI)
  }

  @Test
  @throws[Exception]
  def testFunction2(): Unit = {
    val custom = new Nothing("loglog") {
      def apply(values: Double*): Double = Math.log(Math.log(values(0)))
    }
    val e = new Nothing("loglog(x)").variables("x").function(custom).build.setVariable("x", 1)
    val result = e.evaluate
    assertTrue(result == Math.log(Math.log(1)))
  }

  @Test
  @throws[Exception]
  def testFunction3(): Unit = {
    val custom1 = new Nothing("foo") {
      def apply(values: Double*): Double = values(0) * Math.E
    }
    val custom2 = new Nothing("bar") {
      def apply(values: Double*): Double = values(0) * Math.PI
    }
    val e = new Nothing("foo(bar(x))").function(custom1).function(custom2).variables("x").build.setVariable("x", 1)
    val result = e.evaluate
    assertTrue(result == 1 * Math.E * Math.PI)
  }

  @Test
  @throws[Exception]
  def testFunction4(): Unit = {
    val custom1 = new Nothing("foo") {
      def apply(values: Double*): Double = values(0) * Math.E
    }
    val varX = 32.24979131d
    val e = new Nothing("foo(log(x))").variables("x").function(custom1).build.setVariable("x", varX)
    val result = e.evaluate
    assertTrue(result == Math.log(varX) * Math.E)
  }

  @Test
  @throws[Exception]
  def testFunction5(): Unit = {
    val custom1 = new Nothing("foo") {
      def apply(values: Double*): Double = values(0) * Math.E
    }
    val custom2 = new Nothing("bar") {
      def apply(values: Double*): Double = values(0) * Math.PI
    }
    val varX = 32.24979131d
    val e = new Nothing("bar(foo(log(x)))").variables("x").function(custom1).function(custom2).build.setVariable("x", varX)
    val result = e.evaluate
    assertTrue(result == Math.log(varX) * Math.E * Math.PI)
  }

  @Test
  @throws[Exception]
  def testFunction6(): Unit = {
    val custom1 = new Nothing("foo") {
      def apply(values: Double*): Double = values(0) * Math.E
    }
    val custom2 = new Nothing("bar") {
      def apply(values: Double*): Double = values(0) * Math.PI
    }
    val varX = 32.24979131d
    val e = new Nothing("bar(foo(log(x)))").variables("x").functions(custom1, custom2).build.setVariable("x", varX)
    val result = e.evaluate
    assertTrue(result == Math.log(varX) * Math.E * Math.PI)
  }

  @Test
  @throws[Exception]
  def testFunction7(): Unit = {
    val custom1 = new Nothing("half") {
      def apply(values: Double*): Double = values(0) / 2
    }
    val e = new Nothing("half(x)").variables("x").function(custom1).build.setVariable("x", 1d)
    assertTrue(0.5d == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testFunction10(): Unit = {
    val custom1 = new Nothing(("max", 2)) {
      def apply(values: Double*): Double = if (values(0) < values(1)) values(1)
      else values(0)
    }
    val e = new Nothing("max(x,y)").variables("x", "y").function(custom1).build.setVariable("x", 1d).setVariable("y", 2d)
    assertTrue(2 == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testFunction11(): Unit = {
    val custom1 = new Nothing(("power", 2)) {
      def apply(values: Double*): Double = Math.pow(values(0), values(1))
    }
    val e = new Nothing("power(x,y)").variables("x", "y").function(custom1).build.setVariable("x", 2d).setVariable("y", 4d)
    assertTrue(Math.pow(2, 4) == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testFunction12(): Unit = {
    val custom1 = new Nothing(("max", 5)) {
      def apply(values: Double*): Double = {
        var max = values(0)
        var i = 1
        while ( {
          i < numArguments
        }) {
          if (values(i) > max) max = values(i)
          {
            i += 1; i - 1
          }
        }
        max
      }
    }
    val e = new Nothing("max(1,2.43311,51.13,43,12)").function(custom1).build
    assertTrue(51.13d == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testFunction13(): Unit = {
    val custom1 = new Nothing(("max", 3)) {
      def apply(values: Double*): Double = {
        var max = values(0)
        var i = 1
        while ( {
          i < numArguments
        }) {
          if (values(i) > max) max = values(i)
          {
            i += 1; i - 1
          }
        }
        max
      }
    }
    val varX = Math.E
    val e = new Nothing("max(log(x),sin(x),x)").variables("x").function(custom1).build.setVariable("x", varX)
    assertTrue(varX == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testFunction14(): Unit = {
    val custom1 = new Nothing(("multiply", 2)) {
      def apply(values: Double*): Double = values(0) * values(1)
    }
    val varX = 1
    val e = new Nothing("multiply(sin(x),x+1)").variables("x").function(custom1).build.setVariable("x", varX)
    val expected = Math.sin(varX) * (varX + 1)
    val actual = e.evaluate
    assertTrue(expected == actual)
  }

  @Test
  @throws[Exception]
  def testFunction15(): Unit = {
    val custom1 = new Nothing("timesPi") {
      def apply(values: Double*): Double = values(0) * Math.PI
    }
    val varX = 1
    val e = new Nothing("timesPi(x^2)").variables("x").function(custom1).build.setVariable("x", varX)
    val expected = varX * Math.PI
    val actual = e.evaluate
    assertTrue(expected == actual)
  }

  @Test
  @throws[Exception]
  def testFunction16(): Unit = {
    val custom1 = new Nothing(("multiply", 3)) {
      def apply(values: Double*): Double = values(0) * values(1) * values(2)
    }
    val varX = 1
    val e = new Nothing("multiply(sin(x),x+1^(-2),log(x))").variables("x").function(custom1).build.setVariable("x", varX)
    val expected = Math.sin(varX) * Math.pow(varX + 1, -2) * Math.log(varX)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testFunction17(): Unit = {
    val custom1 = new Nothing("timesPi") {
      def apply(values: Double*): Double = values(0) * Math.PI
    }
    val varX = Math.E
    val e = new Nothing("timesPi(log(x^(2+1)))").variables("x").function(custom1).build.setVariable("x", varX)
    val expected = Math.log(Math.pow(varX, 3)) * Math.PI
    assertTrue(expected == e.evaluate)
  }

  // thanks to Marcin Domanski who issued
  // http://jira.congrace.de/jira/browse/EXP-11
  // i have this test, which fails in 0.2.9
  @Test
  @throws[Exception]
  def testFunction18(): Unit = {
    val minFunction = new Nothing(("min", 2)) {
      def apply(values: Array[Double]): Double = {
        var currentMin = Double.POSITIVE_INFINITY
        for (value <- values) {
          currentMin = Math.min(currentMin, value)
        }
        currentMin
      }
    }
    val b = new Nothing("-min(5, 0) + 10").function(minFunction)
    val calculated = b.build.evaluate
    assertTrue(calculated == 10)
  }

  // thanks to Sylvain Machefert who issued
  // i have this test, which fails in 0.3.2
  @Test
  @throws[Exception]
  def testFunction19(): Unit = {
    val minFunction = new Nothing(("power", 2)) {
      def apply(values: Array[Double]): Double = Math.pow(values(0), values(1))
    }
    val b = new Nothing("power(2,3)").function(minFunction)
    val calculated = b.build.evaluate
    assertEquals(Math.pow(2, 3), calculated, 0d)
  }

  // thanks to Narendra Harmwal who noticed that getArgumentCount was not
  // implemented
  // this test has been added in 0.3.5
  @Test
  @throws[Exception]
  def testFunction20(): Unit = {
    val maxFunction = new Nothing(("max", 3)) {
      def apply(values: Double*): Double = {
        var max = values(0)
        var i = 1
        while ( {
          i < numArguments
        }) {
          if (values(i) > max) max = values(i)
          {
            i += 1; i - 1
          }
        }
        max
      }
    }
    val b = new Nothing("max(1,2,3)").function(maxFunction)
    val calculated = b.build.evaluate
    assertTrue(maxFunction.getNumArguments eq 3)
    assertTrue(calculated == 3)
  }

  @Test
  @throws[Exception]
  def testOperators1(): Unit = {
    val factorial = new Nothing(("!", 1, true, Operator.PRECEDENCE_POWER + 1)) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1
        var i = 1
        while ( {
          i <= arg
        }) {
          result *= i
          {
            i += 1; i - 1
          }
        }
        result
      }
    }
    var e = new Nothing("1!").operator(factorial).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("2!").operator(factorial).build
    assertTrue(2d == e.evaluate)
    e = new Nothing("3!").operator(factorial).build
    assertTrue(6d == e.evaluate)
    e = new Nothing("4!").operator(factorial).build
    assertTrue(24d == e.evaluate)
    e = new Nothing("5!").operator(factorial).build
    assertTrue(120d == e.evaluate)
    e = new Nothing("11!").operator(factorial).build
    assertTrue(39916800d == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testOperators2(): Unit = {
    val factorial = new Nothing(("!", 1, true, Operator.PRECEDENCE_POWER + 1)) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1
        var i = 1
        while ( {
          i <= arg
        }) {
          result *= i
          {
            i += 1; i - 1
          }
        }
        result
      }
    }
    var e = new Nothing("2^3!").operator(factorial).build
    assertEquals(64d, e.evaluate, 0d)
    e = new Nothing("3!^2").operator(factorial).build
    assertTrue(36d == e.evaluate)
    e = new Nothing("-(3!)^-1").operator(factorial).build
    val actual = e.evaluate
    assertEquals(Math.pow(-6d, -1), actual, 0d)
  }

  @Test
  @throws[Exception]
  def testOperators3(): Unit = {
    val gteq = new Nothing((">=", 2, true, Operator.PRECEDENCE_ADDITION - 1)) {
      def apply(values: Array[Double]): Double = if (values(0) >= values(1)) 1d
      else 0d
    }
    var e = new Nothing("1>=2").operator(gteq).build
    assertTrue(0d == e.evaluate)
    e = new Nothing("2>=1").operator(gteq).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("-2>=1").operator(gteq).build
    assertTrue(0d == e.evaluate)
    e = new Nothing("-2>=-1").operator(gteq).build
    assertTrue(0d == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testModulo1(): Unit = {
    val result = new Nothing("33%(20/2)%2").build.evaluate
    assertTrue(result == 1d)
  }

  @Test
  @throws[Exception]
  def testOperators4(): Unit = {
    val greaterEq = new Nothing((">=", 2, true, 4)) {
      def apply(values: Array[Double]): Double = if (values(0) >= values(1)) 1d
      else 0d
    }
    val greater = new Nothing((">", 2, true, 4)) {
      def apply(values: Array[Double]): Double = if (values(0) > values(1)) 1d
      else 0d
    }
    val newPlus = new Nothing((">=>", 2, true, 4)) {
      def apply(values: Array[Double]): Double = values(0) + values(1)
    }
    var e = new Nothing("1>2").operator(greater).build
    assertTrue(0d == e.evaluate)
    e = new Nothing("2>=2").operator(greaterEq).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("1>=>2").operator(newPlus).build
    assertTrue(3d == e.evaluate)
    e = new Nothing("1>=>2>2").operator(greater).operator(newPlus).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("1>=>2>2>=1").operator(greater).operator(newPlus).operator(greaterEq).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("1 >=> 2 > 2 >= 1").operator(greater).operator(newPlus).operator(greaterEq).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("1 >=> 2 >= 2 > 1").operator(greater).operator(newPlus).operator(greaterEq).build
    assertTrue(0d == e.evaluate)
    e = new Nothing("1 >=> 2 >= 2 > 0").operator(greater).operator(newPlus).operator(greaterEq).build
    assertTrue(1d == e.evaluate)
    e = new Nothing("1 >=> 2 >= 2 >= 1").operator(greater).operator(newPlus).operator(greaterEq).build
    assertTrue(1d == e.evaluate)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidOperator1(): Unit = {
    val fail = new Nothing(("2", 2, true, 1)) {
      def apply(values: Array[Double]) = 0
    }
    new Nothing("1").operator(fail).build
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidFunction1(): Unit = {
    val func = new Nothing("1gd") {
      def apply(args: Double*) = 0
    }
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidFunction2(): Unit = {
    val func = new Nothing("+1gd") {
      def apply(args: Double*) = 0
    }
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder01(): Unit = {
    val e = new Nothing("7*x + 3*y").variables("x", "y").build.setVariable("x", 1).setVariable("y", 2)
    val result = e.evaluate
    assertTrue(result == 13d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder02(): Unit = {
    val e = new Nothing("7*x + 3*y").variables("x", "y").build.setVariable("x", 1).setVariable("y", 2)
    val result = e.evaluate
    assertTrue(result == 13d)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder03(): Unit = {
    val varX = 1.3d
    val varY = 4.22d
    val e = new Nothing("7*x + 3*y - log(y/x*12)^y").variables("x", "y").build.setVariable("x", varX).setVariable("y", varY)
    val result = e.evaluate
    assertTrue(result == 7 * varX + 3 * varY - Math.pow(Math.log(varY / varX * 12), varY))
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder04(): Unit = {
    var varX = 1.3d
    var varY = 4.22d
    val e = new Nothing("7*x + 3*y - log(y/x*12)^y").variables("x", "y").build.setVariable("x", varX).setVariable("y", varY)
    var result = e.evaluate
    assertTrue(result == 7 * varX + 3 * varY - Math.pow(Math.log(varY / varX * 12), varY))
    varX = 1.79854d
    varY = 9281.123d
    e.setVariable("x", varX)
    e.setVariable("y", varY)
    result = e.evaluate
    assertTrue(result == 7 * varX + 3 * varY - Math.pow(Math.log(varY / varX * 12), varY))
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder05(): Unit = {
    val varX = 1.3d
    val varY = 4.22d
    val e = new Nothing("3*y").variables("y").build.setVariable("x", varX).setVariable("y", varY)
    val result = e.evaluate
    assertTrue(result == 3 * varY)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder06(): Unit = {
    val varX = 1.3d
    val varY = 4.22d
    val varZ = 4.22d
    val e = new Nothing("x * y * z").variables("x", "y", "z").build
    e.setVariable("x", varX)
    e.setVariable("y", varY)
    e.setVariable("z", varZ)
    val result = e.evaluate
    assertTrue(result == varX * varY * varZ)
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder07(): Unit = {
    val varX = 1.3d
    val e = new Nothing("log(sin(x))").variables("x").build.setVariable("x", varX)
    val result = e.evaluate
    assertTrue(result == Math.log(Math.sin(varX)))
  }

  @Test
  @throws[Exception]
  def testExpressionBuilder08(): Unit = {
    val varX = 1.3d
    val e = new Nothing("log(sin(x))").variables("x").build.setVariable("x", varX)
    val result = e.evaluate
    assertTrue(result == Math.log(Math.sin(varX)))
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testSameName(): Unit = {
    val custom = new Nothing("bar") {
      def apply(values: Double*): Double = values(0) / 2
    }
    val varBar = 1.3d
    val e = new Nothing("bar(bar)").variables("bar").function(custom).build.setVariable("bar", varBar)
    val res = e.validate
    assertFalse(res.isValid)
    assertEquals(1, res.getErrors.size)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidFunction(): Unit = {
    val varY = 4.22d
    val e = new Nothing("3*invalid_function(y)").variables("<").build.setVariable("y", varY)
    e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testMissingVar(): Unit = {
    val varY = 4.22d
    val e = new Nothing("3*y*z").variables("y", "z").build.setVariable("y", varY)
    e.evaluate
  }

  @Test
  @throws[Exception]
  def testUnaryMinusPowerPrecedence(): Unit = {
    val e = new Nothing("-1^2").build
    assertEquals(-1d, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testUnaryMinus(): Unit = {
    val e = new Nothing("-1").build
    assertEquals(-1d, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression1(): Unit = {
    var expr = null
    var expected = .0
    expr = "2 + 4"
    expected = 6d
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression10(): Unit = {
    var expr = null
    var expected = .0
    expr = "1 * 1.5 + 1"
    expected = 1 * 1.5 + 1
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression11(): Unit = {
    val x = 1d
    val y = 2d
    val expr = "log(x) ^ sin(y)"
    val expected = Math.pow(Math.log(x), Math.sin(y))
    val e = new Nothing(expr).variables("x", "y").build.setVariable("x", x).setVariable("y", y)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression12(): Unit = {
    val expr = "log(2.5333333333)^(0-1)"
    val expected = Math.pow(Math.log(2.5333333333d), -1)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression13(): Unit = {
    val expr = "2.5333333333^(0-1)"
    val expected = Math.pow(2.5333333333d, -1)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression14(): Unit = {
    val expr = "2 * 17.41 + (12*2)^(0-1)"
    val expected = 2 * 17.41d + Math.pow(12 * 2, -1)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression15(): Unit = {
    val expr = "2.5333333333 * 17.41 + (12*2)^log(2.764)"
    val expected = 2.5333333333d * 17.41d + Math.pow(12 * 2, Math.log(2.764d))
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression16(): Unit = {
    val expr = "2.5333333333/2 * 17.41 + (12*2)^(log(2.764) - sin(5.6664))"
    val expected = 2.5333333333d / 2 * 17.41d + Math.pow(12 * 2, Math.log(2.764d) - Math.sin(5.6664d))
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression17(): Unit = {
    val expr = "x^2 - 2 * y"
    val x = Math.E
    val y = Math.PI
    val expected = x * x - 2 * y
    val e = new Nothing(expr).variables("x", "y").build.setVariable("x", x).setVariable("y", y)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression18(): Unit = {
    val expr = "-3"
    val expected = -3
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression19(): Unit = {
    val expr = "-3 * -24.23"
    val expected = -3 * -24.23d
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression2(): Unit = {
    var expr = null
    var expected = .0
    expr = "2+3*4-12"
    expected = 2 + 3 * 4 - 12
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression20(): Unit = {
    val expr = "-2 * 24/log(2) -2"
    val expected = -2 * 24 / Math.log(2) - 2
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression21(): Unit = {
    val expr = "-2 *33.34/log(x)^-2 + 14 *6"
    val x = 1.334d
    val expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6
    val e = new Nothing(expr).variables("x").build.setVariable("x", x)
    assertEquals(expected, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionPower(): Unit = {
    val expr = "2^-2"
    val expected = Math.pow(2, -2)
    val e = new Nothing(expr).build
    assertEquals(expected, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpressionMultiplication(): Unit = {
    val expr = "2*-2"
    val expected = -4d
    val e = new Nothing(expr).build
    assertEquals(expected, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression22(): Unit = {
    val expr = "-2 *33.34/log(x)^-2 + 14 *6"
    val x = 1.334d
    val expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6
    val e = new Nothing(expr).variables("x").build.setVariable("x", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression23(): Unit = {
    val expr = "-2 *33.34/(log(foo)^-2 + 14 *6) - sin(foo)"
    val x = 1.334d
    val expected = -2 * 33.34 / (Math.pow(Math.log(x), -2) + 14 * 6) - Math.sin(x)
    val e = new Nothing(expr).variables("foo").build.setVariable("foo", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression24(): Unit = {
    val expr = "3+4-log(23.2)^(2-1) * -1"
    val expected = 3 + 4 - Math.pow(Math.log(23.2), 2 - 1) * -1
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression25(): Unit = {
    val expr = "+3+4-+log(23.2)^(2-1) * + 1"
    val expected = 3 + 4 - Math.log(23.2d)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression26(): Unit = {
    val expr = "14 + -(1 / 2.22^3)"
    val expected = 14 + -1d / Math.pow(2.22d, 3d)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression27(): Unit = {
    val expr = "12^-+-+-+-+-+-+---2"
    val expected = Math.pow(12, -2)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression28(): Unit = {
    val expr = "12^-+-+-+-+-+-+---2 * (-14) / 2 ^ -log(2.22323) "
    val expected = Math.pow(12, -2) * -14 / Math.pow(2, -Math.log(2.22323))
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression29(): Unit = {
    val expr = "24.3343 % 3"
    val expected = 24.3343 % 3
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testVarname1(): Unit = {
    val expr = "12.23 * foo.bar"
    val e = new Nothing(expr).variables("foo.bar").build.setVariable("foo.bar", 1d)
    assertTrue(12.23 == e.evaluate)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testMisplacedSeparator(): Unit = {
    val expr = "12.23 * ,foo"
    val e = new Nothing(expr).build.setVariable(",foo", 1d)
    assertTrue(12.23 == e.evaluate)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidVarname(): Unit = {
    val expr = "12.23 * @foo"
    val e = new Nothing(expr).build.setVariable("@foo", 1d)
    assertTrue(12.23 == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testVarMap(): Unit = {
    val expr = "12.23 * foo - bar"
    val variables = new util.HashMap[String, Double]
    variables.put("foo", 2d)
    variables.put("bar", 3.3d)
    val e = new Nothing(expr).variables(variables.keySet).build.setVariables(variables)
    assertTrue(12.23d * 2d - 3.3d == e.evaluate)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidNumberofArguments1(): Unit = {
    val expr = "log(2,2)"
    val e = new Nothing(expr).build
    e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testInvalidNumberofArguments2(): Unit = {
    val avg = new Nothing(("avg", 4)) {
      def apply(args: Double*): Double = {
        var sum = 0
        for (arg <- args) {
          sum += arg
        }
        sum / args.length
      }
    }
    val expr = "avg(2,2)"
    val e = new Nothing(expr).build
    e.evaluate
  }

  @Test
  @throws[Exception]
  def testExpression3(): Unit = {
    var expr = null
    var expected = .0
    expr = "2+4*5"
    expected = 2 + 4 * 5
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression30(): Unit = {
    val expr = "24.3343 % 3 * 20 ^ -(2.334 % log(2 / 14))"
    val expected = 24.3343d % 3 * Math.pow(20, -2.334 % Math.log(2d / 14d))
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression31(): Unit = {
    val expr = "-2 *33.34/log(y_x)^-2 + 14 *6"
    val x = 1.334d
    val expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6
    val e = new Nothing(expr).variables("y_x").build.setVariable("y_x", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression32(): Unit = {
    val expr = "-2 *33.34/log(y_2x)^-2 + 14 *6"
    val x = 1.334d
    val expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6
    val e = new Nothing(expr).variables("y_2x").build.setVariable("y_2x", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression33(): Unit = {
    val expr = "-2 *33.34/log(_y)^-2 + 14 *6"
    val x = 1.334d
    val expected = -2 * 33.34 / Math.pow(Math.log(x), -2) + 14 * 6
    val e = new Nothing(expr).variables("_y").build.setVariable("_y", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression34(): Unit = {
    val expr = "-2 + + (+4) +(4)"
    val expected = -2 + 4 + 4
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression40(): Unit = {
    val expr = "1e1"
    val expected = 10d
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression41(): Unit = {
    val expr = "1e-1"
    val expected = 0.1d
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  /*
     * Added tests for expressions with scientific notation see http://jira.congrace.de/jira/browse/EXP-17
     */ @Test
  @throws[Exception]
  def testExpression42(): Unit = {
    val expr = "7.2973525698e-3"
    val expected = 7.2973525698e-3d
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression43(): Unit = {
    val expr = "6.02214E23"
    val expected = 6.02214e23d
    val e = new Nothing(expr).build
    val result = e.evaluate
    assertTrue(expected == result)
  }

  @Test
  @throws[Exception]
  def testExpression44(): Unit = {
    val expr = "6.02214E23"
    val expected = 6.02214e23d
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test(expected = classOf[NumberFormatException])
  @throws[Exception]
  def testExpression45(): Unit = {
    val expr = "6.02214E2E3"
    new Nothing(expr).build
  }

  @Test(expected = classOf[NumberFormatException])
  @throws[Exception]
  def testExpression46(): Unit = {
    val expr = "6.02214e2E3"
    new Nothing(expr).build
  }

  // tests for EXP-20: No exception is thrown for unmatched parenthesis in
  // build
  // Thanks go out to maheshkurmi for reporting
  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression48(): Unit = {
    val expr = "(1*2"
    val e = new Nothing(expr).build
    val result = e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression49(): Unit = {
    val expr = "{1*2"
    val e = new Nothing(expr).build
    val result = e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression50(): Unit = {
    val expr = "[1*2"
    val e = new Nothing(expr).build
    val result = e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression51(): Unit = {
    val expr = "(1*{2+[3}"
    val e = new Nothing(expr).build
    val result = e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression52(): Unit = {
    val expr = "(1*(2+(3"
    val e = new Nothing(expr).build
    val result = e.evaluate
  }

  @Test
  @throws[Exception]
  def testExpression53(): Unit = {
    val expr = "14 * 2x"
    val exp = new Nothing(expr).variables("x").build
    exp.setVariable("x", 1.5d)
    assertTrue(exp.validate.isValid)
    assertEquals(14d * 2d * 1.5d, exp.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression54(): Unit = {
    val expr = "2 ((-(x)))"
    val e = new Nothing(expr).variables("x").build
    e.setVariable("x", 1.5d)
    assertEquals(-3d, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression55(): Unit = {
    val expr = "2 sin(x)"
    val e = new Nothing(expr).variables("x").build
    e.setVariable("x", 2d)
    assertTrue(Math.sin(2d) * 2 == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression56(): Unit = {
    val expr = "2 sin(3x)"
    val e = new Nothing(expr).variables("x").build
    e.setVariable("x", 2d)
    assertTrue(Math.sin(6d) * 2d == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample1(): Unit = {
    val e = new Nothing("3 * sin(y) - 2 / (x - 2)").variables("x", "y").build.setVariable("x", 2.3).setVariable("y", 3.14)
    val result = e.evaluate
    val expected = 3 * Math.sin(3.14d) - 2d / (2.3d - 2d)
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample2(): Unit = {
    val exec = Executors.newFixedThreadPool(1)
    val e = new Nothing("3log(y)/(x+1)").variables("x", "y").build.setVariable("x", 2.3).setVariable("y", 3.14)
    val result = e.evaluateAsync(exec)
    val expected = 3 * Math.log(3.14d) / 3.3
    assertEquals(expected, result.get, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample3(): Unit = {
    val result = new Nothing("2cos(xy)").variables("x", "y").build.setVariable("x", 0.5d).setVariable("y", 0.25d).evaluate
    assertEquals(2d * Math.cos(0.5d * 0.25d), result, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample4(): Unit = {
    val expr = "pi+π+e+φ"
    val expected = 2 * Math.PI + Math.E + 1.61803398874d
    val e = new Nothing(expr).build
    assertEquals(expected, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample5(): Unit = {
    val expr = "7.2973525698e-3"
    val expected = expr.toDouble
    val e = new Nothing(expr).build
    assertEquals(expected, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample6(): Unit = {
    val logb = new Nothing(("logb", 2)) {
      def apply(args: Double*): Double = Math.log(args(0)) / Math.log(args(1))
    }
    val result = new Nothing("logb(8, 2)").function(logb).build.evaluate
    val expected = 3
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample7(): Unit = {
    val avg = new Nothing(("avg", 4)) {
      def apply(args: Double*): Double = {
        var sum = 0
        for (arg <- args) {
          sum += arg
        }
        sum / args.length
      }
    }
    val result = new Nothing("avg(1,2,3,4)").function(avg).build.evaluate
    val expected = 2.5d
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample8(): Unit = {
    val factorial = new Nothing(("!", 1, true, Operator.PRECEDENCE_POWER + 1)) {
      def apply(args: Double*): Double = {
        val arg = args(0).toInt
        if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
        if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
        var result = 1
        var i = 1
        while ( {
          i <= arg
        }) {
          result *= i
          {
            i += 1; i - 1
          }
        }
        result
      }
    }
    val result = new Nothing("3!").operator(factorial).build.evaluate
    val expected = 6d
    assertEquals(expected, result, 0d)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample9(): Unit = {
    val gteq = new Nothing((">=", 2, true, Operator.PRECEDENCE_ADDITION - 1)) {
      def apply(values: Array[Double]): Double = if (values(0) >= values(1)) 1d
      else 0d
    }
    var e = new Nothing("1>=2").operator(gteq).build
    assertTrue(0d == e.evaluate)
    e = new Nothing("2>=1").operator(gteq).build
    assertTrue(1d == e.evaluate)
  }

  @Test(expected = classOf[ArithmeticException])
  @throws[Exception]
  def testDocumentationExample10(): Unit = {
    val reciprocal = new Nothing(("$", 1, true, Operator.PRECEDENCE_DIVISION)) {
      def apply(args: Double*): Double = {
        if (args(0) == 0d) throw new ArithmeticException("Division by zero!")
        1d / args(0)
      }
    }
    val e = new Nothing("0$").operator(reciprocal).build
    e.evaluate
  }

  @Test
  @throws[Exception]
  def testDocumentationExample11(): Unit = {
    val e = new Nothing("x").variable("x").build
    var res = e.validate
    assertFalse(res.isValid)
    assertEquals(1, res.getErrors.size)
    e.setVariable("x", 1d)
    res = e.validate
    assertTrue(res.isValid)
  }

  @Test
  @throws[Exception]
  def testDocumentationExample12(): Unit = {
    val e = new Nothing("x").variable("x").build
    val res = e.validate(false)
    assertTrue(res.isValid)
    assertNull(res.getErrors)
  }

  // Thanks go out to Johan Björk for reporting the division by zero problem EXP-22
  // https://www.objecthunter.net/jira/browse/EXP-22
  @Test(expected = classOf[ArithmeticException])
  @throws[Exception]
  def testExpression57(): Unit = {
    val expr = "1 / 0"
    val e = new Nothing(expr).build
    assertTrue(Double.POSITIVE_INFINITY == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression58(): Unit = {
    val expr = "17 * sqrt(-1) * 12"
    val e = new Nothing(expr).build
    assertTrue(Double.isNaN(e.evaluate))
  }

  // Thanks go out to Alex Dolinsky for reporting the missing exception when an empty
  // expression is passed as in new ExpressionBuilder("")
  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression59(): Unit = {
    val e = new Nothing("").build
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testExpression60(): Unit = {
    val e = new Nothing("   ").build
    e.evaluate
  }

  @Test(expected = classOf[ArithmeticException])
  @throws[Exception]
  def testExpression61(): Unit = {
    val e = new Nothing("14 % 0").build
    e.evaluate
  }

  // https://www.objecthunter.net/jira/browse/EXP-24
  // thanks go out to Rémi for the issue report
  @Test
  @throws[Exception]
  def testExpression62(): Unit = {
    val e = new Nothing("x*1.0e5+5").variables("x").build.setVariable("x", Math.E)
    assertTrue(Math.E * 1.0 * Math.pow(10, 5) + 5 == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression63(): Unit = {
    val e = new Nothing("log10(5)").build
    assertEquals(Math.log10(5), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression64(): Unit = {
    val e = new Nothing("log2(5)").build
    assertEquals(Math.log(5) / Math.log(2), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression65(): Unit = {
    val e = new Nothing("2log(e)").variables("e").build.setVariable("e", Math.E)
    assertEquals(2d, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression66(): Unit = {
    val e = new Nothing("log(e)2").variables("e").build.setVariable("e", Math.E)
    assertEquals(2d, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression67(): Unit = {
    val e = new Nothing("2esin(pi/2)").variables("e", "pi").build.setVariable("e", Math.E).setVariable("pi", Math.PI)
    assertEquals(2 * Math.E * Math.sin(Math.PI / 2d), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression68(): Unit = {
    val e = new Nothing("2x").variables("x").build.setVariable("x", Math.E)
    assertEquals(2 * Math.E, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression69(): Unit = {
    val e = new Nothing("2x2").variables("x").build.setVariable("x", Math.E)
    assertEquals(4 * Math.E, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression70(): Unit = {
    val e = new Nothing("2xx").variables("x").build.setVariable("x", Math.E)
    assertEquals(2 * Math.E * Math.E, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression71(): Unit = {
    val e = new Nothing("x2x").variables("x").build.setVariable("x", Math.E)
    assertEquals(2 * Math.E * Math.E, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression72(): Unit = {
    val e = new Nothing("2cos(x)").variables("x").build.setVariable("x", Math.E)
    assertEquals(2 * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression73(): Unit = {
    val e = new Nothing("cos(x)2").variables("x").build.setVariable("x", Math.E)
    assertEquals(2 * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression74(): Unit = {
    val e = new Nothing("cos(x)(-2)").variables("x").build.setVariable("x", Math.E)
    assertEquals(-2d * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression75(): Unit = {
    val e = new Nothing("(-2)cos(x)").variables("x").build.setVariable("x", Math.E)
    assertEquals(-2d * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression76(): Unit = {
    val e = new Nothing("(-x)cos(x)").variables("x").build.setVariable("x", Math.E)
    assertEquals(-E * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression77(): Unit = {
    val e = new Nothing("(-xx)cos(x)").variables("x").build.setVariable("x", Math.E)
    assertEquals(-E * E * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression78(): Unit = {
    val e = new Nothing("(xx)cos(x)").variables("x").build.setVariable("x", Math.E)
    assertEquals(E * E * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression79(): Unit = {
    val e = new Nothing("cos(x)(xx)").variables("x").build.setVariable("x", Math.E)
    assertEquals(E * E * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression80(): Unit = {
    val e = new Nothing("cos(x)(xy)").variables("x", "y").build.setVariable("x", Math.E).setVariable("y", Math.sqrt(2))
    assertEquals(sqrt(2) * E * Math.cos(Math.E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression81(): Unit = {
    val e = new Nothing("cos(xy)").variables("x", "y").build.setVariable("x", Math.E).setVariable("y", Math.sqrt(2))
    assertEquals(cos(sqrt(2) * E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression82(): Unit = {
    val e = new Nothing("cos(2x)").variables("x").build.setVariable("x", Math.E)
    assertEquals(cos(2 * E), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression83(): Unit = {
    val e = new Nothing("cos(xlog(xy))").variables("x", "y").build.setVariable("x", Math.E).setVariable("y", Math.sqrt(2))
    assertEquals(cos(E * log(E * sqrt(2))), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression84(): Unit = {
    val e = new Nothing("3x_1").variables("x_1").build.setVariable("x_1", Math.E)
    assertEquals(3d * E, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testExpression85(): Unit = {
    val e = new Nothing("1/2x").variables("x").build.setVariable("x", 6)
    assertEquals(3d, e.evaluate, 0d)
  }

  // thanks got out to David Sills
  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testSpaceBetweenNumbers(): Unit = {
    val e = new Nothing("1 1").build
  }

  // thanks go out to Janny for providing the tests and the bug report
  @Test
  @throws[Exception]
  def testUnaryMinusInParenthesisSpace(): Unit = {
    val b = new Nothing("( -1)^2")
    val calculated = b.build.evaluate
    assertTrue(calculated == 1d)
  }

  @Test
  @throws[Exception]
  def testUnaryMinusSpace(): Unit = {
    val b = new Nothing(" -1 + 2")
    val calculated = b.build.evaluate
    assertTrue(calculated == 1d)
  }

  @Test
  @throws[Exception]
  def testUnaryMinusSpaces(): Unit = {
    val b = new Nothing(" -1 + + 2 +   -   1")
    val calculated = b.build.evaluate
    assertTrue(calculated == 0d)
  }

  @Test
  @throws[Exception]
  def testUnaryMinusSpace1(): Unit = {
    val b = new Nothing("-1")
    val calculated = b.build.evaluate
    assertTrue(calculated == -1d)
  }

  @Test
  @throws[Exception]
  def testExpression4(): Unit = {
    var expr = null
    var expected = .0
    expr = "2+4 * 5"
    expected = 2 + 4 * 5
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression5(): Unit = {
    var expr = null
    var expected = .0
    expr = "(2+4)*5"
    expected = (2 + 4) * 5
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression6(): Unit = {
    var expr = null
    var expected = .0
    expr = "(2+4)*5 + 2.5*2"
    expected = (2 + 4) * 5 + 2.5 * 2
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression7(): Unit = {
    var expr = null
    var expected = .0
    expr = "(2+4)*5 + 10/2"
    expected = (2 + 4) * 5 + 10 / 2
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression8(): Unit = {
    var expr = null
    var expected = .0
    expr = "(2 * 3 +4)*5 + 10/2"
    expected = (2 * 3 + 4) * 5 + 10 / 2
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testExpression9(): Unit = {
    var expr = null
    var expected = .0
    expr = "(2 * 3 +4)*5 +4 + 10/2"
    expected = (2 * 3 + 4) * 5 + 4 + 10 / 2
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testFailUnknownFunction1(): Unit = {
    var expr = null
    expr = "lig(1)"
    val e = new Nothing(expr).build
    e.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testFailUnknownFunction2(): Unit = {
    var expr = null
    expr = "galength(1)"
    new Nothing(expr).build.evaluate
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testFailUnknownFunction3(): Unit = {
    var expr = null
    expr = "tcos(1)"
    val exp = new Nothing(expr).build
    val result = exp.evaluate
    System.out.println(result)
  }

  @Test
  @throws[Exception]
  def testFunction22(): Unit = {
    var expr = null
    expr = "cos(cos_1)"
    val e = new Nothing(expr).variables("cos_1").build.setVariable("cos_1", 1d)
    assertTrue(e.evaluate eq Math.cos(1d))
  }

  @Test
  @throws[Exception]
  def testFunction23(): Unit = {
    var expr = null
    expr = "log1p(1)"
    val e = new Nothing(expr).build
    assertEquals(log1p(1d), e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testFunction24(): Unit = {
    var expr = null
    expr = "pow(3,3)"
    val e = new Nothing(expr).build
    assertEquals(27d, e.evaluate, 0d)
  }

  @Test
  @throws[Exception]
  def testPostfix1(): Unit = {
    var expr = null
    var expected = .0
    expr = "2.2232^0.1"
    expected = Math.pow(2.2232d, 0.1d)
    val actual = new Nothing(expr).build.evaluate
    assertTrue(expected == actual)
  }

  @Test
  @throws[Exception]
  def testPostfixEverything(): Unit = {
    var expr = null
    var expected = .0
    expr = "(sin(12) + log(34)) * 3.42 - cos(2.234-log(2))"
    expected = (Math.sin(12) + Math.log(34)) * 3.42 - Math.cos(2.234 - Math.log(2))
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testPostfixExponentation1(): Unit = {
    var expr = null
    var expected = .0
    expr = "2^3"
    expected = Math.pow(2, 3)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testPostfixExponentation2(): Unit = {
    var expr = null
    var expected = .0
    expr = "24 + 4 * 2^3"
    expected = 24 + 4 * Math.pow(2, 3)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testPostfixExponentation3(): Unit = {
    var expr = null
    var expected = .0
    val x = 4.334d
    expr = "24 + 4 * 2^x"
    expected = 24 + 4 * Math.pow(2, x)
    val e = new Nothing(expr).variables("x").build.setVariable("x", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testPostfixExponentation4(): Unit = {
    var expr = null
    var expected = .0
    val x = 4.334d
    expr = "(24 + 4) * 2^log(x)"
    expected = (24 + 4) * Math.pow(2, Math.log(x))
    val e = new Nothing(expr).variables("x").build.setVariable("x", x)
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testPostfixFunction1(): Unit = {
    var expr = null
    var expected = .0
    expr = "log(1) * sin(0)"
    expected = Math.log(1) * Math.sin(0)
    val e = new Nothing(expr).build
    assertTrue(expected == e.evaluate)
  }

  @Test
  @throws[Exception]
  def testPostfixFunction10(): Unit = {
    var expr = null
    var expected = .0
    expr = "cbrt(x)"
    val e = new Nothing(expr).variables("x").build
    var x = -10
    while ( {
      x < 10
    }) {
      expected = Math.cbrt(x)
      assertTrue(expected == e.setVariable("x", x).evaluate)
      x = x + 0.5d
    }
  }

  @Test
  @throws[Exception]
  def testPostfixFunction11(): Unit = {
    var expr = null
    var expected = .0
    expr = "cos(x) - (1/cbrt(x))"
    val e = new Nothing(expr).variables("x").build
    var x = -10
    while ( {
      x < 10
    }) if (x == 0d) {
      continue //todo: continue is not supported}
      expected = Math.cos(x) - (1 / Math.cbrt(x))
      assertTrue(expected == e.setVariable("x", x).evaluate)
      x = x + 0.5d
    }

    @Test
    @throws[Exception]
    def testPostfixFunction12() = {
      var expr = null
      var expected = .0
      expr = "acos(x) * expm1(asin(x)) - exp(atan(x)) + floor(x) + cosh(x) - sinh(cbrt(x))"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.acos(x) * Math.expm1(Math.asin(x)) - Math.exp(Math.atan(x)) + Math.floor(x) + Math.cosh(x) - Math.sinh(Math.cbrt(x))
        if (Double.isNaN(expected)) assertTrue(Double.isNaN(e.setVariable("x", x).evaluate))
        else assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction13() = {
      var expr = null
      var expected = .0
      expr = "acos(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.acos(x)
        if (Double.isNaN(expected)) assertTrue(Double.isNaN(e.setVariable("x", x).evaluate))
        else assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction14() = {
      var expr = null
      var expected = .0
      expr = " expm1(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.expm1(x)
        if (Double.isNaN(expected)) assertTrue(Double.isNaN(e.setVariable("x", x).evaluate))
        else assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction15() = {
      var expr = null
      var expected = .0
      expr = "asin(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.asin(x)
        if (Double.isNaN(expected)) assertTrue(Double.isNaN(e.setVariable("x", x).evaluate))
        else assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction16() = {
      var expr = null
      var expected = .0
      expr = " exp(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.exp(x)
        assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction17() = {
      var expr = null
      var expected = .0
      expr = "floor(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.floor(x)
        assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction18() = {
      var expr = null
      var expected = .0
      expr = " cosh(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.cosh(x)
        assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction19() = {
      var expr = null
      var expected = .0
      expr = "sinh(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.sinh(x)
        assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction20() = {
      var expr = null
      var expected = .0
      expr = "cbrt(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.cbrt(x)
        assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction21() = {
      var expr = null
      var expected = .0
      expr = "tanh(x)"
      val e = new Nothing(expr).variables("x").build
      var x = -10
      while ( {
        x < 10
      }) {
        expected = Math.tanh(x)
        assertTrue(expected == e.setVariable("x", x).evaluate)
        x = x + 0.5d
      }
    }

    @Test
    @throws[Exception]
    def testPostfixFunction2() = {
      var expr = null
      var expected = .0
      expr = "log(1)"
      expected = 0d
      val e = new Nothing(expr).build
      assertTrue(expected == e.evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixFunction3() = {
      var expr = null
      var expected = .0
      expr = "sin(0)"
      expected = 0d
      val e = new Nothing(expr).build
      assertTrue(expected == e.evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixFunction5() = {
      var expr = null
      var expected = .0
      expr = "ceil(2.3) +1"
      expected = Math.ceil(2.3) + 1
      val e = new Nothing(expr).build
      assertTrue(expected == e.evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixFunction6() = {
      var expr = null
      var expected = .0
      val x = 1.565d
      val y = 2.1323d
      expr = "ceil(x) + 1 / y * abs(1.4)"
      expected = Math.ceil(x) + 1 / y * Math.abs(1.4)
      val e = new Nothing(expr).variables("x", "y").build
      assertTrue(expected == e.setVariable("x", x).setVariable("y", y).evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixFunction7() = {
      var expr = null
      var expected = .0
      val x = Math.E
      expr = "tan(x)"
      expected = Math.tan(x)
      val e = new Nothing(expr).variables("x").build
      assertTrue(expected == e.setVariable("x", x).evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixFunction8() = {
      var expr = null
      var expected = .0
      val varE = Math.E
      expr = "2^3.4223232 + tan(e)"
      expected = Math.pow(2, 3.4223232d) + Math.tan(Math.E)
      val e = new Nothing(expr).variables("e").build
      assertTrue(expected == e.setVariable("e", varE).evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixFunction9() = {
      var expr = null
      var expected = .0
      val x = Math.E
      expr = "cbrt(x)"
      expected = Math.cbrt(x)
      val e = new Nothing(expr).variables("x").build
      assertTrue(expected == e.setVariable("x", x).evaluate)
    }

    @Test(expected = classOf[IllegalArgumentException])
    @throws[Exception]
    def testPostfixInvalidVariableName() = {
      var expr = null
      var expected = .0
      val x = 4.5334332d
      val log = Math.PI
      expr = "x * pi"
      expected = x * log
      val e = new Nothing(expr).variables("x", "pi").build
      assertTrue(expected == e.setVariable("x", x).setVariable("log", log).evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixParanthesis() = {
      var expr = null
      var expected = .0
      expr = "(3 + 3 * 14) * (2 * (24-17) - 14)/((34) -2)"
      expected = (3 + 3 * 14) * (2 * (24 - 17) - 14) / (34 - 2)
      val e = new Nothing(expr).build
      assertTrue(expected == e.evaluate)
    }

    @Test
    @throws[Exception]
    def testPostfixVariables() = {
      var expr = null
      var expected = .0
      val x = 4.5334332d
      val pi = Math.PI
      expr = "x * pi"
      expected = x * pi
      val e = new Nothing(expr).variables("x", "pi").build
      assertTrue(expected == e.setVariable("x", x).setVariable("pi", pi).evaluate)
    }

    @Test
    @throws[Exception]
    def testUnicodeVariable1() = {
      val e = new Nothing("λ").variable("λ").build.setVariable("λ", E)
      assertEquals(E, e.evaluate, 0d)
    }

    @Test
    @throws[Exception]
    def testUnicodeVariable2() = {
      val e = new Nothing("log(3ε+1)").variable("ε").build.setVariable("ε", E)
      assertEquals(log(3 * E + 1), e.evaluate, 0d)
    }

    @Test
    @throws[Exception]
    def testUnicodeVariable3() = {
      val log = new Nothing(("λωγ", 1)) {
        def apply(args: Double*): Double = log(args(0))
      }
      val e = new Nothing("λωγ(π)").variable("π").function(log).build.setVariable("π", PI)
      assertEquals(log(PI), e.evaluate, 0d)
    }

    @Test
    @throws[Exception]
    def testUnicodeVariable4() = {
      val log = new Nothing(("λ_ωγ", 1)) {
        def apply(args: Double*): Double = log(args(0))
      }
      val e = new Nothing("3λ_ωγ(πε6)").variables("π", "ε").function(log).build.setVariable("π", PI).setVariable("ε", E)
      assertEquals(3 * log(PI * E * 6), e.evaluate, 0d)
    }

    @Test(expected = classOf[IllegalArgumentException])
    @throws[Exception]
    def testImplicitMulitplicationOffNumber() = {
      val e = new Nothing("var_12").variable("var_1").implicitMultiplication(false).build
      e.evaluate
    }

    @Test(expected = classOf[IllegalArgumentException])
    @throws[Exception]
    def testImplicitMulitplicationOffVariable() = {
      val e = new Nothing("var_1var_1").variable("var_1").implicitMultiplication(false).build
      e.evaluate
    }

    @Test(expected = classOf[IllegalArgumentException])
    @throws[Exception]
    def testImplicitMulitplicationOffParantheses() = {
      val e = new Nothing("var_1(2)").variable("var_1").implicitMultiplication(false).build
      e.evaluate
    }

    @Test(expected = classOf[IllegalArgumentException])
    @throws[Exception]
    def testImplicitMulitplicationOffFunction() = {
      val e = new Nothing("var_1log(2)").variable("var_1").implicitMultiplication(false).build.setVariable("var_1", 2)
      e.evaluate
    }

    @Test
    @throws[Exception]
    def testImplicitMulitplicationOnNumber() = {
      val e = new Nothing("var_12").variable("var_1").build.setVariable("var_1", 2)
      assertEquals(4d, e.evaluate, 0d)
    }

    @Test
    @throws[Exception]
    def testImplicitMulitplicationOnVariable() = {
      val e = new Nothing("var_1var_1").variable("var_1").build.setVariable("var_1", 2)
      assertEquals(4d, e.evaluate, 0d)
    }

    @Test
    @throws[Exception]
    def testImplicitMulitplicationOnParantheses() = {
      val e = new Nothing("var_1(2)").variable("var_1").build.setVariable("var_1", 2)
      assertEquals(4d, e.evaluate, 0d)
    }

    @Test
    @throws[Exception]
    def testImplicitMulitplicationOnFunction() = {
      val e = new Nothing("var_1log(2)").variable("var_1").build.setVariable("var_1", 2)
      assertEquals(2 * log(2), e.evaluate, 0d)
    }

    // thanks go out to vandanagopal for reporting the issue
    // https://github.com/fasseg/exp4j/issues/23
    @Test
    @throws[Exception]
    def testSecondArgumentNegative() = {
      val round = new Nothing(("MULTIPLY", 2)) {
        def apply(args: Double*): Double = args(0) * args(1).round
      }
      val result = new Nothing("MULTIPLY(2,-1)").function(round).build.evaluate
      assertEquals(-2d, result, 0d)
    }

    // Test for https://github.com/fasseg/exp4j/issues/65
    @Test
    @throws[Exception]
    def testVariableWithDot() = {
      val result = new Nothing("2*SALARY.Basic").variable("SALARY.Basic").build.setVariable("SALARY.Basic", 1.5d).evaluate
      assertEquals(3d, result, 0d)
    }

    @Test
    @throws[Exception]
    def testTwoAdjacentOperators() = {
      val factorial = new Nothing(("!", 1, true, Operator.PRECEDENCE_POWER + 1)) {
        def apply(args: Double*): Double = {
          val arg = args(0).toInt
          if (arg.toDouble != args(0)) throw new IllegalArgumentException("Operand for factorial has to be an integer")
          if (arg < 0) throw new IllegalArgumentException("The operand of the factorial can not be less than zero")
          var result = 1
          var i = 1
          while ( {
            i <= arg
          }) {
            result *= i
            {
              i += 1; i - 1
            }
          }
          result
        }
      }
      val result = new Nothing("3!+2").operator(factorial).build.evaluate
      val expected = 8d
      assertEquals(expected, result, 0d)
    }

    @Test
    @throws[Exception]
    def testGetVariableNames1() = {
      val e = new Nothing("b*a-9.24c").variables("b", "a", "c").build
      val variableNames = e.getVariableNames
      assertTrue(variableNames.contains("a"))
      assertTrue(variableNames.contains("b"))
      assertTrue(variableNames.contains("c"))
    }

    @Test
    @throws[Exception]
    def testGetVariableNames2() = {
      val e = new Nothing("log(bar)-FOO.s/9.24c").variables("bar", "FOO.s", "c").build
      val variableNames = e.getVariableNames
      assertTrue(variableNames.contains("bar"))
      assertTrue(variableNames.contains("FOO.s"))
      assertTrue(variableNames.contains("c"))
    }

    @Test(expected = classOf[IllegalArgumentException]) def testSameVariableAndBuiltinFunctionName() = {
      val e = new Nothing("log10(log10)").variables("log10").build
    }

    @Test(expected = classOf[IllegalArgumentException]) def testSameVariableAndUserFunctionName() = {
      val e = new Nothing("2*tr+tr(2)").variables("tr").function(new Nothing("tr") {
        def apply(args: Double*) = 0
      }).build
    }

    @Test def testSignum() = {
      var e = new Nothing("signum(1)").build
      assertEquals(1, e.evaluate, 0d)
      e = new Nothing("signum(-1)").build
      assertEquals(-1, e.evaluate, 0d)
      e = new Nothing("signum(--1)").build
      assertEquals(1, e.evaluate, 0d)
      e = new Nothing("signum(+-1)").build
      assertEquals(-1, e.evaluate, 0d)
      e = new Nothing("-+1").build
      assertEquals(-1, e.evaluate, 0d)
      e = new Nothing("signum(-+1)").build
      assertEquals(-1, e.evaluate, 0d)
    }

    @Test def testCustomPercent() = {
      val percentage = new Nothing(("percentage", 2)) {
        def apply(args: Double*): Double = {
          val `val` = args(0)
          val percent = args(1)
          if (percent < 0) `val` - `val` * Math.abs(percent) / 100d
          else `val` - `val` * percent / 100d
        }
      }
      var e = new Nothing("percentage(1000,-10)").function(percentage).build
      assertEquals(0d, 900, e.evaluate)
      e = new Nothing("percentage(1000,12)").function(percentage).build
      assertEquals(0d, 1000d * 0.12d, e.evaluate)
    }
  }
