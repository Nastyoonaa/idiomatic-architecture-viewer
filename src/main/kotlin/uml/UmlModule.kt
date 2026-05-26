package uml


data class UmlModule(
    val name: String,
    val sourceSets: List<UmlSourceSet> = emptyList(),
    val dependencies: List<String> = emptyList()
)