package com.example.kmp.shared

import uml.UmlDiagram

@UmlDiagram
fun SharedScreen(
    useCase: SharedUseCase
): String {
    return useCase.execute()
}
