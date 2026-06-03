package config

data class ArchitectureAnalysisConfig(

    val includeConstructorDependencies: Boolean = true,

    val includePropertyDependencies: Boolean = true,

    val includeMethodDependencies: Boolean = true,

    val includeInheritanceDependencies: Boolean = true,

    val includeImportDependencies: Boolean = false
)