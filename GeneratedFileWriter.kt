package writer

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

class GeneratedFileWriter(
    private val codeGenerator: CodeGenerator
) {

    fun writeText(
        packageName: String,
        fileName: String,
        extension: String,
        content: String
    ) {

        codeGenerator.createNewFile(
            dependencies = Dependencies.ALL_FILES,
            packageName = packageName,
            fileName = fileName,
            extensionName = extension
        ).writer().use {
            it.write(content)
        }
    }
}