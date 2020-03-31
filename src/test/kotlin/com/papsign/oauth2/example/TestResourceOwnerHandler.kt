package com.papsign.api.oauth

import com.papsign.oauth2.example.data.Account
import com.papsign.oauth2.owner.OAuth2PasswordResourceOwnerHandler

object TestResourceOwnerHandler: OAuth2PasswordResourceOwnerHandler<Account> {

    /**
     * This is where you check the credentials and pull the proper account object you will be working with once authenticated
     */
    override suspend fun getByUsernamePassword(username: String, password: String): Account? {
        return Account(setOf())
    }
}
