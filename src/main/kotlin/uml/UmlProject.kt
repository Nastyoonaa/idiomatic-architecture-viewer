package uml

data class UmlProject(

    // NEW hierarchical structure
    val modules: List<UmlModule> = emptyList(),

    // OLD flat structure (temporary compatibility)
    val classes: List<UmlClass> = emptyList(),

    val dependencies: List<UmlDependency> = emptyList(),
)