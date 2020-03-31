package com.papsign.oauth2.authserver.authorization.impl

import com.papsign.oauth2.authserver.OAuth2Flow
import com.papsign.oauth2.authserver.authenticator.OAuth2AuthenticationFlow
import com.papsign.oauth2.authserver.authenticator.OAuth2AuthenticationFlow.FlowState.CONTINUE
import com.papsign.oauth2.authserver.authorization.OAuth2Authorizer
import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.client.OAuth2ClientHandler
import com.papsign.oauth2.error.*
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import com.papsign.oauth2.token.access.OAuth2AccessToken
import com.papsign.oauth2.token.access.OAuth2AccessTokenCreator
import com.papsign.oauth2.token.access.OAuth2RefreshToken
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.request.header
import io.ktor.response.respondRedirect
import org.joda.time.DateTime
import org.joda.time.Instant
import org.joda.time.Seconds

class OAuth2ImplicitAuthorizer<T : OAuth2AccessToken<T, *, O, C>, C : OAuth2Client<O>, O : OAuth2ResourceOwner>(
        private val clientHandler: OAuth2ClientHandler<C, O>,
        private val tokenCreator: OAuth2AccessTokenCreator<T, *, O, C>,
        private val authenticationFlow: OAuth2AuthenticationFlow<C, O>
) : OAuth2Authorizer<C, O>, OAuth2Flow {
    override val responseType: String = "token"
    override val flowName: String = "implicit"

    override suspend fun processAuthorization(call: ApplicationCall) {
        if (authenticationFlow.validateRequest(call) == CONTINUE) {
            val rawURI = call.request.queryParameters["redirect_uri"] ?: throw OAuth2BadRedirectURIException("A Redirection URI must be provided")
            if (!Regex("http(?:s)?://.*").matches(rawURI)) throw OAuth2BadRedirectURIException("The redirection URI must be absolute")
            if (rawURI.split("#").getOrNull(1) != null) throw OAuth2BadRedirectURIException("Fragments are not allowed in redirect URIs")
            val clientID = call.request.queryParameters["client_id"] ?: throw OAuth2UnauthorizedClientException("null", BodyResponse)
            val client = clientHandler.getByID(clientID, flowName) ?: throw OAuth2UnauthorizedClientException(clientID, BodyResponse)
            val validURI = client.allowedRedirectURIs.find {
                it.matches(rawURI)
            }?.let { rawURI } ?: throw OAuth2BadRedirectURIException("The Redirection URI is not authorized")
            val allScopes = call.request.queryParameters["scope"]?.split(Regex(" +"))
                    ?: listOf()
            val scopes = allScopes.intersect(client.allowedScopes)
            if (authenticationFlow.callOwnerPermission(this, client, validURI, scopes, call) == CONTINUE) {
                val owner = authenticationFlow.getOwner(call) ?: throw OAuth2BadCredentialsException(FragmentResponse(Url(validURI)))
                processAuthentication(call, client, owner, validURI, scopes, call.request.queryParameters["state"] ?: "")
            }
        }
    }

    override suspend fun processAuthentication(call: ApplicationCall, client: C, owner: O, redirectURI: String, scopes: Set<String>, state: String) {
        val ownerScopes = scopes.intersect(client.allowedScopes).intersect(owner.allowedScopes)
        val token = tokenCreator.createAccessToken(call, owner, client, ownerScopes)
        val recoder = OAuth2URIRecoder(Url(redirectURI))
        call.respondRedirect(recoder.encodeWithFragment(mapOf(
                "access_token" to token.token,
                "token_type" to token.tokenType,
                "expires_in" to Seconds.secondsBetween(DateTime.now(), token.expireDate).seconds.toString(),
                "scope" to token.scopes.joinToString(" "),
                "state" to state
        )))
    }
}
