package uml

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration

class UmlMethodExtractor {

    fun extract(
        classDeclaration: KSClassDeclaration
    ): String {

        return classDeclaration
            .getDeclaredFunctions()
            .filterNot {
                it.simpleName.asString() == "<init>"
            }
            .joinToString(",\n") { function ->

                val name =
                    function.simpleName.asString()

                val params =
                    function.parameters.joinToString(", ") { param ->

                        val pName =
                            param.name?.asString()
                                ?: "param"

                        val pType =
                            param.type.resolve()
                                .declaration
                                .simpleName
                                .asString()

                        "$pName: $pType"
                    }

                val returnType =
                    function.returnType
                        ?.resolve()
                        ?.declaration
                        ?.simpleName
                        ?.asString()
                        ?: "Unit"

                """UmlMethod("$name", "$params", "$returnType")"""
            }
    }
}