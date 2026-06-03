package architecture.html

import export.style.Styles

object HtmlHeaderBuilder {

    fun build(): String {

        return """
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

${Styles.build()}

</head>

<body>

<h1>Просмотр архитектуры</h1>
"""
    }
}