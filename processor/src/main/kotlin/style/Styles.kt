package export.style

object Styles {

    fun build(): String {

        return """
<style>

body {
    font-family: Arial;
    padding: 24px;
    background: #f5f5f5;
}

.graphLayout {
    display: flex;
    gap: 24px;
}

.graphContainer {
    flex: 1;
    min-width: 0;
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-top: 32px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    height: 85vh;
    overflow: auto;
}

.detailsPanel {
    width: 320px;
    min-width: 320px;
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin-top: 32px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.zoomControls {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
}

.zoomButton {
    border: none;
    border-radius: 8px;
    padding: 8px 12px;
    background: #6C43F3;
    color: white;
    cursor: pointer;
}

.selectedNodeName {
    font-size: 20px;
    font-weight: bold;
    color: #6C43F3;
    margin-bottom: 16px;
}

.infoRow {
    margin-bottom: 10px;
}

.infoLabel {
    font-weight: bold;
}

.dependenciesTitle {
    margin-top: 20px;
    margin-bottom: 10px;
    font-weight: bold;
}

.dependenciesList {
    padding-left: 20px;
}

.selectedNode rect {
    fill: #6C43F3 !important;
    stroke: #6C43F3 !important;
}

.selectedNode text {
    fill: white !important;
}

.dependencyLink {
    color: #6C43F3;
    text-decoration: none;
}

.dependencyLink:hover {
    text-decoration: underline;
}

.viewModelNode rect {
    fill: #7C4DFF !important;
    stroke: #7C4DFF !important;
}

.viewModelNode text {
    fill: white !important;
}

.useCaseNode rect {
    fill: #E8DDFF !important;
    stroke: #B388FF !important;
}

.repositoryNode rect {
    fill: #E3F2FD !important;
    stroke: #64B5F6 !important;
}

.mapperNode rect {
    fill: #E8F5E9 !important;
    stroke: #66BB6A !important;
}

.modelNode rect {
    fill: #E8F5E9 !important;
    stroke: #81C784 !important;
}

.resultNode rect {
    fill: #FFF8E1 !important;
    stroke: #FBC02D !important;
}

.dependencyNode rect {
    fill: #90CAF9 !important;
    stroke: #42A5F5 !important;
}

.dependencyNode text {
    fill: #0D47A1 !important;
}

.viewModelLegend {
    background: #7C4DFF;
}

.useCaseLegend {
    background: #E8DDFF;
}

.repositoryLegend {
    background: #64B5F6;
}

.mapperLegend {
    background: #66BB6A;
}

.dependencyLegend {
    background: #42A5F5;
}

.legendInsidePanel {
    margin-top: 16px;
}

.legendRow {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 12px;
    font-size: 15px;
}

.legendDot {
    width: 14px;
    height: 14px;
    border-radius: 50%;
    display: inline-block;
}

.sidebar {
    width: 320px;
    background: white;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 2px 10px rgba(0,0,0,.08);
}

.sidebarTitle {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
}

.sidebarSection {
    margin-top: 20px;
}

.sidebarSectionTitle {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 12px;
}

.sidebarItem {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 14px;
    border-radius: 10px;
    margin-bottom: 6px;
    cursor: pointer;
}

.sidebarItem:hover {
    background: #f4f1ff;
}

.sidebarBadge {
    background: #ececec;
    border-radius: 10px;
    padding: 4px 10px;
    font-size: 12px;
}

.packageLevel1 {
    padding-left: 24px;
}

.packageLevel2 {
    padding-left: 48px;
}

</style>
""".trimIndent()
    }
}