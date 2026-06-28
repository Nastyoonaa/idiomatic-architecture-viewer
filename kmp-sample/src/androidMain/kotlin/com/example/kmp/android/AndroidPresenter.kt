package com.example.kmp.android

import com.example.kmp.shared.SharedUseCase
import uml.UmlDiagram

@UmlDiagram
class AndroidPresenter(
    private val useCase: SharedUseCase
) {
    fun title(): String =
        useCase.execute()
}
