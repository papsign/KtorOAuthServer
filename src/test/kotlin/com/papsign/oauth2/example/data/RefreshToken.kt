package com.papsign.oauth2.example.data

import com.papsign.api.oauth.TestAccessTokenHandler
import com.papsign.oauth2.token.access.OAuth2RefreshToken

data class RefreshToken(
    override val token: String,
    val originID: String
) : OAuth2RefreshToken<AccessToken, RefreshToken, Account, OAuthClient> {
    override val origin: AccessToken
        get() = TestAccessTokenHandler.tokens[originID] ?: error("Refresh token is no longer valid")
}
