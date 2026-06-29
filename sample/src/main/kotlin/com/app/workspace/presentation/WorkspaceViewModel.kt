package com.app.workspace.presentation

import com.app.workspace.domain.ConfigurationRepository
import com.app.workspace.domain.AssetRepository
import com.app.workspace.domain.WorkspaceRepository
import com.app.workspace.domain.AuditRepository
import com.app.workspace.domain.TemplateRepository
import com.app.workspace.domain.SyncRepository
import com.app.workspace.domain.FeatureFlagRepository
import com.app.workspace.domain.UserPreferenceRepository
import com.app.workspace.domain.EncryptionPolicyRepository
import com.app.workspace.domain.MigrationRepository
import com.app.workspace.domain.SnapshotRepository
import com.app.workspace.domain.PermissionRepository
import com.app.workspace.io.ArchiveReader
import com.app.workspace.io.ArchiveWriter
import com.app.workspace.io.ChecksumCalculator
import com.app.workspace.io.FileGateway
import com.app.workspace.io.ImportProcessor
import com.app.workspace.io.SerializationGateway
import com.app.workspace.io.TempFileProvider
import com.app.workspace.io.WorkspaceExporter
import com.app.workspace.io.WorkspaceImporter
import com.app.workspace.model.AssetManifest
import com.app.workspace.model.ConfigurationDraft
import com.app.workspace.model.ConflictResolution
import com.app.workspace.model.EncryptionProfile
import com.app.workspace.model.ExportFormat
import com.app.workspace.model.ExportResult
import com.app.workspace.model.ImportMode
import com.app.workspace.model.ImportResult
import com.app.workspace.model.ReloadPolicy
import com.app.workspace.model.SavePolicy
import com.app.workspace.model.SerializedConfiguration
import com.app.workspace.model.ValidationReport
import com.app.workspace.model.WorkspaceCommand
import com.app.workspace.model.WorkspaceEvent
import com.app.workspace.model.WorkspaceFile
import com.app.workspace.model.WorkspaceSnapshot
import com.app.workspace.model.WorkspaceState
import com.app.workspace.runtime.Channel
import com.app.workspace.runtime.CoroutineScope
import com.app.workspace.runtime.MutableStateFlow
import com.app.workspace.runtime.StateFlow
import com.app.workspace.ui.Composable
import com.app.workspace.usecase.CompressWorkspaceUseCase
import com.app.workspace.usecase.EncryptWorkspaceUseCase
import com.app.workspace.usecase.ExportConfigurationUseCase
import com.app.workspace.usecase.ImportConfigurationUseCase
import com.app.workspace.usecase.PublishWorkspaceUseCase
import com.app.workspace.usecase.ReloadWorkspaceUseCase
import com.app.workspace.usecase.ResolveAssetUseCase
import com.app.workspace.usecase.RollbackWorkspaceUseCase
import com.app.workspace.usecase.SaveWorkspaceUseCase
import com.app.workspace.usecase.ValidateWorkspaceUseCase
import uml.UmlDiagram

@UmlDiagram
@Composable
fun WorkspaceScreen(
    state: WorkspaceState,
    events: Channel<WorkspaceEvent>
): WorkspaceCommand {
    return when (state) {
        WorkspaceState.Empty ->
            WorkspaceCommand.RefreshAssets

        is WorkspaceState.Ready ->
            WorkspaceCommand.RefreshAssets
    }
}

