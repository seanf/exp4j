package net.objecthunter.exp4j.tokenizer

import org.junit.Assert
import org.junit.Test

/**
  * This test is to check if {@link UnknownFunctionOrVariableException} generated when expression
  * contains unknown function or variable contains necessary expected details.
  *
  * @author Bartosz Firyn (sarxos)
  */
class TokenizerUnknownTokenOrVariableTest {
  @Test(expected = classOf[UnknownFunctionOrVariableException])
  @throws[Exception]
  def testTokenizationOfUnknownVariable(): Unit = {
    val tokenizer = new Tokenizer("3 + x", null, null, null)
    while ( {
      tokenizer.hasNext
    }) tokenizer.nextToken
  }

  @Test
  @throws[Exception]
  def testTokenizationOfUnknownVariable1Details(): Unit = {
    val tokenizer = new Tokenizer("3 + x", null, null, null)
    tokenizer.nextToken // 3
    tokenizer.nextToken // +
    try {
      tokenizer.nextToken // x
      Assert.fail("Variable 'x' should be unknown!")
    } catch {
      case e: UnknownFunctionOrVariableException =>
        Assert.assertEquals("x", e.getToken)
        Assert.assertEquals(4, e.getPosition)
        Assert.assertEquals("3 + x", e.getExpression)
    }
  }

  @Test
  @throws[Exception]
  def testTokenizationOfUnknownVariable2Details(): Unit = {
    val tokenizer = new Tokenizer("x + 3", null, null, null)
    try {
      tokenizer.nextToken
      Assert.fail("Variable 'x' should be unknown!")
    } catch {
      case e: UnknownFunctionOrVariableException =>
        Assert.assertEquals("x", e.getToken)
        Assert.assertEquals(0, e.getPosition)
        Assert.assertEquals("x + 3", e.getExpression)
    }
  }

  @Test(expected = classOf[UnknownFunctionOrVariableException])
  @throws[Exception]
  def testTokenizationOfUnknownFunction(): Unit = {
    val tokenizer = new Tokenizer("3 + p(1)", null, null, null)
    while ( {
      tokenizer.hasNext
    }) tokenizer.nextToken
  }

  @Test
  @throws[Exception]
  def testTokenizationOfUnknownFunction1Details(): Unit = {
    val tokenizer = new Tokenizer("3 + p(1)", null, null, null)
    tokenizer.nextToken
    tokenizer.nextToken
    try {
      tokenizer.nextToken // p
      Assert.fail("Function 'p' should be unknown!")
    } catch {
      case e: UnknownFunctionOrVariableException =>
        Assert.assertEquals("p", e.getToken)
        Assert.assertEquals(4, e.getPosition)
        Assert.assertEquals("3 + p(1)", e.getExpression)
    }
  }

  @Test
  @throws[Exception]
  def testTokenizationOfUnknownFunction2Details(): Unit = {
    val tokenizer = new Tokenizer("p(1) + 3", null, null, null)
    try {
      tokenizer.nextToken
      Assert.fail("Function 'p' should be unknown!")
    } catch {
      case e: UnknownFunctionOrVariableException =>
        Assert.assertEquals("p", e.getToken)
        Assert.assertEquals(0, e.getPosition)
        Assert.assertEquals("p(1) + 3", e.getExpression)
    }
  }
}
