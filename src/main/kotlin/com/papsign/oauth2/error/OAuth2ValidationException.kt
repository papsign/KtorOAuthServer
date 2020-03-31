package com.papsign.oauth2.error

import io.ktor.application.ApplicationCall

open class OAuth2ValidationException(val error: OAuth2Error, val errorDescription: String, val responseHandler: ResponseHandler) : Exception("$error: $errorDescription") {
    val status = error.statusCode
    fun toMap(state: String?) = mapOf(
            "error" to error.statusCode.value.toString(),
            "error_description" to errorDescription
    ).let {
        if (state !== null)
            it + mapOf("state" to state)
        else
            it
    }

    suspend fun handleResponse(applicationCall: ApplicationCall, state: String?) {
        responseHandler.respond(applicationCall, this, state)
    }
}

class OAuth2UnauthorizedClientException(val clientID: String, responseHandler: ResponseHandler) : OAuth2ValidationException(OAuth2Error.unauthorized_client, "Client $clientID is not authorized to request an access token using this method",  responseHandler)
class OAuth2BadCredentialsException(responseHandler: ResponseHandler) : OAuth2ValidationException(OAuth2Error.access_denied, "Resource owner does not exist or has wrong credentials",  responseHandler)
class OAuth2NoJWTException(responseHandler: ResponseHandler) : OAuth2ValidationException(OAuth2Error.access_denied, "JWT token is missing",  responseHandler)
class OAuth2InvalidCodeTokenException(responseHandler: ResponseHandler) : OAuth2ValidationException(OAuth2Error.invalid_request, "Code token does not exist or has expired",  responseHandler)
