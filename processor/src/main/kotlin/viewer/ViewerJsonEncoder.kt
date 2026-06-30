package viewer

class ViewerJsonEncoder {

    fun encode(
        data: ViewerData
    ): String {

        return encodeData(
            data
        )
    }

    fun encodeSnapshot(
        data: ViewerData
    ): String {

        return buildString {
            append("{")
            appendField("schemaVersion", SNAPSHOT_SCHEMA_VERSION)
            append(",")
            appendField("generatedBy", "idiomatic-architecture-viewer")
            append(",\"data\":")
            append(
                encodeData(
                    data
                )
            )
            append("}")
        }
    }

    private fun encodeData(
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
            append(",\"report\":")
            appendReport(data.report)
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
        append(",")
        appendField("context", edge.context.ifBlank { "default" })
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

    private fun StringBuilder.appendReport(
        report: ViewerReport
    ) {
        append("{")
        append("\"cycles\":")
        appendArray(report.cycles) {
            appendCycle(it)
        }
        append(",\"violations\":")
        appendArray(report.violations) {
            appendViolation(it)
        }
        append(",\"hotspots\":")
        appendArray(report.hotspots) {
            appendHotspot(it)
        }
        append("}")
    }

    private fun StringBuilder.appendCycle(
        cycle: ViewerCycle
    ) {
        append("{")
        append("\"nodes\":")
        appendStringArray(cycle.nodes)
        append(",\"edges\":")
        appendStringArray(cycle.edges)
        append("}")
    }

    private fun StringBuilder.appendViolation(
        violation: ArchitectureViolation
    ) {
        append("{")
        appendField("ruleId", violation.ruleId)
        append(",")
        appendField("severity", violation.severity)
        append(",")
        appendField("message", violation.message)
        append(",")
        appendField("from", violation.from)
        append(",")
        appendField("to", violation.to)
        append(",")
        appendField("edgeType", violation.edgeType)
        append("}")
    }

    private fun StringBuilder.appendHotspot(
        hotspot: ArchitectureHotspot
    ) {
        append("{")
        appendField("nodeId", hotspot.nodeId)
        append(",")
        appendField("fanIn", hotspot.fanIn)
        append(",")
        appendField("fanOut", hotspot.fanOut)
        append(",")
        appendField("total", hotspot.total)
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

    private fun StringBuilder.appendStringArray(
        values: List<String>
    ) {
        appendArray(values) {
            append("\"")
            append(escape(it))
            append("\"")
        }
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
        value: Long
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

    companion object {
        const val SNAPSHOT_SCHEMA_VERSION: Long = 1
    }
}
