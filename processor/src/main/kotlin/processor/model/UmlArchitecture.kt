package com.example.processor.model


import com.google.devtools.ksp.symbol.KSClassDeclaration

data class UmlProjectNode(
    val modules: List<UmlModuleNode>
)

data class UmlModuleNode(
    val name: String,
    val sourceSets: List<UmlSourceSetNode>
)

data class UmlSourceSetNode(
    val name: String,
    val packages: List<UmlPackageNode>
)

data class UmlPackageNode(
    val name: String,
    val classes: List<KSClassDeclaration>
)