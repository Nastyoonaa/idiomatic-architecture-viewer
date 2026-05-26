package metrics

import com.example.processor.metrics.ModuleMetrics
import com.google.devtools.ksp.symbol.KSClassDeclaration
import detector.ModuleDetector

class MetricsCalculator {

    fun calculateModuleMetrics(
        classes: List<KSClassDeclaration>
    ): List<ModuleMetrics> {

        val uniqueClasses =
            classes.distinctBy {

                buildString {
                    append(it.qualifiedName?.asString())
                    append(":")
                    append(it.containingFile?.filePath)
                }
            }

        return uniqueClasses
            .groupBy { clazz ->

                ModuleDetector.detect(
                    clazz.containingFile!!.filePath
                )
            }
            .map { (moduleName, moduleClasses) ->

                val dependencyCount =
                    moduleClasses.sumOf { clazz ->

                        clazz.primaryConstructor
                            ?.parameters
                            ?.count {
                                it.type.resolve()
                                    .declaration is KSClassDeclaration
                            }
                            ?: 0
                    }

                ModuleMetrics(
                    moduleName = moduleName,
                    classCount = moduleClasses.size,
                    dependencyCount = dependencyCount
                )
            }
            .sortedByDescending {
                it.dependencyCount
            }
    }
}