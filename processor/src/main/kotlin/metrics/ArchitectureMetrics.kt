package com.example.processor.metrics

data class ModuleMetrics(
    val moduleName: String,
    val classCount: Int,
    val dependencyCount: Int
)

data class ClassMetrics(
    val className: String,
    val incomingDependencies: Int,
    val outgoingDependencies: Int,
    val methodCount: Int,
    val isHotspot: Boolean
)