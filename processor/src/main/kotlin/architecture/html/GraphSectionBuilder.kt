package export.builder

object GraphSectionBuilder {

    fun build(
        mermaidGraph: String
    ): String {

        return """
<h2>Граф зависимостей</h2>

<div class="zoomControls">

<button
class="zoomButton"
onclick="zoomIn()"
>
+
</button>

<button
class="zoomButton"
onclick="zoomOut()"
>
−
</button>

<button
class="zoomButton"
onclick="resetZoom()"
>
Reset
</button>

</div>

<div class="graphLayout">

    <div class="graphContainer">

        <pre class="mermaid">

$mermaidGraph

        </pre>

    </div>

    <div class="detailsPanel">

        <h3>
            Выбранный элемент
        </h3>

        <div
            id="selectedNodeName"
            class="selectedNodeName"
        >
            Ничего не выбрано
        </div>

        <div
            id="selectedNodeInfo"
            class="selectedNodeInfo"
        >
            Нажмите на узел графа
        </div>

        <hr>

        <h3>
            Метрики
        </h3>

        <div id="metricsPanel"></div>

        <hr>

        <h3>
            Обозначения
        </h3>

        <div class="legendInsidePanel">

            <div class="legendRow">
                <span class="legendDot viewModelLegend"></span>
                ViewModel
            </div>

            <div class="legendRow">
                <span class="legendDot useCaseLegend"></span>
                UseCase
            </div>

            <div class="legendRow">
                <span class="legendDot repositoryLegend"></span>
                Repository
            </div>

            <div class="legendRow">
                <span class="legendDot mapperLegend"></span>
                Mapper / Model
            </div>

            <div class="legendRow">
                <span class="legendDot dependencyLegend"></span>
                Dependency
            </div>

        </div>

    </div>

</div>
"""
    }
}