package metrics

import com.example.processor.metrics.ClassMetrics
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ClassMetricsCalculator {

    fun calculate(
        classes: List<KSClassDeclaration>
    ): List<ClassMetrics> {

        val uniqueClasses =
            classes.distinctBy {

                buildString {
                    append(it.qualifiedName?.asString())
                    append(":")
                    append(it.containingFile?.filePath)
                }
            }

        return uniqueClasses
            .map { clazz ->

                val className =
                    clazz.simpleName.asString()

                //
                // METHOD COUNT
                //

                val methodCount =
                    clazz
                        .getDeclaredFunctions()
                        .count()

                //
                // OUTGOING DEPENDENCIES
                //

                val outgoingDependencies =
                    clazz.primaryConstructor
                        ?.parameters
                        ?.count {
                            it.type.resolve()
                                .declaration is KSClassDeclaration
                        }
                        ?: 0

                //
                // INCOMING DEPENDENCIES
                //

                val incomingDependencies =
                    uniqueClasses.count { other ->

                        other.primaryConstructor
                            ?.parameters
                            ?.any {

                                val dependency =
                                    it.type.resolve()
                                        .declaration
                                            as? KSClassDeclaration

                                dependency
                                    ?.simpleName
                                    ?.asString() == className

                            } == true
                    }

                //
                // HOTSPOT DETECTION
                //

                val isHotspot =
                    methodCount > 15 ||
                            outgoingDependencies > 10

                //
                // RESULT
                //

                ClassMetrics(
                    className = className,
                    incomingDependencies = incomingDependencies,
                    outgoingDependencies = outgoingDependencies,
                    methodCount = methodCount,
                    isHotspot = isHotspot
                )
            }
    }
}