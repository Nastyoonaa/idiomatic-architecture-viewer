package com.example.kmp.shared

import uml.UmlDiagram

@UmlDiagram
actual fun platformWorkspaceLabel(
    repository: SharedRepository
): SharedPlatformInfo {
    return SharedPlatformInfo(
        label = "Android ${repository.loadName()}"
    )
}
