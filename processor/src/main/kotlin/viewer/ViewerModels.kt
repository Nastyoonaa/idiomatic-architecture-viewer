package viewer

data class ViewerData(
    val nodes: List<ViewerNode>,
    val edges: List<ViewerEdge>,
    val tree: List<ViewerTreeNode>,
    val summary: ViewerSummary
)

data class ViewerNode(
    val id: String,
    val label: String,
    val packageName: String,
    val moduleName: String,
    val sourceSetName: String,
    val fileName: String,
    val kind: String,
    val layer: String,
    val methods: Int,
    val properties: Int,
    val fanIn: Int,
    val fanOut: Int
)

data class ViewerEdge(
    val from: String,
    val to: String,
    val type: String,
    val snippet: String
)

data class ViewerTreeNode(
    val id: String,
    val label: String,
    val kind: String,
    val children: List<ViewerTreeNode> = emptyList()
)

data class ViewerSummary(
    val classes: Int,
    val dependencies: Int,
    val modules: Int,
    val packages: Int
)
