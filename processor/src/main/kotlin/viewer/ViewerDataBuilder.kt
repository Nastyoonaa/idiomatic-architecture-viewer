package viewer

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import detector.LayerDetector
import detector.ModuleDetector
import detector.SourceSetDetector
import java.io.File

class ViewerDataBuilder {

    fun build(
        classes: List<KSClassDeclaration>,
        functions: List<KSFunctionDeclaration> = emptyList(),
        projectClasses: List<KSClassDeclaration> = classes
    ): ViewerData {

        val distinctClasses =
            classes
                .filter {
                    it.qualifiedName != null
                }
                .distinctBy {
                    it.qualifiedName!!.asString()
                }

        val distinctFunctions =
            functions
                .filter {
                    it.qualifiedName != null
                }
                .distinctBy {
                    it.functionViewerId()
                }

        val projectClassById =
            projectClasses
                .filter {
                    it.qualifiedName != null
                }
                .distinctBy {
                    it.qualifiedName!!.asString()
                }
                .associateBy {
                    it.viewerId()
                }

        val projectRoots =
            (distinctClasses.map {
                it.packageName.asString()
            } + distinctFunctions.map {
                it.packageName.asString()
            })
                .mapNotNull {
                    it.projectRoot()
                }
                .toSet()

        val nodeById =
            linkedMapOf<String, ViewerNode>()

        distinctClasses.forEach { clazz ->
            nodeById[clazz.viewerId()] =
                clazz.toViewerNode(
                    methods =
                        clazz.getDeclaredFunctions()
                            .count {
                                it.simpleName.asString() != "<init>"
                            },
                    properties =
                        clazz.getAllProperties()
                            .count()
                )
        }

        distinctFunctions.forEach { function ->
            nodeById[function.functionViewerId()] =
                function.toViewerNode()
        }

        fun ensureClassNode(
            dependency: KSClassDeclaration?
        ): String? {
            val id =
                dependency
                    ?.qualifiedName
                    ?.asString()
                    ?: return null

            if (
                id !in nodeById
                &&
                id in projectClassById
            ) {
                nodeById[id] =
                    projectClassById
                        .getValue(id)
                        .toViewerNode(
                            kindOverride = "imported-class",
                            methods = 0,
                            properties = 0
                        )
            }

            return id.takeIf {
                it in nodeById
            }
        }

        fun ensureImportNode(
            importedName: String
        ): String? {
            if (importedName in nodeById) {
                return importedName
            }

            val knownClass =
                projectClassById[importedName]

            if (knownClass != null) {
                nodeById[importedName] =
                    knownClass.toViewerNode(
                        kindOverride = "imported-class",
                        methods = 0,
                        properties = 0
                    )

                return importedName
            }

            if (
                projectRoots.isNotEmpty()
                &&
                projectRoots.none {
                    importedName == it || importedName.startsWith("$it.")
                }
            ) {
                return null
            }

            val packageName =
                importedName.substringBeforeLast(
                    delimiter = ".",
                    missingDelimiterValue = ""
                )

            val label =
                importedName.substringAfterLast(".")

            nodeById[importedName] =
                ViewerNode(
                    id = importedName,
                    label = label,
                    packageName = packageName,
                    moduleName = "imported",
                    sourceSetName = "unknown",
                    fileName = "${label}.kt",
                    kind = "imported-class",
                    layer = packageName.viewerLayerByPackage(),
                    methods = 0,
                    properties = 0,
                    fanIn = 0,
                    fanOut = 0
                )

            return importedName
        }

        val edges =
            mutableListOf<ViewerEdge>()

        fun addEdge(
            fromId: String,
            toId: String?,
            type: String,
            snippet: String
        ) {
            if (
                toId == null
                ||
                toId == fromId
                ||
                toId !in nodeById
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

        distinctClasses.forEach { clazz ->
            clazz.extractEdges(
                ensureClassNode = ::ensureClassNode,
                ensureImportNode = ::ensureImportNode,
                addEdge = ::addEdge
            )
        }

        distinctFunctions.forEach { function ->
            function.extractEdges(
                ensureClassNode = ::ensureClassNode,
                ensureImportNode = ::ensureImportNode,
                addEdge = ::addEdge
            )
        }

        val distinctEdges =
            edges
                .distinctBy {
                    "${it.from}|${it.to}|${it.type}"
                }

        val nodes =
            nodeById
                .values
                .map { node ->
                    node.copy(
                        fanIn =
                            distinctEdges.count {
                                it.to == node.id
                            },
                        fanOut =
                            distinctEdges.count {
                                it.from == node.id
                            }
                    )
                }

        return ViewerData(
            nodes = nodes,
            edges = distinctEdges,
            tree = buildTree(nodes),
            summary =
                ViewerSummary(
                    classes =
                        distinctClasses.size,
                    dependencies =
                        distinctEdges.size,
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
        ensureClassNode: (KSClassDeclaration?) -> String?,
        ensureImportNode: (String) -> String?,
        addEdge: (String, String?, String, String) -> Unit
    ) {

        val fromId =
            viewerId()

        primaryConstructor
            ?.parameters
            .orEmpty()
            .forEach { parameter ->
                val resolvedType =
                    parameter.type.resolve()

                addEdge(
                    fromId,
                    ensureClassNode(
                        resolvedType.declaration as? KSClassDeclaration
                    ),
                    "constructor",
                    "${parameter.name?.asString().orEmpty()}: ${resolvedType.declaration.simpleName.asString()}"
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
                val resolvedType =
                    property.type.resolve()

                addEdge(
                    fromId,
                    ensureClassNode(
                        resolvedType.declaration as? KSClassDeclaration
                    ),
                    "property",
                    "val ${property.simpleName.asString()}: ${resolvedType.declaration.simpleName.asString()}"
                )
            }

        getDeclaredFunctions()
            .filter {
                it.simpleName.asString() != "<init>"
            }
            .forEach { function ->
                function.addSignatureEdges(
                    fromId = fromId,
                    ensureClassNode = ensureClassNode,
                    addEdge = addEdge
                )
            }

        superTypes
            .forEach { superType ->
                addEdge(
                    fromId,
                    ensureClassNode(
                        superType.resolve()
                            .declaration as? KSClassDeclaration
                    ),
                    "inheritance",
                    "super type"
                )
            }

        extractImports()
            .forEach { importedName ->
                addEdge(
                    fromId,
                    ensureImportNode(importedName),
                    "import",
                    "import $importedName"
                )
            }
    }

    private fun KSFunctionDeclaration.extractEdges(
        ensureClassNode: (KSClassDeclaration?) -> String?,
        ensureImportNode: (String) -> String?,
        addEdge: (String, String?, String, String) -> Unit
    ) {

        val fromId =
            functionViewerId()

        addSignatureEdges(
            fromId = fromId,
            ensureClassNode = ensureClassNode,
            addEdge = addEdge
        )

        extractImports()
            .forEach { importedName ->
                addEdge(
                    fromId,
                    ensureImportNode(importedName),
                    "import",
                    "import $importedName"
                )
            }
    }

    private fun KSFunctionDeclaration.addSignatureEdges(
        fromId: String,
        ensureClassNode: (KSClassDeclaration?) -> String?,
        addEdge: (String, String?, String, String) -> Unit
    ) {
        parameters
            .forEach { parameter ->
                val resolvedType =
                    parameter.type.resolve()

                addEdge(
                    fromId,
                    ensureClassNode(
                        resolvedType.declaration as? KSClassDeclaration
                    ),
                    "method",
                    "fun ${simpleName.asString()}(${parameter.name?.asString().orEmpty()}: ${resolvedType.declaration.simpleName.asString()})"
                )
            }

        val resolvedReturnType =
            returnType
                ?.resolve()

        addEdge(
            fromId,
            ensureClassNode(
                resolvedReturnType
                    ?.declaration as? KSClassDeclaration
            ),
            "return-type",
            "fun ${simpleName.asString()}(): ${resolvedReturnType?.declaration?.simpleName?.asString().orEmpty()}"
        )
    }

    private fun KSClassDeclaration.toViewerNode(
        kindOverride: String? = null,
        methods: Int,
        properties: Int
    ): ViewerNode {
        return ViewerNode(
            id = viewerId(),
            label =
                simpleName.asString(),
            packageName =
                packageName.asString(),
            moduleName =
                ModuleDetector.detect(
                    filePath()
                ),
            sourceSetName =
                SourceSetDetector.detect(
                    filePath()
                ),
            fileName =
                containingFile
                    ?.fileName
                    ?: "${simpleName.asString()}.kt",
            kind =
                kindOverride
                    ?: viewerKind(),
            layer =
                viewerLayer(),
            methods =
                methods,
            properties =
                properties,
            fanIn =
                0,
            fanOut =
                0
        )
    }

    private fun KSFunctionDeclaration.toViewerNode(): ViewerNode {
        val kind =
            functionViewerKind()

        return ViewerNode(
            id = functionViewerId(),
            label =
                simpleName.asString(),
            packageName =
                packageName.asString(),
            moduleName =
                ModuleDetector.detect(
                    filePath()
                ),
            sourceSetName =
                SourceSetDetector.detect(
                    filePath()
                ),
            fileName =
                containingFile
                    ?.fileName
                    ?: "${simpleName.asString()}.kt",
            kind =
                kind,
            layer =
                packageName.asString()
                    .viewerLayerByPackage(),
            methods =
                1,
            properties =
                0,
            fanIn =
                0,
            fanOut =
                0
        )
    }

    private fun KSClassDeclaration.extractImports(): List<String> {
        return containingFile
            ?.filePath
            .orEmpty()
            .extractImportsFromFile()
    }

    private fun KSFunctionDeclaration.extractImports(): List<String> {
        return containingFile
            ?.filePath
            .orEmpty()
            .extractImportsFromFile()
    }

    private fun String.extractImportsFromFile(): List<String> {
        if (isBlank()) {
            return emptyList()
        }

        val source =
            runCatching {
                File(this).readText()
            }.getOrDefault("")

        return Regex(
            """import\s+([\w.]+)(?:\s+as\s+\w+)?"""
        )
            .findAll(source)
            .map {
                it.groupValues[1]
            }
            .filterNot {
                it.endsWith(".*")
            }
            .distinct()
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
                                                                    kind = it.kind
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

    private fun KSFunctionDeclaration.functionViewerId(): String {
        return qualifiedName
            ?.asString()
            ?: "${packageName.asString()}.${simpleName.asString()}"
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

    private fun KSFunctionDeclaration.functionViewerKind(): String {
        val isComposable =
            annotations.any {
                it.shortName.asString() == "Composable"
            }

        val platformModifier =
            when {
                Modifier.EXPECT in modifiers ->
                    "expect"

                Modifier.ACTUAL in modifiers ->
                    "actual"

                else ->
                    null
            }

        return listOfNotNull(
            platformModifier,
            if (isComposable) {
                "composable"
            } else {
                null
            },
            "function"
        ).joinToString("-")
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
                packageName.asString()
                    .viewerLayerByPackage()
        }
    }

    private fun String.viewerLayerByPackage(): String {
        return when {
            ".presentation." in this || ".ui." in this ->
                "presentation"

            ".domain." in this ->
                "domain"

            ".data." in this ->
                "data"

            else ->
                "core"
        }
    }

    private fun String.projectRoot(): String? {
        val parts =
            split(".")
                .filter {
                    it.isNotBlank()
                }

        return when {
            parts.size >= 2 ->
                "${parts[0]}.${parts[1]}"

            parts.size == 1 ->
                parts[0]

            else ->
                null
        }
    }

    private fun KSClassDeclaration.filePath(): String {
        return containingFile
            ?.filePath
            .orEmpty()
    }

    private fun KSFunctionDeclaration.filePath(): String {
        return containingFile
            ?.filePath
            .orEmpty()
    }
}
