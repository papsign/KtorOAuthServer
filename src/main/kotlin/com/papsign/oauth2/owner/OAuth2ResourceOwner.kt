package com.papsign.oauth2.owner

interface OAuth2ResourceOwner {
    val allowedScopes: Set<String>
}
