package export

import architecture.ArchitectureProject

class ArchitectureHtmlExporter {

    fun export(
        project: ArchitectureProject,
        mermaidGraph: String
    ): String {

        return buildString {

            appendLine(
                """
<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Architecture Viewer</title>

    <script type="module">

        import mermaid from
        'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';

        mermaid.initialize({
        startOnLoad: true,
        securityLevel: 'loose',
    
        flowchart: {
            useMaxWidth: false,
            nodeSpacing: 80,
            rankSpacing: 140
        }
    });

    </script>

    <style>

        body {
            font-family: Arial;
            padding: 24px;
            background: #f5f5f5;
        }

        h1 {
            margin-bottom: 32px;
        }

        h2 {
            margin-top: 48px;
        }

        details {
            margin-top: 12px;
        }

        summary {
            cursor: pointer;
            font-weight: bold;
            padding: 8px;
            border-radius: 8px;
        }

        .module {
            background: white;
            border-radius: 12px;
            padding: 16px;
            margin-bottom: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .sourceSet {
            margin-left: 24px;
            margin-top: 16px;
        }

        .package {
            margin-left: 48px;
            margin-top: 12px;
        }

        .packageContent {
            margin-left: 24px;
            margin-top: 8px;
            color: #555;
        }

        .graphContainer {
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-top: 32px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);

    overflow-x: auto;
    overflow-y: auto;
}
.graphContainer svg {
    min-width: 2500px;
    height: auto;
}
.graphContainer {
    max-height: 90vh;
}

        a {
            color: #6C43F3;
            text-decoration: none;
            font-weight: bold;
        }

        a:hover {
            text-decoration: underline;
        }

        .classLink {
            color: #444;
            font-weight: normal;
        }

    </style>

</head>

<body>

<h1>Просмотр архитектуры</h1>

"""
            )

            //
            // MODULE TREE
            //

            project.modules.forEach { module ->

                appendLine(
                    """
<details class="module" open>

<summary>
    ${module.name}
</summary>
"""
                )

                if (module.dependencies.isNotEmpty()) {

                    appendLine(
                        """
<div class="packageContent">
    Зависимости:
    ${module.dependencies.joinToString(", ")}
</div>
"""
                    )
                }

                module.sourceSets.forEach { sourceSet ->

                    appendLine(
                        """
<details class="sourceSet" open>

<summary>
    ${sourceSet.name}
</summary>
"""
                    )

                    sourceSet.packages.forEach { pkg ->

                        val packageFileName =
                            pkg.name.replace(".", "_")

                        val classCountText =
                            when (pkg.classes.size) {
                                1 -> "1 класс"
                                2,3,4 -> "${pkg.classes.size} класса"
                                else -> "${pkg.classes.size} классов"
                            }

                        appendLine(
                            """
<details class="package">

<summary>

<a href="$packageFileName.html">
    ${pkg.name}
</a>

($classCountText)

</summary>
"""
                        )

                        appendLine(
                            """
<div class="packageContent">

<ul>
"""
                        )

                        pkg.classes.forEach { clazz ->

                            val className =
                                clazz.simpleName.asString()

                            appendLine(
                                """
<li>

<a
    class="classLink"
    href="$className.html"
>
    $className
</a>

</li>
"""
                            )
                        }

                        appendLine(
                            """
</ul>

</div>

</details>
"""
                        )
                    }

                    appendLine("</details>")
                }

                appendLine("</details>")
            }

            //
            // DEPENDENCY GRAPH
            //

            appendLine(
                """
<h2>Граф зависимостей</h2>

<div class="graphContainer">

<pre class="mermaid">

$mermaidGraph

</pre>

</div>
"""
            )

            appendLine(
                """
</body>

</html>
"""
            )
        }
    }
}