package com.papsign.oauth2.client

import com.papsign.oauth2.owner.OAuth2ResourceOwner

interface OAuth2Client<O: OAuth2ResourceOwner> {
    val identifier: String
    val secret: String
    val public: Boolean
    val allowedScopes: Set<String>
    val allowedRedirectURIs: List<Regex>

    val clientResourceOwner: O?
}
