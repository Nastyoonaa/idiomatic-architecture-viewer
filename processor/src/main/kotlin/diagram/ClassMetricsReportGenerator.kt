package diagram

import com.example.processor.metrics.ClassMetrics

class ClassMetricsReportGenerator {

    fun generate(
        metrics: List<ClassMetrics>
    ): String {

        return buildString {

            appendLine("# Class Metrics")
            appendLine()

            metrics.forEach {

                appendLine(
                    """
Class: ${it.className}
Methods: ${it.methodCount}
Hotspot: ${it.isHotspot}
Incoming Dependencies: ${it.incomingDependencies}
Outgoing Dependencies: ${it.outgoingDependencies}
                    """.trimIndent()
                )

                appendLine()
            }
        }
    }
}