package com.papsign.oauth2.error

class OAuth2BadRedirectURIException(val reason: String) : OAuth2ValidationException(OAuth2Error.invalid_request, "Invalid redirect URI: $reason", BodyResponse)
