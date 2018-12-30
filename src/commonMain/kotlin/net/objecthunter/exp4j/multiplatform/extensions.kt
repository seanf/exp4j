package net.objecthunter.exp4j.multiplatform

internal fun Char.isDigit(): Boolean = this in '0'..'9'

// TODO edge cases?
internal fun Char.isLetter(): Boolean =
    this.toLowerCase() != this.toUpperCase()

expect fun Double.cbrt(): Double
