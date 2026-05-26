package export

import com.google.devtools.ksp.symbol.KSClassDeclaration

class PackageHtmlExporter {

    fun export(
        packageName: String,
        classes: List<KSClassDeclaration>
    ): String {

        val safeName =
            packageName.replace(".", "_")

        return buildString {

            appendLine(
                """
<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>$packageName</title>

    <script type="module">
        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';

        mermaid.initialize({
            startOnLoad: true
        });
    </script>

    <style>

        body {
            font-family: Arial;
            padding: 24px;
            background: #f5f5f5;
        }

        .container {
            background: white;
            border-radius: 16px;
            padding: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        h1 {
            margin-bottom: 32px;
        }

        .classCard {
            background: #fafafa;
            border-radius: 12px;
            padding: 16px;
            margin-bottom: 16px;
            border-left: 6px solid #7c4dff;
        }

        .dependency {
            margin-top: 8px;
            color: #555;
        }

        .back {
            display: inline-block;
            margin-bottom: 24px;
            text-decoration: none;
            color: #7c4dff;
            font-weight: bold;
        }

        .classLink {
            color: black;
            text-decoration: none;
            font-weight: bold;
        }

        .classLink:hover {
            text-decoration: underline;
        }

        .graphContainer {
            background: white;
            border-radius: 16px;
            padding: 24px;
            margin-top: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

    </style>

</head>

<body>

<a class="back" href="architecture.html">
    ← Back to architecture
</a>

<div class="container">

<h1>$packageName</h1>

<h2>Classes</h2>

"""
            )

            classes.forEach { clazz ->

                val className =
                    clazz.simpleName.asString()

                val dependencies =
                    clazz.primaryConstructor
                        ?.parameters
                        .orEmpty()
                        .mapNotNull {
                            it.type.resolve()
                                .declaration as? KSClassDeclaration
                        }

                appendLine(
                    """
<div class="classCard">

<h3>

<a
    class="classLink"
    href="$className.html"
>
    $className
</a>

</h3>
"""
                )

                if (dependencies.isNotEmpty()) {

                    appendLine(
                        """
<div class="dependency">

Depends on:

<ul>
"""
                    )

                    dependencies.forEach { dependency ->

                        val dependencyName =
                            dependency.simpleName.asString()

                        appendLine(
                            """
<li>

<a
    class="classLink"
    href="$dependencyName.html"
>
    $dependencyName
</a>

</li>
"""
                        )
                    }

                    appendLine(
                        """
</ul>

</div>
"""
                    )
                }

                appendLine("</div>")
            }

            appendLine(
                """
<h2>Dependency Graph</h2>

<div class="graphContainer">

<pre class="mermaid">

graph TD
"""
            )

            classes.forEach { clazz ->

                val className =
                    clazz.simpleName.asString()

                appendLine(
                    """
$className[$className]
click $className "$className.html"
"""
                )

                val dependencies =
                    clazz.primaryConstructor
                        ?.parameters
                        .orEmpty()
                        .mapNotNull {
                            it.type.resolve()
                                .declaration as? KSClassDeclaration
                        }

                dependencies.forEach { dependency ->

                    val dependencyName =
                        dependency.simpleName.asString()

                    appendLine(
                        """
$dependencyName[$dependencyName]
click $dependencyName "$dependencyName.html"

$className --> $dependencyName
"""
                    )
                }
            }

            appendLine(
                """
</pre>

</div>

</div>

</body>
</html>
"""
            )
        }
    }
}