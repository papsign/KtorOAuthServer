package com.papsign.oauth2.error

import com.papsign.oauth2.encodeQuery
import io.ktor.application.ApplicationCall
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.response.respondRedirect

sealed class ResponseHandler {
    abstract suspend fun respond(applicationCall: ApplicationCall, ex: OAuth2ValidationException, state: String?)
}

class OAuth2URIRecoder(url: Url) {
    private val builder = URLBuilder(url)

    fun encodeWithQuery(map: Map<String, String?>) = URLBuilder(builder).also { query ->
        map.forEach { (t, u) ->
            query.parameters.append(t, u ?: "")
        }
    }.build().toString()

    fun encodeWithFragment(map: Map<String, String?>) = URLBuilder(builder).also { query ->
        query.fragment = map.map { (key, value) ->
            if (value != null)
                "${encodeQuery(key)}=${encodeQuery(value)}"
            else
                encodeQuery(key)
        }.joinToString("&")
    }.build().toString()
}

sealed class RedirectResponseHandler(url: Url) : ResponseHandler() {
    val recoder = OAuth2URIRecoder(url)
}

class RetryReferrerResponse(call: ApplicationCall) : QueryResponse(call.refererURL()!!)

open class QueryResponse(url: Url) : RedirectResponseHandler(url) {
    override suspend fun respond(applicationCall: ApplicationCall, ex: OAuth2ValidationException, state: String?) {
        applicationCall.respondRedirect(recoder.encodeWithQuery(ex.toMap(state)))
    }
}

class FragmentResponse(url: Url) : RedirectResponseHandler(url) {
    override suspend fun respond(applicationCall: ApplicationCall, ex: OAuth2ValidationException, state: String?) {
        applicationCall.respondRedirect(recoder.encodeWithFragment(ex.toMap(state)))
    }
}

object BodyResponse : ResponseHandler() {
    override suspend fun respond(applicationCall: ApplicationCall, ex: OAuth2ValidationException, state: String?) {
        applicationCall.respond(ex.status, ex.toMap(state))
    }
}

fun ApplicationCall.refererURL(): Url? {
    return request.header("Referer")?.let { Url(it) }
}
