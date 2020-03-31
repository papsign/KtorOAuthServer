package com.papsign.oauth2.authserver.granter

import com.papsign.oauth2.authserver.OAuth2Flow
import com.papsign.oauth2.authserver.module.OAuth2AuthServerModule
import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters

interface OAuth2Granter: OAuth2AuthServerModule, OAuth2Flow {

    val grantType: String

    override val flowName: String
        get() = grantType

    suspend fun processGrant(parameters: Parameters, call: ApplicationCall)

}
