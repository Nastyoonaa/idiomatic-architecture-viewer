package export

class ArchitectureHtmlExporter {

    fun export(
        viewerDataJson: String
    ): String {

        return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Idiomatic Architecture Viewer</title>
<style>
:root {
  --background: #0e0e12;
  --foreground: #e2e2e8;
  --card: #16161c;
  --sidebar: #12121a;
  --secondary: #1e1e28;
  --muted: #1a1a22;
  --muted-foreground: #8b8b9c;
  --border: rgba(255, 255, 255, 0.08);
  --primary: #7c4dff;
  --presentation: #a78bfa;
  --domain: #60a5fa;
  --data: #34d399;
  --core: #fbbf24;
  --external: #71717a;
}
* { box-sizing: border-box; }
html, body { height: 100%; margin: 0; }
body {
  background: var(--background);
  color: var(--foreground);
  font: 13px Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  overflow: hidden;
}
button, input {
  font: inherit;
}
button {
  border: 0;
  color: inherit;
  cursor: pointer;
}
.app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  min-width: 960px;
}
.topbar {
  align-items: center;
  background: rgba(14, 14, 18, 0.96);
  border-bottom: 1px solid var(--border);
  display: flex;
  gap: 12px;
  height: 44px;
  padding: 0 16px;
}
.brand {
  align-items: center;
  display: flex;
  gap: 8px;
  font-weight: 700;
  white-space: nowrap;
}
.brandIcon {
  align-items: center;
  background: linear-gradient(135deg, #7c4dff, #4da6ff);
  border-radius: 6px;
  display: flex;
  height: 22px;
  justify-content: center;
  width: 22px;
}
.search {
  align-items: center;
  background: rgba(30, 30, 40, 0.72);
  border: 1px solid var(--border);
  border-radius: 8px;
  display: flex;
  gap: 8px;
  height: 30px;
  max-width: 360px;
  padding: 0 10px;
  width: 34vw;
}
.search input {
  background: transparent;
  border: 0;
  color: var(--foreground);
  outline: 0;
  width: 100%;
}
.toolbar {
  align-items: center;
  display: flex;
  gap: 6px;
  margin-left: auto;
}
.filters {
  align-items: center;
  display: flex;
  gap: 6px;
}
.filterSelect {
  background: rgba(30, 30, 40, 0.72);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--muted-foreground);
  height: 30px;
  max-width: 150px;
  outline: 0;
  padding: 0 8px;
}
.chip, .iconBtn {
  align-items: center;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--muted-foreground);
  display: inline-flex;
  gap: 6px;
  height: 28px;
  padding: 0 10px;
}
.chip.active, .iconBtn.active {
  background: rgba(124, 77, 255, 0.18);
  border-color: rgba(124, 77, 255, 0.42);
  color: var(--primary);
}
.main {
  display: flex;
  flex: 1;
  min-height: 0;
}
.sidebar {
  background: var(--sidebar);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  width: 280px;
}
.panelTitle {
  align-items: center;
  border-bottom: 1px solid var(--border);
  color: var(--muted-foreground);
  display: flex;
  font-size: 10px;
  font-weight: 700;
  height: 36px;
  letter-spacing: 0.12em;
  padding: 0 12px;
  text-transform: uppercase;
}
.tree {
  flex: 1;
  overflow: auto;
  padding: 8px 6px;
}
.treeItem {
  align-items: center;
  border-radius: 6px;
  color: var(--muted-foreground);
  display: flex;
  gap: 6px;
  height: 24px;
  overflow: hidden;
  padding-right: 8px;
  user-select: none;
}
.treeItem:hover {
  background: var(--secondary);
  color: var(--foreground);
}
.treeItem.selected {
  background: rgba(124, 77, 255, 0.18);
  color: var(--primary);
}
.treeToggle {
  color: rgba(226, 226, 232, 0.36);
  width: 12px;
}
.treeLabel {
  font-family: "JetBrains Mono", ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 11px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.canvasShell {
  flex: 1;
  min-width: 0;
  position: relative;
}
.canvasShell.hasEdgePanel {
  padding-bottom: 250px;
}
.canvasTopLeft, .canvasTopRight {
  display: flex;
  gap: 6px;
  position: absolute;
  top: 12px;
  z-index: 2;
}
.canvasTopLeft { left: 14px; }
.canvasTopRight { right: 14px; }
.legend {
  align-items: center;
  display: flex;
  gap: 12px;
}
.legendItem {
  align-items: center;
  color: var(--muted-foreground);
  display: flex;
  font-size: 10px;
  gap: 6px;
}
.dot {
  border-radius: 999px;
  height: 8px;
  width: 8px;
}
.graph {
  height: 100%;
  width: 100%;
}
.canvasShell.handMode .graph {
  cursor: grab;
}
.canvasShell.handMode.isPanning .graph {
  cursor: grabbing;
}
.node {
  cursor: pointer;
  filter: drop-shadow(0 0 0 rgba(124, 77, 255, 0));
  transition:
    opacity 150ms ease,
    filter 150ms ease;
}
.node:hover {
  filter: drop-shadow(0 0 10px rgba(124, 77, 255, 0.18));
}
.node.selected {
  filter: drop-shadow(0 0 14px rgba(124, 77, 255, 0.24));
}
.node.dragging {
  cursor: grabbing;
  filter: drop-shadow(0 0 18px rgba(124, 77, 255, 0.34));
  transition: opacity 120ms ease;
}
.nodeCore,
.nodeRing {
  transition:
    opacity 140ms ease,
    r 140ms ease,
    stroke-width 140ms ease;
}
.node:hover .nodeCore {
  r: 28.5px;
}
.node.selected .nodeCore {
  r: 29px;
}
.node.dragging .nodeCore {
  r: 28px;
}
.canvasShell.handMode .node,
.canvasShell.handMode .edgeHitArea {
  cursor: grab;
}
.canvasShell.handMode.isPanning .node,
.canvasShell.handMode.isPanning .edgeHitArea {
  cursor: grabbing;
}
.nodeLabel {
  fill: var(--foreground);
  font-size: 11px;
  font-weight: 600;
  pointer-events: none;
  text-anchor: middle;
}
.nodePkg {
  fill: rgba(139, 139, 156, 0.72);
  font: 9px "JetBrains Mono", ui-monospace, monospace;
  pointer-events: none;
  text-anchor: middle;
}
.edge {
  cursor: pointer;
  fill: none;
  marker-end: url(#arrow);
  stroke: rgba(255, 255, 255, 0.13);
  stroke-width: 1.2;
  transition:
    opacity 140ms ease,
    stroke 140ms ease,
    stroke-width 140ms ease;
}
.edge.edge-import {
  marker-end: url(#arrowImport);
  opacity: 0.22;
  stroke-width: 0.65;
}
.edge.active {
  marker-end: url(#arrowActive);
  stroke: var(--primary);
  stroke-width: 1.8;
}
.edge.edge-import.active {
  opacity: 0.58;
  stroke-width: 1.05;
}
.edge.dimmed {
  opacity: 0.07;
}
.edgeHitArea {
  cursor: pointer;
  fill: none;
  pointer-events: stroke;
  stroke: transparent;
  stroke-linecap: round;
  stroke-width: 18;
}
.edgeHitArea.edgeHitArea-import {
  stroke-width: 12;
}
.importNotice {
  background: rgba(14, 14, 18, 0.82);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: rgba(226, 226, 232, 0.74);
  font-size: 11px;
  left: 14px;
  line-height: 1.35;
  max-width: 360px;
  padding: 8px 10px;
  position: absolute;
  top: 46px;
  z-index: 2;
}
.importNotice.hidden {
  display: none;
}
.inspector {
  background: var(--card);
  border-left: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  width: 320px;
}
.emptyState {
  align-items: center;
  color: var(--muted-foreground);
  display: flex;
  flex: 1;
  justify-content: center;
  line-height: 1.5;
  padding: 28px;
  text-align: center;
}
.report {
  overflow: auto;
  padding: 16px;
}
.reportGrid {
  display: grid;
  gap: 8px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin-bottom: 18px;
}
.reportCard {
  background: rgba(30, 30, 40, 0.58);
  border-radius: 8px;
  padding: 12px;
}
.reportValue {
  color: rgba(226, 226, 232, 0.82);
  font-family: "JetBrains Mono", ui-monospace, monospace;
  font-size: 26px;
  font-weight: 800;
  line-height: 1;
}
.reportLabel {
  color: var(--muted-foreground);
  margin-top: 5px;
}
.typeRow {
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.055);
  color: var(--muted-foreground);
  display: grid;
  gap: 10px;
  grid-template-columns: 28px 1fr auto;
  padding: 8px 0;
}
.typeLine {
  background: currentColor;
  display: block;
  height: 2px;
  width: 26px;
}
.typeLine.line-constructor,
.typeLine.line-inheritance {
  background: currentColor;
}
.typeLine.line-property {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 8px,
    transparent 8px 12px
  );
}
.typeLine.line-return-type {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 4px,
    transparent 4px 8px
  );
}
.typeLine.line-method {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 2px,
    transparent 2px 5px,
    currentColor 5px 13px,
    transparent 13px 17px
  );
}
.typeLine.line-import {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 2px,
    transparent 2px 6px
  );
}
.typeCount {
  font-family: "JetBrains Mono", ui-monospace, monospace;
  font-size: 11px;
}
.analyzerRow {
  display: grid;
  gap: 8px;
  grid-template-columns: 16px 1fr;
  margin: 14px 0;
}
.analyzerName {
  color: rgba(226, 226, 232, 0.78);
  font-weight: 700;
}
.analyzerDescription {
  color: rgba(139, 139, 156, 0.7);
  line-height: 1.35;
  margin-top: 3px;
}
.inspectorHeader {
  align-items: flex-start;
  border-bottom: 1px solid var(--border);
  display: flex;
  gap: 10px;
  padding: 14px;
}
.nodeBadge {
  align-items: center;
  border-radius: 7px;
  display: flex;
  font-weight: 700;
  height: 28px;
  justify-content: center;
  width: 28px;
}
.inspectorBody {
  overflow: auto;
}
.section {
  border-bottom: 1px solid var(--border);
  padding: 14px;
}
.sectionTitle {
  color: var(--muted-foreground);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.12em;
  margin-bottom: 10px;
  text-transform: uppercase;
}
.row {
  display: grid;
  gap: 10px;
  grid-template-columns: 82px 1fr;
  margin: 7px 0;
}
.rowLabel {
  color: var(--muted-foreground);
  font-size: 11px;
}
.rowValue {
  color: rgba(226, 226, 232, 0.84);
  font-family: "JetBrains Mono", ui-monospace, monospace;
  font-size: 11px;
  overflow-wrap: anywhere;
}
.depChip {
  align-items: center;
  background: rgba(30, 30, 40, 0.45);
  border-radius: 7px;
  cursor: pointer;
  display: flex;
  gap: 8px;
  margin: 6px 0;
  padding: 7px 9px;
}
.dependencyGroup {
  margin: 10px 0;
}
.dependencyGroupHeader {
  align-items: center;
  color: rgba(226, 226, 232, 0.78);
  display: flex;
  font-size: 11px;
  font-weight: 700;
  gap: 8px;
  margin: 8px 0 6px;
}
.dependencyGroupHeader .count {
  color: var(--muted-foreground);
  font-family: "JetBrains Mono", ui-monospace, monospace;
  margin-left: auto;
}
.showImportsButton {
  justify-content: center;
  margin-top: 10px;
  width: 100%;
}
.showImportsButton.active {
  background: rgba(96, 165, 250, 0.16);
  border-color: rgba(96, 165, 250, 0.4);
  color: #60a5fa;
}
.dependencySearch {
  background: rgba(30, 30, 40, 0.72);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--foreground);
  height: 30px;
  margin-bottom: 10px;
  outline: 0;
  padding: 0 10px;
  width: 100%;
}
.dependencyList {
  display: grid;
  gap: 6px;
  max-height: 320px;
  overflow: auto;
}
.dependencyRow {
  background: rgba(30, 30, 40, 0.45);
  border: 1px solid transparent;
  border-radius: 8px;
  color: rgba(226, 226, 232, 0.82);
  cursor: pointer;
  display: grid;
  gap: 4px;
  padding: 8px 9px;
}
.dependencyRow:hover,
.dependencyRow.selected {
  background: rgba(124, 77, 255, 0.14);
  border-color: rgba(124, 77, 255, 0.32);
}
.dependencyPath {
  align-items: center;
  display: flex;
  font-family: "JetBrains Mono", ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 11px;
  gap: 7px;
  min-width: 0;
}
.dependencyPath span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.dependencyType {
  align-items: center;
  color: var(--muted-foreground);
  display: flex;
  font-size: 10px;
  gap: 6px;
}
.dependencyEmpty {
  color: var(--muted-foreground);
  font-size: 12px;
  line-height: 1.45;
  padding: 8px 0;
}
.analysisItem {
  align-items: center;
  color: var(--muted-foreground);
  display: flex;
  gap: 8px;
  margin: 8px 0;
}
.analysisItem.active {
  color: var(--foreground);
}
.analysisItem.planned {
  opacity: 0.42;
}
.analysisCount {
  color: var(--muted-foreground);
  font-family: "JetBrains Mono", ui-monospace, monospace;
  font-size: 10px;
  margin-left: auto;
}
.edgePanel {
  background: rgba(10, 10, 14, 0.96);
  border-top: 1px solid var(--border);
  bottom: 0;
  display: grid;
  grid-template-columns: 320px 1fr 34px;
  min-height: 250px;
  position: absolute;
  width: 100%;
  z-index: 3;
}
.edgeMeta {
  border-right: 1px solid var(--border);
  padding: 20px 22px;
}
.edgeDetail {
  padding: 20px 24px;
}
.edgeClose {
  background: transparent;
  color: var(--muted-foreground);
  font-size: 22px;
  height: 36px;
}
.edgeKicker {
  color: rgba(139, 139, 156, 0.55);
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.14em;
  margin-bottom: 10px;
  text-transform: uppercase;
}
.edgeDependency {
  align-items: center;
  color: rgba(226, 226, 232, 0.82);
  display: flex;
  font-family: "JetBrains Mono", ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 13px;
  font-weight: 700;
  gap: 8px;
  margin-bottom: 18px;
  min-width: 0;
}
.edgeName {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.edgeArrow {
  background: currentColor;
  flex: 0 0 28px;
  height: 2px;
  opacity: 0.66;
  position: relative;
}
.edgeArrow.line-property {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 8px,
    transparent 8px 12px
  );
}
.edgeArrow.line-return-type {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 4px,
    transparent 4px 8px
  );
}
.edgeArrow.line-method {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 2px,
    transparent 2px 5px,
    currentColor 5px 13px,
    transparent 13px 17px
  );
}
.edgeArrow.line-import {
  background: repeating-linear-gradient(
    to right,
    currentColor 0 2px,
    transparent 2px 6px
  );
}
.edgeArrow::after {
  border-right: 2px solid currentColor;
  border-top: 2px solid currentColor;
  content: "";
  height: 7px;
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%) rotate(45deg);
  width: 7px;
}
.typeBadge {
  align-items: center;
  background: rgba(30, 30, 40, 0.88);
  border-radius: 6px;
  display: inline-flex;
  font-weight: 800;
  gap: 10px;
  padding: 7px 10px;
}
.confidence {
  align-items: center;
  color: var(--muted-foreground);
  display: flex;
  gap: 7px;
  margin-top: 8px;
}
.sourceBlock {
  background: #09090d;
  border: 1px solid var(--border);
  border-radius: 8px;
  color: #d8b4fe;
  font: 13px "JetBrains Mono", ui-monospace, SFMono-Regular, Menlo, monospace;
  line-height: 1.65;
  margin-top: 10px;
  min-height: 76px;
  overflow: auto;
  padding: 14px 16px;
  white-space: pre-wrap;
}
.metrics {
  display: grid;
  gap: 8px;
  grid-template-columns: repeat(2, 1fr);
}
.metric {
  background: rgba(30, 30, 40, 0.58);
  border-radius: 8px;
  padding: 10px;
}
.metricLabel {
  color: var(--muted-foreground);
  font-size: 10px;
}
.metricValue {
  font-family: "JetBrains Mono", ui-monospace, monospace;
  font-size: 20px;
  font-weight: 700;
  margin-top: 2px;
}
.status {
  align-items: center;
  background: #0a0a0e;
  border-top: 1px solid var(--border);
  color: rgba(139, 139, 156, 0.72);
  display: flex;
  font-size: 10px;
  height: 26px;
  justify-content: space-between;
  padding: 0 16px;
}
.hidden { display: none; }
</style>
</head>
<body>
<script id="architecture-data" type="application/json">
$viewerDataJson
</script>
<div class="app">
  <header class="topbar">
    <div class="brand"><span class="brandIcon">IA</span><span>Idiomatic AV</span></div>
    <label class="search"><span>Search</span><input id="searchInput" placeholder="classes, packages, modules"></label>
    <div class="filters">
      <select id="moduleFilter" class="filterSelect"></select>
      <select id="sourceSetFilter" class="filterSelect"></select>
      <select id="packageFilter" class="filterSelect"></select>
      <select id="layerFilter" class="filterSelect"></select>
    </div>
    <div class="toolbar">
      <button class="chip active" data-level="class">Class</button>
      <button class="chip" data-level="package">Package</button>
      <button class="chip" data-level="module">Module</button>
      <button class="chip active" data-type="constructor">Constructor</button>
      <button class="chip" data-type="import">Import</button>
      <button class="chip active" data-type="property">Property</button>
      <button class="chip active" data-type="inheritance">Inheritance</button>
      <button class="chip active" data-type="method">Method</button>
      <button class="chip active" data-type="return-type">Return</button>
      <button id="focusToggle" class="chip">Focus</button>
    </div>
  </header>
  <div class="main">
    <aside class="sidebar">
      <div class="panelTitle">Project</div>
      <div id="tree" class="tree"></div>
    </aside>
    <main class="canvasShell">
      <div class="canvasTopLeft">
        <div class="legend">
          <span class="legendItem"><i class="dot" style="background:var(--presentation)"></i>Presentation</span>
          <span class="legendItem"><i class="dot" style="background:var(--domain)"></i>Domain</span>
          <span class="legendItem"><i class="dot" style="background:var(--data)"></i>Data</span>
          <span class="legendItem"><i class="dot" style="background:var(--core)"></i>Core</span>
        </div>
      </div>
      <div class="canvasTopRight">
        <button id="zoomIn" class="iconBtn">+</button>
        <button id="zoomOut" class="iconBtn">-</button>
        <button id="fitGraph" class="iconBtn">Fit</button>
        <button id="handTool" class="iconBtn">Hand</button>
        <button id="resetLayout" class="iconBtn">Reset Layout</button>
      </div>
      <svg id="graph" class="graph"></svg>
      <div id="importNotice" class="importNotice hidden"></div>
      <div id="edgePanel" class="edgePanel hidden"></div>
    </main>
    <aside id="inspector" class="inspector"></aside>
  </div>
  <footer class="status">
    <span id="statusLeft"></span>
    <span>generated static HTML</span>
  </footer>
