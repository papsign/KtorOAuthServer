package com.papsign.oauth2.authserver

interface OAuth2AuthServerLocation {
    val tokenURL: String
    val authorizationURL: String
    val refreshURL: String
        get() = tokenURL
    val revokeURL: String
}
