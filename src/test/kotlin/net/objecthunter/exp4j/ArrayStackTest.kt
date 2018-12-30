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
 */
package net.objecthunter.exp4j

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

import java.util.EmptyStackException
import java.util.stream.IntStream

import org.junit.Test
import java.util.function.IntConsumer

/**
 *
 * @author Federico Vera (dktcoding [at] gmail)
 */
class ArrayStackTest {

    @Test(expected = IllegalArgumentException::class)
    fun testConstructor() {
        val stack = ArrayStack(-1)
    }

    @Test
    fun testPushNoSize() {
        val stack = ArrayStack()

        stack.push(0.0)
        stack.push(1.0)
        stack.push(3.0)

        assertEquals(3, stack.size().toLong())
    }

    @Test
    fun testPushLessSize() {
        val stack = ArrayStack(5)

        IntStream.range(0, 5).forEach(IntConsumer { stack.push(it.toDouble()) })

        assertEquals(5, stack.size().toLong())
    }

    @Test
    fun testPeek() {
        val stack = ArrayStack(5)

        IntStream.range(0, 5).forEach(IntConsumer { stack.push(it.toDouble()) })

        assertEquals(4.0, stack.peek(), 0.0)
        assertEquals(4.0, stack.peek(), 0.0)
        assertEquals(4.0, stack.peek(), 0.0)
    }

    @Test
    fun testPeek2() {
        val stack = ArrayStack(5)
        stack.push(-1.0)
        var old = -1.0
        for (i in 0..4) {
            assertEquals(old, stack.peek(), 0.0)
            stack.push(i.toDouble())
            old = i.toDouble()
            assertEquals(old, stack.peek(), 0.0)
        }
    }

    @Test(expected = EmptyStackException::class)
    fun testPeekNoData() {
        val stack = ArrayStack(5)
        stack.peek()
    }

    @Test
    fun testPop() {
        val stack = ArrayStack(5)

        IntStream.range(0, 5).forEach(IntConsumer { stack.push(it.toDouble()) })

        while (!stack.isEmpty) {
            stack.pop()
        }
    }

    @Test(expected = EmptyStackException::class)
    fun testPop2() {
        val stack = ArrayStack(5)

        IntStream.range(0, 5).forEach(IntConsumer { stack.push(it.toDouble()) })

        while (true) {
            stack.pop()
        }
    }

    @Test
    fun testPop3() {
        val stack = ArrayStack(5)

        IntStream.range(0, 5).forEach { i ->
            stack.push(i.toDouble())
            assertEquals(1, stack.size().toLong())
            assertEquals(i.toDouble(), stack.pop(), 0.0)
        }

        assertEquals(0, stack.size().toLong())
        assertTrue(stack.isEmpty)
    }

    @Test(expected = EmptyStackException::class)
    fun testPopNoData() {
        val stack = ArrayStack(5)
        stack.pop()
    }

    @Test
    fun testIsEmpty() {
        val stack = ArrayStack(5)
        assertTrue(stack.isEmpty)
        stack.push(4.0)
        assertFalse(stack.isEmpty)
        stack.push(4.0)
        assertFalse(stack.isEmpty)
        stack.push(4.0)
        assertFalse(stack.isEmpty)
        stack.pop()
        stack.pop()
        stack.pop()
        assertTrue(stack.isEmpty)
        stack.push(4.0)
        assertFalse(stack.isEmpty)
        stack.peek()
        assertFalse(stack.isEmpty)
        stack.pop()
        assertTrue(stack.isEmpty)
    }

    @Test
    fun testSize() {
        val stack = ArrayStack(5)
        assertEquals(0, stack.size().toLong())
        stack.push(4.0)
        assertEquals(1, stack.size().toLong())
        stack.peek()
        assertEquals(1, stack.size().toLong())
        stack.pop()
        assertEquals(0, stack.size().toLong())
    }

}
