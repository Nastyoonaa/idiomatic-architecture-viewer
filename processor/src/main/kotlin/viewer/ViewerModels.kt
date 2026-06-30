package viewer

data class ViewerData(
    val nodes: List<ViewerNode>,
    val edges: List<ViewerEdge>,
    val tree: List<ViewerTreeNode>,
    val summary: ViewerSummary,
    val report: ViewerReport
)

data class ViewerNode(
    val id: String,
    val label: String,
    val packageName: String,
    val moduleName: String,
    val sourceSetName: String,
    val fileName: String,
    val kind: String,
    val origin: String,
    val resolved: Boolean,
    val layer: String,
    val methods: Int,
    val properties: Int,
    val isComposable: Boolean,
    val platformModifier: String,
    val fanIn: Int,
    val fanOut: Int
)

data class ViewerEdge(
    val from: String,
    val to: String,
    val type: String,
    val snippet: String,
    val context: String = "default"
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

data class ViewerReport(
    val cycles: List<ViewerCycle>,
    val violations: List<ArchitectureViolation>,
    val hotspots: List<ArchitectureHotspot>
)

data class ViewerCycle(
    val nodes: List<String>,
    val edges: List<String>
)

data class ArchitectureViolation(
    val ruleId: String,
    val severity: String,
    val message: String,
    val from: String,
    val to: String,
    val edgeType: String
)

data class ArchitectureHotspot(
    val nodeId: String,
    val fanIn: Int,
    val fanOut: Int,
    val total: Int
)
