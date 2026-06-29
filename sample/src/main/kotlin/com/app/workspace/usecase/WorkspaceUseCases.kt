package com.app.workspace.usecase

import com.app.workspace.model.ConfigurationDraft
import com.app.workspace.model.ConflictResolution
import com.app.workspace.model.EncryptionProfile
import com.app.workspace.model.ExportResult
import com.app.workspace.model.ImportResult
import com.app.workspace.model.ReloadPolicy
import com.app.workspace.model.SavePolicy
import com.app.workspace.model.SerializedConfiguration
import com.app.workspace.model.ValidationReport
import com.app.workspace.model.WorkspaceFile
import com.app.workspace.model.WorkspaceSnapshot

class ImportConfigurationUseCase {
    fun import(draft: ConfigurationDraft, report: ValidationReport): ImportResult =
        ImportResult(draft.name, report.hasBlockingIssues)
}

class ExportConfigurationUseCase {
    fun export(file: WorkspaceFile, manifest: Any): ExportResult =
        ExportResult(file.name, manifest.hashCode())
}

class SaveWorkspaceUseCase {
    fun save(draft: ConfigurationDraft, policy: SavePolicy): WorkspaceSnapshot =
        WorkspaceSnapshot("${draft.name}-${policy.name}")
}

class ReloadWorkspaceUseCase {
    fun reload(
        policy: ReloadPolicy,
        permissions: Set<String>,
        flags: List<String>,
        preferences: Map<String, String>,
        migration: String,
        template: String,
        encryptionProfile: EncryptionProfile
    ) = com.app.workspace.model.WorkspaceState.Ready(
        ConfigurationDraft(
            "${policy.name}-${permissions.size}-${flags.size}-${preferences.size}-${migration}-${template}-${encryptionProfile.id}",
            emptyList()
        )
    )
}

class ValidateWorkspaceUseCase {
    fun validate(draft: ConfigurationDraft): ValidationReport =
        ValidationReport(draft.assetIds.isEmpty())
}

class ResolveAssetUseCase {
    fun resolve(assetId: String): WorkspaceFile = WorkspaceFile("$assetId.bin")
}

class CompressWorkspaceUseCase {
    fun compress(configuration: SerializedConfiguration): SerializedConfiguration =
        configuration.copy(payload = "compressed:${configuration.payload}")
}

class EncryptWorkspaceUseCase {
    fun encrypt(
        configuration: SerializedConfiguration,
        profile: EncryptionProfile
    ): SerializedConfiguration =
        configuration.copy(payload = "${profile.id}:${configuration.payload}")
}

class PublishWorkspaceUseCase {
    fun publish(snapshot: WorkspaceSnapshot) {
        snapshot.name.length
    }
}

class RollbackWorkspaceUseCase {
    fun rollback(conflictResolution: ConflictResolution) {
        conflictResolution.name.length
    }
}

