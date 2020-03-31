package com.papsign.oauth2.authserver.authorization

import com.papsign.oauth2.authserver.OAuth2AuthServer
import com.papsign.oauth2.authserver.module.OAuth2PathedAuthServerModule
import com.papsign.oauth2.error.OAuth2InvalidResponseTypeException
import com.papsign.oauth2.error.OAuth2ValidationException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.util.pipeline.PipelineContext

/**
 * [requestWrapper] in case you need to apply a context like an exposed database transaction
 */
class OAuth2AuthorizationHandler(
    val authorizationURL: String,
    val requestWrapper: suspend PipelineContext<Unit, ApplicationCall>.(suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) -> Unit = { it() }
) : OAuth2PathedAuthServerModule {

    override fun Routing.buildRoute(server: OAuth2AuthServer) {
        val authorizers = server.modules.filterIsInstance<OAuth2Authorizer<*, *>>().associateBy { it.responseType }
        get(authorizationURL) {
            requestWrapper {
                try {
                    val responseType = call.request.queryParameters["response_type"]
                        ?: throw OAuth2InvalidResponseTypeException("null")
                    authorizers[responseType]?.processAuthorization(call) ?: throw OAuth2InvalidResponseTypeException(
                        responseType
                    )
                } catch (e: OAuth2ValidationException) {
                    e.handleResponse(call, call.request.queryParameters["state"] ?: "")
                }
            }
        }
    }


}
