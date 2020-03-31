package com.papsign.oauth2.example.data

import com.papsign.oauth2.client.OAuth2Client

data class OAuthClient(
    override val identifier: String,
    override val secret: String,
    override val public: Boolean,
    override val allowedScopes: Set<String>,
    override val allowedRedirectURIs: List<Regex>,
    override val clientResourceOwner: Account?
): OAuth2Client<Account>
