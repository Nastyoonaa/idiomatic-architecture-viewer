package diagram

import detector.SourceSetDetector
import com.google.devtools.ksp.symbol.KSClassDeclaration

class OverviewDiagramGenerator {

    fun generate(
        classes: List<KSClassDeclaration>
    ): String {

        val uniqueClasses =
            classes.distinctBy {
                it.qualifiedName?.asString()
            }

        val dependencies =
            mutableSetOf<String>()

        return buildString {

            appendLine("@startuml")
            appendLine("skinparam packageStyle rectangle")
            appendLine()

            uniqueClasses
                .groupBy { clazz ->

                    SourceSetDetector.detect(
                        clazz.containingFile!!.filePath
                    )
                }
                .forEach {
                        (sourceSet, sourceSetClasses) ->

                    appendLine(
                        """package "$sourceSet" {"""
                    )

                    sourceSetClasses
                        .groupBy {
                            it.packageName.asString()
                        }
                        .forEach {
                                (packageName, packageClasses) ->

                            appendLine(
                                """    package "$packageName" {"""
                            )

                            packageClasses
                                .distinctBy {
                                    it.simpleName.asString()
                                }
                                .forEach {

                                    appendLine(
                                        "        class ${it.simpleName.asString()}"
                                    )
                                }

                            appendLine("    }")
                            appendLine()
                        }

                    appendLine("}")
                    appendLine()
                }

            uniqueClasses.forEach { clazz ->

                val className =
                    clazz.simpleName.asString()

                clazz.primaryConstructor
                    ?.parameters
                    .orEmpty()
                    .forEach { param ->

                        val dep =
                            param.type.resolve()
                                .declaration as? KSClassDeclaration
                                ?: return@forEach

                        val depName =
                            dep.simpleName.asString()

                        if (depName == className) {
                            return@forEach
                        }

                        dependencies +=
                            "\"$className\" --> \"$depName\""
                    }
            }

            dependencies.forEach {
                appendLine(it)
            }

            appendLine("@enduml")
        }
    }
}