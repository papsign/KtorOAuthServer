package com.papsign.oauth2.token.access

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import org.joda.time.DateTime
import org.joda.time.Instant

interface OAuth2AccessToken<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>> {
    val token: String
    val tokenType: String
    val expireDate: DateTime
    val scopes: Set<String>
    val resourceOwner: O
    val client: C
    val refreshToken: R?
}
