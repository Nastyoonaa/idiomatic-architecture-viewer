package com.app.data.repository

import com.app.core.network.NetworkClient
import com.app.data.local.UserEntity
import com.app.data.mapper.UserMapper
import com.app.data.remote.UserApiService
import com.app.domain.repository.UserRepository
import uml.UmlDiagram

@UmlDiagram
class UserRepositoryImpl(
    private val apiService: UserApiService =
        UserApiService(),
    private val networkClient: NetworkClient =
        NetworkClient()
) : UserRepository {

    private val mapper: UserMapper =
        UserMapper

    override fun getUser(
        id: String
    ): UserEntity {

        return mapper.toEntity(
            apiService.fetchUser(
                id,
                networkClient
            )
        )
    }
}
