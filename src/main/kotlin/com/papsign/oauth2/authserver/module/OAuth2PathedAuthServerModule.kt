package com.papsign.oauth2.authserver.module

import com.papsign.oauth2.authserver.OAuth2AuthServer
import io.ktor.routing.Routing

interface OAuth2PathedAuthServerModule: OAuth2AuthServerModule {
    fun Routing.buildRoute(server: OAuth2AuthServer)
}
