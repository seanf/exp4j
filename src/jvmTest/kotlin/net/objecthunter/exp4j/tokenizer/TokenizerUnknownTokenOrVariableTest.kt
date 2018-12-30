package net.objecthunter.exp4j.tokenizer

import org.junit.Assert
import org.junit.Test


/**
 * This test is to check if [UnknownFunctionOrVariableException] generated when expression
 * contains unknown function or variable contains necessary expected details.
 *
 * @author Bartosz Firyn (sarxos)
 */
class TokenizerUnknownTokenOrVariableTest {

    @Test(expected = UnknownFunctionOrVariableException::class)
    @Throws(Exception::class)
    fun testTokenizationOfUnknownVariable() {
        val tokenizer = Tokenizer("3 + x", null, null, null)
        while (tokenizer.hasNext()) {
            tokenizer.nextToken()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testTokenizationOfUnknownVariable1Details() {

        val tokenizer = Tokenizer("3 + x", null, null, null)
        tokenizer.nextToken() // 3
        tokenizer.nextToken() // +

        try {
            tokenizer.nextToken() // x
            Assert.fail("Variable 'x' should be unknown!")
        } catch (e: UnknownFunctionOrVariableException) {
            Assert.assertEquals("x", e.token)
            Assert.assertEquals(4, e.position.toLong())
            Assert.assertEquals("3 + x", e.expression)
        }

    }

    @Test
    @Throws(Exception::class)
    fun testTokenizationOfUnknownVariable2Details() {

        val tokenizer = Tokenizer("x + 3", null, null, null)

        try {
            tokenizer.nextToken() // x
            Assert.fail("Variable 'x' should be unknown!")
        } catch (e: UnknownFunctionOrVariableException) {
            Assert.assertEquals("x", e.token)
            Assert.assertEquals(0, e.position.toLong())
            Assert.assertEquals("x + 3", e.expression)
        }

    }

    @Test(expected = UnknownFunctionOrVariableException::class)
    @Throws(Exception::class)
    fun testTokenizationOfUnknownFunction() {
        val tokenizer = Tokenizer("3 + p(1)", null, null, null)
        while (tokenizer.hasNext()) {
            tokenizer.nextToken()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testTokenizationOfUnknownFunction1Details() {

        val tokenizer = Tokenizer("3 + p(1)", null, null, null)
        tokenizer.nextToken() // 3
        tokenizer.nextToken() // +

        try {
            tokenizer.nextToken() // p
            Assert.fail("Function 'p' should be unknown!")
        } catch (e: UnknownFunctionOrVariableException) {
            Assert.assertEquals("p", e.token)
            Assert.assertEquals(4, e.position.toLong())
            Assert.assertEquals("3 + p(1)", e.expression)
        }

    }

    @Test
    @Throws(Exception::class)
    fun testTokenizationOfUnknownFunction2Details() {

        val tokenizer = Tokenizer("p(1) + 3", null, null, null)

        try {
            tokenizer.nextToken() // p
            Assert.fail("Function 'p' should be unknown!")
        } catch (e: UnknownFunctionOrVariableException) {
            Assert.assertEquals("p", e.token)
            Assert.assertEquals(0, e.position.toLong())
            Assert.assertEquals("p(1) + 3", e.expression)
        }

    }
}
