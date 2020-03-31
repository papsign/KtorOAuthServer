package com.papsign.oauth2.authserver

import com.papsign.oauth2.authserver.authorization.OAuth2AuthorizationHandler
import com.papsign.oauth2.authserver.granter.OAuth2GranterHandler
import com.papsign.oauth2.authserver.module.OAuth2AuthServerModule
import com.papsign.oauth2.authserver.module.OAuth2PathedAuthServerModule
import io.ktor.application.Application
import io.ktor.routing.Routing
import io.ktor.routing.routing

class OAuth2AuthServer(val modules: Set<OAuth2AuthServerModule>)

inline fun oauthServer(accessTokenURL: String, authrizationURL: String, crossinline config: MutableSet<OAuth2AuthServerModule>.()->Unit): OAuth2AuthServer {
    val set: MutableSet<OAuth2AuthServerModule> = mutableSetOf(OAuth2AuthorizationHandler(authrizationURL), OAuth2GranterHandler(accessTokenURL))
    set.config()
    return OAuth2AuthServer(set)
}

fun Routing.oauth2server(server: OAuth2AuthServer) {
    server.modules.filterIsInstance<OAuth2PathedAuthServerModule>().forEach {
        with(it) {
            buildRoute(server)
        }
    }
}

fun Application.oauth2server(server: OAuth2AuthServer) {
    routing {
        oauth2server(server)
    }
}
