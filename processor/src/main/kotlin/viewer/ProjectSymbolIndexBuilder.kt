package viewer

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import detector.LayerDetector
import detector.ModuleDetector
import detector.SourceSetDetector

class ProjectSymbolIndexBuilder {

    fun build(
        resolver: Resolver,
        annotatedClasses: List<KSClassDeclaration>,
        annotatedFunctions: List<KSFunctionDeclaration> = emptyList()
    ): ProjectSymbolIndex {

        val annotatedIds =
            (annotatedClasses + annotatedFunctions)
                .mapNotNull {
                    it.qualifiedName?.asString()
                }
                .toSet()

        val symbols =
            resolver
                .getAllFiles()
                .flatMap {
                    it.projectDeclarations()
                }
                .mapNotNull {
                    it.toProjectSymbol(
                        annotatedIds
                    )
                }
                .sortedWith(
                    compareBy<ProjectSymbol> {
                        it.qualifiedName
                    }.thenBy {
                        it.platformModifier.indexPriority()
                    }
                )
                .distinctBy {
                    it.qualifiedName
                }
                .toList()

        return ProjectSymbolIndex(
            symbols
        )
    }

    private fun KSFile.projectDeclarations(): Sequence<KSDeclaration> {
        return declarations.flatMap { declaration ->
            sequenceOf(declaration) +
                if (declaration is KSClassDeclaration) {
                    declaration.declarations
                        .filterIsInstance<KSDeclaration>()
                        .flatMap {
                            it.nestedDeclarations()
                        }
                } else {
                    emptySequence()
                }
        }
    }

    private fun KSDeclaration.nestedDeclarations(): Sequence<KSDeclaration> {
        return sequenceOf(this) +
            if (this is KSClassDeclaration) {
                declarations
                    .filterIsInstance<KSDeclaration>()
                    .flatMap {
                        it.nestedDeclarations()
                    }
            } else {
                emptySequence()
            }
    }

    private fun KSDeclaration.toProjectSymbol(
        annotatedIds: Set<String>
    ): ProjectSymbol? {

        return when (this) {
            is KSClassDeclaration ->
                toProjectSymbol(
                    annotatedIds
                )

            is KSFunctionDeclaration ->
                toProjectSymbol(
                    annotatedIds
                )

            else ->
                null
        }
    }

    private fun KSClassDeclaration.toProjectSymbol(
        annotatedIds: Set<String>
    ): ProjectSymbol? {

        val qualifiedName =
            qualifiedName
                ?.asString()
                ?: return null

        val filePath =
            containingFile
                ?.filePath
                .orEmpty()

        return ProjectSymbol(
            qualifiedName = qualifiedName,
            simpleName = simpleName.asString(),
            packageName = packageName.asString(),
            moduleName = ModuleDetector.detect(filePath),
            sourceSetName = SourceSetDetector.detect(filePath),
            filePath = filePath,
            fileName =
                containingFile
                    ?.fileName
                    ?: "${simpleName.asString()}.kt",
            kind = projectSymbolKind(),
            layer = viewerLayer(),
            methods =
                getDeclaredFunctions()
                    .count {
                        it.simpleName.asString() != "<init>"
                    },
            properties =
                getAllProperties()
                    .count(),
            isAnnotated = qualifiedName in annotatedIds,
            isResolved = true
        )
    }

    private fun KSFunctionDeclaration.toProjectSymbol(
        annotatedIds: Set<String>
    ): ProjectSymbol? {

        val qualifiedName =
            qualifiedName
                ?.asString()
                ?: return null

        val filePath =
            containingFile
                ?.filePath
                .orEmpty()

        return ProjectSymbol(
            qualifiedName = qualifiedName,
            simpleName = simpleName.asString(),
            packageName = packageName.asString(),
            moduleName = ModuleDetector.detect(filePath),
            sourceSetName = SourceSetDetector.detect(filePath),
            filePath = filePath,
            fileName =
                containingFile
                    ?.fileName
                    ?: "${simpleName.asString()}.kt",
            kind = ProjectSymbolKind.FUNCTION,
            layer = packageName.asString().viewerLayer(),
            methods = 0,
            properties = parameters.size,
            isAnnotated = qualifiedName in annotatedIds,
            isResolved = true,
            isComposable = isComposableFunction(),
            platformModifier = platformModifier()
        )
    }

    private fun KSFunctionDeclaration.isComposableFunction(): Boolean {
        return annotations.any { annotation ->
            annotation.shortName.asString() == "Composable" ||
                annotation.annotationType
                    .resolve()
                    .declaration
                    .qualifiedName
                    ?.asString() == "androidx.compose.runtime.Composable"
        }
    }

    private fun KSFunctionDeclaration.platformModifier(): ProjectSymbolPlatformModifier {
        return when {
            Modifier.EXPECT in modifiers ->
                ProjectSymbolPlatformModifier.EXPECT

            Modifier.ACTUAL in modifiers ->
                ProjectSymbolPlatformModifier.ACTUAL

            else ->
                ProjectSymbolPlatformModifier.NONE
        }
    }

    private fun ProjectSymbolPlatformModifier.indexPriority(): Int {
        return when (this) {
            ProjectSymbolPlatformModifier.ACTUAL ->
                0

            ProjectSymbolPlatformModifier.NONE ->
                1

            ProjectSymbolPlatformModifier.EXPECT ->
                2
        }
    }

    private fun KSClassDeclaration.projectSymbolKind(): ProjectSymbolKind {
        return when (classKind) {
            ClassKind.OBJECT ->
                ProjectSymbolKind.OBJECT

            ClassKind.INTERFACE ->
                ProjectSymbolKind.INTERFACE

            ClassKind.ENUM_CLASS ->
                ProjectSymbolKind.ENUM

            ClassKind.ENUM_ENTRY ->
                ProjectSymbolKind.OBJECT

            ClassKind.ANNOTATION_CLASS ->
                ProjectSymbolKind.ANNOTATION

            else ->
                if (Modifier.DATA in modifiers) {
                    ProjectSymbolKind.DATA_CLASS
                } else {
                    ProjectSymbolKind.CLASS
                }
        }
    }

    private fun KSClassDeclaration.viewerLayer(): String {
        return when (
            LayerDetector.detect(
                this
            )
        ) {
            "controller" ->
                "presentation"

            "service" ->
                "domain"

            "repository" ->
                "data"

            else ->
                packageName.asString().viewerLayer()
        }
    }

    private fun String.viewerLayer(): String {
        return when {
            ".presentation." in this ->
                "presentation"

            ".domain." in this ->
                "domain"

            ".data." in this ->
                "data"

            else ->
                "core"
        }
    }
}
