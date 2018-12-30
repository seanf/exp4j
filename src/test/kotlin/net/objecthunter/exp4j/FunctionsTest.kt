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

import net.objecthunter.exp4j.function.Function
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class FunctionsTest {

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testFunctionNameEmpty() {
        val f = object : Function("") {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFunctionNameZeroArgs() {
        val f = object : Function("foo", 0) {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
        assertEquals(0.0, f.apply(), 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testFunctionNameNegativeArgs() {
        val f = object : Function("foo", -1) {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalFunctionName1() {
        val f = object : Function("1foo") {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalFunctionName2() {
        val f = object : Function("_&oo") {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalFunctionName3() {
        val f = object : Function("o+o") {
            override fun apply(vararg args: Double): Double {
                return 0.0
            }
        }
    }

    @Test
    fun testGetAllowedFunctionChars() {
        val chars = Function.allowedFunctionCharacters
        assertEquals(53, chars.size.toLong())
        chars.sort()
        assertTrue(chars.binarySearch('a') > -1)
        assertTrue(chars.binarySearch('b') > -1)
        assertTrue(chars.binarySearch('c') > -1)
        assertTrue(chars.binarySearch('d') > -1)
        assertTrue(chars.binarySearch('e') > -1)
        assertTrue(chars.binarySearch('f') > -1)
        assertTrue(chars.binarySearch('g') > -1)
        assertTrue(chars.binarySearch('h') > -1)
        assertTrue(chars.binarySearch('i') > -1)
        assertTrue(chars.binarySearch('j') > -1)
        assertTrue(chars.binarySearch('k') > -1)
        assertTrue(chars.binarySearch('l') > -1)
        assertTrue(chars.binarySearch('m') > -1)
        assertTrue(chars.binarySearch('n') > -1)
        assertTrue(chars.binarySearch('o') > -1)
        assertTrue(chars.binarySearch('p') > -1)
        assertTrue(chars.binarySearch('q') > -1)
        assertTrue(chars.binarySearch('r') > -1)
        assertTrue(chars.binarySearch('s') > -1)
        assertTrue(chars.binarySearch('t') > -1)
        assertTrue(chars.binarySearch('u') > -1)
        assertTrue(chars.binarySearch('v') > -1)
        assertTrue(chars.binarySearch('w') > -1)
        assertTrue(chars.binarySearch('x') > -1)
        assertTrue(chars.binarySearch('y') > -1)
        assertTrue(chars.binarySearch('z') > -1)
        assertTrue(chars.binarySearch('A') > -1)
        assertTrue(chars.binarySearch('B') > -1)
        assertTrue(chars.binarySearch('C') > -1)
        assertTrue(chars.binarySearch('D') > -1)
        assertTrue(chars.binarySearch('E') > -1)
        assertTrue(chars.binarySearch('F') > -1)
        assertTrue(chars.binarySearch('G') > -1)
        assertTrue(chars.binarySearch('H') > -1)
        assertTrue(chars.binarySearch('I') > -1)
        assertTrue(chars.binarySearch('J') > -1)
        assertTrue(chars.binarySearch('K') > -1)
        assertTrue(chars.binarySearch('L') > -1)
        assertTrue(chars.binarySearch('M') > -1)
        assertTrue(chars.binarySearch('N') > -1)
        assertTrue(chars.binarySearch('O') > -1)
        assertTrue(chars.binarySearch('P') > -1)
        assertTrue(chars.binarySearch('Q') > -1)
        assertTrue(chars.binarySearch('R') > -1)
        assertTrue(chars.binarySearch('S') > -1)
        assertTrue(chars.binarySearch('T') > -1)
        assertTrue(chars.binarySearch('U') > -1)
        assertTrue(chars.binarySearch('V') > -1)
        assertTrue(chars.binarySearch('W') > -1)
        assertTrue(chars.binarySearch('X') > -1)
        assertTrue(chars.binarySearch('Y') > -1)
        assertTrue(chars.binarySearch('Z') > -1)
        assertTrue(chars.binarySearch('_') > -1)
    }

    @Test
    fun testCheckFunctionNames() {
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
        assertFalse(Function.isValidFunctionName("del\$a"))
    }
}
