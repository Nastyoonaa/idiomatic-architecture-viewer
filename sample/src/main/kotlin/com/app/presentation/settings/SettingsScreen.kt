package com.app.presentation.settings

import com.app.settings.ImportedClassDependency
import com.app.settings.ImportedEnumDependency
import com.app.settings.ImportedInterfaceDependency
import com.app.settings.ImportedObjectDependency
import com.app.settings.SettingsDependency
import java.time.Instant
import uml.UmlDiagram

@UmlDiagram
class SettingsScreen {

    fun render(
        dependency: ImportedInterfaceDependency
    ): ImportedEnumDependency {
        ImportedObjectDependency.touch(
            ImportedClassDependency()
        )

        SettingsDependency()
        dependency.toString()
        Instant.now()

        return ImportedEnumDependency.ENABLED
    }
}

