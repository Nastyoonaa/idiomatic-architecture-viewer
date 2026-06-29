package viewer

data class ProjectSymbol(
    val qualifiedName: String,
    val simpleName: String,
    val packageName: String,
    val moduleName: String,
    val sourceSetName: String,
    val filePath: String,
    val fileName: String,
    val kind: ProjectSymbolKind,
    val layer: String,
    val methods: Int,
    val properties: Int,
    val isAnnotated: Boolean,
    val isResolved: Boolean,
    val isComposable: Boolean = false,
    val platformModifier: ProjectSymbolPlatformModifier = ProjectSymbolPlatformModifier.NONE
)

enum class ProjectSymbolKind(
    val viewerKind: String
) {
    CLASS("class"),
    DATA_CLASS("data-class"),
    OBJECT("object"),
    INTERFACE("interface"),
    ENUM("enum"),
    ANNOTATION("annotation"),
    FUNCTION("function"),
    UNKNOWN("unknown")
}

enum class ViewerNodeOrigin {
    DECLARATION,
    IMPORT,
    UNRESOLVED_IMPORT
}

enum class ProjectSymbolPlatformModifier {
    NONE,
    EXPECT,
    ACTUAL
}
