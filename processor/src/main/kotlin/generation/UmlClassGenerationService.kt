package generation

import com.google.devtools.ksp.symbol.KSClassDeclaration
import dependency.UmlDependencyExtractor
import detector.LayerDetector
import detector.ModuleDetector
import detector.SourceSetDetector
import uml.UmlDependencyCodeBuilder
import uml.UmlMethodExtractor
import uml.UmlPropertyExtractor
import writer.GeneratedFileWriter

class UmlClassGenerationService(

    private val umlPropertyExtractor:
    UmlPropertyExtractor,

    private val umlMethodExtractor:
    UmlMethodExtractor,

    private val umlDependencyExtractor:
    UmlDependencyExtractor,

    private val umlDependencyCodeBuilder:
    UmlDependencyCodeBuilder,

    private val fileWriter: GeneratedFileWriter,

    private val shouldGenerate:
        (String) -> Boolean
) {

    fun generateUml(
        classDeclaration: KSClassDeclaration
    ) {

        val className =
            classDeclaration.simpleName.asString()

        val generatedFileName =
            "${className}Uml"

        if (
            !shouldGenerate(
                generatedFileName
            )
        ) {
            return
        }

        val packageName =
            classDeclaration.packageName.asString()

        val functionName =
            className.replaceFirstChar {
                it.lowercase()
            } + "Uml"

        val layer =
            LayerDetector.detect(classDeclaration)

        val filePath =
            classDeclaration
                .containingFile
                ?.filePath
                ?: return

        val moduleName =
            ModuleDetector.detect(filePath)

        val sourceSet =
            SourceSetDetector.detect(filePath)

        val platform =
            detectPlatform(sourceSet)

        val properties =
            umlPropertyExtractor
                .extract(classDeclaration)

        val methods =
            umlMethodExtractor
                .extract(classDeclaration)

        val dependencies =
            umlDependencyExtractor
                .extract(
                    classDeclaration,
                    className
                )

        val dependenciesCode =
            umlDependencyCodeBuilder
                .build(dependencies)

        val code = """
package com.example.generated

import uml.*
import uml.Platform

fun $functionName(): UmlClass {

    return UmlClass(
        name = "$className",
        packageName = "$packageName",
        layer = "$layer",
        moduleName = "$moduleName",
        sourceSet = "$sourceSet",
        platform = Platform.$platform,

        properties = listOf(
            $properties
        ),

        methods = listOf(
            $methods
        ),

        dependencies = $dependenciesCode
    )
}
""".trimIndent()

        fileWriter.writeText(
            packageName =
                "com.example.generated",

            fileName =
                generatedFileName,

            extension =
                "kt",

            content =
                code
        )
    }

    private fun detectPlatform(
        sourceSet: String
    ): String {

        return when (sourceSet) {

            "commonMain" -> "COMMON"
            "androidMain" -> "ANDROID"
            "iosMain" -> "IOS"

            else -> "UNKNOWN"
        }
    }
}