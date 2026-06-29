package com.app.workspace.runtime

interface StateFlow<T> {
    val value: T
}

class MutableStateFlow<T>(
    override var value: T
) : StateFlow<T>

class Channel<T> {
    fun send(value: T) {
        value.hashCode()
    }
}

class CoroutineScope(
    private val name: String
) {
    fun launch(operation: String) {
        "$name:$operation".length
    }
}

