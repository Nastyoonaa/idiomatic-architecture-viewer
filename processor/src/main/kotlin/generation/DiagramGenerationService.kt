package generation

import com.example.architecture.ArchitectureTreeBuilder
import com.google.devtools.ksp.symbol.KSClassDeclaration
import detector.ModuleDetector
import detector.SourceSetDetector
import diagram.ArchitectureGraphGenerator
import diagram.ModuleDependencyDiagramGenerator
import diagram.ModuleDiagramGenerator
import diagram.OverviewDiagramGenerator
import diagram.PackageDiagramGenerator
import writer.GeneratedFileWriter

class DiagramGenerationService(

    private val overviewDiagramGenerator:
    OverviewDiagramGenerator,

    private val moduleDiagramGenerator:
    ModuleDiagramGenerator,

    private val moduleDependencyDiagramGenerator:
    ModuleDependencyDiagramGenerator,

    private val packageDiagramGenerator:
    PackageDiagramGenerator,

    private val architectureGraphGenerator:
    ArchitectureGraphGenerator,

    private val architectureTreeBuilder:
    ArchitectureTreeBuilder,

    private val fileWriter: GeneratedFileWriter,

    private val shouldGenerate:
        (String) -> Boolean
) {

    fun generateOverviewDiagram(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ArchitectureOverview"
            )
        ) {
            return
        }

        val diagram =
            overviewDiagramGenerator
                .generate(classes)

        fileWriter.writeText(
            packageName =
                "com.example.generated.uml",

            fileName =
                "ArchitectureOverview",

            extension =
                "puml",

            content =
                diagram
        )
    }

    fun generateModuleDiagrams(
        classes: List<KSClassDeclaration>
    ) {

        val grouped =
            classes.groupBy {
                ModuleDetector.detect(
                    it.containingFile!!.filePath
                )
            }

        grouped.forEach {
                (moduleName, moduleClasses) ->

            if (
                !shouldGenerate(
                    "${moduleName}ModuleDiagram"
                )
            ) {
                return@forEach
            }

            val diagram =
                moduleDiagramGenerator
                    .generate(moduleClasses)

            fileWriter.writeText(
                packageName =
                    "com.example.generated.uml",

                fileName =
                    "${moduleName}ModuleDiagram",

                extension =
                    "puml",

                content =
                    diagram
            )
        }
    }

    fun generateModuleDependencyDiagram(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ModuleDependencies"
            )
        ) {
            return
        }

        val diagram =
            moduleDependencyDiagramGenerator
                .generate(classes)

        fileWriter.writeText(
            packageName =
                "com.example.generated.uml",

            fileName =
                "ModuleDependencies",

            extension =
                "puml",

            content =
                diagram
        )
    }

    fun generatePackageDiagrams(
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
                    "${safeName}PackageDiagram"
                )
            ) {
                return@forEach
            }

            val diagram =
                packageDiagramGenerator
                    .generate(
                        packageName,
                        packageClasses
                    )

            fileWriter.writeText(
                packageName =
                    "com.example.generated.uml",

                fileName =
                    "${safeName}PackageDiagram",

                extension =
                    "puml",

                content =
                    diagram
            )
        }
    }

    fun generateArchitectureGraph(
        classes: List<KSClassDeclaration>
    ) {

        if (
            classes.isEmpty()
            ||
            !shouldGenerate(
                "ArchitectureGraph"
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

        val diagram =
            architectureGraphGenerator
                .generate(tree)

        fileWriter.writeText(
            packageName =
                "com.example.generated.architecture",

            fileName =
                "ArchitectureGraph",

            extension =
                "puml",

            content =
                diagram
        )
    }
}