</div>
<script>
const data = JSON.parse(document.getElementById("architecture-data").textContent);
const state = {
  selectedId: null,
  selectedEdgeKey: null,
  hoveredEdgeKey: null,
  hoveredId: null,
  focusMode: false,
  handMode: false,
  isPanning: false,
  level: "class",
  filters: {
    module: "",
    sourceSet: "",
    pkg: "",
    layer: ""
  },
  activeTypes: new Set(["constructor", "property", "inheritance", "method", "return-type"]),
  search: "",
  dependencySearch: "",
  zoom: 1,
  pan: { x: 0, y: 0 },
  nodePositions: {},
  draggingNodeId: null,
  graphFrame: null,
  expanded: new Set()
};
const layerColors = {
  presentation: "#a78bfa",
  domain: "#60a5fa",
  data: "#34d399",
  core: "#fbbf24",
  external: "#71717a"
};
const typeDash = {
  constructor: "",
  import: "2 5",
  property: "8 4",
  inheritance: "",
  method: "2 4 8 4",
  "return-type": "4 4",
  annotation: "1 3"
};
const kindAbbr = {
  "class": "C",
  "object": "O",
  "interface": "I",
  "enum": "E",
  "annotation": "@",
  "data-class": "D",
  "function": "F",
  "composable-function": "CF",
  "expect-function": "EF",
  "actual-function": "AF",
  "unknown": "?",
  "external": "E"
};
const analyzerMeta = {
  constructor: { label: "Constructor", abbr: "C", analyzer: "Constructor Analyzer", confidence: "Strong", color: "#34d399" },
  import: { label: "Import", abbr: "I", analyzer: "Import Analyzer", confidence: "Informational", color: "#60a5fa" },
  property: { label: "Property", abbr: "P", analyzer: "Property Analyzer", confidence: "Medium", color: "#fbbf24" },
  inheritance: { label: "Inheritance", abbr: "H", analyzer: "Inheritance Analyzer", confidence: "Strong", color: "#34d399" },
  method: { label: "Method Param", abbr: "M", analyzer: "Method Parameter Analyzer", confidence: "Medium", color: "#fbbf24" },
  "return-type": { label: "Return Type", abbr: "R", analyzer: "Return Type Analyzer", confidence: "Medium", color: "#fbbf24" },
  annotation: { label: "Annotation", abbr: "@", analyzer: "Annotation Analyzer", confidence: "Informational", color: "#60a5fa" }
};
const graph = document.getElementById("graph");
const treeEl = document.getElementById("tree");
const inspectorEl = document.getElementById("inspector");
const statusLeft = document.getElementById("statusLeft");
const edgePanelEl = document.getElementById("edgePanel");
const importNoticeEl = document.getElementById("importNotice");
const canvasShell = document.querySelector(".canvasShell");
const filterElements = {
  module: document.getElementById("moduleFilter"),
  sourceSet: document.getElementById("sourceSetFilter"),
  pkg: document.getElementById("packageFilter"),
  layer: document.getElementById("layerFilter")
};

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;");
}

