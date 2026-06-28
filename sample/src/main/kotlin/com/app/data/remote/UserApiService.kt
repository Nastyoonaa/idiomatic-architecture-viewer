package com.app.data.remote

import com.app.core.network.NetworkClient
import uml.UmlDiagram

@UmlDiagram
class UserApiService {

    fun fetchUser(
        id: String,
        client: NetworkClient
    ): UserDto {

        client.execute(
            "/users/$id"
        )

        return UserDto(
            id = id,
            name = "User $id"
        )
    }
}
