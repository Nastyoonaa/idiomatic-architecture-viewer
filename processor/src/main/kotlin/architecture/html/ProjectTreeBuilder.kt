package architecture.html

import architecture.ArchitectureProject

object ProjectTreeBuilder {

    fun build(
        project: ArchitectureProject
    ): String {

        return buildString {

            appendLine(
                """
<div class="sidebar">

<div class="sidebarTitle">
Проект
</div>

<div class="sidebarSection">

<div class="sidebarSectionTitle">
Модули
</div>
"""
            )

            project.modules.forEach { module ->

                appendLine(
                    """
<div class="sidebarItem">

<span>
📁 ${module.name}
</span>

<span class="sidebarBadge">
${module.sourceSets.size}
</span>

</div>
"""
                )
            }

            appendLine(
                """
</div>

<div class="sidebarSection">

<div class="sidebarSectionTitle">
Пакеты
</div>
"""
            )

            project.modules.forEach { module ->

                module.sourceSets.forEach { sourceSet ->

                    appendLine(
                        """
<div class="sidebarItem">

<span>
📂 ${sourceSet.name}
</span>

</div>
"""
                    )

                    sourceSet.packages.forEach { pkg ->

                        val packageFileName =
                            pkg.name.replace(".", "_")

                        appendLine(
                            """
<a
    href="$packageFileName.html"
    class="sidebarItem packageLevel1"
>

<span>
📦 ${'$'}{
    pkg.name
        .split(".")
        .takeLast(2)
        .joinToString(".")
}
</span>

<span class="sidebarBadge">
${pkg.classes.size}
</span>

</a>
"""
                        )

                        pkg.classes.forEach { clazz ->

                            val className =
                                clazz.simpleName.asString()

                            appendLine(
                                """
<a
    href="$className.html"
    class="sidebarItem packageLevel2"
>

<span>
📄 $className
</span>

</a>
"""
                            )
                        }
                    }
                }
            }

            appendLine(
                """
</div>

</div>
"""
            )
        }
    }
}