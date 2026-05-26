package generation

import com.example.architecture.ArchitectureTreeBuilder
import com.google.devtools.ksp.symbol.KSClassDeclaration
import detector.ModuleDetector
import detector.SourceSetDetector
import export.ArchitectureJsonExporter
import writer.GeneratedFileWriter

class JsonGenerationService(

    private val architectureTreeBuilder:
    ArchitectureTreeBuilder,

    private val architectureJsonExporter:
    ArchitectureJsonExporter,

    private val fileWriter:
    GeneratedFileWriter,

    private val shouldGenerate:
        (String) -> Boolean
) {

    fun generateArchitectureJson(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ArchitectureJson"
            )
        ) {
            return
        }

        val tree =
            architectureTreeBuilder.build(
                classes = classes,

                detectModuleName = {
                    ModuleDetector.detect(it)
                },

                detectSourceSet = {
                    SourceSetDetector.detect(it)
                }
            )

        val json =
            architectureJsonExporter
                .export(tree)

        fileWriter.writeText(
            packageName =
                "com.example.generated.architecture",

            fileName =
                "architecture",

            extension =
                "json",

            content =
                json
        )
    }
}