package com.example.processor

import analysis.CycleDetector
import com.example.architecture.ArchitectureTreeBuilder
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import config.ArchitectureAnalysisConfig
import dependency.DependencyAnalyzer
import dependency.ImportDependencyAnalyzer
import dependency.UmlDependencyExtractor
import diagram.*
import export.*
import generation.*
import metrics.ClassMetricsCalculator
import metrics.MetricsCalculator
import uml.UmlDependencyCodeBuilder
import uml.UmlMethodExtractor
import uml.UmlPropertyExtractor
import viewer.ViewerDataBuilder
import viewer.ViewerJsonEncoder
import writer.GeneratedFileWriter

class UmlProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private val generatedArtifacts =
        mutableSetOf<String>()

    private fun shouldGenerate(
        artifactName: String
    ): Boolean {

        return generatedArtifacts.add(
            artifactName
        )
    }

    //
    // SHARED
    //

    private val dependencyAnalyzer =
        DependencyAnalyzer()

    private val fileWriter =
        GeneratedFileWriter(
            codeGenerator
        )

    //
    // UML
    //
    private val importDependencyAnalyzer = ImportDependencyAnalyzer()

    private val config = ArchitectureAnalysisConfig(
            includeImportDependencies = true
        )
    private val umlClassGenerationService =
        UmlClassGenerationService(

            umlPropertyExtractor =
                UmlPropertyExtractor(),

            umlMethodExtractor =
                UmlMethodExtractor(),

            umlDependencyExtractor =
                UmlDependencyExtractor(
                    dependencyAnalyzer =
                        dependencyAnalyzer,

                    importDependencyAnalyzer =
                        importDependencyAnalyzer,

                    config =
                        config
                ),

            umlDependencyCodeBuilder =
                UmlDependencyCodeBuilder(),

            fileWriter =
                fileWriter,

            shouldGenerate =
                ::shouldGenerate
        )

    //
    // HTML
    //

    private val htmlGenerationService =
        HtmlGenerationService(
            architectureHtmlExporter =
                ArchitectureHtmlExporter(),

            packageHtmlExporter =
                PackageHtmlExporter(),

            classHtmlExporter =
                ClassHtmlExporter(
                    dependencyAnalyzer,
                    importDependencyAnalyzer
                ),

            viewerDataBuilder =
                ViewerDataBuilder(),

            viewerJsonEncoder =
                ViewerJsonEncoder(),

            fileWriter =
                fileWriter,

            shouldGenerate =
                ::shouldGenerate
        )

    //
    // DIAGRAMS
    //

    private val diagramGenerationService =
        DiagramGenerationService(

            overviewDiagramGenerator =
                OverviewDiagramGenerator(),

            moduleDiagramGenerator =
                ModuleDiagramGenerator(),

            moduleDependencyDiagramGenerator =
                ModuleDependencyDiagramGenerator(),

            packageDiagramGenerator =
                PackageDiagramGenerator(),

            architectureGraphGenerator =
                ArchitectureGraphGenerator(),

            architectureTreeBuilder =
                ArchitectureTreeBuilder(),

            fileWriter =
                fileWriter,

            shouldGenerate =
                ::shouldGenerate
        )

    //
    // JSON
    //

    private val jsonGenerationService =
        JsonGenerationService(

            architectureTreeBuilder =
                ArchitectureTreeBuilder(),

            architectureJsonExporter =
                ArchitectureJsonExporter(),

            fileWriter =
                fileWriter,

            shouldGenerate =
                ::shouldGenerate
        )

    //
    // METRICS
    //

    private val metricsGenerationService =
        MetricsGenerationService(

            metricsCalculator =
                MetricsCalculator(),

            classMetricsCalculator =
                ClassMetricsCalculator(),

            metricsReportGenerator =
                MetricsReportGenerator(),

            classMetricsReportGenerator =
                ClassMetricsReportGenerator(),

            fileWriter =
                fileWriter,

            shouldGenerate =
                ::shouldGenerate
        )

    //
    // CYCLES
    //

    private val cycleGenerationService =
        CycleGenerationService(

            cycleDetector =
                CycleDetector(),

            cycleReportGenerator =
                CycleReportGenerator(),

            fileWriter =
                fileWriter,

            shouldGenerate =
                ::shouldGenerate
        )

    override fun process(
        resolver: Resolver
    ): List<KSAnnotated> {

        val symbols =
            resolver
                .getSymbolsWithAnnotation(
                    "uml.UmlDiagram"
                )
                .filterIsInstance<KSClassDeclaration>()

        val invalid =
            symbols
                .filterNot {
                    it.validate()
                }
                .toList()

        val valid =
            symbols
                .filter {
                    it.validate()
                }
                .toList()

        val allClasses =
            valid
                .filter {
                    it.containingFile != null
                }
                .distinctBy {

                    buildString {
                        append(
                            it.qualifiedName
                                ?.asString()
                        )

                        append(":")

                        append(
                            it.containingFile
                                ?.filePath
                        )
                    }
                }

        //
        // GENERATION
        //

        val projectClasses =
            allClasses
                .map {
                    it.simpleName.asString()
                }
                .toSet()

        allClasses.forEach {
            umlClassGenerationService
                .generateUml(
                    it,
                    projectClasses
                )
        }

        diagramGenerationService
            .generateOverviewDiagram(
                allClasses
            )

        diagramGenerationService
            .generateModuleDiagrams(
                allClasses
            )

        diagramGenerationService
            .generateModuleDependencyDiagram(
                allClasses
            )

        diagramGenerationService
            .generatePackageDiagrams(
                allClasses
            )

        diagramGenerationService
            .generateArchitectureGraph(
                allClasses
            )

        htmlGenerationService
            .generateArchitectureHtml(
                allClasses
            )

        htmlGenerationService
            .generatePackageHtml(
                allClasses
            )

        htmlGenerationService
            .generateClassHtmlPages(
                allClasses
            )

        jsonGenerationService
            .generateArchitectureJson(
                allClasses
            )

        metricsGenerationService
            .generateMetricsReport(
                allClasses
            )

        metricsGenerationService
            .generateClassMetricsReport(
                allClasses
            )

        cycleGenerationService
            .generateCycleReport(
                allClasses
            )

        return invalid
    }
}
