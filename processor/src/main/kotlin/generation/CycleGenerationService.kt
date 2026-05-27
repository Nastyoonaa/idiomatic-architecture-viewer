package generation

import analysis.CycleDetector
import com.google.devtools.ksp.symbol.KSClassDeclaration
import diagram.CycleReportGenerator
import writer.GeneratedFileWriter

class CycleGenerationService(

    private val cycleDetector:
    CycleDetector,

    private val cycleReportGenerator:
    CycleReportGenerator,

    private val fileWriter: GeneratedFileWriter,


    private val shouldGenerate:
        (String) -> Boolean
) {

    fun generateCycleReport(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "DependencyCycles"
            )
        ) {
            return
        }

        val cycles =
            cycleDetector.detect(classes)

        val report =
            cycleReportGenerator
                .generate(cycles)

        fileWriter.writeText(
            packageName =
                "com.example.generated.analysis",

            fileName =
                "DependencyCycles",

            extension =
                "md",

            content =
                report
        )
    }
}