package uml

data class UmlPackage(
    val name: String,
    val classes: List<UmlClass> = emptyList()
)