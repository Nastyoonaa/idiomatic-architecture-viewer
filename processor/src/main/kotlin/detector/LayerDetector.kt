package detector

import com.google.devtools.ksp.symbol.KSClassDeclaration

object LayerDetector {

    fun detect(
        clazz: KSClassDeclaration
    ): String {

        val name =
            clazz.simpleName.asString()

        return when {

            name.endsWith("Controller") ->
                "controller"

            name.endsWith("Service") ->
                "service"

            name.endsWith("Repository") ->
                "repository"

            else ->
                "application"
        }
    }
}