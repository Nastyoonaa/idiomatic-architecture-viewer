package uml

import com.google.devtools.ksp.symbol.KSClassDeclaration

class UmlPropertyExtractor {

    fun extract(
        classDeclaration: KSClassDeclaration
    ): String {

        return classDeclaration
            .primaryConstructor
            ?.parameters
            .orEmpty()
            .joinToString(",\n") {

                val name =
                    it.name?.asString()
                        ?: "unknown"

                val type =
                    it.type.resolve()
                        .declaration
                        .simpleName
                        .asString()

                """UmlProperty("$name", "$type")"""
            }
    }
}