function classNodesByFilters() {
  return data.nodes.filter(node => {
    return (!state.filters.module || node.module === state.filters.module)
      && (!state.filters.sourceSet || node.sourceSet === state.filters.sourceSet)
      && (!state.filters.pkg || node.pkg === state.filters.pkg)
      && (!state.filters.layer || node.layer === state.filters.layer);
  });
}

function getLevelData() {
  const classNodes = classNodesByFilters();
  const classIds = new Set(classNodes.map(node => node.id));
  const classEdges = data.edges.filter(edge => classIds.has(edge.from) && classIds.has(edge.to));
  if (state.level === "class") {
    return { nodes: classNodes, edges: classEdges };
  }
  const key = state.level === "module" ? "module" : "pkg";
  const groups = new Map();
  classNodes.forEach(node => {
    const id = node[key];
    if (!groups.has(id)) {
      groups.set(id, {
        id,
        label: state.level === "module" ? id : id.split(".").at(-1),
        pkg: id,
        module: state.level === "module" ? id : node.module,
        sourceSet: node.sourceSet,
        file: "-",
        kind: state.level,
        origin: "DECLARATION",
        resolved: true,
        layer: node.layer,
        methods: 0,
        properties: 0,
        fanIn: 0,
        fanOut: 0
      });
    }
    const group = groups.get(id);
    group.methods += node.methods;
    group.properties += 1;
  });
  const seen = new Set();
  const edges = [];
  classEdges.forEach(edge => {
    const fromNode = classNodes.find(node => node.id === edge.from);
    const toNode = classNodes.find(node => node.id === edge.to);
    if (!fromNode || !toNode) return;
    const from = fromNode[key];
    const to = toNode[key];
    if (from === to) return;
    const edgeKey = `${'$'}{from}|${'$'}{to}|${'$'}{edge.type}`;
    if (seen.has(edgeKey)) return;
    seen.add(edgeKey);
    edges.push({ from, to, type: edge.type, snippet: edge.snippet });
  });
  const nodes = Array.from(groups.values()).map(node => ({
    ...node,
    fanIn: edges.filter(edge => edge.to === node.id).length,
    fanOut: edges.filter(edge => edge.from === node.id).length
  }));
  return { nodes, edges };
}

