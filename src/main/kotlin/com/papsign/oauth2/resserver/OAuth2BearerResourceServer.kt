package com.papsign.oauth2.resserver

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner
import com.papsign.oauth2.token.access.OAuth2AccessToken
import com.papsign.oauth2.token.access.OAuth2AccessTokenAccessor
import com.papsign.oauth2.token.access.OAuth2RefreshToken
import io.ktor.application.ApplicationCall
import io.ktor.request.authorization

open class OAuth2BearerResourceServer<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>>(tokenAccessor: OAuth2AccessTokenAccessor<T, R, O, C>): OAuth2ResourceServer<T, R, O, C>(tokenAccessor) {
    override fun getToken(call: ApplicationCall): String? {
        val auth = call.request.authorization() ?: return null
        val prefix = "bearer "
        return if (auth.startsWith(prefix, true)) {
            auth.removeRange(0..prefix.lastIndex)
        } else null
    }
}
