package net.objecthunter.exp4j.tokenizer

/**
  * This exception is being thrown whenever {@link Tokenizer} finds unknown function or variable.
  *
  * @author Bartosz Firyn (sarxos)
  */
@SerialVersionUID(1L)
object UnknownFunctionOrVariableException {
  private def token(expression: String, position: Int, length: Int) = {
    val len = expression.length
    var end = position + length - 1
    if (len < end) end = len
    expression.substring(position, end)
  }
}

@SerialVersionUID(1L)
class UnknownFunctionOrVariableException(val expression: String, val position: Int, val length: Int) extends IllegalArgumentException {
  this.token = UnknownFunctionOrVariableException.token(expression, position, length)
  this.message = "Unknown function or variable '" + token + "' at pos " + position + " in expression '" + expression + "'"
  final private var message = null
  final private var token = null

  override def getMessage: String = message

  /**
    * @return Expression which contains unknown function or variable
    */
  def getExpression: String = expression

  /**
    * @return The name of unknown function or variable
    */
  def getToken: String = token

  /**
    * @return The position of unknown function or variable
    */
  def getPosition: Int = position
}
