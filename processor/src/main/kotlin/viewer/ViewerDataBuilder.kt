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
                    snippet = dependency.snippet
                )
            }

        val edges =
            (declarationEdges + importEdges)
                .distinctBy {
                    "${it.from}|${it.to}|${it.type}"
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
                )
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
