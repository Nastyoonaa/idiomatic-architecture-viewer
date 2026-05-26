package diagram

import com.google.devtools.ksp.symbol.KSClassDeclaration
import detector.ModuleDetector

class ModuleDependencyDiagramGenerator {

    fun generate(
        classes: List<KSClassDeclaration>
    ): String {

        val dependencies =
            mutableSetOf<Pair<String, String>>()

        classes.forEach { clazz ->

            val sourceFile =
                clazz.containingFile
                    ?: return@forEach

            val fromModule =
                ModuleDetector.detect(
                    sourceFile.filePath
                )

            clazz.primaryConstructor
                ?.parameters
                .orEmpty()
                .forEach { param ->

                    val dependency =
                        param.type.resolve()
                            .declaration as? KSClassDeclaration
                            ?: return@forEach

                    val dependencyFile =
                        dependency.containingFile
                            ?: return@forEach

                    val toModule =
                        ModuleDetector.detect(
                            dependencyFile.filePath
                        )

                    if (fromModule == toModule) {
                        return@forEach
                    }

                    dependencies +=
                        fromModule to toModule
                }
        }

        return buildString {

            appendLine("@startuml")
            appendLine("skinparam packageStyle rectangle")
            appendLine()

            dependencies
                .flatMap {
                    listOf(it.first, it.second)
                }
                .distinct()
                .forEach { module ->

                    appendLine(
                        """package "$module" {}"""
                    )
                }

            appendLine()

            dependencies.forEach {
                    (from, to) ->

                appendLine(
                    """"$from" --> "$to""""
                )
            }

            appendLine()
            appendLine("@enduml")
        }
    }
}