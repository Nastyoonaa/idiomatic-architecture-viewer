package diagram

import com.example.processor.metrics.ModuleMetrics

class MetricsReportGenerator {

    fun generate(
        metrics: List<ModuleMetrics>
    ): String {

        return buildString {

            appendLine("# Architecture Metrics")
            appendLine()

            metrics.forEach {

                appendLine(
                    """
Module: ${it.moduleName}
Classes: ${it.classCount}
Dependencies: ${it.dependencyCount}
                    """.trimIndent()
                )

                appendLine()
            }
        }
    }
}