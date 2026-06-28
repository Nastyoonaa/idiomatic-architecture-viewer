package com.app.data.mapper

import com.app.data.local.UserEntity
import com.app.data.remote.UserDto
import uml.UmlDiagram

@UmlDiagram
object UserMapper {

    fun toEntity(
        dto: UserDto
    ): UserEntity {

        return UserEntity(
            id = dto.id,
            name = dto.name
        )
    }
}
