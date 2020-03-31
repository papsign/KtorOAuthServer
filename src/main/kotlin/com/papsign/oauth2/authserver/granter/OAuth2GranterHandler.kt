package com.papsign.oauth2.authserver.granter

import com.papsign.oauth2.authserver.OAuth2AuthServer
import com.papsign.oauth2.authserver.module.OAuth2PathedAuthServerModule
import com.papsign.oauth2.error.OAuth2InvalidGrantTypeException
import com.papsign.oauth2.error.OAuth2ValidationException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext

/**
 * [requestWrapper] in case you need to apply a context like an exposed database transaction
 */
class OAuth2GranterHandler(
    val tokenURL: String,
    val requestWrapper: suspend PipelineContext<Unit, ApplicationCall>.(suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) -> Unit = { it() }
) : OAuth2PathedAuthServerModule {

    override fun Routing.buildRoute(server: OAuth2AuthServer) {
        val granters = server.modules.filterIsInstance<OAuth2Granter>().associateBy { it.grantType }
        post(tokenURL) {
            requestWrapper {
                try {
                    val body = call.receive<Parameters>()
                    val grantType = call.request.queryParameters["grant_type"] ?: body["grant_type"]
                    ?: throw OAuth2InvalidGrantTypeException("null")
                    granters[grantType]?.processGrant(body, call) ?: throw OAuth2InvalidGrantTypeException(grantType)
                } catch (e: OAuth2ValidationException) {
                    e.handleResponse(call, "")
                }
            }
        }
    }
}
