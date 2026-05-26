package uml

data class UmlSourceSet(
    val name: String,
    val packages: List<UmlPackage> = emptyList()
)