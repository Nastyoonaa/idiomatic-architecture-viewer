package generation

import analysis.CycleDetector
import com.google.devtools.ksp.symbol.KSClassDeclaration
import diagram.CycleReportGenerator
import writer.GeneratedFileWriter

class AnalysisGenerationService(
    private val cycleDetector: CycleDetector,
    private val cycleReportGenerator: CycleReportGenerator,
    private val fileWriter: GeneratedFileWriter
) {

    fun generateCycleReport(
        classes: List<KSClassDeclaration>
    ) {

        if (classes.isEmpty()) {
            return
        }

        val cycles =
            cycleDetector.detect(classes)

        val report =
            cycleReportGenerator
                .generate(cycles)

        fileWriter.writeText(
            packageName = "com.example.generated.analysis",
            fileName = "DependencyCycles",
            extension = "md",
            content = report
        )
    }
}