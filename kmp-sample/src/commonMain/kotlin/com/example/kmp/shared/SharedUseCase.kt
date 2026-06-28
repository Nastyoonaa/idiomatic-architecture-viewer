package com.example.kmp.shared

import uml.UmlDiagram

@UmlDiagram
class SharedUseCase(
    private val repository: SharedRepository
) {
    fun execute(): String =
        repository.loadName()
}