function filteredData() {
  const base = getLevelData();
  const query = state.search.trim().toLowerCase();
  const edges = base.edges.filter(edge => edge.type === "import" || state.activeTypes.has(edge.type));
  let nodes = base.nodes;
  if (query) {
    const matching = new Set(nodes
      .filter(node => [node.label, node.pkg, node.module].join(" ").toLowerCase().includes(query))
      .map(node => node.id));
    edges.forEach(edge => {
      if (matching.has(edge.from)) matching.add(edge.to);
      if (matching.has(edge.to)) matching.add(edge.from);
    });
    nodes = nodes.filter(node => matching.has(node.id));
  }
  const ids = new Set(nodes.map(node => node.id));
  let visibleEdges = edges.filter(edge => ids.has(edge.from) && ids.has(edge.to));
  if (state.focusMode && state.level === "class") {
    const focusId = state.selectedId || state.hoveredId;
    if (focusId) {
      const focusIds = connectedIds(focusId, visibleEdges);
      nodes = nodes.filter(node => focusIds.has(node.id));
      visibleEdges = visibleEdges.filter(edge => focusIds.has(edge.from) && focusIds.has(edge.to));
    }
  }
  return {
    nodes,
    edges: visibleEdges
  };
}

function visibleGraphData(nodes, edges, activeId) {
  const highlightedEdgeKey =
    state.hoveredEdgeKey
    || state.selectedEdgeKey;
  const highlightedEdge =
    highlightedEdgeKey
      ? findEdgeByKey(highlightedEdgeKey)
      : null;
  const highlightedEdgeKeyValue = highlightedEdge ? edgeKey(highlightedEdge) : null;
  const importCandidates = edges.filter(edge => {
    if (edge.type !== "import") return false;
    if (state.activeTypes.has("import")) {
      return activeId
        ? edge.from === activeId || edge.to === activeId
        : true;
    }
    return highlightedEdgeKeyValue && edgeKey(edge) === highlightedEdgeKeyValue;
  });
  const visibleImportKeys =
    new Set(
      importCandidates
        .map(edgeKey)
    );
  const graphEdges =
    edges.filter(edge => {
      if (edge.type !== "import") return true;
      return visibleImportKeys.has(edgeKey(edge));
    });
  const graphEdgeIds =
    new Set();
  graphEdges.forEach(edge => {
    graphEdgeIds.add(edge.from);
    graphEdgeIds.add(edge.to);
  });
  const graphNodes =
    nodes.filter(node => {
      return node.origin === "DECLARATION"
        || graphEdgeIds.has(node.id)
        || node.id === activeId;
    });
  return {
    nodes: graphNodes,
    edges: graphEdges,
    visibleImportCount: visibleImportKeys.size,
    totalImportCount:
      activeId
        ? edges.filter(edge => edge.type === "import" && (edge.from === activeId || edge.to === activeId)).length
        : edges.filter(edge => edge.type === "import").length,
    showAllImports: state.activeTypes.has("import"),
    highlightedImport: highlightedEdge && highlightedEdge.type === "import"
  };
}

function renderImportNotice(visibility) {
  if (!visibility || !visibility.totalImportCount || !visibility.showAllImports && !visibility.highlightedImport) {
    importNoticeEl.classList.add("hidden");
    importNoticeEl.textContent = "";
    return;
  }
  if (visibility.showAllImports) {
    importNoticeEl.textContent =
      `Showing all ${'$'}{visibility.visibleImportCount} import dependencies on the graph.`;
    importNoticeEl.classList.remove("hidden");
    return;
  }
  if (visibility.highlightedImport) {
    importNoticeEl.textContent =
      "Showing selected import dependency.";
    importNoticeEl.classList.remove("hidden");
    return;
  }
  importNoticeEl.classList.add("hidden");
  importNoticeEl.textContent = "";
}

function layout(nodes) {
  const rect = graph.getBoundingClientRect();
  const width = Math.max(900, rect.width);
  const height = Math.max(560, rect.height);
  const order = ["presentation", "domain", "data", "core", "external"];
  const byLayer = Object.fromEntries(order.map(layer => [layer, []]));
  nodes.forEach(node => (byLayer[node.layer] || byLayer.core).push(node));
  const positions = {};
  order.forEach((layer, layerIndex) => {
    const layerNodes = byLayer[layer] || [];
    const y = ((layerIndex + 1) / (order.length + 1)) * height;
    layerNodes.forEach((node, index) => {
      const span = Math.max(180, (layerNodes.length - 1) * 150);
      const x = width / 2 - span / 2 + (layerNodes.length === 1 ? span / 2 : (index / (layerNodes.length - 1)) * span);
      positions[node.id] = state.nodePositions[node.id] || { x, y };
    });
  });
  return positions;
}

function connectedIds(id, edges) {
  const result = new Set();
  if (!id) return result;
  result.add(id);
  edges.forEach(edge => {
    if (edge.from === id) result.add(edge.to);
    if (edge.to === id) result.add(edge.from);
  });
  return result;
}

function edgeKey(edge) {
  return `${'$'}{edge.from}|${'$'}{edge.to}|${'$'}{edge.type}`;
}

function nodeLabel(id) {
  const node = data.nodes.find(item => item.id === id);
  return node ? node.label : String(id).split(".").at(-1);
}

function nodeInfo(id) {
  return data.nodes.find(item => item.id === id) || {
    id,
    label: nodeLabel(id),
    pkg: "",
    module: "",
    sourceSet: "",
    file: "",
    kind: "unknown",
    origin: "UNRESOLVED_IMPORT",
    resolved: false,
    layer: "external"
  };
}

function dependencyReason(edge) {
  const from = nodeLabel(edge.from);
  const to = nodeLabel(edge.to);
  switch (edge.type) {
    case "constructor": return `${'$'}{from} receives ${'$'}{to} through constructor injection`;
    case "property": return `${'$'}{to} is declared as a property in ${'$'}{from}`;
    case "method": return `${'$'}{to} is used as a method parameter or method dependency in ${'$'}{from}`;
    case "return-type": return `${'$'}{to} is used as a return type in ${'$'}{from}`;
    case "inheritance": return `${'$'}{from} extends or implements ${'$'}{to}`;
    case "import": return `${'$'}{to} is imported into ${'$'}{from}`;
    case "annotation": return `${'$'}{to} is applied as an annotation in ${'$'}{from}`;
    default: return `${'$'}{from} depends on ${'$'}{to}`;
  }
}

function selectEdge(edge) {
  const key = edgeKey(edge);
  state.selectedEdgeKey = state.selectedEdgeKey === key ? null : key;
  state.hoveredEdgeKey = null;
  state.selectedId = null;
  renderAll();
}

function openDependency(edge) {
  state.selectedEdgeKey = edgeKey(edge);
  state.hoveredEdgeKey = null;
  renderGraph();
  renderInspector();
  renderEdgePanel();
}

function explorerBaseEdges(scopeId) {
  const visibleNodeIds = new Set(classNodesByFilters().map(node => node.id));
  return data.edges.filter(edge => {
    return visibleNodeIds.has(edge.from)
      && visibleNodeIds.has(edge.to)
      && (!scopeId || edge.from === scopeId || edge.to === scopeId);
  });
}