@UmlDiagram
class WorkspaceViewModel(
    private val configurationRepository: ConfigurationRepository = ConfigurationRepository(),
    private val assetRepository: AssetRepository = AssetRepository(),
    private val workspaceRepository: WorkspaceRepository = WorkspaceRepository(),
    private val auditRepository: AuditRepository = AuditRepository(),
    private val templateRepository: TemplateRepository = TemplateRepository(),
    private val syncRepository: SyncRepository = SyncRepository(),
    private val featureFlagRepository: FeatureFlagRepository = FeatureFlagRepository(),
    private val userPreferenceRepository: UserPreferenceRepository = UserPreferenceRepository(),
    private val encryptionPolicyRepository: EncryptionPolicyRepository = EncryptionPolicyRepository(),
    private val migrationRepository: MigrationRepository = MigrationRepository(),
    private val snapshotRepository: SnapshotRepository = SnapshotRepository(),
    private val permissionRepository: PermissionRepository = PermissionRepository(),
    private val importConfigurationUseCase: ImportConfigurationUseCase = ImportConfigurationUseCase(),
    private val exportConfigurationUseCase: ExportConfigurationUseCase = ExportConfigurationUseCase(),
    private val saveWorkspaceUseCase: SaveWorkspaceUseCase = SaveWorkspaceUseCase()
) {

    private val scope =
        CoroutineScope("workspace")

    private val mutableState =
        MutableStateFlow<WorkspaceState>(
            WorkspaceState.Empty
        )

    val state: StateFlow<WorkspaceState> =
        mutableState

    private val events =
        Channel<WorkspaceEvent>()

    private val commands =
        Channel<WorkspaceCommand>()

    private val reloadWorkspaceUseCase =
        ReloadWorkspaceUseCase()

    private val validateWorkspaceUseCase =
        ValidateWorkspaceUseCase()

    private val resolveAssetUseCase =
        ResolveAssetUseCase()

    private val compressWorkspaceUseCase =
        CompressWorkspaceUseCase()

    private val encryptWorkspaceUseCase =
        EncryptWorkspaceUseCase()

    private val publishWorkspaceUseCase =
        PublishWorkspaceUseCase()

    private val rollbackWorkspaceUseCase =
        RollbackWorkspaceUseCase()

    private val workspaceExporter =
        WorkspaceExporter()

    private val workspaceImporter =
        WorkspaceImporter()

    private val importProcessor =
        ImportProcessor()

    private val fileGateway =
        FileGateway()

    private val serializationGateway =
        SerializationGateway()

    private val checksumCalculator =
        ChecksumCalculator()

    private val archiveWriter =
        ArchiveWriter()

    private val archiveReader =
        ArchiveReader()

    private val tempFileProvider =
        TempFileProvider()

    fun importConfiguration(
        file: WorkspaceFile,
        mode: ImportMode
    ): ImportResult {
        val temporaryFile =
            tempFileProvider.create(
                "workspace-import"
            )
        val rawPayload =
            fileGateway.read(
                file
            )
        val archive =
            archiveReader.read(
                rawPayload
            )
        val draft =
            importProcessor.process(
                archive,
                mode
            )
        val validation =
            validateWorkspaceUseCase.validate(
                draft
            )
        val result =
            importConfigurationUseCase.import(
                draft,
                validation
            )

        workspaceImporter.import(
            temporaryFile,
            result
        )
        auditRepository.record(
            WorkspaceEvent.Imported
        )
        mutableState.value =
            WorkspaceState.Ready(
                draft
            )

        return result
    }

    fun exportConfiguration(
        format: ExportFormat,
        encryptionProfile: EncryptionProfile
    ): ExportResult {
        val snapshot =
            snapshotRepository.current()
        val manifest =
            assetRepository.manifest()
        val serialized =
            serializationGateway.encode(
                snapshot,
                manifest
            )
        val encrypted =
            encryptWorkspaceUseCase.encrypt(
                serialized,
                encryptionProfile
            )
        val compressed =
            compressWorkspaceUseCase.compress(
                encrypted
            )
        val file =
            workspaceExporter.export(
                compressed,
                format
            )

        archiveWriter.write(
            file,
            checksumCalculator.calculate(
                compressed
            )
        )

        return exportConfigurationUseCase.export(
            file,
            manifest
        )
    }

    fun save(
        policy: SavePolicy,
        conflictResolution: ConflictResolution
    ): WorkspaceSnapshot {
        val draft =
            configurationRepository.draft()
        val report =
            validateWorkspaceUseCase.validate(
                draft
            )

        if (
            report.hasBlockingIssues
        ) {
            rollbackWorkspaceUseCase.rollback(
                conflictResolution
            )
        }

        val snapshot =
            saveWorkspaceUseCase.save(
                draft,
                policy
            )
        workspaceRepository.persist(
            snapshot
        )
        syncRepository.schedule(
            snapshot
        )
        events.send(
            WorkspaceEvent.Saved
        )

        return snapshot
    }

    fun reload(
        policy: ReloadPolicy
    ): WorkspaceState {
        val permissions =
            permissionRepository.permissions()
        val flags =
            featureFlagRepository.flags()
        val preferences =
            userPreferenceRepository.preferences()
        val migration =
            migrationRepository.plan()
        val template =
            templateRepository.defaultTemplate()
        val profile =
            encryptionPolicyRepository.activeProfile()

        val state =
            reloadWorkspaceUseCase.reload(
                policy = policy,
                permissions = permissions,
                flags = flags,
                preferences = preferences,
                migration = migration,
                template = template,
                encryptionProfile = profile
            )

        mutableState.value =
            state
        commands.send(
            WorkspaceCommand.RefreshAssets
        )

        return state
    }

    private fun serializeDraft(
        draft: ConfigurationDraft,
        manifest: AssetManifest
    ): SerializedConfiguration {
        return serializationGateway.encode(
            WorkspaceSnapshot(
                draft.name
            ),
            manifest
        )
    }

    private fun resolveFiles(
        draft: ConfigurationDraft
    ): List<WorkspaceFile> {
        return draft.assetIds.map {
            resolveAssetUseCase.resolve(
                it
            )
        }
    }

    private fun publish(
        snapshot: WorkspaceSnapshot,
        validationReport: ValidationReport
    ) {
        if (
            validationReport.hasBlockingIssues
        ) {
            return
        }

        publishWorkspaceUseCase.publish(
            snapshot
        )
        scope.launch(
            "publish"
        )
    }
}
