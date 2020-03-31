package com.papsign.oauth2.authserver.authenticator

import com.papsign.oauth2.authserver.authorization.OAuth2Authorizer
import com.papsign.oauth2.authserver.granter.OAuth2Granter
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import io.ktor.application.ApplicationCall

interface OAuth2AuthenticationFlow<C: OAuth2Client<O>, O: OAuth2ResourceOwner> {

    enum class FlowState {
        /**
         * continues in same request
         */
        CONTINUE,
        /**
         * breaks current request to allow user agent interaction
         */
        BREAK
    }

    suspend fun validateRequest(call: ApplicationCall): FlowState = FlowState.CONTINUE

    suspend fun callOwnerPermission(authorizer: OAuth2Authorizer<C, O>,  client: C, redirectURI: String, scopes: Set<String>, call: ApplicationCall): FlowState = FlowState.CONTINUE

    /**
     * @return authenticated resource owner or null
     */
    suspend fun getOwner(call: ApplicationCall): O? = null
}
