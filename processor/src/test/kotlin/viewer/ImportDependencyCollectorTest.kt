package viewer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImportDependencyCollectorTest {

    private val sourceSymbol =
        symbol(
            qualifiedName = "com.app.presentation.SettingsScreen",
            kind = ProjectSymbolKind.CLASS,
            isAnnotated = true
        )

    private val collector =
        ImportDependencyCollector()

    @Test
    fun `collects project imports for all supported symbol kinds`() {
        val index =
            ProjectSymbolIndex(
                listOf(
                    sourceSymbol,
                    symbol("com.app.domain.ImportedClass", ProjectSymbolKind.CLASS),
                    symbol("com.app.domain.ImportedObject", ProjectSymbolKind.OBJECT),
                    symbol("com.app.domain.ImportedInterface", ProjectSymbolKind.INTERFACE),
                    symbol("com.app.domain.ImportedEnum", ProjectSymbolKind.ENUM)
                )
            )

        val result =
            collector.collectFromSource(
                sourceSymbol = sourceSymbol,
                source =
                    """
                    import com.app.domain.ImportedClass
                    import com.app.domain.ImportedObject
                    import com.app.domain.ImportedInterface
                    import com.app.domain.ImportedEnum
                    """.trimIndent(),
                symbolIndex = index
            )

        assertEquals(
            setOf(
                "com.app.domain.ImportedClass",
                "com.app.domain.ImportedObject",
                "com.app.domain.ImportedInterface",
                "com.app.domain.ImportedEnum"
            ),
            result.map { it.to.qualifiedName }.toSet()
        )

        assertTrue(
            result.all {
                it.origin == ViewerNodeOrigin.IMPORT &&
                    it.to.isResolved
            }
        )
    }

    @Test
    fun `ignores explicit external imports`() {
        val index =
            ProjectSymbolIndex(
                listOf(
                    sourceSymbol
                )
            )

        val result =
            collector.collectFromSource(
                sourceSymbol = sourceSymbol,
                source =
                    """
                    import androidx.compose.runtime.Composable
                    import java.time.Instant
                    import kotlin.collections.List
                    """.trimIndent(),
                symbolIndex = index
            )

        assertTrue(
            result.isEmpty()
        )
    }

    @Test
    fun `keeps unresolved imports only when they belong to a known project prefix`() {
        val index =
            ProjectSymbolIndex(
                listOf(
                    sourceSymbol,
                    symbol("com.app.domain.ExistingDependency", ProjectSymbolKind.CLASS)
                )
            )

        val result =
            collector.collectFromSource(
                sourceSymbol = sourceSymbol,
                source =
                    """
                    import com.app.domain.MissingDependency
                    import com.external.MissingDependency
                    """.trimIndent(),
                symbolIndex = index
            )

        assertEquals(
            listOf(
                "com.app.domain.MissingDependency"
            ),
            result.map {
                it.to.qualifiedName
            }
        )
        assertEquals(
            ViewerNodeOrigin.UNRESOLVED_IMPORT,
            result.single().origin
        )
        assertFalse(
            result.single().to.isResolved
        )
    }

    @Test
    fun `resolves duplicate simple names by qualified name`() {
        val expected =
            symbol("com.app.feature.UserState", ProjectSymbolKind.CLASS)
        val other =
            symbol("com.app.shared.UserState", ProjectSymbolKind.CLASS)

        val index =
            ProjectSymbolIndex(
                listOf(
                    sourceSymbol,
                    expected,
                    other
                )
            )

        val result =
            collector.collectFromSource(
                sourceSymbol = sourceSymbol,
                source =
                    """
                    import com.app.feature.UserState
                    """.trimIndent(),
                symbolIndex = index
            )

        assertEquals(
            "com.app.feature.UserState",
            result.single().to.qualifiedName
        )
        assertFalse(
            result.any {
                it.to.qualifiedName == "com.app.shared.UserState"
            }
        )
    }

    private fun symbol(
        qualifiedName: String,
        kind: ProjectSymbolKind,
        isAnnotated: Boolean = false
    ): ProjectSymbol {
        val packageName =
            qualifiedName.substringBeforeLast(".")

        return ProjectSymbol(
            qualifiedName = qualifiedName,
            simpleName = qualifiedName.substringAfterLast("."),
            packageName = packageName,
            moduleName = "sample",
            sourceSetName = "main",
            filePath = "",
            fileName = "${qualifiedName.substringAfterLast(".")}.kt",
            kind = kind,
            layer = "core",
            methods = 0,
            properties = 0,
            isAnnotated = isAnnotated,
            isResolved = true
        )
    }
}

