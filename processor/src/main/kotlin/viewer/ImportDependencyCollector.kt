package viewer

import java.io.File

class ImportDependencyCollector {

    fun collect(
        annotatedSymbols: List<ProjectSymbol>,
        symbolIndex: ProjectSymbolIndex
    ): ImportDependencyResult {

        val dependencies =
            annotatedSymbols
                .flatMap { symbol ->
                    val source =
                        symbol.filePath
                            .takeIf {
                                it.isNotBlank()
                            }
                            ?.let {
                                File(it)
                            }
                            ?.takeIf {
                                it.exists()
                            }
                            ?.readText()
                            .orEmpty()

                    collectFromSource(
                        sourceSymbol = symbol,
                        source = source,
                        symbolIndex = symbolIndex
                    )
                }

        return ImportDependencyResult(
            dependencies = dependencies
        )
    }

    fun collectFromSource(
        sourceSymbol: ProjectSymbol,
        source: String,
        symbolIndex: ProjectSymbolIndex
    ): List<ImportDependency> {

        return IMPORT_REGEX
            .findAll(source)
            .flatMap { match ->
                val importedName =
                    match.groupValues[1]

                val isWildcard =
                    match.groupValues[2] == ".*"

                collectImport(
                    sourceSymbol = sourceSymbol,
                    importedName = importedName,
                    isWildcard = isWildcard,
                    symbolIndex = symbolIndex
                )
            }
            .filter {
                it.from != it.to.qualifiedName
            }
            .distinctBy {
                "${it.from}|${it.to.qualifiedName}|${it.origin}|${it.snippet}"
            }
            .toList()
    }

    private fun collectImport(
        sourceSymbol: ProjectSymbol,
        importedName: String,
        isWildcard: Boolean,
        symbolIndex: ProjectSymbolIndex
    ): Sequence<ImportDependency> {

        if (isExternalImport(importedName)) {
            return emptySequence()
        }

        if (isWildcard) {
            return symbolIndex
                .findByPackage(
                    importedName
                )
                .asSequence()
                .map {
                    ImportDependency(
                        from = sourceSymbol.qualifiedName,
                        to = it,
                        origin = ViewerNodeOrigin.IMPORT,
                        snippet = "import $importedName.*"
                    )
                }
        }

        val resolved =
            symbolIndex.findByQualifiedName(
                importedName
            )

        if (resolved != null) {
            return sequenceOf(
                ImportDependency(
                    from = sourceSymbol.qualifiedName,
                    to = resolved,
                    origin = ViewerNodeOrigin.IMPORT,
                    snippet = "import $importedName"
                )
            )
        }

        if (!symbolIndex.containsProjectPrefix(importedName)) {
            return emptySequence()
        }

        return sequenceOf(
            ImportDependency(
                from = sourceSymbol.qualifiedName,
                to = importedName.toUnresolvedSymbol(
                    sourceSymbol
                ),
                origin = ViewerNodeOrigin.UNRESOLVED_IMPORT,
                snippet = "import $importedName"
            )
        )
    }

    private fun String.toUnresolvedSymbol(
        sourceSymbol: ProjectSymbol
    ): ProjectSymbol {

        val packageName =
            substringBeforeLast(
                ".",
                ""
            )

        val simpleName =
            substringAfterLast(
                "."
            )

        return ProjectSymbol(
            qualifiedName = "unresolved:$this",
            simpleName = simpleName,
            packageName = packageName,
            moduleName = sourceSymbol.moduleName,
            sourceSetName = sourceSymbol.sourceSetName,
            filePath = "",
            fileName = "unresolved",
            kind = ProjectSymbolKind.UNKNOWN,
            layer = "external",
            methods = 0,
            properties = 0,
            isAnnotated = false,
            isResolved = false
        )
    }

    private fun isExternalImport(
        importedName: String
    ): Boolean {
        return EXTERNAL_IMPORT_PREFIXES.any { prefix ->
            importedName == prefix ||
                importedName.startsWith("$prefix.")
        }
    }

    companion object {
        private val IMPORT_REGEX =
            Regex(
                """^\s*import\s+([\w.]+)(\.\*)?(?:\s+as\s+\w+)?\s*$""",
                RegexOption.MULTILINE
            )

        private val EXTERNAL_IMPORT_PREFIXES =
            listOf(
                "android",
                "androidx",
                "java",
                "javax",
                "kotlin",
                "kotlinx",
                "org.jetbrains",
                "com.google",
                "uml"
            )
    }
}

data class ImportDependencyResult(
    val dependencies: List<ImportDependency>
)

data class ImportDependency(
    val from: String,
    val to: ProjectSymbol,
    val origin: ViewerNodeOrigin,
    val snippet: String
)
