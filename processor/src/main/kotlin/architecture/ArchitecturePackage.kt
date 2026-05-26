package architecture

import com.google.devtools.ksp.symbol.KSClassDeclaration

data class ArchitecturePackage(
    val name: String,
    val classes: List<KSClassDeclaration>
)