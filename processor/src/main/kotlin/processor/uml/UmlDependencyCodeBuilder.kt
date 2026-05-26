package uml

class UmlDependencyCodeBuilder {

    fun build(
        dependencies: List<String>
    ): String {

        return if (dependencies.isEmpty()) {

            "emptyList()"

        } else {

            dependencies.joinToString(
                ",\n",
                "listOf(\n",
                "\n)"
            )
        }
    }
}