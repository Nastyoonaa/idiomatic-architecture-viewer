package detector

object SourceSetDetector {

    fun detect(
        filePath: String
    ): String {

        return when {

            "commonMain" in filePath ->
                "commonMain"

            "androidMain" in filePath ->
                "androidMain"

            "iosMain" in filePath ->
                "iosMain"

            else ->
                "commonMain"
        }
    }
}