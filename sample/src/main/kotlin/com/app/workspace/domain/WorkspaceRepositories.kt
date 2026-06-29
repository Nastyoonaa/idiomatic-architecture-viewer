package com.app.workspace.domain

import com.app.workspace.model.AssetManifest
import com.app.workspace.model.ConfigurationDraft
import com.app.workspace.model.ConflictResolution
import com.app.workspace.model.EncryptionProfile
import com.app.workspace.model.WorkspaceEvent
import com.app.workspace.model.WorkspaceSnapshot

class ConfigurationRepository {
    fun draft(): ConfigurationDraft = ConfigurationDraft("draft", listOf("asset-a"))
}

class AssetRepository {
    fun manifest(): AssetManifest = AssetManifest(1)
}

class WorkspaceRepository {
    fun persist(snapshot: WorkspaceSnapshot) {
        snapshot.name.length
    }
}

class AuditRepository {
    fun record(event: WorkspaceEvent) {
        event.name.length
    }
}

class TemplateRepository {
    fun defaultTemplate(): String = "default"
}

class SyncRepository {
    fun schedule(snapshot: WorkspaceSnapshot) {
        snapshot.name.length
    }
}

class FeatureFlagRepository {
    fun flags(): List<String> = listOf("workspace-import")
}

class UserPreferenceRepository {
    fun preferences(): Map<String, String> = mapOf("theme" to "dark")
}

class EncryptionPolicyRepository {
    fun activeProfile(): EncryptionProfile = EncryptionProfile("local")
}

class MigrationRepository {
    fun plan(): String = "noop"
}

class SnapshotRepository {
    fun current(): WorkspaceSnapshot = WorkspaceSnapshot("current")
}

class PermissionRepository {
    fun permissions(): Set<String> = setOf("read", "write")
}

class ConflictRepository {
    fun resolve(conflictResolution: ConflictResolution): String = conflictResolution.name
}

