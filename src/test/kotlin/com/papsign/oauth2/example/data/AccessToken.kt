package com.papsign.oauth2.example.data

import com.papsign.oauth2.token.access.OAuth2AccessToken
import org.joda.time.DateTime

data class AccessToken(
    override val token: String,
    override val tokenType: String,
    override val expireDate: DateTime,
    override val scopes: Set<String>,
    override val resourceOwner: Account,
    override val client: OAuthClient,
    override val refreshToken: RefreshToken?
) : OAuth2AccessToken<AccessToken, RefreshToken, Account, OAuthClient>

