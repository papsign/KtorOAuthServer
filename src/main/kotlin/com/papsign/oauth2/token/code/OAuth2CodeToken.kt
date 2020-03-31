package com.papsign.oauth2.token.code

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import org.joda.time.Instant

interface OAuth2CodeToken<O: OAuth2ResourceOwner, C: OAuth2Client<O>> {
    val code: String
    val client: C
    val owner: O
    val validity: Instant
    val redirectURI: String
    val scopes: Set<String>
}
