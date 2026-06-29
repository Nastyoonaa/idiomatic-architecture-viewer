package com.app.workspace.model

sealed class WorkspaceState {
    data object Empty : WorkspaceState()
    data class Ready(val draft: ConfigurationDraft) : WorkspaceState()
}

data class ConfigurationDraft(
    val name: String,
    val assetIds: List<String>
)

data class SerializedConfiguration(
    val payload: String
)

data class WorkspaceFile(
    val name: String
)

data class ExportResult(
    val fileName: String,
    val checksumSize: Int
)

data class ImportResult(
    val name: String,
    val hasWarnings: Boolean
)

data class ValidationReport(
    val hasBlockingIssues: Boolean
)

data class AssetManifest(
    val assetCount: Int
)

data class WorkspaceSnapshot(
    val name: String
)

enum class WorkspaceEvent {
    Imported,
    Saved
}

enum class WorkspaceCommand {
    RefreshAssets
}

enum class SavePolicy {
    LocalOnly,
    SyncAfterSave
}

enum class ReloadPolicy {
    KeepCache,
    DropCache
}

enum class ConflictResolution {
    PreferLocal,
    PreferRemote
}

data class EncryptionProfile(
    val id: String
)

enum class ExportFormat(
    val extension: String
) {
    Json("json"),
    Archive("zip")
}

enum class ImportMode {
    Replace,
    Merge
}

