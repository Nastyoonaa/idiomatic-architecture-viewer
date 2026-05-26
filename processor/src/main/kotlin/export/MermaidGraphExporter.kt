package export

import com.google.devtools.ksp.symbol.KSClassDeclaration

class MermaidGraphExporter {

    fun export(
        classes: List<KSClassDeclaration>
    ): String {

        return buildString {

            appendLine("graph TD")
            appendLine()

            classes.forEach { clazz ->

                val className =
                    clazz.simpleName.asString()

                clazz.primaryConstructor
                    ?.parameters
                    .orEmpty()
                    .forEach { parameter ->

                        val dependency =
                            parameter.type.resolve()
                                .declaration
                                    as? KSClassDeclaration
                                ?: return@forEach

                        val dependencyName =
                            dependency.simpleName.asString()

                        appendLine(
                            "$className --> $dependencyName"
                        )
                    }
            }
        }
    }
}