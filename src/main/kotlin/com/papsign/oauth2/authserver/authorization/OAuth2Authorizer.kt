package com.papsign.oauth2.authserver.authorization

import com.papsign.oauth2.authserver.OAuth2Flow
import com.papsign.oauth2.authserver.granter.OAuth2Granter
import com.papsign.oauth2.authserver.module.OAuth2AuthServerModule
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.error.OAuth2ValidationException
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import io.ktor.application.ApplicationCall

interface OAuth2Authorizer<C: OAuth2Client<O>, O: OAuth2ResourceOwner>: OAuth2AuthServerModule, OAuth2Flow {
    val responseType: String

    /**
     * @throws OAuth2ValidationException::class
     */
    @Throws(OAuth2ValidationException::class)
    suspend fun processAuthorization(call: ApplicationCall)

    /**
     * @throws OAuth2ValidationException::class
     */
    @Throws(OAuth2ValidationException::class)
    suspend fun processAuthentication(call: ApplicationCall, client: C, owner: O, redirectURI: String, scopes: Set<String>, state: String)


}
