package detector

object ModuleDetector {

    fun detect(
        filePath: String
    ): String {

        val normalized =
            filePath.replace("\\", "/")

        return when {

            "/shared/" in normalized ->
                "shared"

            "/androidApp/" in normalized ->
                "androidApp"

            "/iosApp/" in normalized ->
                "iosApp"

            "/feature/" in normalized -> {

                normalized
                    .substringAfter("/feature/")
                    .substringBefore("/")
            }

            "/data/" in normalized ->
                "data"

            "/domain/" in normalized ->
                "domain"

            else -> "core"
        }
    }
}