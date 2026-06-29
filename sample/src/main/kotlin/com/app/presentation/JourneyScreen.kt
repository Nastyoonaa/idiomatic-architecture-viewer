package com.app.presentation

import com.app.core.storage.InternalStorage
import com.app.presentation.vm.UserViewModel
import uml.UmlDiagram

@UmlDiagram
fun JourneyScreen(
    viewModel: UserViewModel
): String {
    return InternalStorage.read(
        key = viewModel.toString()
    )
}
