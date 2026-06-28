package com.app.domain.usecase

import com.app.core.extensions.StringExtensions
import com.app.data.repository.UserRepositoryImpl
import com.app.domain.repository.UserRepository
import uml.UmlDiagram

@UmlDiagram
class GetUserUseCase(
    private val repository: UserRepository =
        UserRepositoryImpl()
) {

    fun execute(
        id: String
    ) =
        repository.getUser(
            StringExtensions.normalize(
                id
            )
        )
}
