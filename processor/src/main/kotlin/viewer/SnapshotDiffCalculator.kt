package viewer

class SnapshotDiffCalculator {

    fun diff(
        previous: ViewerData,
        current: ViewerData
    ): SnapshotDiff {

        val previousNodes =
            previous.nodes.associateBy {
                it.id
            }

        val currentNodes =
            current.nodes.associateBy {
                it.id
            }

        val previousEdges =
            previous.edges.associateBy {
                it.identity()
            }

        val currentEdges =
            current.edges.associateBy {
                it.identity()
            }

        return SnapshotDiff(
            addedNodes =
                currentNodes
                    .filterKeys {
                        it !in previousNodes
                    }
                    .values
                    .sortedBy {
                        it.id
                    },
            removedNodes =
                previousNodes
                    .filterKeys {
                        it !in currentNodes
                    }
                    .values
                    .sortedBy {
                        it.id
                    },
            unchangedNodes =
                currentNodes
                    .filterKeys {
                        it in previousNodes
                    }
                    .values
                    .sortedBy {
                        it.id
                    },
            addedEdges =
                currentEdges
                    .filterKeys {
                        it !in previousEdges
                    }
                    .values
                    .sortedBy {
                        it.identity()
                    },
            removedEdges =
                previousEdges
                    .filterKeys {
                        it !in currentEdges
                    }
                    .values
                    .sortedBy {
                        it.identity()
                    },
            metricsDiff =
                SnapshotMetricsDiff(
                    nodes =
                        SnapshotMetricDiff(
                            before = previous.nodes.size,
                            after = current.nodes.size
                        ),
                    edges =
                        SnapshotMetricDiff(
                            before = previous.edges.size,
                            after = current.edges.size
                        ),
                    violations =
                        SnapshotMetricDiff(
                            before = previous.report.violations.size,
                            after = current.report.violations.size
                        ),
                    cycles =
                        SnapshotMetricDiff(
                            before = previous.report.cycles.size,
                            after = current.report.cycles.size
                        )
                )
        )
    }

    private fun ViewerEdge.identity(): String {
        return listOf(
            from,
            to,
            type,
            context.ifBlank {
                "default"
            }
        ).joinToString("|")
    }
}

data class SnapshotDiff(
    val addedNodes: List<ViewerNode>,
    val removedNodes: List<ViewerNode>,
    val unchangedNodes: List<ViewerNode>,
    val addedEdges: List<ViewerEdge>,
    val removedEdges: List<ViewerEdge>,
    val metricsDiff: SnapshotMetricsDiff
)

data class SnapshotMetricsDiff(
    val nodes: SnapshotMetricDiff,
    val edges: SnapshotMetricDiff,
    val violations: SnapshotMetricDiff,
    val cycles: SnapshotMetricDiff
)

data class SnapshotMetricDiff(
    val before: Int,
    val after: Int
) {
    val delta: Int =
        after - before
}
