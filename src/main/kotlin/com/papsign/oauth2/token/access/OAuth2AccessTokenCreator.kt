package com.papsign.oauth2.token.access

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import io.ktor.application.ApplicationCall
import org.joda.time.Duration
import org.joda.time.Instant

interface OAuth2AccessTokenCreator<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>> {
    suspend fun createAccessToken(call: ApplicationCall, owner: O, client: C, scopes: Set<String>, refreshable: Boolean = false): T
    suspend fun refreshAccessToken(call: ApplicationCall, refresh: R, scopes: Set<String>, refreshable: Boolean = false): T
}
