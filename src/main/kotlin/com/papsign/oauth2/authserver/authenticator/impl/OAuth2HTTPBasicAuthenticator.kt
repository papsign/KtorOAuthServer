package com.papsign.oauth2.authserver.authenticator.impl

import com.papsign.oauth2.authserver.authenticator.OAuth2AuthenticationFlow
import com.papsign.oauth2.authserver.authenticator.OAuth2AuthenticationFlow.FlowState.BREAK
import com.papsign.oauth2.authserver.authenticator.OAuth2AuthenticationFlow.FlowState.CONTINUE
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import com.papsign.oauth2.owner.OAuth2PasswordResourceOwnerHandler
import io.ktor.application.ApplicationCall
import io.ktor.auth.basicAuthenticationCredentials
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respond

class OAuth2HTTPBasicAuthenticator<C : OAuth2Client<O>, O : OAuth2ResourceOwner>(
        private val resourceOwnerHandler: OAuth2PasswordResourceOwnerHandler<O>
) : OAuth2AuthenticationFlow<C, O> {

    override suspend fun validateRequest(call: ApplicationCall): OAuth2AuthenticationFlow.FlowState {
        val basicAuth = call.request.basicAuthenticationCredentials()
        return if (basicAuth == null) {
            call.response.header(HttpHeaders.WWWAuthenticate, "Basic")
            call.respond(HttpStatusCode.Unauthorized, "")
            BREAK
        } else {
            CONTINUE
        }
    }

    override suspend fun getOwner(call: ApplicationCall): O? {
        val basicAuth = call.request.basicAuthenticationCredentials()!!
        return resourceOwnerHandler.getByUsernamePassword(basicAuth.name, basicAuth.password)
    }
}
