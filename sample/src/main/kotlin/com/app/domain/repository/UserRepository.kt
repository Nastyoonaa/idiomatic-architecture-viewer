package com.app.domain.repository

import com.app.data.local.UserEntity
import uml.UmlDiagram

@UmlDiagram
interface UserRepository {

    fun getUser(
        id: String
    ): UserEntity
}
