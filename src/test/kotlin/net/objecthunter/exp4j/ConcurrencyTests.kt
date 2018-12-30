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

import org.junit.Test

import java.text.DateFormat
import java.util.HashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.stream.IntStream

import org.junit.Assert.assertEquals

class ConcurrencyTests {

    @Test
    @Throws(Exception::class)
    fun testFutureEvaluation() {
        val exec = Executors.newFixedThreadPool(10)
        val numTests = 10000
        val correct1 = DoubleArray(numTests)
        val results1 = arrayOfNulls<Future<*>>(numTests)

        val correct2 = DoubleArray(numTests)
        val results2 = arrayOfNulls<Future<*>>(numTests)

        IntStream.range(0, numTests).forEach { i ->
            correct1[i] = Math.sin(2 * Math.PI / (i + 1))
            results1[i] = ExpressionBuilder("sin(2pi/(n+1))")
                    .variables("pi", "n")
                    .build()
                    .setVariable("pi", Math.PI)
                    .setVariable("n", i.toDouble())
                    .evaluateAsync(exec)
            correct2[i] = Math.log(Math.E * Math.PI * (i + 1).toDouble())
            results2[i] = ExpressionBuilder("log(epi(n+1))")
                    .variables("pi", "n", "e")
                    .build()
                    .setVariable("pi", Math.PI)
                    .setVariable("e", Math.E)
                    .setVariable("n", i.toDouble())
                    .evaluateAsync(exec)
        }

        for (i in 0 until numTests) {
            assertEquals(correct1[i], results1[i]!!.get() as Double, 0.0)
            assertEquals(correct2[i], results2[i]!!.get() as Double, 0.0)
        }
    }
}
