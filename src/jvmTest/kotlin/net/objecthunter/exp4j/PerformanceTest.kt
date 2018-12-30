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

import javax.script.ScriptEngineManager

import org.junit.Test
import kotlin.random.Random

class PerformanceTest {

    @Test
    @Throws(Exception::class)
    fun testBenches() {
        print("+------------------------+---------------------------+--------------------------+%n".format())
        print("| %-22s | %-25s | %-24s |%n".format("Implementation", "Calculations per Second", "Percentage of Java Math"))
        print("+------------------------+---------------------------+--------------------------+%n".format())

        val math = benchJavaMath()
        val mathRate = math.toDouble() / BENCH_TIME.toDouble()
        print("| %-22s | %25.2f | %22.2f %% |%n".format("Java Math", mathRate, 100f))

        val db = benchDouble()
        val dbRate = db.toDouble() / BENCH_TIME.toDouble()
        print("| %-22s | %25.2f | %22.2f %% |%n".format("exp4j", dbRate, dbRate * 100 / mathRate))

        val js1 = benchJavaScript("Nashorn")
        val js1Rate = js1.toDouble() / BENCH_TIME
        print("| %-22s | %25.2f | %22.2f %% |%n".format("JSR-223 (Nashorn)", js1Rate, js1Rate * 100 / mathRate))

        val js2 = benchJavaScript("rhino")
        val js2Rate = js2.toDouble() / BENCH_TIME
        print("| %-22s | %25.2f | %22.2f %% |%n".format("JSR-223 (Rhino)", js2Rate, js2Rate * 100 / mathRate))
        print("+------------------------+---------------------------+--------------------------+%n".format())

    }

    private val seed = System.currentTimeMillis()

    private fun benchDouble(): Int {
        val expression = ExpressionBuilder(EXPRESSION)
                .variables("x", "y")
                .build()
        var `val`: Double
        val rnd = Random(seed)
        val timeout = BENCH_TIME
        val time = System.currentTimeMillis() + 1000 * timeout
        var count = 0
        while (time > System.currentTimeMillis()) {
            expression.setVariable("x", rnd.nextDouble())
            expression.setVariable("y", rnd.nextDouble())
            `val` = expression.evaluate()
            count++
        }
        val rate = (count / timeout).toDouble()
        return count
    }

    private fun benchJavaMath(): Int {
        val timeout = BENCH_TIME
        val time = System.currentTimeMillis() + 1000 * timeout
        var x: Double
        var y: Double
        var `val`: Double
        val rate: Double
        var count = 0
        val rnd = Random(seed)
        while (time > System.currentTimeMillis()) {
            x = rnd.nextDouble()
            y = rnd.nextDouble()
            `val` = Math.log(x) - y * Math.sqrt(Math.pow(x, Math.cos(y)))
            count++
        }
        rate = (count / timeout).toDouble()
        return count
    }

    @Throws(Exception::class)
    private fun benchJavaScript(engineName: String): Int {
        val mgr = ScriptEngineManager()
        val engine = mgr.getEngineByName(engineName)
        val timeout = BENCH_TIME
        val time = System.currentTimeMillis() + 1000 * timeout
        var x: Double
        var y: Double
        val `val`: Double
        val rate: Double
        var count = 0
        val rnd = Random(seed)
        if (engine == null) {
            System.err.println("Unable to instantiate javascript engine. skipping naive JS bench.")
            return -1
        } else {
            count = 0
            while (time > System.currentTimeMillis()) {
                x = rnd.nextDouble()
                y = rnd.nextDouble()
                // TODO this changes the expression every single time, unlike benchDouble
                engine.eval("Math.log($x) - $y* (Math.sqrt($x^Math.cos($y)))")
                count++
            }
            rate = (count / timeout.toDouble())
        }
        return count
    }

    companion object {

        private val BENCH_TIME = 2L
        private val EXPRESSION = "log(x) - y * (sqrt(x^cos(y)))"
    }

}
