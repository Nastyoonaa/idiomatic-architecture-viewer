package dependency

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSClassDeclaration

class DependencyAnalyzer {

    fun extractDependencies(
        clazz: KSClassDeclaration
    ): List<String> {

        val className =
            clazz.simpleName.asString()

        val dependencies =
            buildSet {

                //
                // constructor
                //

                clazz.primaryConstructor
                    ?.parameters
                    .orEmpty()
                    .mapNotNull {
                        it.type.resolve()
                            .declaration as? KSClassDeclaration
                    }
                    .forEach(::add)

                //
                // properties
                //

                clazz.getAllProperties()
                    .mapNotNull {
                        it.type.resolve()
                            .declaration as? KSClassDeclaration
                    }
                    .forEach(::add)

                //
                // method parameters
                //

                clazz.getDeclaredFunctions()
                    .flatMap { function ->

                        function.parameters
                            .mapNotNull {
                                it.type.resolve()
                                    .declaration as? KSClassDeclaration
                            }
                    }
                    .forEach(::add)

                //
                // return types
                //

                clazz.getDeclaredFunctions()
                    .mapNotNull { function ->

                        function.returnType
                            ?.resolve()
                            ?.declaration as? KSClassDeclaration
                    }
                    .forEach(::add)

                //
                // inheritance
                //

                clazz.superTypes
                    .mapNotNull { superType ->

                        superType.resolve()
                            .declaration as? KSClassDeclaration
                    }
                    .forEach(::add)
            }

        return dependencies
            .distinctBy {
                it.qualifiedName?.asString()
            }
            .filter {
                it.simpleName.asString() != className
            }
            .map { dependency ->

                """
UmlDependency(
    "$className",
    "${dependency.simpleName.asString()}",
    "-->"
)
                """.trimIndent()
            }
    }
}