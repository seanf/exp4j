package net.objecthunter.exp4j.shuntingyard

class Stack<T> {
    private val elements = mutableListOf<T>()

    fun empty() = elements.isEmpty()
    fun peek(): T = elements.last()
    fun pop(): T = elements.removeAt(elements.lastIndex)
    fun push(t: T) {
        elements.add(t)
    }
}
