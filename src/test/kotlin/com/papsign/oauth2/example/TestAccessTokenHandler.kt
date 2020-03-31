package com.papsign.api.oauth

import com.papsign.oauth2.example.data.AccessToken
import com.papsign.oauth2.example.data.Account
import com.papsign.oauth2.example.data.OAuthClient
import com.papsign.oauth2.example.data.RefreshToken
import com.papsign.oauth2.token.access.OAuth2AccessTokenHandler
import io.ktor.application.ApplicationCall
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.HashMap

/**
 * Please implement with proper persistence.
 */
object TestAccessTokenHandler : OAuth2AccessTokenHandler<AccessToken, RefreshToken, Account, OAuthClient> {

    val tokens = HashMap<String, AccessToken>()
    val refreshTokens = HashMap<String, RefreshToken>()

    fun randomID(): String {
        return UUID.randomUUID().toString()
    }

    fun createAccessToken(owner: Account, client: OAuthClient, scopes: Set<String>, refreshable: Boolean): AccessToken {
        val id = randomID()
        val refresh = if (refreshable) {
            createRefreshTokenToken(id)
        } else null
        val token = AccessToken(id, "Bearer", DateTime.now().plusDays(1), scopes, owner, client, refresh)
        tokens[token.token]
        return token
    }

    fun createRefreshTokenToken(tokenID: String): RefreshToken {
        val id = randomID()
        val token = RefreshToken(id, tokenID)
        refreshTokens[token.token]
        return token
    }

    override suspend fun createAccessToken(call: ApplicationCall, owner: Account, client: OAuthClient, scopes: Set<String>, refreshable: Boolean): AccessToken {
        return createAccessToken(owner, client, scopes, refreshable)
    }

    override suspend fun refreshAccessToken(call: ApplicationCall, refresh: RefreshToken, scopes: Set<String>, refreshable: Boolean): AccessToken {
        return try {
            val origin = refresh.origin
            val newID = randomID()
            val newRefresh = if (refreshable) {
                createRefreshTokenToken(newID)
            } else null
            val new = origin.copy(token = randomID(), scopes = origin.scopes.intersect(scopes), refreshToken = newRefresh)
            tokens.remove(origin.token)
            tokens[newID] = new
            new
        } finally {
            refreshTokens.remove(refresh.token)
        }
    }

    override suspend fun getAccessToken(token: String): AccessToken? {
        return tokens[token]
    }

    override suspend fun getRefreshToken(token: String, client: OAuthClient): RefreshToken? {
        return refreshTokens[token]
    }
}
