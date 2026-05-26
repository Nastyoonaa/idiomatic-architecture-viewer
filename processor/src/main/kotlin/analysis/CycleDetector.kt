package analysis

import com.google.devtools.ksp.symbol.KSClassDeclaration

class CycleDetector {

    fun detect(
        classes: List<KSClassDeclaration>
    ): List<List<String>> {

        val graph =
            buildGraph(classes)

        val visited =
            mutableSetOf<String>()

        val stack =
            mutableSetOf<String>()

        val cycles =
            mutableListOf<List<String>>()

        fun dfs(
            node: String,
            path: MutableList<String>
        ) {

            if (node in stack) {

                val cycleStart =
                    path.indexOf(node)

                if (cycleStart >= 0) {

                    cycles +=
                        path.drop(cycleStart) + node
                }

                return
            }

            if (!visited.add(node)) {
                return
            }

            stack += node
            path += node

            graph[node]
                .orEmpty()
                .forEach { dependency ->

                    dfs(
                        dependency,
                        path.toMutableList()
                    )
                }

            stack -= node
        }

        graph.keys.forEach { node ->

            dfs(node, mutableListOf())
        }

        return cycles.distinct()
    }

    private fun buildGraph(
        classes: List<KSClassDeclaration>
    ): Map<String, List<String>> {

        return classes.associate { clazz ->

            val className =
                clazz.simpleName.asString()

            val dependencies =
                clazz.primaryConstructor
                    ?.parameters
                    .orEmpty()
                    .mapNotNull {

                        (
                                it.type.resolve()
                                    .declaration
                                        as? KSClassDeclaration
                                )
                            ?.simpleName
                            ?.asString()
                    }

            className to dependencies
        }
    }
}