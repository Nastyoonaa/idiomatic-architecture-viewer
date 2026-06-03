package export.graph

import architecture.ArchitectureProject

object GraphDataBuilder {

    fun build(
        project: ArchitectureProject
    ): String {

        return buildString {

            appendLine("const graphData = {")

            project.modules.forEach { module ->

                module.sourceSets.forEach { sourceSet ->

                    sourceSet.packages.forEach { pkg ->

                        pkg.classes.forEach { clazz ->

                            val className =
                                clazz.simpleName.asString()

                            val constructorDependencies =
                                clazz.primaryConstructor
                                    ?.parameters
                                    ?.mapNotNull {
                                        it.type
                                            .resolve()
                                            .declaration
                                            .simpleName
                                            .asString()
                                    }
                                    ?: emptyList()

                            val properties =
                                clazz.getAllProperties()
                                    .map {
                                        it.simpleName.asString()
                                    }
                                    .toList()

                            val methods =
                                clazz.getAllFunctions()
                                    .map {
                                        it.simpleName.asString()
                                    }
                                    .filter {
                                        it !in setOf(
                                            "equals",
                                            "hashCode",
                                            "toString"
                                        )
                                    }
                                    .toList()

                            appendLine(
                                """
"$className": {
    packageName: "${pkg.name}",
    moduleName: "${module.name}",
    fileName: "$className.kt",
    dependencies: [${constructorDependencies.joinToString(",") { "\"$it\"" }}],
    properties: [${properties.joinToString(",") { "\"$it\"" }}],
    methods: [${methods.joinToString(",") { "\"$it\"" }}]
},
                                """.trimIndent()
                            )
                        }
                    }
                }
            }

            appendLine("}")
        }
    }
}