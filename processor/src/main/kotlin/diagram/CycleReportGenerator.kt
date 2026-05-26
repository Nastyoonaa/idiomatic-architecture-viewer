package diagram

class CycleReportGenerator {

    fun generate(
        cycles: List<List<String>>
    ): String {

        return buildString {

            appendLine("# Dependency Cycles")
            appendLine()

            if (cycles.isEmpty()) {

                appendLine(
                    "No dependency cycles detected."
                )

            } else {

                cycles.forEach { cycle ->

                    appendLine(
                        cycle.joinToString(
                            " -> "
                        )
                    )

                    appendLine()
                }
            }
        }
    }
}