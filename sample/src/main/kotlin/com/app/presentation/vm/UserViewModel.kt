package com.app.presentation.vm

import com.app.core.extensions.StringExtensions
import com.app.domain.repository.UserRepository
import com.app.domain.usecase.GetUserUseCase
import uml.UmlDiagram

@UmlDiagram
class UserViewModel(
    private val getUserUseCase: GetUserUseCase =
        GetUserUseCase()
) {

    private val repository: UserRepository? =
        null

    fun loadUser(
        id: String
    ): String {

        return StringExtensions.normalize(
            getUserUseCase.execute(
                id
            ).name
        )
    }
}
