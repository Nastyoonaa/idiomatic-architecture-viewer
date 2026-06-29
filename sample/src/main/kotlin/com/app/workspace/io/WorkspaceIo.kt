package com.app.workspace.io

import com.app.workspace.model.AssetManifest
import com.app.workspace.model.ExportFormat
import com.app.workspace.model.ExportResult
import com.app.workspace.model.ImportMode
import com.app.workspace.model.ImportResult
import com.app.workspace.model.SerializedConfiguration
import com.app.workspace.model.WorkspaceFile
import com.app.workspace.model.WorkspaceSnapshot

class WorkspaceExporter {
    fun export(configuration: SerializedConfiguration, format: ExportFormat): WorkspaceFile =
        WorkspaceFile("workspace.${format.extension}")
}

class WorkspaceImporter {
    fun import(file: WorkspaceFile, result: ImportResult) {
        file.name.length + result.name.length
    }
}

class ImportProcessor {
    fun process(configuration: SerializedConfiguration, mode: ImportMode) =
        com.app.workspace.model.ConfigurationDraft(
            "${mode.name}-${configuration.payload}",
            listOf("asset-a", "asset-b")
        )
}

class FileGateway {
    fun read(file: WorkspaceFile): SerializedConfiguration =
        SerializedConfiguration("raw:${file.name}")
}

class SerializationGateway {
    fun encode(snapshot: WorkspaceSnapshot, manifest: AssetManifest): SerializedConfiguration =
        SerializedConfiguration("${snapshot.name}:${manifest.assetCount}")
}

class ChecksumCalculator {
    fun calculate(configuration: SerializedConfiguration): String =
        configuration.payload.length.toString()
}

class ArchiveWriter {
    fun write(file: WorkspaceFile, checksum: String): ExportResult =
        ExportResult(file.name, checksum.length)
}

class ArchiveReader {
    fun read(configuration: SerializedConfiguration): SerializedConfiguration =
        configuration.copy(payload = "archive:${configuration.payload}")
}

class TempFileProvider {
    fun create(prefix: String): WorkspaceFile = WorkspaceFile("$prefix.tmp")
}

