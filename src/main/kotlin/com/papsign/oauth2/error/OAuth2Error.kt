package com.papsign.oauth2.error

import io.ktor.http.HttpStatusCode

enum class OAuth2Error(val statusCode: HttpStatusCode) {
    invalid_request(HttpStatusCode.BadRequest),
    unauthorized_client(HttpStatusCode.Unauthorized),
    access_denied(HttpStatusCode.Forbidden),
    unsupported_response_type(HttpStatusCode.BadRequest),
    invalid_scope(HttpStatusCode.BadRequest),
    server_error(HttpStatusCode.InternalServerError),
    temporarily_unavailable(HttpStatusCode.ServiceUnavailable)
}
