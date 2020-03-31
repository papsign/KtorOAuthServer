package com.papsign.oauth2.example

import com.papsign.oauth2.client.OAuth2ClientHandler
import com.papsign.oauth2.example.data.Account
import com.papsign.oauth2.example.data.OAuthClient

object TestClientHandler: OAuth2ClientHandler<OAuthClient, Account> {

    /**
     * you should check if it exists in the database, not create it, this is just so it works
     */
    override suspend fun getByID(id: String, flow: String): OAuthClient? {
        return OAuthClient(id, "secret", false, setOf("theScope"), listOf(Regex(".*")), null)
    }

}
