package com.papsign.oauth2.token.code

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import io.ktor.application.ApplicationCall
import org.joda.time.Duration
import org.joda.time.Instant

interface OAuth2CodeTokenHandler<T: OAuth2CodeToken<O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>> {
    suspend fun createToken(client: C, owner: O, redirectURI: String, scopes: Set<String>, call: ApplicationCall): T
    suspend fun consumeToken(token: String): T?
}
