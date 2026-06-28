package com.example.kmp.ios

import com.example.kmp.shared.SharedRepository
import uml.UmlDiagram

@UmlDiagram
class IosRepository : SharedRepository {
    override fun loadName(): String =
        "iOS"
}
