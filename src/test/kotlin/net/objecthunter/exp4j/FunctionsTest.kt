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

import java.util.ArrayList
import java.util.Arrays

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
        Arrays.sort(chars)
        assertTrue(Arrays.binarySearch(chars, 'a') > -1)
        assertTrue(Arrays.binarySearch(chars, 'b') > -1)
        assertTrue(Arrays.binarySearch(chars, 'c') > -1)
        assertTrue(Arrays.binarySearch(chars, 'd') > -1)
        assertTrue(Arrays.binarySearch(chars, 'e') > -1)
        assertTrue(Arrays.binarySearch(chars, 'f') > -1)
        assertTrue(Arrays.binarySearch(chars, 'g') > -1)
        assertTrue(Arrays.binarySearch(chars, 'h') > -1)
        assertTrue(Arrays.binarySearch(chars, 'i') > -1)
        assertTrue(Arrays.binarySearch(chars, 'j') > -1)
        assertTrue(Arrays.binarySearch(chars, 'k') > -1)
        assertTrue(Arrays.binarySearch(chars, 'l') > -1)
        assertTrue(Arrays.binarySearch(chars, 'm') > -1)
        assertTrue(Arrays.binarySearch(chars, 'n') > -1)
        assertTrue(Arrays.binarySearch(chars, 'o') > -1)
        assertTrue(Arrays.binarySearch(chars, 'p') > -1)
        assertTrue(Arrays.binarySearch(chars, 'q') > -1)
        assertTrue(Arrays.binarySearch(chars, 'r') > -1)
        assertTrue(Arrays.binarySearch(chars, 's') > -1)
        assertTrue(Arrays.binarySearch(chars, 't') > -1)
        assertTrue(Arrays.binarySearch(chars, 'u') > -1)
        assertTrue(Arrays.binarySearch(chars, 'v') > -1)
        assertTrue(Arrays.binarySearch(chars, 'w') > -1)
        assertTrue(Arrays.binarySearch(chars, 'x') > -1)
        assertTrue(Arrays.binarySearch(chars, 'y') > -1)
        assertTrue(Arrays.binarySearch(chars, 'z') > -1)
        assertTrue(Arrays.binarySearch(chars, 'A') > -1)
        assertTrue(Arrays.binarySearch(chars, 'B') > -1)
        assertTrue(Arrays.binarySearch(chars, 'C') > -1)
        assertTrue(Arrays.binarySearch(chars, 'D') > -1)
        assertTrue(Arrays.binarySearch(chars, 'E') > -1)
        assertTrue(Arrays.binarySearch(chars, 'F') > -1)
        assertTrue(Arrays.binarySearch(chars, 'G') > -1)
        assertTrue(Arrays.binarySearch(chars, 'H') > -1)
        assertTrue(Arrays.binarySearch(chars, 'I') > -1)
        assertTrue(Arrays.binarySearch(chars, 'J') > -1)
        assertTrue(Arrays.binarySearch(chars, 'K') > -1)
        assertTrue(Arrays.binarySearch(chars, 'L') > -1)
        assertTrue(Arrays.binarySearch(chars, 'M') > -1)
        assertTrue(Arrays.binarySearch(chars, 'N') > -1)
        assertTrue(Arrays.binarySearch(chars, 'O') > -1)
        assertTrue(Arrays.binarySearch(chars, 'P') > -1)
        assertTrue(Arrays.binarySearch(chars, 'Q') > -1)
        assertTrue(Arrays.binarySearch(chars, 'R') > -1)
        assertTrue(Arrays.binarySearch(chars, 'S') > -1)
        assertTrue(Arrays.binarySearch(chars, 'T') > -1)
        assertTrue(Arrays.binarySearch(chars, 'U') > -1)
        assertTrue(Arrays.binarySearch(chars, 'V') > -1)
        assertTrue(Arrays.binarySearch(chars, 'W') > -1)
        assertTrue(Arrays.binarySearch(chars, 'X') > -1)
        assertTrue(Arrays.binarySearch(chars, 'Y') > -1)
        assertTrue(Arrays.binarySearch(chars, 'Z') > -1)
        assertTrue(Arrays.binarySearch(chars, '_') > -1)
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
