package viewer

class ViewerDataBuilder {

    fun build(
        declaredSymbols: List<ProjectSymbol>,
        importedDependencies: List<ImportDependency>,
        declarationEdges: List<ViewerEdge>
    ): ViewerData {

        val declaredById =
            declaredSymbols.associateBy {
                it.qualifiedName
            }

        val importSymbols =
            importedDependencies
                .map {
                    it.to
                }
                .filter {
                    it.qualifiedName !in declaredById
                }
                .distinctBy {
                    it.qualifiedName
                }

        val symbolOrigins =
            buildMap {
                declaredSymbols.forEach { symbol ->
                    put(
                        symbol.qualifiedName,
                        ViewerNodeOrigin.DECLARATION
                    )
                }

                importedDependencies.forEach { dependency ->
                    putIfAbsent(
                        dependency.to.qualifiedName,
                        dependency.origin
                    )
                }
            }

        val importEdges =
            importedDependencies.map { dependency ->
                ViewerEdge(
                    from = dependency.from,
                    to = dependency.to.qualifiedName,
                    type = "import",
                    snippet = dependency.snippet,
                    context =
                        "import:${dependency.snippet}"
                )
            }

        val edges =
            (declarationEdges + importEdges)
                .distinctBy {
                    "${it.from}|${it.to}|${it.type}|${it.context}"
                }

        val nodes =
            (declaredSymbols + importSymbols)
                .distinctBy {
                    it.qualifiedName
                }
                .map { symbol ->
                    val id =
                        symbol.qualifiedName

                    ViewerNode(
                        id = id,
                        label = symbol.simpleName,
                        packageName = symbol.packageName,
                        moduleName = symbol.moduleName,
                        sourceSetName = symbol.sourceSetName,
                        fileName = symbol.fileName,
                        kind = symbol.viewerKind(),
                        origin =
                            symbolOrigins[id]
                                ?.name
                                ?: ViewerNodeOrigin.IMPORT.name,
                        resolved = symbol.isResolved,
                        layer = symbol.layer,
                        methods = symbol.methods,
                        properties = symbol.properties,
                        isComposable = symbol.isComposable,
                        platformModifier = symbol.platformModifier.name,
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
                ),
            report =
                buildReport(
                    nodes = nodes,
                    edges = edges
                )
        )
    }

    private fun buildReport(
        nodes: List<ViewerNode>,
        edges: List<ViewerEdge>
    ): ViewerReport {

        val nodesById =
            nodes.associateBy {
                it.id
            }

        return ViewerReport(
            cycles =
                detectCycles(
                    edges =
                        edges.filter {
                            it.type != "import"
                        }
                ),
            violations =
                validateArchitecture(
                    nodesById = nodesById,
                    edges = edges
                ),
            hotspots =
                nodes
                    .map {
                        ArchitectureHotspot(
                            nodeId = it.id,
                            fanIn = it.fanIn,
                            fanOut = it.fanOut,
                            total = it.fanIn + it.fanOut
                        )
                    }
                    .filter {
                        it.total > 0
                    }
                    .sortedByDescending {
                        it.total
                    }
                    .take(8)
        )
    }

    private fun detectCycles(
        edges: List<ViewerEdge>
    ): List<ViewerCycle> {

        val graph =
            edges
                .groupBy {
                    it.from
                }
                .mapValues { (_, nodeEdges) ->
                    nodeEdges.map {
                        it.to
                    }
                }

        val edgeTypes =
            edges.associateBy {
                "${it.from}|${it.to}"
            }

        val cycles =
            mutableListOf<ViewerCycle>()

        fun visit(
            nodeId: String,
            path: List<String>
        ) {
            val existingIndex =
                path.indexOf(nodeId)

            if (existingIndex >= 0) {
                val cycleNodes =
                    path.drop(existingIndex) + nodeId

                val cycleEdges =
                    cycleNodes
                        .zipWithNext()
                        .mapNotNull { (from, to) ->
                            edgeTypes["$from|$to"]
                                ?.let {
                                    "${it.from}|${it.to}|${it.type}|${it.context.ifBlank { "default" }}"
                                }
                        }

                cycles +=
                    ViewerCycle(
                        nodes = cycleNodes,
                        edges = cycleEdges
                    )

                return
            }

            if (path.size > 24) {
                return
            }

            graph[nodeId]
                .orEmpty()
                .forEach { dependency ->
                    visit(
                        nodeId = dependency,
                        path = path + nodeId
                    )
                }
        }

        graph.keys.forEach {
            visit(
                nodeId = it,
                path = emptyList()
            )
        }

        return cycles
            .distinctBy {
                it.nodes
                    .dropLast(1)
                    .sorted()
                    .joinToString("|")
            }
            .take(12)
    }

    private fun validateArchitecture(
        nodesById: Map<String, ViewerNode>,
        edges: List<ViewerEdge>
    ): List<ArchitectureViolation> {

        return edges
            .mapNotNull { edge ->
                val from =
                    nodesById[edge.from]
                        ?: return@mapNotNull null

                val to =
                    nodesById[edge.to]
                        ?: return@mapNotNull null

                val fromLayer =
                    from.architectureLayer()

                val toLayer =
                    to.architectureLayer()

                when {
                    fromLayer == "presentation" && toLayer == "data" ->
                        edge.violation(
                            ruleId = "presentation-no-data",
                            severity = "error",
                            message = "Presentation layer depends directly on Data layer"
                        )

                    fromLayer == "domain" && toLayer == "presentation" ->
                        edge.violation(
                            ruleId = "domain-no-presentation",
                            severity = "error",
                            message = "Domain layer depends on Presentation layer"
                        )

                    fromLayer == "domain" && toLayer == "data" ->
                        edge.violation(
                            ruleId = "domain-no-data",
                            severity = "warning",
                            message = "Domain layer depends on Data layer"
                        )

                    from.sourceSetName == "commonMain" &&
                        to.sourceSetName.endsWith("Main") &&
                        to.sourceSetName != "commonMain" ->
                        edge.violation(
                            ruleId = "common-no-platform",
                            severity = "error",
                            message = "commonMain depends on platform-specific source set"
                        )

                    else ->
                        null
                }
            }
            .distinctBy {
                "${it.ruleId}|${it.from}|${it.to}|${it.edgeType}"
            }
    }

    private fun ViewerNode.architectureLayer(): String {

        return when {
            ".presentation." in packageName ||
                moduleName == "presentation" ->
                "presentation"

            ".domain." in packageName ||
                moduleName == "domain" ->
                "domain"

            ".data." in packageName ||
                moduleName == "data" ->
                "data"

            else ->
                layer
        }
    }

    private fun ViewerEdge.violation(
        ruleId: String,
        severity: String,
        message: String
    ): ArchitectureViolation {

        return ArchitectureViolation(
            ruleId = ruleId,
            severity = severity,
            message = message,
            from = from,
            to = to,
            edgeType = type
        )
    }

    private fun ProjectSymbol.viewerKind(): String {
        if (kind != ProjectSymbolKind.FUNCTION) {
            return kind.viewerKind
        }

        return when {
            isComposable &&
                platformModifier == ProjectSymbolPlatformModifier.EXPECT ->
                "expect-composable-function"

            isComposable &&
                platformModifier == ProjectSymbolPlatformModifier.ACTUAL ->
                "actual-composable-function"

            isComposable ->
                "composable-function"

            platformModifier == ProjectSymbolPlatformModifier.EXPECT ->
                "expect-function"

            platformModifier == ProjectSymbolPlatformModifier.ACTUAL ->
                "actual-function"

            else ->
                kind.viewerKind
        }
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
}
