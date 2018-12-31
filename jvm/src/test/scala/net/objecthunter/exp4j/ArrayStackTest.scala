/*
 * Copyright 2015 Federico Vera
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
 * Copyright 2015 Federico Vera
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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
  *
  * @author Federico Vera (dktcoding [at] gmail)
  */
class ArrayStackTest() {
  @Test(expected = classOf[IllegalArgumentException]) def testConstructor(): Unit = {
    val stack = new ArrayStack(-1)
  }

  @Test def testPushNoSize(): Unit = {
    val stack = new ArrayStack
    stack.push(0)
    stack.push(1)
    stack.push(3)
    assertEquals(3, stack.size)
  }

  @Test def testPushLessSize(): Unit = {
    val stack = new ArrayStack(5)
    for (i <- 0 to 4) stack.push(i)
    assertEquals(5, stack.size)
  }

  @Test def testPeek(): Unit = {
    val stack = new ArrayStack(5)
    for (i <- 0 to 4) stack.push(i)
    assertEquals(4d, stack.peek, 0d)
    assertEquals(4d, stack.peek, 0d)
    assertEquals(4d, stack.peek, 0d)
  }

  @Test def testPeek2(): Unit = {
    val stack = new ArrayStack(5)
    stack.push(-1)
    var old = -1d

    for (i <- 0 to 4) {
      assertEquals(old, stack.peek, 0d)
      stack.push(i)
      old = i
      assertEquals(old, stack.peek, 0d)
    }
  }

  @Test(expected = classOf[EmptyStackException]) def testPeekNoData(): Unit = {
    val stack = new ArrayStack(5)
    stack.peek
  }

  @Test def testPop(): Unit = {
    val stack = new ArrayStack(5)
    for (i <- 0 to 4) stack.push(i)
    while (!stack.isEmpty) stack.pop
  }

  @Test(expected = classOf[EmptyStackException]) def testPop2(): Unit = {
    val stack = new ArrayStack(5)
    for (i <- 0 to 4) stack.push(i)
    while (true) stack.pop
  }

  @Test def testPop3(): Unit = {
    val stack = new ArrayStack(5)
    for (i <- 0 to 4) {
      stack.push(i)
      assertEquals(1, stack.size)
      assertEquals(i, stack.pop, 0d)
    }
    assertEquals(0, stack.size)
    assertTrue(stack.isEmpty)
  }

  @Test(expected = classOf[EmptyStackException]) def testPopNoData(): Unit = {
    val stack = new ArrayStack(5)
    stack.pop
  }

  @Test def testIsEmpty(): Unit = {
    val stack = new ArrayStack(5)
    assertTrue(stack.isEmpty)
    stack.push(4)
    assertFalse(stack.isEmpty)
    stack.push(4)
    assertFalse(stack.isEmpty)
    stack.push(4)
    assertFalse(stack.isEmpty)
    stack.pop
    stack.pop
    stack.pop
    assertTrue(stack.isEmpty)
    stack.push(4)
    assertFalse(stack.isEmpty)
    stack.peek
    assertFalse(stack.isEmpty)
    stack.pop
    assertTrue(stack.isEmpty)
  }

  @Test def testSize(): Unit = {
    val stack = new ArrayStack(5)
    assertEquals(0, stack.size)
    stack.push(4)
    assertEquals(1, stack.size)
    stack.peek
    assertEquals(1, stack.size)
    stack.pop
    assertEquals(0, stack.size)
  }
}
