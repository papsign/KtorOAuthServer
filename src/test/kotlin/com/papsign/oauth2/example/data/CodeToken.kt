package com.papsign.oauth2.example.data

import com.papsign.oauth2.token.code.OAuth2CodeToken
import org.joda.time.Instant

data class CodeToken(
    override val code: String,
    override val client: OAuthClient,
    override val owner: Account,
    override val validity: Instant,
    override val redirectURI: String,
    override val scopes: Set<String>
) : OAuth2CodeToken<Account, OAuthClient>
