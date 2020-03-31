package com.papsign.oauth2.authserver.authorization.impl

import com.papsign.oauth2.authserver.authenticator.OAuth2AuthenticationFlow
import com.papsign.oauth2.authserver.authorization.OAuth2Authorizer
import com.papsign.oauth2.authserver.client.DefaultClientAccessor
import com.papsign.oauth2.authserver.client.OAuth2AuthRequestClientAccessor
import com.papsign.oauth2.authserver.granter.OAuth2Granter
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.client.OAuth2ClientHandler
import com.papsign.oauth2.error.*
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import com.papsign.oauth2.respondBody
import com.papsign.oauth2.token.access.OAuth2AccessToken
import com.papsign.oauth2.token.access.OAuth2AccessTokenCreator
import com.papsign.oauth2.token.access.OAuth2RefreshToken
import com.papsign.oauth2.token.code.OAuth2CodeToken
import com.papsign.oauth2.token.code.OAuth2CodeTokenHandler
import io.ktor.application.ApplicationCall
import io.ktor.auth.basicAuthenticationCredentials
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.response.respondRedirect

class OAuth2CodeAuthorizer<T : OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, CT : OAuth2CodeToken<O, C>, C : OAuth2Client<O>, O : OAuth2ResourceOwner>(
        private val clientHandler: OAuth2ClientHandler<C, O>,
        private val accessTokenCreator: OAuth2AccessTokenCreator<T, R, O, C>,
        private val codeTokenHandler: OAuth2CodeTokenHandler<CT, O, C>,
        private val authenticationFlow: OAuth2AuthenticationFlow<C, O>,
        private val clientAccessor: OAuth2AuthRequestClientAccessor<C, O> = DefaultClientAccessor()
) : OAuth2Authorizer<C, O>, OAuth2Granter {
    override val responseType: String = "code"
    override val grantType: String = "authorization_code"

    override suspend fun processAuthorization(call: ApplicationCall) {
        if (authenticationFlow.validateRequest(call) == OAuth2AuthenticationFlow.FlowState.CONTINUE) {
            val rawURI = call.request.queryParameters["redirect_uri"] ?: throw OAuth2BadRedirectURIException("A Redirection URI must be provided")
            if (!Regex("http(?:s)?://.*").matches(rawURI)) throw OAuth2BadRedirectURIException("The redirection URI must be absolute")
            if (rawURI.split("#").getOrNull(1) != null) throw OAuth2BadRedirectURIException("Fragments ar not allowed in redirect URIs")
            val clientID = call.request.queryParameters["client_id"] ?: throw OAuth2UnauthorizedClientException("null", BodyResponse)
            val client = clientHandler.getByID(clientID, flowName) ?: throw OAuth2UnauthorizedClientException(clientID, BodyResponse)
            val validURI = client.allowedRedirectURIs.find {
                it.matches(rawURI)
            }?.let { rawURI } ?: throw OAuth2BadRedirectURIException("The Redirection URI is not authorized")
            val allScopes = call.request.queryParameters["scope"]?.split(Regex(" +"))
                    ?: listOf()
            val scopes = allScopes.intersect(client.allowedScopes)
            if (authenticationFlow.callOwnerPermission(this, client, validURI, scopes, call) == OAuth2AuthenticationFlow.FlowState.CONTINUE) {
                val owner = authenticationFlow.getOwner(call) ?: throw OAuth2BadCredentialsException(QueryResponse(Url(validURI)))
                processAuthentication(call, client, owner, validURI, scopes, call.request.queryParameters["state"] ?: "")
            }
        }
    }

    override suspend fun processAuthentication(call: ApplicationCall, client: C, owner: O, redirectURI: String, scopes: Set<String>, state: String) {
        val ownerScopes = scopes.intersect(client.allowedScopes).intersect(owner.allowedScopes)
        val code = codeTokenHandler.createToken(client, owner, redirectURI, ownerScopes, call)
        val recoder = OAuth2URIRecoder(Url(redirectURI))
        call.respondRedirect(recoder.encodeWithQuery(mapOf(
                "code" to code.code,
                "state" to state
        )))
    }

    override suspend fun processGrant(parameters: Parameters, call: ApplicationCall) {
        val rawURI = parameters["redirect_uri"] ?: throw OAuth2BadRedirectURIException("A Redirection URI must be provided")
        if (!Regex("http(?:s)?://.*").matches(rawURI)) throw OAuth2BadRedirectURIException("The redirection URI must be absolute")
        if (rawURI.split("#").getOrNull(1) != null) throw OAuth2BadRedirectURIException("Fragments are not allowed in redirect URIs")
        val codeToken = parameters["code"] ?: throw OAuth2InvalidCodeTokenException(BodyResponse)
        val code = codeTokenHandler.consumeToken(codeToken) ?: throw OAuth2InvalidCodeTokenException(BodyResponse)
        if (rawURI != code.redirectURI) throw OAuth2BadRedirectURIException("The redirection URI is not authorized")
        val client = clientAccessor.requestClient(parameters, call, clientHandler, this)
        val token = accessTokenCreator.createAccessToken(call, code.owner, client, code.scopes)
        token.respondBody(call)
    }

}
