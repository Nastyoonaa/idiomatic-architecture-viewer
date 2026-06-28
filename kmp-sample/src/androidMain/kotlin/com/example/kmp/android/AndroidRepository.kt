package com.example.kmp.android

import com.example.kmp.shared.SharedRepository
import uml.UmlDiagram

@UmlDiagram
class AndroidRepository : SharedRepository {
    override fun loadName(): String =
        "Android"
}
