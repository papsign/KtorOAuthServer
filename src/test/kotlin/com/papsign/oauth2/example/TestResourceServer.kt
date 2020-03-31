package com.papsign.oauth2.example

import com.papsign.api.oauth.TestAccessTokenHandler
import com.papsign.oauth2.example.data.AccessToken
import com.papsign.oauth2.example.data.Account
import com.papsign.oauth2.example.data.OAuthClient
import com.papsign.oauth2.example.data.RefreshToken
import com.papsign.oauth2.resserver.OAuth2BearerResourceServer

object TestResourceServer: OAuth2BearerResourceServer<AccessToken, RefreshToken, Account, OAuthClient>(
    TestAccessTokenHandler
)
