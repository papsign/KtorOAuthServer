package com.papsign.oauth2.authserver.granter.impl

import com.papsign.oauth2.authserver.client.DefaultClientAccessor
import com.papsign.oauth2.authserver.client.OAuth2AuthRequestClientAccessor
import com.papsign.oauth2.authserver.granter.OAuth2Granter
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.client.OAuth2ClientHandler
import com.papsign.oauth2.error.BodyResponse
import com.papsign.oauth2.error.OAuth2BadCredentialsException
import com.papsign.oauth2.error.OAuth2UnauthorizedClientException
import com.papsign.oauth2.owner.OAuth2PasswordResourceOwnerHandler
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import com.papsign.oauth2.respondBody
import com.papsign.oauth2.token.access.OAuth2AccessToken
import com.papsign.oauth2.token.access.OAuth2AccessTokenCreator
import com.papsign.oauth2.token.access.OAuth2RefreshToken
import io.ktor.application.ApplicationCall
import io.ktor.auth.basicAuthenticationCredentials
import io.ktor.http.Parameters

class OAuth2PasswordGranter<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, C: OAuth2Client<O>, O: OAuth2ResourceOwner>(
        val clientHandler: OAuth2ClientHandler<C, O>,
        val resourceOwnerHandler: OAuth2PasswordResourceOwnerHandler<O>,
        val accessTokenCreator: OAuth2AccessTokenCreator<T, R, O, C>,
        val clientAccessor: OAuth2AuthRequestClientAccessor<C, O> = DefaultClientAccessor()
): OAuth2Granter {
    override val grantType: String = "password"

    override suspend fun processGrant(parameters: Parameters, call: ApplicationCall) {
        val client = clientAccessor.requestClient(parameters, call, clientHandler, this)
        val allScopes = parameters["scope"]?.split(Regex(" +")) ?: listOf()
        val username = parameters["username"] ?: throw OAuth2BadCredentialsException(BodyResponse)
        val password = parameters["password"] ?: throw OAuth2BadCredentialsException(BodyResponse)
        val owner = resourceOwnerHandler.getByUsernamePassword(username, password) ?: throw OAuth2BadCredentialsException(BodyResponse)
        val scopes = allScopes.intersect(client.allowedScopes).intersect(owner.allowedScopes)
        val token = accessTokenCreator.createAccessToken(call, owner, client, scopes, true)
        token.respondBody(call)
    }
}
