package com.papsign.oauth2

import com.papsign.oauth2.token.access.OAuth2AccessToken
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.joda.time.DateTime
import org.joda.time.Seconds
import java.net.URLDecoder
import java.net.URLEncoder

suspend fun OAuth2AccessToken<*, *, *, *>.respondBody(call: ApplicationCall) {
    call.respond(makeResponse())
}

fun OAuth2AccessToken<*, *, *, *>.makeResponse(): Map<String, String?> {
    return mapOf(
            "access_token" to token,
            "token_type" to tokenType,
            "expires_in" to Seconds.secondsBetween(DateTime.now(), expireDate).seconds.toString(),
            "refresh_token" to refreshToken?.token,
            "scope" to scopes.joinToString(" ")
    )
}

fun encodeQuery(str: String) = URLEncoder.encode(str, "UTF-8")
fun decodeQuery(str: String) = URLDecoder.decode(str, "UTF-8")
