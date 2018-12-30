package net.objecthunter.exp4j.tokenizer

/**
 * This exception is being thrown whenever [Tokenizer] finds unknown function or variable.
 *
 * @author Bartosz Firyn (sarxos)
 */
class UnknownFunctionOrVariableException(
        /**
         * @return Expression which contains unknown function or variable
         */
        val expression: String,
        /**
         * @return The position of unknown function or variable
         */
        val position: Int, length: Int) : IllegalArgumentException() {

    private val _message: String

    /**
     * @return The name of unknown function or variable
     */
    val token: String

    init {
        this.token = token(expression, position, length)
        this._message = "Unknown function or variable '$token' at pos $position in expression '$expression'"
    }

    override val message: String?
        get() = _message

    companion object {

        /**
         * Serial version UID.
         */
        private val serialVersionUID = 1L

        private fun token(expression: String, position: Int, length: Int): String {

            val len = expression.length
            var end = position + length - 1

            if (len < end) {
                end = len
            }

            return expression.substring(position, end)
        }
    }
}
