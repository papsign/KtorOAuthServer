package com.papsign.oauth2.example.data

import com.papsign.oauth2.owner.OAuth2ResourceOwner

data class Account(override val allowedScopes: Set<String>): OAuth2ResourceOwner
