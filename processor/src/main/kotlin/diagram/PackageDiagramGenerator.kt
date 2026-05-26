package diagram

import com.google.devtools.ksp.symbol.KSClassDeclaration

class PackageDiagramGenerator {

    fun generate(
        packageName: String,
        classes: List<KSClassDeclaration>
    ): String {

        return buildString {

            appendLine("@startuml")
            appendLine()

            val shortName =
                packageName.substringAfterLast(".")

            appendLine(
                """package "$shortName" {"""
            )

            //
            // CLASSES
            //

            classes.forEach { clazz ->

                appendLine(
                    """  class ${clazz.simpleName.asString()}"""
                )
            }

            appendLine()

            //
            // DEPENDENCIES
            //

            classes.forEach { clazz ->

                val className =
                    clazz.simpleName.asString()

                clazz.primaryConstructor
                    ?.parameters
                    .orEmpty()
                    .forEach { parameter ->

                        val dependency =
                            parameter.type.resolve()
                                .declaration as? KSClassDeclaration
                                ?: return@forEach

                        val dependencyName =
                            dependency.simpleName.asString()

                        appendLine(
                            """  $className --> $dependencyName"""
                        )
                    }
            }

            appendLine("}")
            appendLine()

            appendLine("@enduml")
        }
    }
}