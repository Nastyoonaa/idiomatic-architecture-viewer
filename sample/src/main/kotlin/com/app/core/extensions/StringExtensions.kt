package com.app.core.extensions

import uml.UmlDiagram

@UmlDiagram
object StringExtensions {

    fun normalize(
        value: String
    ): String =
        value.trim()
}
