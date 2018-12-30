package net.objecthunter.exp4j.multiplatform

actual fun Double.cbrt(): Double {
    @Suppress("UNUSED_VARIABLE") val n: Double = this
    return js("Math.cbrt(n)") as Double
}
