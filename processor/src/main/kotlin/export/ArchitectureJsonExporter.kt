package export

import architecture.ArchitectureProject

class ArchitectureJsonExporter {

    fun export(
        project: ArchitectureProject
    ): String {

        return buildString {

            appendLine("{")
            appendLine("""  "modules": [""")

            project.modules.forEachIndexed {
                    moduleIndex,
                    module ->

                appendLine("    {")

                //
                // MODULE NAME
                //

                appendLine(
                    """      "name": "${module.name}","""
                )

                //
                // MODULE DEPENDENCIES
                //

                appendLine(
                    """      "dependencies": ["""
                )

                module.dependencies.forEachIndexed {
                        dependencyIndex,
                        dependency ->

                    append(
                        """        "$dependency""""
                    )

                    if (
                        dependencyIndex <
                        module.dependencies.lastIndex
                    ) {
                        append(",")
                    }

                    appendLine()
                }

                appendLine(
                    """      ],"""
                )

                //
                // SOURCE SETS
                //

                appendLine(
                    """      "sourceSets": ["""
                )

                module.sourceSets.forEachIndexed {
                        sourceSetIndex,
                        sourceSet ->

                    appendLine("        {")

                    appendLine(
                        """          "name": "${sourceSet.name}","""
                    )

                    appendLine(
                        """          "packages": ["""
                    )

                    sourceSet.packages.forEachIndexed {
                            packageIndex,
                            pkg ->

                        appendLine("            {")

                        appendLine(
                            """              "name": "${pkg.name}","""
                        )

                        appendLine(
                            """              "classCount": ${pkg.classes.size}"""
                        )

                        append("            }")

                        if (
                            packageIndex <
                            sourceSet.packages.lastIndex
                        ) {
                            append(",")
                        }

                        appendLine()
                    }

                    appendLine("          ]")

                    append("        }")

                    if (
                        sourceSetIndex <
                        module.sourceSets.lastIndex
                    ) {
                        append(",")
                    }

                    appendLine()
                }

                appendLine("      ]")

                append("    }")

                if (
                    moduleIndex <
                    project.modules.lastIndex
                ) {
                    append(",")
                }

                appendLine()
            }

            appendLine("  ]")
            appendLine("}")
        }
    }
}