package architecture


data class ArchitectureModule(
    val name: String,
    val sourceSets: List<ArchitectureSourceSet>,
    val dependencies: List<String>
)