function dependencyMatchesQuery(edge, query) {
  if (!query) return true;
  const from = nodeInfo(edge.from);
  const to = nodeInfo(edge.to);
  const meta = analyzerMeta[edge.type] || analyzerMeta.import;
  return [
    from.label,
    from.pkg,
    from.module,
    to.label,
    to.pkg,
    to.module,
    edge.type,
    meta.label,
    dependencyReason(edge),
    edge.snippet || ""
  ].join(" ").toLowerCase().includes(query);
}

function dependencyExplorerEdges(scopeId) {
  const query = state.dependencySearch.trim().toLowerCase();
  return explorerBaseEdges(scopeId)
    .filter(edge => dependencyMatchesQuery(edge, query))
    .sort((left, right) => {
      const leftText = `${'$'}{nodeLabel(left.from)} ${'$'}{nodeLabel(left.to)} ${'$'}{left.type}`;
      const rightText = `${'$'}{nodeLabel(right.from)} ${'$'}{nodeLabel(right.to)} ${'$'}{right.type}`;
      return leftText.localeCompare(rightText);
    });
}

function renderDependencyExplorer(scopeId = null) {
  const edges = dependencyExplorerEdges(scopeId);
  const total = explorerBaseEdges(scopeId).length;
  const visible = edges.slice(0, 80);
  const label = scopeId ? "Class Dependency Explorer" : "Dependency Explorer";
  return `
    <section class="section" style="${'$'}{scopeId ? "" : "padding-left:0;padding-right:0"}">
      <div class="sectionTitle">${'$'}{label}</div>
      <input class="dependencySearch" data-dependency-search data-scope-id="${'$'}{escapeHtml(scopeId || "")}" value="${'$'}{escapeHtml(state.dependencySearch)}" placeholder="Search dependencies, classes, packages, types...">
      <div class="dependencyList" data-dependency-list data-scope-id="${'$'}{escapeHtml(scopeId || "")}">
        ${'$'}{renderDependencyRows(visible)}
        ${'$'}{edges.length > visible.length ? `<div class="dependencyEmpty">Showing 80 of ${'$'}{edges.length}. Narrow the search to find more.</div>` : ""}
        ${'$'}{!edges.length ? `<div class="dependencyEmpty">${'$'}{total ? "No dependencies match the search." : "No dependencies match current filters."}</div>` : ""}
      </div>
    </section>
  `;
}

function renderDependencyRows(edges) {
  return edges.map(edge => {
    const from = nodeInfo(edge.from);
    const to = nodeInfo(edge.to);
    const meta = analyzerMeta[edge.type] || analyzerMeta.import;
    const selected = state.selectedEdgeKey === edgeKey(edge);
    return `
      <div class="dependencyRow ${'$'}{selected ? "selected" : ""}" data-edge-key="${'$'}{escapeHtml(edgeKey(edge))}">
        <div class="dependencyPath">
          <i class="dot" style="background:${'$'}{layerColors[from.layer] || layerColors.external}"></i>
          <span title="${'$'}{escapeHtml(from.pkg)}">${'$'}{escapeHtml(from.label)}</span>
          <span class="edgeArrow line-${'$'}{escapeHtml(edge.type)}" style="color:${'$'}{meta.color}"></span>
          <i class="dot" style="background:${'$'}{layerColors[to.layer] || layerColors.external}"></i>
          <span title="${'$'}{escapeHtml(to.pkg)}">${'$'}{escapeHtml(to.label)}</span>
        </div>
        <div class="dependencyType"><span>[${'$'}{escapeHtml(meta.abbr)}]</span><span>${'$'}{escapeHtml(meta.label)}</span><span>·</span><span>${'$'}{escapeHtml(meta.analyzer)}</span></div>
      </div>
    `;
  }).join("");
}

function renderGraph() {
  const filtered = filteredData();
  const activeId = state.hoveredId || state.selectedId;
  const graphData = visibleGraphData(filtered.nodes, filtered.edges, activeId);
  const { nodes, edges } = graphData;
  renderImportNotice(graphData);
  const positions = layout(nodes);
  const connected = connectedIds(activeId, edges);
  const highlightedEdgeKey =
    state.hoveredEdgeKey
    || state.selectedEdgeKey;
  const selectedEdge = findEdgeByKey(highlightedEdgeKey);
  const selectedEdgeIds = selectedEdge ? new Set([selectedEdge.from, selectedEdge.to]) : null;
  graph.innerHTML = `
    <defs>
      <pattern id="grid" width="24" height="24" patternUnits="userSpaceOnUse">
        <path d="M 24 0 L 0 0 0 24" fill="none" stroke="rgba(255,255,255,0.04)" stroke-width="0.5"></path>
      </pattern>
      <marker id="arrow" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
        <path d="M0,0 L0,6 L6,3 z" fill="rgba(255,255,255,0.22)"></path>
      </marker>
      <marker id="arrowActive" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
        <path d="M0,0 L0,6 L6,3 z" fill="#7c4dff"></path>
      </marker>
      <marker id="arrowImport" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
        <path d="M0,0 L0,6 L6,3 z" fill="rgba(96,165,250,0.32)"></path>
      </marker>
    </defs>
    <rect width="100%" height="100%" fill="url(#grid)"></rect>
    <g id="viewport" transform="translate(${'$'}{state.pan.x},${'$'}{state.pan.y}) scale(${'$'}{state.zoom})"></g>
  `;
  const viewport = graph.querySelector("#viewport");
  edges.forEach(edge => {
    const from = positions[edge.from];
    const to = positions[edge.to];
    if (!from || !to) return;
    const mx = (from.x + to.x) / 2;
    const my = (from.y + to.y) / 2 - 42;
    const pathData = `M${'$'}{from.x},${'$'}{from.y} Q${'$'}{mx},${'$'}{my} ${'$'}{to.x},${'$'}{to.y}`;
    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", pathData);
    const isSelectedEdge = highlightedEdgeKey === edgeKey(edge);
    const isConnectedEdge = activeId && connected.has(edge.from) && connected.has(edge.to);
    const isActiveEdge = edge.type === "import" ? isSelectedEdge : isSelectedEdge || isConnectedEdge;
    const pathClasses = ["edge", `edge-${'$'}{edge.type}`];
    if (isActiveEdge) pathClasses.push("active");
    if (activeId && !isConnectedEdge && !isSelectedEdge) pathClasses.push("dimmed");
    if (!activeId && selectedEdgeIds && !isSelectedEdge) pathClasses.push("dimmed");
    path.setAttribute("class", pathClasses.join(" "));
    if (typeDash[edge.type]) path.setAttribute("stroke-dasharray", typeDash[edge.type]);
    viewport.appendChild(path);
    const hitArea = document.createElementNS("http://www.w3.org/2000/svg", "path");
    hitArea.setAttribute("d", pathData);
    hitArea.setAttribute("class", edge.type === "import" ? "edgeHitArea edgeHitArea-import" : "edgeHitArea");
    hitArea.addEventListener("click", event => {
      event.stopPropagation();
      if (state.handMode) {
        return;
      }
      selectEdge(edge);
    });
    viewport.appendChild(hitArea);
  });
  nodes.forEach(node => {
    const pos = positions[node.id];
    if (!pos) return;
    const color = layerColors[node.layer] || layerColors.core;
    const faded =
      (activeId && !connected.has(node.id))
      || (!activeId && selectedEdgeIds && !selectedEdgeIds.has(node.id));
    const neighbor =
      activeId
      && connected.has(node.id)
      && node.id !== activeId;
    const selected = node.id === state.selectedId;
    const dragging = node.id === state.draggingNodeId;
    const group = document.createElementNS("http://www.w3.org/2000/svg", "g");
    group.setAttribute("class", ["node", selected ? "selected" : "", dragging ? "dragging" : ""].filter(Boolean).join(" "));
    group.setAttribute("transform", `translate(${'$'}{pos.x},${'$'}{pos.y})`);
    group.style.opacity = faded ? "0.3" : neighbor ? "0.9" : "1";
    let dragStart = null;
    group.addEventListener("mousedown", event => {
      if (state.handMode) {
        return;
      }
      event.stopPropagation();
      dragStart = {
        x: event.clientX,
        y: event.clientY,
        pos: { ...pos },
        moved: false
      };
      state.draggingNodeId = node.id;
      renderGraph();
      const move = moveEvent => {
        const dx = (moveEvent.clientX - dragStart.x) / state.zoom;
        const dy = (moveEvent.clientY - dragStart.y) / state.zoom;
        if (Math.abs(dx) > 2 || Math.abs(dy) > 2) {
          dragStart.moved = true;
        }
        state.nodePositions[node.id] = {
          x: dragStart.pos.x + dx,
          y: dragStart.pos.y + dy
        };
        scheduleGraphRender();
      };
      const up = upEvent => {
        window.removeEventListener("mousemove", move);
        window.removeEventListener("mouseup", up);
        state.draggingNodeId = null;
        if (!dragStart.moved) {
          state.selectedEdgeKey = null;
          state.selectedId = state.selectedId === node.id ? null : node.id;
          renderAll();
        } else {
          renderGraph();
        }
        dragStart = null;
      };
      window.addEventListener("mousemove", move);
      window.addEventListener("mouseup", up);
    });
    group.addEventListener("mouseenter", () => { state.hoveredId = node.id; renderGraph(); });
    group.addEventListener("mouseleave", () => { state.hoveredId = null; renderGraph(); });
    group.innerHTML = `
      ${'$'}{selected ? `<circle class="nodeRing" r="34" fill="none" stroke="${'$'}{color}" stroke-width="1.4" stroke-dasharray="4 3"></circle>` : ""}
      <circle class="nodeCore" r="27" fill="${'$'}{color}22" stroke="${'$'}{color}" stroke-width="${'$'}{selected ? 2 : 1.2}"></circle>
      <text y="4" text-anchor="middle" fill="${'$'}{color}" font-size="10" font-weight="700">${'$'}{kindAbbr[node.kind] || "N"}</text>
      <text class="nodeLabel" y="42">${'$'}{escapeHtml(node.label)}</text>
      <text class="nodePkg" y="56">${'$'}{escapeHtml(String(node.pkg).split(".").at(-1))}</text>
    `;
    viewport.appendChild(group);
  });
}

