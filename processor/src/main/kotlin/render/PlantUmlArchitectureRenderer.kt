package com.example.render

import com.example.processor.model.*

class PlantUmlArchitectureRenderer {

    fun render(
        project: UmlProjectNode
    ): String {

        return buildString {

            appendLine("@startuml")
            appendLine("skinparam packageStyle rectangle")
            appendLine()

            project.modules.forEach { module ->

                appendLine(
                    """package "${module.name}" {"""
                )

                module.sourceSets.forEach { sourceSet ->

                    appendLine(
                        """    package "${sourceSet.name}" {"""
                    )

                    sourceSet.packages.forEach { pkg ->

                        appendLine(
                            """        package "${pkg.name}" {"""
                        )

                        pkg.classes
                            .distinctBy {
                                it.simpleName.asString()
                            }
                            .forEach { clazz ->

                                appendLine(
                                    "            class ${clazz.simpleName.asString()}"
                                )
                            }

                        appendLine("        }")
                        appendLine()
                    }

                    appendLine("    }")
                    appendLine()
                }

                appendLine("}")
                appendLine()
            }

            appendLine("@enduml")
        }
    }
}