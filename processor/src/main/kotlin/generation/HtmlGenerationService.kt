package generation

import com.google.devtools.ksp.symbol.KSClassDeclaration
import export.ArchitectureHtmlExporter
import export.ClassHtmlExporter
import export.PackageHtmlExporter
import viewer.ViewerDataBuilder
import viewer.ViewerJsonEncoder
import writer.GeneratedFileWriter

class HtmlGenerationService(
    private val architectureHtmlExporter: ArchitectureHtmlExporter,
    private val packageHtmlExporter: PackageHtmlExporter,
    private val classHtmlExporter: ClassHtmlExporter,
    private val viewerDataBuilder: ViewerDataBuilder,
    private val viewerJsonEncoder: ViewerJsonEncoder,
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

        val html =
            architectureHtmlExporter
                .export(
                    viewerDataJson =
                        viewerJsonEncoder.encode(
                            viewerDataBuilder.build(
                                classes
                            )
                        )
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
