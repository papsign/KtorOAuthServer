package com.papsign.oauth2.owner

interface OAuth2PasswordResourceOwnerHandler<O: OAuth2ResourceOwner> {
    suspend fun getByUsernamePassword(username: String, password: String): O?
}
