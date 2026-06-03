package architecture.html

import architecture.html.script.ViewerScript

object ScriptBuilder {

    fun build(
        graphData: String
    ): String {

        return """
<script src="https://unpkg.com/panzoom@9.4.0/dist/panzoom.min.js"></script>

<script>

$graphData

${ViewerScript.build()}

</script>

</body>

</html>
""".trimIndent()
    }
}