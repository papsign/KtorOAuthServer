package com.papsign.oauth2.client

import com.papsign.oauth2.authserver.OAuth2Flow
import com.papsign.oauth2.owner.OAuth2ResourceOwner

interface OAuth2ClientHandler<C: OAuth2Client<O>, O: OAuth2ResourceOwner> {
    suspend fun getByID(id: String, flow: String): C?
    suspend fun verifyCredentialsValid(client: C, secret: String) = client.secret == secret
}