function scheduleGraphRender() {
  if (state.graphFrame) {
    return;
  }
  state.graphFrame =
    requestAnimationFrame(() => {
      state.graphFrame = null;
      renderGraph();
    });
}

function renderTreeNodes(nodes, depth = 0) {
  return nodes.map(node => {
    const hasChildren = node.children && node.children.length;
    const expanded = state.expanded.has(node.id);
    const selected = state.selectedId === node.id;
    const icon = node.kind === "module" ? "M" : node.kind === "sourceSet" ? "S" : node.kind === "package" ? "P" : "C";
    return `
      <div class="treeItem ${'$'}{selected ? "selected" : ""}" data-tree-id="${'$'}{escapeHtml(node.id)}" style="padding-left:${'$'}{8 + depth * 14}px">
        <span class="treeToggle">${'$'}{hasChildren ? (expanded ? "v" : ">") : ""}</span>
        <span>${'$'}{icon}</span>
        <span class="treeLabel">${'$'}{escapeHtml(node.label)}</span>
      </div>
      ${'$'}{hasChildren && expanded ? renderTreeNodes(node.children, depth + 1) : ""}
    `;
  }).join("");
}

function renderTree() {
  if (state.expanded.size === 0) {
    data.tree.forEach(moduleNode => state.expanded.add(moduleNode.id));
  }
  treeEl.innerHTML = renderTreeNodes(data.tree);
  treeEl.querySelectorAll(".treeItem").forEach(item => {
    item.addEventListener("click", () => {
      const id = item.getAttribute("data-tree-id");
      const node = findTreeNode(id, data.tree);
      if (node && node.children && node.children.length) {
        if (state.expanded.has(id)) state.expanded.delete(id);
        else state.expanded.add(id);
      } else {
        state.selectedEdgeKey = null;
        state.selectedId = id;
      }
      renderAll();
    });
    item.addEventListener("mouseenter", () => {
      const id = item.getAttribute("data-tree-id");
      const node = findTreeNode(id, data.tree);
      if (node && (!node.children || node.children.length === 0)) {
        state.hoveredId = id;
        renderGraph();
      }
    });
    item.addEventListener("mouseleave", () => {
      state.hoveredId = null;
      renderGraph();
    });
  });
}

function findTreeNode(id, nodes) {
  for (const node of nodes) {
    if (node.id === id) return node;
    const found = findTreeNode(id, node.children || []);
    if (found) return found;
  }
  return null;
}

function renderInspector() {
  const node = data.nodes.find(item => item.id === state.selectedId);
  if (!node) {
    inspectorEl.innerHTML = renderArchitectureReport();
    bindDependencyRows();
    return;
  }
  const outgoing = data.edges.filter(edge => edge.from === node.id);
  const incoming = data.edges.filter(edge => edge.to === node.id);
  inspectorEl.innerHTML = `
    <div class="inspectorHeader">
      <div class="nodeBadge" style="background:${'$'}{layerColors[node.layer]}22;color:${'$'}{layerColors[node.layer]}">${'$'}{kindAbbr[node.kind] || "C"}</div>
      <div>
        <div style="font-weight:700">${'$'}{escapeHtml(node.label)}</div>
        <div style="color:var(--muted-foreground);font-size:11px">${'$'}{escapeHtml(symbolSubtitle(node))}</div>
      </div>
    </div>
    <div class="inspectorBody">
      <section class="section">
        <div class="sectionTitle">Symbol Information</div>
        ${'$'}{infoRow("Package", node.pkg)}
        ${'$'}{infoRow("Module", node.module)}
        ${'$'}{infoRow("Source Set", node.sourceSet)}
        ${'$'}{infoRow("File", node.file)}
        ${'$'}{infoRow("Layer", node.layer)}
        ${'$'}{infoRow("Origin", node.origin || "DECLARATION")}
        ${'$'}{infoRow("Resolved", node.resolved === false ? "false" : "true")}
        ${'$'}{node.kind.includes("function") ? infoRow("Composable", node.isComposable ? "true" : "false") : ""}
        ${'$'}{node.platformModifier && node.platformModifier !== "NONE" ? infoRow("Platform", node.platformModifier) : ""}
      </section>
      <section class="section">
        <div class="sectionTitle">Dependencies</div>
        ${'$'}{renderGroupedDependencySection("Outgoing", outgoing, edge => edge.to)}
        ${'$'}{renderGroupedDependencySection("Incoming", incoming, edge => edge.from)}
        ${'$'}{nodeHasImportDependencies(node.id) ? `<button class="chip showImportsButton ${'$'}{state.activeTypes.has("import") ? "active" : ""}" data-show-imports>${'$'}{state.activeTypes.has("import") ? "Hide import edges" : "Show all import edges"}</button>` : ""}
      </section>
      ${'$'}{renderDependencyExplorer(node.id)}
      <section class="section">
        <div class="sectionTitle">Metrics</div>
        <div class="metrics">
          ${'$'}{metric("Fan In", node.fanIn)}
          ${'$'}{metric("Fan Out", node.fanOut)}
          ${'$'}{metric("Methods", node.methods)}
          ${'$'}{metric("Properties", node.properties)}
        </div>
      </section>
      <section class="section">
        <div class="sectionTitle">Architecture Analysis</div>
        ${'$'}{renderAnalysisItems(node)}
      </section>
    </div>
  `;
  inspectorEl.querySelectorAll("[data-node-id]").forEach(item => {
    item.addEventListener("click", () => {
      state.selectedEdgeKey = null;
      state.selectedId = item.getAttribute("data-node-id");
      renderAll();
    });
  });
  bindDependencyRows();
}

