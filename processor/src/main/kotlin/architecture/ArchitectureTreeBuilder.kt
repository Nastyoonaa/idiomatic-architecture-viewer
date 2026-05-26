package com.example.architecture


import architecture.ArchitectureModule
import architecture.ArchitecturePackage
import architecture.ArchitectureProject
import architecture.ArchitectureSourceSet
import com.example.processor.model.*
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ArchitectureTreeBuilder {

    fun build(
        classes: List<KSClassDeclaration>,
        detectModuleName: (String) -> String,
        detectSourceSet: (String) -> String
    ): ArchitectureProject {

        val modules =
            classes
                .groupBy {
                    detectModuleName(
                        it.containingFile!!.filePath
                    )
                }
                .map { (moduleName, moduleClasses) ->

                    //
                    // MODULE DEPENDENCIES
                    //

                    val dependencies =
                        moduleClasses
                            .flatMap { clazz ->

                                clazz.primaryConstructor
                                    ?.parameters
                                    .orEmpty()
                                    .mapNotNull { parameter ->

                                        val dependencyClass =
                                            parameter.type.resolve()
                                                .declaration as? KSClassDeclaration
                                                ?: return@mapNotNull null

                                        val dependencyFile =
                                            dependencyClass.containingFile
                                                ?: return@mapNotNull null

                                        val dependencyModule =
                                            detectModuleName(
                                                dependencyFile.filePath
                                            )

                                        dependencyModule
                                    }
                            }
                            .filter {
                                it != moduleName
                            }
                            .distinct()

                    //
                    // SOURCE SETS
                    //

                    val sourceSets =
                        moduleClasses
                            .groupBy {
                                detectSourceSet(
                                    it.containingFile!!.filePath
                                )
                            }
                            .map { (sourceSetName, sourceSetClasses) ->

                                val packages =
                                    sourceSetClasses
                                        .groupBy {
                                            it.packageName.asString()
                                        }
                                        .map { (packageName, packageClasses) ->

                                            ArchitecturePackage(
                                                name = packageName,
                                                classes = packageClasses
                                            )
                                        }

                                ArchitectureSourceSet(
                                    name = sourceSetName,
                                    packages = packages
                                )
                            }

                    ArchitectureModule(
                        name = moduleName,
                        sourceSets = sourceSets,
                        dependencies = dependencies
                    )
                }

        return ArchitectureProject(
            modules = modules
        )
    }
}