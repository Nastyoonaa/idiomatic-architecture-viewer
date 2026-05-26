package diagram

import architecture.ArchitectureProject
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ArchitectureGraphGenerator {

    fun generate(
        project: ArchitectureProject
    ): String {

        return buildString {

            appendLine("graph TD")
            appendLine()

            //
            // HIERARCHY
            //

            project.modules.forEach { module ->

                val moduleId =
                    module.name.safe()

                appendLine(
                    """$moduleId["${module.name}"]"""
                )

                module.sourceSets.forEach { sourceSet ->

                    val sourceSetId =
                        "${module.name}_${sourceSet.name}"
                            .safe()

                    appendLine(
                        """$sourceSetId["${sourceSet.name}"]"""
                    )

                    appendLine(
                        "$moduleId --> $sourceSetId"
                    )

                    sourceSet.packages.forEach { pkg ->

                        val packageId =
                            pkg.name.safe()

                        appendLine(
                            """$packageId["${pkg.name}"]"""
                        )

                        appendLine(
                            "$sourceSetId --> $packageId"
                        )
                    }
                }
            }

            appendLine()

            //
            // REAL DEPENDENCIES
            //

            val dependencyEdges =
                mutableSetOf<String>()

            project.modules.forEach { module ->

                module.sourceSets.forEach { sourceSet ->

                    sourceSet.packages.forEach { pkg ->

                        pkg.classes.forEach { clazz ->

                            clazz.primaryConstructor
                                ?.parameters
                                .orEmpty()
                                .forEach { parameter ->

                                    val dependencyClass =
                                        parameter.type.resolve()
                                            .declaration as? KSClassDeclaration
                                            ?: return@forEach

                                    val targetPackage =
                                        dependencyClass.packageName
                                            .asString()

                                    if (targetPackage != pkg.name) {

                                        val fromId =
                                            pkg.name.safe()

                                        val toId =
                                            targetPackage.safe()

                                        dependencyEdges +=
                                            "$fromId -.-> $toId"
                                    }
                                }
                        }
                    }
                }
            }

            dependencyEdges.forEach {
                appendLine(it)
            }
        }
    }

    private fun String.safe(): String {

        return this
            .replace(".", "_")
            .replace("-", "_")
            .replace(" ", "_")
    }
}