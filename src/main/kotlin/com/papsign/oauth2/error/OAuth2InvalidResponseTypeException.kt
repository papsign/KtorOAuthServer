package com.papsign.oauth2.error

class OAuth2InvalidResponseTypeException(val responseType: String) : OAuth2ValidationException(OAuth2Error.unsupported_response_type, "Response type $responseType is not supported", BodyResponse)
