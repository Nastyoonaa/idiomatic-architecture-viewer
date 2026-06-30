package viewer

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class DeclarationDependencyCollector {

    fun collect(
        classes: List<KSClassDeclaration>,
        functions: List<KSFunctionDeclaration> = emptyList(),
        symbolIndex: ProjectSymbolIndex
    ): List<ViewerEdge> {

        return (classes
            .flatMap {
                it.extractEdges(
                    symbolIndex
                )
            } +
            functions.flatMap {
                it.extractEdges(
                    symbolIndex
                )
            })
            .distinctBy {
                "${it.from}|${it.to}|${it.type}|${it.context}"
            }
    }

    private fun KSClassDeclaration.extractEdges(
        symbolIndex: ProjectSymbolIndex
    ): List<ViewerEdge> {

        val fromId =
            qualifiedName
                ?.asString()
                ?: return emptyList()

        val edges =
            mutableListOf<ViewerEdge>()

        fun addEdge(
            dependency: KSClassDeclaration?,
            type: String,
            snippet: String,
            context: String = "default"
        ) {
            val toId =
                dependency
                    ?.qualifiedName
                    ?.asString()
                    ?: return

            if (
                toId == fromId ||
                symbolIndex.findByQualifiedName(toId) == null
            ) {
                return
            }

            edges += ViewerEdge(
                from = fromId,
                to = toId,
                type = type,
                snippet = snippet,
                context = context
            )
        }

        primaryConstructor
            ?.parameters
            .orEmpty()
            .forEach { parameter ->
                addEdge(
                    dependency =
                        parameter.type.resolve()
                            .declaration as? KSClassDeclaration,
                    type = "constructor",
                    snippet =
                        "${parameter.name?.asString().orEmpty()}: ${parameter.type.resolve().declaration.simpleName.asString()}",
                    context =
                        "constructor:${simpleName.asString()}:${parameter.name?.asString().orEmpty()}"
                )
            }

        val constructorParameterNames =
            primaryConstructor
                ?.parameters
                .orEmpty()
                .mapNotNull {
                    it.name?.asString()
                }
                .toSet()

        getAllProperties()
            .filter {
                it.simpleName.asString() !in constructorParameterNames
            }
            .forEach { property ->
                addEdge(
                    dependency =
                        property.type.resolve()
                            .declaration as? KSClassDeclaration,
                    type = "property",
                    snippet =
                        "val ${property.simpleName.asString()}: ${property.type.resolve().declaration.simpleName.asString()}",
                    context =
                        "property:${property.simpleName.asString()}"
                )
            }

        getDeclaredFunctions()
            .filter {
                it.simpleName.asString() != "<init>"
            }
            .forEach { function ->
                function.parameters
                    .forEach { parameter ->
                        addEdge(
                            dependency =
                                parameter.type.resolve()
                                    .declaration as? KSClassDeclaration,
                            type = "method",
                            snippet =
                                "fun ${function.simpleName.asString()}(${parameter.name?.asString().orEmpty()}: ${parameter.type.resolve().declaration.simpleName.asString()})",
                            context =
                                "method:${function.simpleName.asString()}(${parameter.name?.asString().orEmpty()})"
                        )
                    }

                addEdge(
                    dependency =
                        function.returnType
                            ?.resolve()
                            ?.declaration as? KSClassDeclaration,
                    type = "return-type",
                    snippet =
                        "fun ${function.simpleName.asString()}(): ${function.returnType?.resolve()?.declaration?.simpleName?.asString().orEmpty()}",
                    context =
                        "return:${function.simpleName.asString()}"
                )
            }

        superTypes
            .forEach { superType ->
                addEdge(
                    dependency =
                        superType.resolve()
                            .declaration as? KSClassDeclaration,
                    type = "inheritance",
                    snippet = "super type",
                    context =
                        "inheritance:${superType.resolve().declaration.qualifiedName?.asString().orEmpty()}"
                )
            }

        return edges
    }

    private fun KSFunctionDeclaration.extractEdges(
        symbolIndex: ProjectSymbolIndex
    ): List<ViewerEdge> {

        val fromId =
            qualifiedName
                ?.asString()
                ?: return emptyList()

        val edges =
            mutableListOf<ViewerEdge>()

        fun addEdge(
            dependency: KSClassDeclaration?,
            type: String,
            snippet: String,
            context: String = "default"
        ) {
            val toId =
                dependency
                    ?.qualifiedName
                    ?.asString()
                    ?: return

            if (
                toId == fromId ||
                symbolIndex.findByQualifiedName(toId) == null
            ) {
                return
            }

            edges += ViewerEdge(
                from = fromId,
                to = toId,
                type = type,
                snippet = snippet,
                context = context
            )
        }

        parameters
            .forEach { parameter ->
                val resolved =
                    parameter.type.resolve()

                addEdge(
                    dependency =
                        resolved.declaration as? KSClassDeclaration,
                    type = "method",
                    snippet =
                        "fun ${simpleName.asString()}(${parameter.name?.asString().orEmpty()}: ${resolved.declaration.simpleName.asString()})",
                    context =
                        "method:${simpleName.asString()}(${parameter.name?.asString().orEmpty()})"
                )
            }

        val resolvedReturnType =
            returnType
                ?.resolve()

        addEdge(
            dependency =
                resolvedReturnType
                    ?.declaration as? KSClassDeclaration,
            type = "return-type",
            snippet =
                "fun ${simpleName.asString()}(): ${resolvedReturnType?.declaration?.simpleName?.asString().orEmpty()}",
            context =
                "return:${simpleName.asString()}"
        )

        return edges
    }
}