function renderArchitectureReport() {
  return `
    <div class="report">
      <div class="sectionTitle">Architecture Report</div>
      <div class="reportGrid">
        ${'$'}{reportCard(data.summary.classes, "Classes")}
        ${'$'}{reportCard(data.summary.packages, "Packages")}
        ${'$'}{reportCard(data.summary.modules, "Modules")}
        ${'$'}{reportCard(data.summary.dependencies, "Dependencies")}
      </div>
      ${'$'}{renderDependencyExplorer()}
      <section class="section" style="padding-left:0;padding-right:0">
        <div class="sectionTitle">Dependencies By Type</div>
        ${'$'}{renderDependencyTypeRows()}
      </section>
      <section class="section" style="padding-left:0;padding-right:0">
        <div class="sectionTitle">Implemented Analyzers</div>
        ${'$'}{renderAnalyzerRows()}
      </section>
      <section class="section" style="padding-left:0;padding-right:0;border-bottom:0">
        <div class="sectionTitle">Planned Analyzers</div>
        <div class="analysisItem planned"><i class="dot" style="border:1px solid var(--muted-foreground)"></i><span>Annotation Analyzer</span><span class="analysisCount">planned</span></div>
      </section>
    </div>
  `;
}

function reportCard(value, label) {
  return `<div class="reportCard"><div class="reportValue">${'$'}{escapeHtml(value)}</div><div class="reportLabel">${'$'}{escapeHtml(label)}</div></div>`;
}

function renderDependencyTypeRows() {
  const types = ["constructor", "inheritance", "property", "return-type", "method", "import"];
  return types.map(type => {
    const meta = analyzerMeta[type];
    const count = data.edges.filter(edge => edge.type === type).length;
    return `<div class="typeRow"><span class="typeLine line-${'$'}{escapeHtml(type)}" style="color:${'$'}{meta.color}"></span><span>${'$'}{escapeHtml(meta.label)}</span><span class="typeCount">${'$'}{count}</span></div>`;
  }).join("");
}

function renderAnalyzerRows() {
  const descriptions = {
    constructor: "Detects dependencies injected through constructors.",
    inheritance: "Detects superclass and interface implementations.",
    property: "Detects dependencies declared as class-level properties.",
    "return-type": "Detects types referenced as function return types.",
    method: "Detects dependencies used as function parameter types.",
    import: "Detects imported project classes and objects."
  };
  return ["constructor", "inheritance", "property", "return-type", "method", "import"].map(type => {
    const meta = analyzerMeta[type];
    return `<div class="analyzerRow"><i class="dot" style="background:transparent;border:1px solid ${'$'}{meta.color}"></i><div><div class="analyzerName">${'$'}{escapeHtml(meta.analyzer)}</div><div class="analyzerDescription">${'$'}{escapeHtml(descriptions[type])}</div></div></div>`;
  }).join("");
}

function renderAnalysisItems(node) {
  const implemented = ["constructor", "inheritance", "property", "return-type", "method", "import"];
  const planned = ["annotation"];
  return `
    ${'$'}{implemented.map(type => {
      const meta = analyzerMeta[type];
      const count = data.edges.filter(edge => (edge.from === node.id || edge.to === node.id) && edge.type === type).length;
      return `<div class="analysisItem ${'$'}{count ? "active" : ""}"><i class="dot" style="background:${'$'}{count ? meta.color : "transparent"};border:1px solid ${'$'}{meta.color}"></i><span>${'$'}{escapeHtml(meta.label)}</span><span class="analysisCount">${'$'}{count || ""}</span></div>`;
    }).join("")}
    <div class="edgeKicker" style="margin-top:14px">Planned</div>
    ${'$'}{planned.map(type => `<div class="analysisItem planned"><i class="dot" style="border:1px solid var(--muted-foreground)"></i><span>${'$'}{escapeHtml(analyzerMeta[type].label)}</span><span class="analysisCount">planned</span></div>`).join("")}
  `;
}

function renderEdgePanel() {
  const { edges } = filteredData();
  const edge =
    edges.find(item => edgeKey(item) === state.selectedEdgeKey)
    || data.edges.find(item => edgeKey(item) === state.selectedEdgeKey);
  if (!edge) {
    edgePanelEl.classList.add("hidden");
    canvasShell.classList.remove("hasEdgePanel");
    edgePanelEl.innerHTML = "";
    return;
  }
  const meta = analyzerMeta[edge.type] || analyzerMeta.import;
  edgePanelEl.classList.remove("hidden");
  canvasShell.classList.add("hasEdgePanel");
  edgePanelEl.innerHTML = `
    <div class="edgeMeta">
      <div class="edgeKicker">Dependency</div>
      <div class="edgeDependency"><i class="dot" style="background:${'$'}{meta.color}"></i><span class="edgeName">${'$'}{escapeHtml(nodeLabel(edge.from))}</span><span class="edgeArrow line-${'$'}{escapeHtml(edge.type)}"></span><i class="dot" style="background:${'$'}{meta.color}"></i><span class="edgeName">${'$'}{escapeHtml(nodeLabel(edge.to))}</span></div>
      <div class="edgeKicker">Type</div>
      <div class="typeBadge"><span>[${'$'}{escapeHtml(meta.abbr)}]</span>${'$'}{escapeHtml(meta.label)}</div>
      <div class="edgeKicker" style="margin-top:18px">Detected By</div>
      <div style="font-weight:700;color:rgba(226,226,232,.78)">${'$'}{escapeHtml(meta.analyzer)}</div>
      <div class="confidence"><i class="dot" style="background:${'$'}{meta.color}"></i>${'$'}{escapeHtml(meta.confidence)}</div>
    </div>
    <div class="edgeDetail">
      <div class="edgeKicker">Reason</div>
      <div style="font-size:14px;color:rgba(226,226,232,.76);margin-bottom:18px">${'$'}{escapeHtml(dependencyReason(edge))}</div>
      <div class="edgeKicker">Source</div>
      <pre class="sourceBlock">${'$'}{escapeHtml(edge.snippet || dependencyReason(edge))}</pre>
    </div>
    <button class="edgeClose" title="Close">x</button>
  `;
  edgePanelEl.querySelector(".edgeClose").addEventListener("click", () => {
    state.selectedEdgeKey = null;
    renderAll();
  });
}

function infoRow(label, value) {
  return `<div class="row"><span class="rowLabel">${'$'}{label}</span><span class="rowValue">${'$'}{escapeHtml(value)}</span></div>`;
}

function symbolSubtitle(node) {
  const parts = [node.kind, node.origin || "DECLARATION"];
  if (node.isComposable) {
    parts.push("Composable");
  }
  if (node.platformModifier && node.platformModifier !== "NONE") {
    parts.push(node.platformModifier);
  }
  return parts.join(" · ");
}

function nodeHasImportDependencies(nodeId) {
  return data.edges.some(edge => edge.type === "import" && (edge.from === nodeId || edge.to === nodeId));
}

function renderGroupedDependencySection(label, edges, targetSelector) {
  if (!edges.length) {
    return `
      <div style="color:var(--muted-foreground);font-size:10px;margin:12px 0 6px">${'$'}{label} (0)</div>
      <div class="rowValue">None</div>
    `;
  }
  const groups =
    dependencyTypeOrder()
      .map(type => ({
        type,
        edges: edges.filter(edge => edge.type === type)
      }))
      .filter(group => group.edges.length);
  return `
    <div style="color:var(--muted-foreground);font-size:10px;margin:12px 0 6px">${'$'}{label} (${'$'}{edges.length})</div>
    ${'$'}{groups.map(group => renderDependencyGroup(group.type, group.edges, targetSelector)).join("")}
  `;
}

