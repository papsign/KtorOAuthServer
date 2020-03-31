package com.papsign.oauth2.example

import com.papsign.api.oauth.TestAccessTokenHandler
import com.papsign.api.oauth.TestResourceOwnerHandler
import com.papsign.oauth2.authserver.authenticator.impl.OAuth2HTTPBasicAuthenticator
import com.papsign.oauth2.authserver.authorization.impl.OAuth2CodeAuthorizer
import com.papsign.oauth2.authserver.authorization.impl.OAuth2ImplicitAuthorizer
import com.papsign.oauth2.authserver.granter.impl.OAuth2ClientCredentialGranter
import com.papsign.oauth2.authserver.granter.impl.OAuth2PasswordGranter
import com.papsign.oauth2.authserver.oauth2server
import com.papsign.oauth2.authserver.oauthServer
import com.papsign.oauth2.example.data.Account
import com.papsign.oauth2.example.data.OAuthClient
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.Url
import io.ktor.request.header

const val accessTokenURL = "/oauth/token"
const val authorizeURL = "/oauth/authorize"

fun Application.registerTestOAuth() {

    val authenticationFlow = OAuth2HTTPBasicAuthenticator<OAuthClient, Account>(TestResourceOwnerHandler)
    val implicit = OAuth2ImplicitAuthorizer(TestClientHandler, TestAccessTokenHandler, authenticationFlow)
    val code = OAuth2CodeAuthorizer(TestClientHandler, TestAccessTokenHandler, TestCodeTokenHandler, authenticationFlow)
    val password = OAuth2PasswordGranter(TestClientHandler, TestResourceOwnerHandler, TestAccessTokenHandler)
    val clientCredentials = OAuth2ClientCredentialGranter(TestClientHandler, TestAccessTokenHandler)

    val server = oauthServer(accessTokenURL, authorizeURL) {
        add(implicit)
        add(code)
        add(password)
        add(clientCredentials)
    }

    oauth2server(server)
}
