package com.papsign.oauth2.authserver.client

import com.papsign.oauth2.authserver.OAuth2Flow
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.client.OAuth2ClientHandler
import com.papsign.oauth2.error.BodyResponse
import com.papsign.oauth2.error.OAuth2UnauthorizedClientException
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import io.ktor.application.ApplicationCall
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.basicAuthenticationCredentials
import io.ktor.http.Parameters

interface OAuth2AuthRequestClientAccessor< C : OAuth2Client<O>, O : OAuth2ResourceOwner> {
    @Throws(OAuth2UnauthorizedClientException::class)
    suspend fun requestClient(parameters: Parameters, call: ApplicationCall, clientHandler: OAuth2ClientHandler<C, O>, flow: OAuth2Flow): C {
        val query = call.request.queryParameters
        val clientID = query["client_id"] ?: parameters["client_id"]
        val clientSecret = query["client_secret"] ?: parameters["client_secret"]
        val clientAuth = clientID?.let { UserPasswordCredential(it, clientSecret ?: "") } ?: call.request.basicAuthenticationCredentials() ?: throw OAuth2UnauthorizedClientException("null", BodyResponse)
        val client = clientHandler.getByID(clientAuth.name, flow.flowName) ?: throw OAuth2UnauthorizedClientException(clientAuth.name, BodyResponse)
        if (!clientHandler.verifyCredentialsValid(client, clientAuth.password)) throw OAuth2UnauthorizedClientException(clientAuth.name, BodyResponse)
        return client
    }
}

class DefaultClientAccessor<C : OAuth2Client<O>, O : OAuth2ResourceOwner>: OAuth2AuthRequestClientAccessor<C, O>
