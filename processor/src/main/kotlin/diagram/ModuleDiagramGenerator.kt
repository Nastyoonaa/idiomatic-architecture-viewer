package diagram

import detector.SourceSetDetector
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ModuleDiagramGenerator {

    fun generate(
        moduleClasses: List<KSClassDeclaration>
    ): String {

        val dependencies =
            mutableSetOf<String>()

        moduleClasses.forEach { clazz ->

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

        return buildString {

            appendLine("@startuml")
            appendLine("skinparam packageStyle rectangle")
            appendLine()

            moduleClasses
                .groupBy {

                    SourceSetDetector.detect(
                        it.containingFile!!.filePath
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

            dependencies.forEach {
                appendLine(it)
            }

            appendLine()
            appendLine("@enduml")
        }
    }
}