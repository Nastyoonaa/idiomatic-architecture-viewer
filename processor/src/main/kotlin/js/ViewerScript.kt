package architecture.html.script

object ViewerScript {

    fun build(): String {

        return """
let graphPanZoom = null;

const metrics = {

    totalClasses:
        Object.keys(graphData).length,

    totalDependencies:
        Object.values(graphData)
            .reduce(
                (
                    total,
                    node
                ) =>
                    total +
                    node.dependencies.length,
                0
            )
};

function renderMetrics() {

    document.getElementById(
        "metricsPanel"
    ).innerHTML =
        `
        <div class="infoRow">
            Классов:
            ${'$'}{metrics.totalClasses}
        </div>

        <div class="infoRow">
            Зависимостей:
            ${'$'}{metrics.totalDependencies}
        </div>
        `;
}

function highlightDependencies(
    nodeName
) {

    document
        .querySelectorAll(".node")
        .forEach(node => {

            node.classList.remove(
                "dependencyNode"
            );
        });

    const info =
        graphData[nodeName];

    if (!info) {
        return;
    }

    info.dependencies.forEach(
        dependency => {

            const dependencyNode =
                [...document.querySelectorAll(".node")]
                    .find(
                        node =>
                            node.textContent.trim()
                            === dependency
                    );

            if (
                dependencyNode
            ) {

                dependencyNode.classList.add(
                    "dependencyNode"
                );
            }
        }
    );
}

function colorizeNodes() {

    document
        .querySelectorAll(".node")
        .forEach(node => {

            const name =
                node.textContent.trim();

            if (
                name.endsWith("ViewModel")
            ) {

                node.classList.add(
                    "viewModelNode"
                );
            }

            else if (
                name.endsWith("UseCase")
            ) {

                node.classList.add(
                    "useCaseNode"
                );
            }

            else if (
                name.endsWith("Repository")
            ) {

                node.classList.add(
                    "repositoryNode"
                );
            }

            else if (
                name.endsWith("Mapper")
            ) {

                node.classList.add(
                    "mapperNode"
                );
            }

            else if (
                name.endsWith("Dto") ||
                name.endsWith("Model")
            ) {

                node.classList.add(
                    "modelNode"
                );
            }

            else if (
                name === "Result" ||
                name === "Success" ||
                name === "Error"
            ) {

                node.classList.add(
                    "resultNode"
                );
            }
        });
}

function attachNodeListeners() {

    const nodes =
        document.querySelectorAll(
            ".node"
        );

    nodes.forEach(node => {

        node.addEventListener(
            "click",
            () => {

                document
                    .querySelectorAll(".node")
                    .forEach(
                        it => it.classList.remove(
                            "selectedNode"
                        )
                    );

                node.classList.add(
                    "selectedNode"
                );

                const nodeName =
                    node.textContent.trim();

                highlightDependencies(
                    nodeName
                );

                const info =
                    graphData[nodeName];

                document.getElementById(
                    "selectedNodeName"
                ).innerText =
                    nodeName;

                if (!info) {

                    document.getElementById(
                        "selectedNodeInfo"
                    ).innerHTML =
                        "<div>Информация отсутствует</div>";

                    return;
                }

                const dependenciesHtml =
                    info.dependencies
                        .map(
                            dependency =>
                                `
                                <li>
                                    <a
                                        href="#"
                                        class="dependencyLink"
                                        data-node="${'$'}{dependency}"
                                    >
                                        ${'$'}{dependency}
                                    </a>
                                </li>
                                `
                        )
                        .join("");

                document.getElementById(
                    "selectedNodeInfo"
                ).innerHTML =
                    `
                    <div class="infoRow">
                        <span class="infoLabel">
                            Пакет:
                        </span>
                        ${'$'}{info.packageName}
                    </div>

                    <div class="infoRow">
                        <span class="infoLabel">
                            Модуль:
                        </span>
                        ${'$'}{info.moduleName}
                    </div>

                    <div class="infoRow">
                        <span class="infoLabel">
                            Файл:
                        </span>
                        ${'$'}{info.fileName}
                    </div>

                    <div class="dependenciesTitle">
                        Зависимости
                    </div>

                    <ul class="dependenciesList">
                        ${'$'}{dependenciesHtml}
                    </ul>
                    `;

                attachDependencyListeners();
            }
        );
    });
}

function attachDependencyListeners() {

    document
        .querySelectorAll(
            ".dependencyLink"
        )
        .forEach(link => {

            link.addEventListener(
                "click",
                event => {

                    event.preventDefault();

                    const nodeName =
                        link.dataset.node;

                    const targetNode =
                        [...document.querySelectorAll(".node")]
                            .find(
                                node =>
                                    node.textContent
                                        .includes(nodeName)
                            );

                    if (targetNode) {

                        targetNode.dispatchEvent(
                            new MouseEvent(
                                "click",
                                {
                                    bubbles: true
                                }
                            )
                        );

                        targetNode.scrollIntoView({
                            behavior: "smooth",
                            block: "center"
                        });
                    }
                }
            );
        });
}

function attachPanZoom() {

    const svg =
        document.querySelector(
            ".graphContainer svg"
        );

    if (!svg) {

        setTimeout(
            attachPanZoom,
            500
        );

        return;
    }

    graphPanZoom =
        panzoom(
            svg,
            {
                maxZoom: 10,
                minZoom: 0.1,
                smoothScroll: false,
                bounds: false
            }
        );

    colorizeNodes();

    attachNodeListeners();
}

window.addEventListener(
    "load",
    () => {

        attachPanZoom();

        renderMetrics();
    }
);

function zoomIn() {

    if (!graphPanZoom) {
        return;
    }

    graphPanZoom.smoothZoom(
        0,
        0,
        1.2
    );
}

function zoomOut() {

    if (!graphPanZoom) {
        return;
    }

    graphPanZoom.smoothZoom(
        0,
        0,
        0.8
    );
}

function resetZoom() {

    if (!graphPanZoom) {
        return;
    }

    graphPanZoom.moveTo(
        0,
        0
    );

    graphPanZoom.zoomAbs(
        0,
        0,
        1
    );
}
""".trimIndent()
    }
}