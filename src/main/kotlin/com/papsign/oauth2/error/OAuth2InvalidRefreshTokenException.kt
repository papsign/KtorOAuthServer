package com.papsign.oauth2.error

class OAuth2InvalidRefreshTokenException(val token: String) : OAuth2ValidationException(OAuth2Error.invalid_request, "Refresh token $token is expired or invalid", BodyResponse)
