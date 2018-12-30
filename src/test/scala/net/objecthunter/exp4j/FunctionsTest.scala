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
import org.junit.Test
import java.util
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class FunctionsTest {
  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testFunctionNameNull(): Unit = {
    val f = new Function(null) {
      def apply(args: Double*) = 0
    }
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testFunctionNameEmpty(): Unit = {
    val f = new Function("") {
      def apply(args: Double*) = 0
    }
  }

  @Test
  @throws[Exception]
  def testFunctionNameZeroArgs(): Unit = {
    val f = new Function("foo", 0) {
      def apply(args: Double*) = 0
    }
    assertEquals(0f, f.apply(), 0f)
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testFunctionNameNegativeArgs(): Unit = {
    val f = new Function("foo", -1) {
      def apply(args: Double*) = 0
    }
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testIllegalFunctionName1(): Unit = {
    val f = new Function("1foo") {
      def apply(args: Double*) = 0
    }
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testIllegalFunctionName2(): Unit = {
    val f = new Function("_&oo") {
      def apply(args: Double*) = 0
    }
  }

  @Test(expected = classOf[IllegalArgumentException])
  @throws[Exception]
  def testIllegalFunctionName3(): Unit = {
    val f = new Function("o+o") {
      def apply(args: Double*) = 0
    }
  }

  @Test def testCheckFunctionNames(): Unit = {
    assertTrue(Function.isValidFunctionName("log"))
    assertTrue(Function.isValidFunctionName("sin"))
    assertTrue(Function.isValidFunctionName("abz"))
    assertTrue(Function.isValidFunctionName("alongfunctionnamecanhappen"))
    assertTrue(Function.isValidFunctionName("_log"))
    assertTrue(Function.isValidFunctionName("__blah"))
    assertTrue(Function.isValidFunctionName("foox"))
    assertTrue(Function.isValidFunctionName("aZ"))
    assertTrue(Function.isValidFunctionName("Za"))
    assertTrue(Function.isValidFunctionName("ZZaa"))
    assertTrue(Function.isValidFunctionName("_"))
    assertTrue(Function.isValidFunctionName("log2"))
    assertTrue(Function.isValidFunctionName("lo32g2"))
    assertTrue(Function.isValidFunctionName("_o45g2"))
    assertFalse(Function.isValidFunctionName("&"))
    assertFalse(Function.isValidFunctionName("_+log"))
    assertFalse(Function.isValidFunctionName("_k&l"))
    assertFalse(Function.isValidFunctionName("k&l"))
    assertFalse(Function.isValidFunctionName("+log"))
    assertFalse(Function.isValidFunctionName("fo-o"))
    assertFalse(Function.isValidFunctionName("log+"))
    assertFalse(Function.isValidFunctionName("perc%"))
    assertFalse(Function.isValidFunctionName("del$a"))
  }
}
