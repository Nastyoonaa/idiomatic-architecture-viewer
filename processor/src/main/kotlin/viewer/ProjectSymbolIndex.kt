package viewer

class ProjectSymbolIndex(
    symbols: List<ProjectSymbol>
) {

    private val byQualifiedName =
        symbols.associateBy {
            it.qualifiedName
        }

    private val byPackage =
        symbols.groupBy {
            it.packageName
        }

    val allSymbols: List<ProjectSymbol> =
        symbols.distinctBy {
            it.qualifiedName
        }

    val packagePrefixes: Set<String> =
        allSymbols
            .mapNotNull {
                it.packageName.projectPackagePrefix()
            }
            .toSet()

    fun findByQualifiedName(
        qualifiedName: String
    ): ProjectSymbol? {
        return byQualifiedName[qualifiedName]
    }

    fun findByPackage(
        packageName: String
    ): List<ProjectSymbol> {
        return byPackage[packageName].orEmpty()
    }

    fun containsProjectPrefix(
        qualifiedName: String
    ): Boolean {
        return packagePrefixes.any { prefix ->
            qualifiedName == prefix ||
                qualifiedName.startsWith("$prefix.")
        }
    }

    private fun String.projectPackagePrefix(): String? {
        val parts =
            split(".")
                .filter {
                    it.isNotBlank()
                }

        return when {
            parts.size >= 2 ->
                parts.take(2).joinToString(".")

            parts.size == 1 ->
                parts.first()

            else ->
                null
        }
    }
}

