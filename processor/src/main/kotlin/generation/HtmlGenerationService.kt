package generation

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import export.ArchitectureHtmlExporter
import export.ClassHtmlExporter
import export.PackageHtmlExporter
import viewer.DeclarationDependencyCollector
import viewer.ImportDependencyCollector
import viewer.ProjectSymbolIndexBuilder
import viewer.ViewerDataBuilder
import viewer.ViewerJsonEncoder
import writer.GeneratedFileWriter

class HtmlGenerationService(
    private val architectureHtmlExporter: ArchitectureHtmlExporter,
    private val packageHtmlExporter: PackageHtmlExporter,
    private val classHtmlExporter: ClassHtmlExporter,
    private val projectSymbolIndexBuilder: ProjectSymbolIndexBuilder,
    private val declarationDependencyCollector: DeclarationDependencyCollector,
    private val importDependencyCollector: ImportDependencyCollector,
    private val viewerDataBuilder: ViewerDataBuilder,
    private val viewerJsonEncoder: ViewerJsonEncoder,
    private val fileWriter: GeneratedFileWriter,
    private val shouldGenerate: (String) -> Boolean
) {

    fun generateArchitectureHtml(
        classes: List<KSClassDeclaration>,
        functions: List<KSFunctionDeclaration> = emptyList(),
        resolver: Resolver
    ) {

        if (
            (classes.isEmpty() && functions.isEmpty())
            ||
            !shouldGenerate(
                "ArchitectureHtml"
            )
        ) {
            return
        }

        val symbolIndex =
            projectSymbolIndexBuilder.build(
                resolver = resolver,
                annotatedClasses = classes,
                annotatedFunctions = functions
            )

        val declaredSymbols =
            (classes + functions)
                .mapNotNull {
                    it.qualifiedName?.asString()
                }
                .distinct()
                .mapNotNull {
                    symbolIndex.findByQualifiedName(
                        it
                    )
                }

        val declarationEdges =
            declarationDependencyCollector.collect(
                classes = classes,
                functions = functions,
                symbolIndex = symbolIndex
            )

        val importedDependencies =
            importDependencyCollector
                .collect(
                    annotatedSymbols = declaredSymbols,
                    symbolIndex = symbolIndex
                )
                .dependencies

        val viewerData =
            viewerDataBuilder.build(
                declaredSymbols = declaredSymbols,
                importedDependencies = importedDependencies,
                declarationEdges = declarationEdges
            )

        val html =
            architectureHtmlExporter
                .export(
                    viewerDataJson =
                        viewerJsonEncoder.encode(
                            viewerData
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

        fileWriter.writeText(
            packageName =
                "com.example.generated.architecture",

            fileName =
                "architecture-snapshot",

            extension =
                "json",

            content =
                viewerJsonEncoder.encodeSnapshot(
                    viewerData
                )
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
