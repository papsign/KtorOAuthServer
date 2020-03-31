package com.papsign.oauth2.example

import com.papsign.oauth2.example.data.*
import com.papsign.oauth2.token.code.OAuth2CodeTokenHandler
import io.ktor.application.ApplicationCall
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Instant
import java.lang.Exception
import java.util.*

object TestCodeTokenHandler : OAuth2CodeTokenHandler<CodeToken, Account, OAuthClient> {

    val tokens = HashMap<String, CodeToken>()

    fun createAccessToken(client: OAuthClient, owner: Account, redirectURI: String, scopes: Set<String>): CodeToken {
        val id = UUID.randomUUID().toString()
        val token = CodeToken(id, client, owner, Instant.now().plus(Duration.standardMinutes(10)), redirectURI, scopes)
        tokens[token.code]
        return token
    }

    override suspend fun createToken(client: OAuthClient, owner: Account, redirectURI: String, scopes: Set<String>, call: ApplicationCall): CodeToken {
        return createAccessToken(client, owner, redirectURI, scopes)
    }

    override suspend fun consumeToken(token: String): CodeToken? {
        return tokens.remove(token)
    }

}
