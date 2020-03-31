package com.papsign.oauth2.token.access

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner

interface OAuth2AccessTokenAccessor<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>> {
    /**
     * @return a valid non expired access token or null
     */
    suspend fun getAccessToken(token: String): T?
    /**
     * @return a valid non expired access token or null
     */
    suspend fun getRefreshToken(token: String, client: C): R?
}
