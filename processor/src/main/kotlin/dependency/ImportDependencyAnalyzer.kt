package dependency

import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.File

class ImportDependencyAnalyzer {

    fun extractDependencies(
        clazz: KSClassDeclaration,
        projectClasses: Set<String>
    ): List<String> {

        val file =
            clazz.containingFile
                ?: return emptyList()

        val source =
            File(file.filePath)
                .readText()

        val className =
            clazz.simpleName.asString()

        return Regex(
            """import\s+([\w.]+)"""
        )
            .findAll(source)
            .map {

                it.groupValues[1]
                    .substringAfterLast(".")
            }
            .filter {
                it in projectClasses
            }
            .filter {
                it != className
            }
            .distinct()
            .map {

                """
UmlDependency(
    "$className",
    "$it",
    "..>"
)
                """.trimIndent()
            }
            .toList()
    }
}