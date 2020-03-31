package com.papsign.oauth2.ktor

import io.ktor.routing.RouteSelector
import io.ktor.routing.RouteSelectorEvaluation
import io.ktor.routing.RoutingResolveContext

class OAuth2RouteSelector(val configuration: OAuth2RouteConfiguration): RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Constant
    }

    override fun toString(): String = "(authenticate oauth ${configuration.scopes.joinToString()}"
}
