package viewer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SnapshotDiffCalculatorTest {

    private val calculator =
        SnapshotDiffCalculator()

    @Test
    fun `compares snapshot nodes edges and report metrics from viewer model`() {
        val previous =
            viewerData(
                nodes =
                    listOf(
                        node("com.app.LegacyNode"),
                        node("com.app.SharedNode")
                    ),
                edges =
                    listOf(
                        edge("com.app.LegacyNode", "com.app.SharedNode", "import")
                    ),
                cycles =
                    listOf(
                        cycle("com.app.LegacyNode", "com.app.SharedNode")
                    ),
                violations =
                    listOf(
                        violation("com.app.LegacyNode", "com.app.SharedNode")
                    )
            )

        val current =
            viewerData(
                nodes =
                    listOf(
                        node("com.app.SharedNode"),
                        node("com.app.NewNode", kind = "function")
                    ),
                edges =
                    listOf(
                        edge("com.app.NewNode", "com.app.SharedNode", "constructor")
                    ),
                cycles = emptyList(),
                violations = emptyList()
            )

        val diff =
            calculator.diff(
                previous = previous,
                current = current
            )

        assertEquals(
            listOf("com.app.NewNode"),
            diff.addedNodes.map {
                it.id
            }
        )
        assertEquals(
            listOf("com.app.LegacyNode"),
            diff.removedNodes.map {
                it.id
            }
        )
        assertEquals(
            listOf("com.app.SharedNode"),
            diff.unchangedNodes.map {
                it.id
            }
        )
        assertEquals(
            listOf("com.app.NewNode|com.app.SharedNode|constructor|default"),
            diff.addedEdges.map {
                it.identityForTest()
            }
        )
        assertEquals(
            listOf("com.app.LegacyNode|com.app.SharedNode|import|default"),
            diff.removedEdges.map {
                it.identityForTest()
            }
        )
        assertEquals(
            SnapshotMetricDiff(
                before = 2,
                after = 2
            ),
            diff.metricsDiff.nodes
        )
        assertEquals(
            SnapshotMetricDiff(
                before = 1,
                after = 1
            ),
            diff.metricsDiff.edges
        )
        assertEquals(
            SnapshotMetricDiff(
                before = 1,
                after = 0
            ),
            diff.metricsDiff.violations
        )
        assertEquals(
            SnapshotMetricDiff(
                before = 1,
                after = 0
            ),
            diff.metricsDiff.cycles
        )
    }

    @Test
    fun `produces empty diff for identical input`() {
        val data =
            viewerData(
                nodes =
                    listOf(
                        node("com.app.A"),
                        node("com.app.B", kind = "object")
                    ),
                edges =
                    listOf(
                        edge("com.app.A", "com.app.B", "method", "method:load(input)")
                    )
            )

        val diff =
            calculator.diff(
                previous = data,
                current = data
            )

        assertTrue(
            diff.addedNodes.isEmpty()
        )
        assertTrue(
            diff.removedNodes.isEmpty()
        )
        assertTrue(
            diff.addedEdges.isEmpty()
        )
        assertTrue(
            diff.removedEdges.isEmpty()
        )
        assertEquals(
            0,
            diff.metricsDiff.nodes.delta
        )
        assertEquals(
            0,
            diff.metricsDiff.edges.delta
        )
    }

    @Test
    fun `diff output is deterministic when graph order changes`() {
        val previous =
            viewerData(
                nodes =
                    listOf(
                        node("com.app.B"),
                        node("com.app.A")
                    ),
                edges =
                    listOf(
                        edge("com.app.B", "com.app.A", "import")
                    )
            )

        val current =
            viewerData(
                nodes =
                    listOf(
                        node("com.app.D"),
                        node("com.app.C"),
                        node("com.app.A")
                    ),
                edges =
                    listOf(
                        edge("com.app.D", "com.app.A", "property"),
                        edge("com.app.C", "com.app.A", "constructor")
                    )
            )

        val diff =
            calculator.diff(
                previous = previous,
                current = current
            )

        assertEquals(
            listOf(
                "com.app.C",
                "com.app.D"
            ),
            diff.addedNodes.map {
                it.id
            }
        )
        assertEquals(
            listOf(
                "com.app.C|com.app.A|constructor|default",
                "com.app.D|com.app.A|property|default"
            ),
            diff.addedEdges.map {
                it.identityForTest()
            }
        )
    }

    private fun viewerData(
        nodes: List<ViewerNode>,
        edges: List<ViewerEdge>,
        cycles: List<ViewerCycle> = emptyList(),
        violations: List<ArchitectureViolation> = emptyList()
    ): ViewerData {
        return ViewerData(
            nodes = nodes,
            edges = edges,
            tree = emptyList(),
            summary =
                ViewerSummary(
                    classes = nodes.size,
                    dependencies = edges.size,
                    modules = 1,
                    packages = 1
                ),
            report =
                ViewerReport(
                    cycles = cycles,
                    violations = violations,
                    hotspots = emptyList()
                )
        )
    }

    private fun node(
        id: String,
        kind: String = "class"
    ): ViewerNode {
        return ViewerNode(
            id = id,
            label = id.substringAfterLast("."),
            packageName = id.substringBeforeLast("."),
            moduleName = "sample",
            sourceSetName = "commonMain",
            fileName = "${id.substringAfterLast(".")}.kt",
            kind = kind,
            origin = ViewerNodeOrigin.DECLARATION.name,
            resolved = true,
            layer = "domain",
            methods = 0,
            properties = 0,
            isComposable = false,
            platformModifier = ProjectSymbolPlatformModifier.NONE.name,
            fanIn = edgesIncomingCountPlaceholder,
            fanOut = edgesOutgoingCountPlaceholder
        )
    }

    private fun edge(
        from: String,
        to: String,
        type: String,
        context: String = "default"
    ): ViewerEdge {
        return ViewerEdge(
            from = from,
            to = to,
            type = type,
            snippet = "$from -> $to",
            context = context
        )
    }

    private fun cycle(
        from: String,
        to: String
    ): ViewerCycle {
        return ViewerCycle(
            nodes =
                listOf(
                    from,
                    to,
                    from
                ),
            edges =
                listOf(
                    "$from|$to|import|default",
                    "$to|$from|import|default"
                )
        )
    }

    private fun violation(
        from: String,
        to: String
    ): ArchitectureViolation {
        return ArchitectureViolation(
            ruleId = "sample-rule",
            severity = "error",
            message = "Sample violation",
            from = from,
            to = to,
            edgeType = "import"
        )
    }

    private fun ViewerEdge.identityForTest(): String {
        return listOf(
            from,
            to,
            type,
            context.ifBlank {
                "default"
            }
        ).joinToString("|")
    }

    private companion object {
        const val edgesIncomingCountPlaceholder = 0
        const val edgesOutgoingCountPlaceholder = 0
    }
}
