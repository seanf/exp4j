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

import java.util.Formatter
import java.util.Random
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.junit.Test

object PerformanceTest {
  private val BENCH_TIME = 2l
  private val EXPRESSION = "log(x) - y * (sqrt(x^cos(y)))"
}

class PerformanceTest {
  @Test
  @throws[Exception]
  def testBenches(): Unit = {
    val sb = new StringBuffer
    val fmt = new Formatter(sb)
    fmt.format("+------------------------+---------------------------+--------------------------+%n")
    fmt.format("| %-22s | %-25s | %-24s |%n", "Implementation", "Calculations per Second", "Percentage of Math")
    fmt.format("+------------------------+---------------------------+--------------------------+%n")
    System.out.print(sb.toString)
    sb.setLength(0)
    val math = benchJavaMath
    val mathRate = math.toDouble / PerformanceTest.BENCH_TIME.toDouble
    fmt.format("| %-22s | %25.2f | %22.2f %% |%n", "Java Math", mathRate, 100f)
    System.out.print(sb.toString)
    sb.setLength(0)
    val db = benchDouble
    val dbRate = db.toDouble / PerformanceTest.BENCH_TIME.toDouble
    fmt.format("| %-22s | %25.2f | %22.2f %% |%n", "exp4j", dbRate, dbRate * 100 / mathRate)
    System.out.print(sb.toString)
    sb.setLength(0)
    val js = benchJavaScript
    val jsRate = js.toDouble / PerformanceTest.BENCH_TIME.toDouble
    fmt.format("| %-22s | %25.2f | %22.2f %% |%n", "JSR-223 (Java Script)", jsRate, jsRate * 100 / mathRate)
    fmt.format("+------------------------+---------------------------+--------------------------+%n")
    System.out.print(sb.toString)
  }

  private def benchDouble = {
    val expression = new Nothing(PerformanceTest.EXPRESSION).variables("x", "y").build
    var `val` = .0
    val rnd = new Random
    val timeout = PerformanceTest.BENCH_TIME
    val time = System.currentTimeMillis + (1000 * timeout)
    var count = 0
    while ( {
      time > System.currentTimeMillis
    }) {
      expression.setVariable("x", rnd.nextDouble)
      expression.setVariable("y", rnd.nextDouble)
      `val` = expression.evaluate
      count += 1
    }
    val rate = count / timeout
    count
  }

  private def benchJavaMath = {
    val timeout = PerformanceTest.BENCH_TIME
    val time = System.currentTimeMillis + (1000 * timeout)
    var x = .0
    var y = .0
    var `val` = .0
    var rate = .0
    var count = 0
    val rnd = new Random
    while ( {
      time > System.currentTimeMillis
    }) {
      x = rnd.nextDouble
      y = rnd.nextDouble
      `val` = Math.log(x) - y * Math.sqrt(Math.pow(x, Math.cos(y)))
      count += 1
    }
    rate = count / timeout
    count
  }

  @throws[Exception]
  private def benchJavaScript: Int = {
    val mgr = new ScriptEngineManager
    val engine = mgr.getEngineByName("JavaScript")
    val timeout = PerformanceTest.BENCH_TIME
    var time = System.currentTimeMillis + (1000 * timeout)
    var x = .0
    var y = .0
    val `val` = .0
    var rate = .0
    var count = 0
    val rnd = new Random
    if (engine == null) {
      System.err.println("Unable to instantiate javascript engine. skipping naive JS bench.")
      return -1
    }
    else {
      time = System.currentTimeMillis + (1000 * timeout)
      count = 0
      while ( {
        time > System.currentTimeMillis
      }) {
        x = rnd.nextDouble
        y = rnd.nextDouble
        engine.eval("Math.log(" + x + ") - " + y + "* (Math.sqrt(" + x + "^Math.cos(" + y + ")))")
        count += 1
      }
      rate = count / timeout
    }
    count
  }
}