function renderDependencyGroup(type, edges, targetSelector) {
  const meta = analyzerMeta[type] || analyzerMeta.import;
  const open = type !== "import" || edges.length <= 12;
  return `
    <details class="dependencyGroup" ${'$'}{open ? "open" : ""}>
      <summary class="dependencyGroupHeader">
        <span>${'$'}{escapeHtml(meta.label)}</span>
        <span class="count">${'$'}{edges.length}</span>
      </summary>
      ${'$'}{edges.map(edge => depChip(edge, targetSelector(edge))).join("")}
    </details>
  `;
}

function dependencyTypeOrder() {
  return ["constructor", "inheritance", "property", "method", "return-type", "import", "annotation"];
}

function depChip(edge, id) {
  const node = data.nodes.find(item => item.id === id);
  if (!node) return "";
  const meta = analyzerMeta[edge.type] || analyzerMeta.import;
  return `<div class="depChip" data-edge-key="${'$'}{escapeHtml(edgeKey(edge))}"><i class="dot" style="background:${'$'}{layerColors[node.layer]}"></i><span style="flex:1">${'$'}{escapeHtml(node.label)}</span><span class="rowValue">[${'$'}{escapeHtml(meta.abbr)}]</span></div>`;
}

function findEdgeByKey(key) {
  return data.edges.find(edge => edgeKey(edge) === key);
}

function bindDependencyRows() {
  inspectorEl.querySelectorAll("[data-edge-key]").forEach(item => {
    item.onmouseenter = () => {
      const key = item.getAttribute("data-edge-key");
      const edge = findEdgeByKey(key);
      if (edge && edge.type === "import" && !state.activeTypes.has("import")) {
        state.hoveredEdgeKey = key;
        renderGraph();
      }
    };
    item.onmouseleave = () => {
      if (state.hoveredEdgeKey === item.getAttribute("data-edge-key")) {
        state.hoveredEdgeKey = null;
        renderGraph();
      }
    };
    item.onclick = event => {
      event.stopPropagation();
      const edge = findEdgeByKey(item.getAttribute("data-edge-key"));
      if (edge) {
        openDependency(edge);
      }
    };
  });
  inspectorEl.querySelectorAll("[data-show-imports]").forEach(button => {
    button.onclick = event => {
      event.stopPropagation();
      if (state.activeTypes.has("import")) {
        state.activeTypes.delete("import");
      } else {
        state.activeTypes.add("import");
      }
      document.querySelectorAll("[data-type='import']").forEach(item => {
        item.classList.toggle("active", state.activeTypes.has("import"));
      });
      renderAll();
    };
  });
  inspectorEl.querySelectorAll("[data-dependency-search]").forEach(input => {
    input.oninput = event => {
      state.dependencySearch = event.target.value;
      const scopeId = input.getAttribute("data-scope-id") || null;
      const list = inspectorEl.querySelector(`[data-dependency-list][data-scope-id="${'$'}{scopeId || ""}"]`);
      if (!list) return;
      const edges = dependencyExplorerEdges(scopeId);
      const visible = edges.slice(0, 80);
      list.innerHTML = `
        ${'$'}{renderDependencyRows(visible)}
        ${'$'}{edges.length > visible.length ? `<div class="dependencyEmpty">Showing 80 of ${'$'}{edges.length}. Narrow the search to find more.</div>` : ""}
        ${'$'}{!edges.length ? `<div class="dependencyEmpty">No dependencies match the search.</div>` : ""}
      `;
      bindDependencyRows();
    };
  });
}

function metric(label, value) {
  return `<div class="metric"><div class="metricLabel">${'$'}{label}</div><div class="metricValue">${'$'}{value}</div></div>`;
}

function renderStatus() {
  statusLeft.textContent = `${'$'}{data.summary.classes} classes · ${'$'}{data.summary.dependencies} dependencies · ${'$'}{data.summary.modules} modules · ${'$'}{data.summary.packages} packages`;
}

function renderAll() {
  renderTree();
  renderGraph();
  renderInspector();
  renderEdgePanel();
  renderStatus();
}

function renderFilters() {
  fillFilter(filterElements.module, "All Modules", data.nodes.map(node => node.module), state.filters.module);
  fillFilter(filterElements.sourceSet, "All Source Sets", data.nodes.map(node => node.sourceSet), state.filters.sourceSet);
  fillFilter(filterElements.pkg, "All Packages", data.nodes.map(node => node.pkg), state.filters.pkg);
  fillFilter(filterElements.layer, "All Layers", data.nodes.map(node => node.layer), state.filters.layer);
}

function fillFilter(element, label, values, selected) {
  const unique = Array.from(new Set(values)).sort();
  element.innerHTML = `<option value="">${'$'}{label}</option>` + unique.map(value => `<option value="${'$'}{escapeHtml(value)}" ${'$'}{value === selected ? "selected" : ""}>${'$'}{escapeHtml(value)}</option>`).join("");
}

document.getElementById("searchInput").addEventListener("input", event => {
  state.search = event.target.value;
  renderGraph();
});
Object.entries(filterElements).forEach(([key, element]) => {
  element.addEventListener("change", event => {
    state.filters[key] = event.target.value;
    state.selectedId = null;
    state.selectedEdgeKey = null;
    renderAll();
  });
});
document.querySelectorAll("[data-level]").forEach(button => {
  button.addEventListener("click", () => {
    state.level = button.getAttribute("data-level");
    document.querySelectorAll("[data-level]").forEach(item => item.classList.toggle("active", item === button));
    state.selectedId = null;
    state.selectedEdgeKey = null;
    renderAll();
  });
});
document.querySelectorAll("[data-type]").forEach(button => {
  button.addEventListener("click", () => {
    const type = button.getAttribute("data-type");
    if (state.activeTypes.has(type)) state.activeTypes.delete(type);
    else state.activeTypes.add(type);
    button.classList.toggle("active", state.activeTypes.has(type));
    renderAll();
  });
});
document.getElementById("focusToggle").addEventListener("click", event => {
  state.focusMode = !state.focusMode;
  event.currentTarget.classList.toggle("active", state.focusMode);
  renderGraph();
});
document.getElementById("handTool").addEventListener("click", event => {
  state.handMode = !state.handMode;
  event.currentTarget.classList.toggle("active", state.handMode);
  canvasShell.classList.toggle("handMode", state.handMode);
  if (state.handMode) {
    state.selectedEdgeKey = null;
    renderEdgePanel();
  }
});
document.getElementById("zoomIn").addEventListener("click", () => { state.zoom = Math.min(2.8, state.zoom * 1.2); renderGraph(); });
document.getElementById("zoomOut").addEventListener("click", () => { state.zoom = Math.max(0.35, state.zoom * 0.8); renderGraph(); });
document.getElementById("fitGraph").addEventListener("click", () => { state.zoom = 1; state.pan = { x: 0, y: 0 }; renderGraph(); });
document.getElementById("resetLayout").addEventListener("click", () => {
  state.nodePositions = {};
  state.pan = { x: 0, y: 0 };
  state.zoom = 1;
  renderGraph();
});
let dragStart = null;
graph.addEventListener("mousedown", event => {
  if (!state.handMode) return;
  state.isPanning = true;
  canvasShell.classList.add("isPanning");
  dragStart = { x: event.clientX, y: event.clientY, pan: { ...state.pan } };
});
window.addEventListener("mousemove", event => {
  if (!dragStart) return;
  state.pan = {
    x: dragStart.pan.x + event.clientX - dragStart.x,
    y: dragStart.pan.y + event.clientY - dragStart.y
  };
  renderGraph();
});
window.addEventListener("mouseup", () => {
  dragStart = null;
  state.isPanning = false;
  canvasShell.classList.remove("isPanning");
});
graph.addEventListener("wheel", event => {
  event.preventDefault();
  state.zoom = Math.max(0.35, Math.min(2.8, state.zoom * (event.deltaY < 0 ? 1.08 : 0.92)));
  renderGraph();
}, { passive: false });
window.addEventListener("resize", renderGraph);
renderFilters();
renderAll();
</script>
</body>
</html>
        """.trimIndent()
    }
}
