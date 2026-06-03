package dependency

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import config.ArchitectureAnalysisConfig

class UmlDependencyExtractor(
    private val dependencyAnalyzer: DependencyAnalyzer,
    private val importDependencyAnalyzer: ImportDependencyAnalyzer,
    private val config: ArchitectureAnalysisConfig
) {

    fun extract(
        classDeclaration: KSClassDeclaration,
        className: String,
        projectClasses: Set<String>
    ): List<String> {

        return buildList {

            val superTypes =
                classDeclaration
                    .superTypes
                    .mapNotNull {
                        it.resolve()
                            .declaration as? KSClassDeclaration
                    }

            //
            // CONSTRUCTOR DEPENDENCIES
            //

            classDeclaration
                .primaryConstructor
                ?.parameters
                .orEmpty()
                .forEach {

                    val type =
                        it.type.resolve()
                            .declaration as? KSClassDeclaration
                            ?: return@forEach

                    if (
                        type.simpleName.asString()
                        != className
                    ) {

                        add(
                            """UmlDependency(
                                "$className",
                                "${type.simpleName.asString()}",
                                "*--"
                            )""".trimIndent()
                        )
                    }
                }

            //
            // METHOD DEPENDENCIES
            //

            addAll(
                dependencyAnalyzer
                    .extractDependencies(
                        classDeclaration
                    )
            )

            //
            // INHERITANCE
            //

            superTypes
                .firstOrNull {
                    it.classKind == ClassKind.CLASS
                }
                ?.takeIf {
                    it.simpleName.asString() != className
                }
                ?.let {

                    add(
                        """UmlDependency(
                            "$className",
                            "${it.simpleName.asString()}",
                            "--|>"
                        )""".trimIndent()
                    )
                }

            //
            // INTERFACES
            //

            superTypes
                .filter {
                    it.classKind == ClassKind.INTERFACE
                }
                .forEach {

                    add(
                        """UmlDependency(
                            "$className",
                            "${it.simpleName.asString()}",
                            "..|>"
                        )""".trimIndent()
                    )
                }
            if (config.includeImportDependencies) {

                addAll(
                    importDependencyAnalyzer
                        .extractDependencies(
                            classDeclaration,
                            projectClasses
                        )
                )
            }
        }.distinct()
    }
}