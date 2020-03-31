package com.papsign.oauth2.error

class OAuth2InvalidGrantTypeException(val grantType: String) : OAuth2ValidationException(OAuth2Error.invalid_request, "Grant type $grantType is not supported", BodyResponse)
