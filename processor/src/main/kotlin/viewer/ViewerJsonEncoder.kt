package viewer

class ViewerJsonEncoder {

    fun encode(
        data: ViewerData
    ): String {

        return buildString {
            append("{")
            append("\"nodes\":")
            appendArray(data.nodes) {
                appendNode(it)
            }
            append(",\"edges\":")
            appendArray(data.edges) {
                appendEdge(it)
            }
            append(",\"tree\":")
            appendArray(data.tree) {
                appendTreeNode(it)
            }
            append(",\"summary\":")
            appendSummary(data.summary)
            append("}")
        }
    }

    private fun StringBuilder.appendNode(
        node: ViewerNode
    ) {
        append("{")
        appendField("id", node.id)
        append(",")
        appendField("label", node.label)
        append(",")
        appendField("pkg", node.packageName)
        append(",")
        appendField("module", node.moduleName)
        append(",")
        appendField("sourceSet", node.sourceSetName)
        append(",")
        appendField("file", node.fileName)
        append(",")
        appendField("kind", node.kind)
        append(",")
        appendField("origin", node.origin)
        append(",")
        appendField("resolved", node.resolved)
        append(",")
        appendField("layer", node.layer)
        append(",")
        appendField("methods", node.methods)
        append(",")
        appendField("properties", node.properties)
        append(",")
        appendField("isComposable", node.isComposable)
        append(",")
        appendField("platformModifier", node.platformModifier)
        append(",")
        appendField("fanIn", node.fanIn)
        append(",")
        appendField("fanOut", node.fanOut)
        append("}")
    }

    private fun StringBuilder.appendEdge(
        edge: ViewerEdge
    ) {
        append("{")
        appendField("from", edge.from)
        append(",")
        appendField("to", edge.to)
        append(",")
        appendField("type", edge.type)
        append(",")
        appendField("snippet", edge.snippet)
        append("}")
    }

    private fun StringBuilder.appendTreeNode(
        node: ViewerTreeNode
    ) {
        append("{")
        appendField("id", node.id)
        append(",")
        appendField("label", node.label)
        append(",")
        appendField("kind", node.kind)
        append(",\"children\":")
        appendArray(node.children) {
            appendTreeNode(it)
        }
        append("}")
    }

    private fun StringBuilder.appendSummary(
        summary: ViewerSummary
    ) {
        append("{")
        appendField("classes", summary.classes)
        append(",")
        appendField("dependencies", summary.dependencies)
        append(",")
        appendField("modules", summary.modules)
        append(",")
        appendField("packages", summary.packages)
        append("}")
    }

    private fun <T> StringBuilder.appendArray(
        values: List<T>,
        appendValue: StringBuilder.(T) -> Unit
    ) {
        append("[")
        values.forEachIndexed { index, value ->
            if (index > 0) {
                append(",")
            }
            appendValue(value)
        }
        append("]")
    }

    private fun StringBuilder.appendField(
        name: String,
        value: String
    ) {
        append("\"")
        append(escape(name))
        append("\":\"")
        append(escape(value))
        append("\"")
    }

    private fun StringBuilder.appendField(
        name: String,
        value: Int
    ) {
        append("\"")
        append(escape(name))
        append("\":")
        append(value)
    }

    private fun StringBuilder.appendField(
        name: String,
        value: Boolean
    ) {
        append("\"")
        append(escape(name))
        append("\":")
        append(value)
    }

    private fun escape(
        value: String
    ): String {
        return buildString {
            value.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
        }
    }
}
