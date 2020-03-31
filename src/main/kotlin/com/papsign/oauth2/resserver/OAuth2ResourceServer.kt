package com.papsign.oauth2.resserver

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import com.papsign.oauth2.token.access.OAuth2AccessToken
import com.papsign.oauth2.token.access.OAuth2AccessTokenAccessor
import com.papsign.oauth2.token.access.OAuth2RefreshToken
import io.ktor.application.ApplicationCall

/**
 * [requestWrapper] in case you need to apply a context like an exposed database transaction
 */
abstract class OAuth2ResourceServer<T : OAuth2AccessToken<T, R, O, C>, R : OAuth2RefreshToken<T, R, O, C>, O : OAuth2ResourceOwner, C : OAuth2Client<O>>(
    val tokenAccessor: OAuth2AccessTokenAccessor<T, R, O, C>,
    val requestWrapper: suspend (suspend () -> Authorization<O, C>?) -> Authorization<O, C>? = { it() }
) {
    data class Authorization<O : OAuth2ResourceOwner, C : OAuth2Client<O>>(val oauthClient: C, val resourceOwner: O)

    abstract fun getToken(call: ApplicationCall): String?

    suspend fun validateAuthCall(call: ApplicationCall, requiredScopes: Set<String>): Authorization<O, C>? {
        return requestWrapper {
            val tokenString = getToken(call) ?: return@requestWrapper null
            val token = tokenAccessor.getAccessToken(tokenString) ?: return@requestWrapper null
            if (!token.scopes.containsAll(requiredScopes)) return@requestWrapper null
            Authorization(token.client, token.resourceOwner)
        }
    }

}
