package com.example.kmp.shared

import uml.UmlDiagram

@UmlDiagram
interface SharedRepository {
    fun loadName(): String
}
