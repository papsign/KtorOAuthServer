package com.papsign.oauth2.token.access

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner

interface OAuth2RefreshToken<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>> {
    val token: String
    val origin: T
}
