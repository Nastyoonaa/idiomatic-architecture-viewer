package generation

import com.google.devtools.ksp.symbol.KSClassDeclaration
import diagram.ClassMetricsReportGenerator
import diagram.MetricsReportGenerator
import metrics.ClassMetricsCalculator
import metrics.MetricsCalculator
import writer.GeneratedFileWriter

class MetricsGenerationService(

    private val metricsCalculator:
    MetricsCalculator,

    private val classMetricsCalculator:
    ClassMetricsCalculator,

    private val metricsReportGenerator:
    MetricsReportGenerator,

    private val classMetricsReportGenerator:
    ClassMetricsReportGenerator,

    private val fileWriter:
    GeneratedFileWriter,

    private val shouldGenerate:
        (String) -> Boolean
) {

    fun generateMetricsReport(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ArchitectureMetrics"
            )
        ) {
            return
        }

        val metrics =
            metricsCalculator
                .calculateModuleMetrics(
                    classes
                )

        val report =
            metricsReportGenerator
                .generate(metrics)

        fileWriter.writeText(
            packageName =
                "com.example.generated.metrics",

            fileName =
                "ArchitectureMetrics",

            extension =
                "md",

            content =
                report
        )
    }

    fun generateClassMetricsReport(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ClassMetrics"
            )
        ) {
            return
        }

        val metrics =
            classMetricsCalculator
                .calculate(classes)

        val report =
            classMetricsReportGenerator
                .generate(metrics)

        fileWriter.writeText(
            packageName =
                "com.example.generated.metrics",

            fileName =
                "ClassMetrics",

            extension =
                "md",

            content =
                report
        )
    }
}