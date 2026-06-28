package viewer

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import detector.LayerDetector
import detector.ModuleDetector
import detector.SourceSetDetector
import java.io.File

class ViewerDataBuilder {

    fun build(
        classes: List<KSClassDeclaration>
    ): ViewerData {

        val distinctClasses =
            classes
                .filter {
                    it.qualifiedName != null
                }
                .distinctBy {
                    it.qualifiedName!!.asString()
                }

        val classById =
            distinctClasses.associateBy {
                it.viewerId()
            }

        val edges =
            distinctClasses
                .flatMap {
                    it.extractEdges(classById)
                }
                .distinctBy {
                    "${it.from}|${it.to}|${it.type}"
                }

        val nodes =
            distinctClasses
                .map { clazz ->
                    val id =
                        clazz.viewerId()

                    ViewerNode(
                        id = id,
                        label =
                            clazz.simpleName.asString(),
                        packageName =
                            clazz.packageName.asString(),
                        moduleName =
                            ModuleDetector.detect(
                                clazz.filePath()
                            ),
                        sourceSetName =
                            SourceSetDetector.detect(
                                clazz.filePath()
                            ),
                        fileName =
                            clazz.containingFile
                                ?.fileName
                                ?: "${clazz.simpleName.asString()}.kt",
                        kind =
                            clazz.viewerKind(),
                        layer =
                            clazz.viewerLayer(),
                        methods =
                            clazz.getDeclaredFunctions()
                                .count {
                                    it.simpleName.asString() != "<init>"
                                },
                        properties =
                            clazz.getAllProperties()
                                .count(),
                        fanIn =
                            edges.count {
                                it.to == id
                            },
                        fanOut =
                            edges.count {
                                it.from == id
                            }
                    )
                }

        return ViewerData(
            nodes = nodes,
            edges = edges,
            tree = buildTree(nodes),
            summary =
                ViewerSummary(
                    classes = nodes.size,
                    dependencies = edges.size,
                    modules =
                        nodes.map {
                            it.moduleName
                        }.distinct().size,
                    packages =
                        nodes.map {
                            it.packageName
                        }.distinct().size
                )
        )
    }

    private fun KSClassDeclaration.extractEdges(
        classById: Map<String, KSClassDeclaration>
    ): List<ViewerEdge> {

        val fromId =
            viewerId()

        val edges =
            mutableListOf<ViewerEdge>()

        fun addEdge(
            dependency: KSClassDeclaration?,
            type: String,
            snippet: String
        ) {
            val toId =
                dependency
                    ?.qualifiedName
                    ?.asString()
                    ?: return

            if (
                toId == fromId
                ||
                toId !in classById
            ) {
                return
            }

            edges += ViewerEdge(
                from = fromId,
                to = toId,
                type = type,
                snippet = snippet
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
                        "${parameter.name?.asString().orEmpty()}: ${parameter.type.resolve().declaration.simpleName.asString()}"
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
                        "val ${property.simpleName.asString()}: ${property.type.resolve().declaration.simpleName.asString()}"
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
                                "fun ${function.simpleName.asString()}(${parameter.name?.asString().orEmpty()}: ${parameter.type.resolve().declaration.simpleName.asString()})"
                        )
                    }

                addEdge(
                    dependency =
                        function.returnType
                            ?.resolve()
                            ?.declaration as? KSClassDeclaration,
                    type = "return-type",
                    snippet =
                        "fun ${function.simpleName.asString()}(): ${function.returnType?.resolve()?.declaration?.simpleName?.asString().orEmpty()}"
                )
            }

        superTypes
            .forEach { superType ->
                addEdge(
                    dependency =
                        superType.resolve()
                            .declaration as? KSClassDeclaration,
                    type = "inheritance",
                    snippet = "super type"
                )
            }

        extractImportDependencies(
            classById
        ).forEach { edge ->
            edges += edge
        }

        return edges
    }

    private fun KSClassDeclaration.extractImportDependencies(
        classById: Map<String, KSClassDeclaration>
    ): List<ViewerEdge> {

        val file =
            containingFile
                ?: return emptyList()

        val source =
            File(file.filePath)
                .readText()

        val fromId =
            viewerId()

        val projectIds =
            classById.keys

        return Regex(
            """import\s+([\w.]+)(\.\*)?"""
        )
            .findAll(source)
            .flatMap { match ->
                val importedName =
                    match.groupValues[1]

                val isWildcard =
                    match.groupValues[2] == ".*"

                if (isWildcard) {
                    projectIds
                        .filter {
                            it.startsWith("$importedName.")
                        }
                } else {
                    listOf(importedName)
                        .filter {
                            it in projectIds
                        }
                }
            }
            .filter {
                it != fromId
            }
            .distinct()
            .map {
                ViewerEdge(
                    from = fromId,
                    to = it,
                    type = "import",
                    snippet = "import $it"
                )
            }
            .toList()
    }

    private fun buildTree(
        nodes: List<ViewerNode>
    ): List<ViewerTreeNode> {

        return nodes
            .groupBy {
                it.moduleName
            }
            .toSortedMap()
            .map { (moduleName, moduleNodes) ->
                ViewerTreeNode(
                    id = "module:$moduleName",
                    label = moduleName,
                    kind = "module",
                    children =
                        moduleNodes
                            .groupBy {
                                it.sourceSetName
                            }
                            .toSortedMap()
                            .map { (sourceSetName, sourceSetNodes) ->
                                ViewerTreeNode(
                                    id = "sourceSet:$moduleName:$sourceSetName",
                                    label = sourceSetName,
                                    kind = "sourceSet",
                                    children =
                                        sourceSetNodes
                                            .groupBy {
                                                it.packageName
                                            }
                                            .toSortedMap()
                                            .map { (packageName, packageNodes) ->
                                                ViewerTreeNode(
                                                    id = "package:$packageName",
                                                    label = packageName,
                                                    kind = "package",
                                                    children =
                                                        packageNodes
                                                            .sortedBy {
                                                                it.label
                                                            }
                                                            .map {
                                                                ViewerTreeNode(
                                                                    id = it.id,
                                                                    label = it.label,
                                                                    kind = "class"
                                                                )
                                                            }
                                                )
                                            }
                                )
                            }
                )
            }
    }

    private fun KSClassDeclaration.viewerId(): String {
        return qualifiedName!!.asString()
    }

    private fun KSClassDeclaration.viewerKind(): String {
        return when (classKind) {
            ClassKind.OBJECT ->
                "object"

            ClassKind.INTERFACE ->
                "interface"

            ClassKind.ENUM_CLASS ->
                "class"

            ClassKind.ENUM_ENTRY ->
                "object"

            ClassKind.ANNOTATION_CLASS ->
                "class"

            else ->
                if (
                    modifiers.any {
                        it.name == "DATA"
                    }
                ) {
                    "data-class"
                } else {
                    "class"
                }
        }
    }

    private fun KSClassDeclaration.viewerLayer(): String {
        return when (
            LayerDetector.detect(
                this
            )
        ) {
            "controller" ->
                "presentation"

            "service" ->
                "domain"

            "repository" ->
                "data"

            else ->
                when {
                    ".presentation." in packageName.asString() ->
                        "presentation"

                    ".domain." in packageName.asString() ->
                        "domain"

                    ".data." in packageName.asString() ->
                        "data"

                    else ->
                        "core"
                }
        }
    }

    private fun KSClassDeclaration.filePath(): String {
        return containingFile
            ?.filePath
            .orEmpty()
    }
}
