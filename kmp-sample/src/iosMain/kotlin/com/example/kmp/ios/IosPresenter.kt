package com.example.kmp.ios

import com.example.kmp.shared.SharedUseCase
import uml.UmlDiagram

@UmlDiagram
class IosPresenter(
    private val useCase: SharedUseCase
) {
    fun title(): String =
        useCase.execute()
}
