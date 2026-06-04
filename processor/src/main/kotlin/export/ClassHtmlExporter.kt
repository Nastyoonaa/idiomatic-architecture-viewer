package export

import com.google.devtools.ksp.symbol.KSClassDeclaration
import dependency.DependencyAnalyzer
import dependency.ImportDependencyAnalyzer

class ClassHtmlExporter(
    private val dependencyAnalyzer: DependencyAnalyzer,
    private val importDependencyAnalyzer: ImportDependencyAnalyzer,
) {

    fun export(
        clazz: KSClassDeclaration,
        projectClasses: Set<String>
    ): String {

        val className =
            clazz.simpleName.asString()

        val packageName =
            clazz.packageName.asString()

        val dependencies =
            clazz.primaryConstructor
                ?.parameters
                .orEmpty()
                .mapNotNull {
                    it.type.resolve()
                        .declaration as? KSClassDeclaration
                }
                .distinctBy {
                    it.qualifiedName?.asString()
                }
        val importDependencies =
            importDependencyAnalyzer
                .extractImportedClassNames(
                    clazz,
                    projectClasses
                )
        val dependencyNames =
            dependencies
                .map {
                    it.simpleName.asString()
                }
                .plus(importDependencies)
                .distinct()
        val methods =
            clazz.getAllFunctions()
                .map {
                    it.simpleName.asString()
                }
                .filter {
                    it != "<init>"
                }
                .distinct()

        val properties =
            clazz.getAllProperties()
                .map {
                    it.simpleName.asString()
                }
                .distinct()

        val mermaidGraph =
            buildString {

                appendLine("graph TD")

                //
                // MAIN CLASS
                //

                appendLine(
                    "$className[$className]"
                )

                appendLine(
                    """click $className "$className.html""""
                )

                //
                // DEPENDENCIES
                //

                dependencyNames.forEach { dependencyName ->

                    appendLine(
                        "$dependencyName[$dependencyName]"
                    )

                    appendLine(
                        "$className --> $dependencyName"
                    )

                    appendLine(
                        """click $dependencyName "$dependencyName.html""""
                    )
                }
            }

        return buildString {

            appendLine(
                """
<!DOCTYPE html>
<html>

<head>

<meta charset="UTF-8">

<title>$className</title>

<script type="module">

import mermaid from
'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';

mermaid.initialize({
    startOnLoad: true
});

</script>

<style>

body {
    font-family: Arial;
    padding: 32px;
    background: #f5f5f5;
}

.card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    margin-top: 24px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

h1 {
    margin-bottom: 8px;
}

h2 {
    margin-top: 32px;
}

.graphContainer {
    background: white;
    border-radius: 16px;
    padding: 24px;
    margin-top: 24px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

a {
    color: #6C43F3;
    text-decoration: none;
    font-weight: bold;
}

a:hover {
    text-decoration: underline;
}

</style>

</head>

<body>

<a href="architecture.html">
← Back to architecture
</a>

<h1>$className</h1>

<div>
$packageName
</div>

<div class="card">

<h2>Dependencies</h2>

<ul>
"""
            )

            dependencyNames.forEach { dependencyName ->

                appendLine(
                    """
<li>

<a href="$dependencyName.html">
    $dependencyName
</a>

</li>
"""
                )
            }

            appendLine(
                """
</ul>

<h2>Properties</h2>

<ul>
"""
            )

            properties.forEach {

                appendLine(
                    "<li>$it</li>"
                )
            }

            appendLine(
                """
</ul>

<h2>Methods</h2>

<ul>
"""
            )

            methods.forEach {

                appendLine(
                    "<li>$it()</li>"
                )
            }

            appendLine(
                """
</ul>

</div>

<h2>Dependency Graph</h2>

<div class="graphContainer">

<pre class="mermaid">

$mermaidGraph

</pre>

</div>

</body>

</html>
"""
            )
        }
    }
}