package com.app.validation

import uml.UmlDiagram

@UmlDiagram
class CycleEntry(
    private val dependency: CycleDependency
)

@UmlDiagram
class CycleDependency(
    private val dependency: CycleEntry
)
