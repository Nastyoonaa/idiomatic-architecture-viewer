package dependency

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration

class DependencyAnalyzer {

    fun extractMethodDependencies(
        clazz: KSClassDeclaration,
        className: String
    ): List<String> {

        return clazz
            .getDeclaredFunctions()
            .flatMap { function ->

                function.parameters
                    .mapNotNull {

                        it.type.resolve()
                            .declaration as? KSClassDeclaration
                    }
            }
            .distinctBy {
                it.simpleName.asString()
            }
            .filter {
                it.simpleName.asString() != className
            }
            .map {

                """
UmlDependency(
    "$className",
    "${it.simpleName.asString()}",
    "-->"
)
                """.trimIndent()
            }
            .toList()
    }
}