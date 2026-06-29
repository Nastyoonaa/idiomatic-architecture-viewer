package com.example.kmp.shared

import uml.UmlDiagram

data class SharedPlatformInfo(
    val label: String
)

@UmlDiagram
expect fun platformWorkspaceLabel(
    repository: SharedRepository
): SharedPlatformInfo
