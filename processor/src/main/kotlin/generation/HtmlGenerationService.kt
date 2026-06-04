package generation

import com.example.architecture.ArchitectureTreeBuilder
import com.google.devtools.ksp.symbol.KSClassDeclaration
import detector.ModuleDetector
import detector.SourceSetDetector
import diagram.ArchitectureGraphGenerator
import export.ArchitectureHtmlExporter
import export.ClassHtmlExporter
import export.PackageHtmlExporter
import writer.GeneratedFileWriter

class HtmlGenerationService(
    private val architectureTreeBuilder: ArchitectureTreeBuilder,
    private val architectureGraphGenerator: ArchitectureGraphGenerator,
    private val architectureHtmlExporter: ArchitectureHtmlExporter,
    private val packageHtmlExporter: PackageHtmlExporter,
    private val classHtmlExporter: ClassHtmlExporter,
    private val fileWriter: GeneratedFileWriter,
    private val shouldGenerate: (String) -> Boolean
) {

    fun generateArchitectureHtml(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ArchitectureHtml"
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

        val mermaidGraph =
            architectureGraphGenerator
                .generate(tree)

        val html =
            architectureHtmlExporter
                .export(
                    project = tree,
                    mermaidGraph = mermaidGraph
                )

        fileWriter.writeText(
            packageName =
                "com.example.generated.architecture",

            fileName =
                "architecture",

            extension =
                "html",

            content =
                html
        )
    }

    fun generatePackageHtml(
        classes: List<KSClassDeclaration>
    ) {

        val grouped =
            classes.groupBy {
                it.packageName.asString()
            }

        grouped.forEach {
                (packageName, packageClasses) ->

            val safeName =
                packageName.replace(".", "_")

            if (
                !shouldGenerate(
                    "${safeName}PackageHtml"
                )
            ) {
                return@forEach
            }

            val html =
                packageHtmlExporter
                    .export(
                        packageName,
                        packageClasses
                    )

            fileWriter.writeText(
                packageName =
                    "com.example.generated.architecture",

                fileName =
                    safeName,

                extension =
                    "html",

                content =
                    html
            )
        }
    }

    fun generateClassHtmlPages(
        classes: List<KSClassDeclaration>
    ) {
        val projectClasses =
            classes
                .map {
                    it.simpleName.asString()
                }
                .toSet()
        classes
            .distinctBy {
                it.qualifiedName?.asString()
            }
            .forEach { clazz ->

                val className =
                    clazz.simpleName.asString()

                if (
                    !shouldGenerate(
                        "${className}Html"
                    )
                ) {
                    return@forEach
                }

                val html =
                    classHtmlExporter
                        .export(
                            clazz,
                            projectClasses
                        )
                fileWriter.writeText(
                    packageName =
                        "com.example.generated.architecture",

                    fileName =
                        className,

                    extension =
                        "html",

                    content =
                        html
                )
            }
    }
}