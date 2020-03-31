package com.papsign.oauth2.example.openapigen

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.openapi.*
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.throws
import com.papsign.oauth2.example.TestResourceServer
import com.papsign.oauth2.example.accessTokenURL
import com.papsign.oauth2.example.authorizeURL
import com.papsign.oauth2.example.data.Account
import com.papsign.oauth2.example.data.OAuthClient
import com.papsign.oauth2.ktor.OAuth2RouteConfiguration
import com.papsign.oauth2.ktor.OAuth2RouteSelector
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.util.pipeline.PipelineContext
import java.lang.Exception


enum class OAuthScope(override val description: String): Described {
    USER("User information"),
    SIGN("Access to signature functions"),
    FILE("Access to user files"),
    ADMIN("Access to admin functions"),
    OAUTH("Access to user OAuth configurations");

    companion object {
        val allScopes = values().toSet()
        val adminScopes = allScopes
        val basicScopes = adminScopes - setOf(ADMIN)
        val clientScopes = adminScopes - setOf(ADMIN, OAUTH)

        val stringAllScopes = values().map { it.name }.toSet()
        val stringAdminScopes = stringAllScopes
        val stringBasicScopes = stringAdminScopes - setOf(ADMIN.name)
        val stringClientScopes = stringAdminScopes - setOf(ADMIN.name, OAUTH.name)
    }
}

data class APIPrincipal(val account: Account, val client: OAuthClient)

class InvalidAccessTokenException: Exception("Invalid AccessToken")

class TestOpenAPIOAuthProvider(val scopes: List<OAuthScope>) : AuthProvider<APIPrincipal> {


    companion object {
        val scopes = OAuthScope.adminScopes
        val scheme = SecurityScheme(SecuritySchemeType.oauth2, "papsign", APIKeyLocation.header, HttpSecurityScheme.bearer, "UUID", flows = Flows<OAuthScope>().apply {
            implicit(
                    scopes, authorizeURL,
                    accessTokenURL
            )
            password(
                    scopes, accessTokenURL,
                    accessTokenURL
            )
            clientCredentials(
                    scopes, accessTokenURL,
                    accessTokenURL
            )
            authorizationCode(
                    scopes,
                    authorizeURL,
                    accessTokenURL, accessTokenURL
            )
        })
    }

    override suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): APIPrincipal {
        val auth = TestResourceServer.validateAuthCall(pipeline.context, scopes.map { it.name }.toSet())
        return if (auth == null) {
            throw InvalidAccessTokenException()
        } else {
            APIPrincipal(auth.resourceOwner, auth.oauthClient)
        }
    }


    override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<APIPrincipal> {
        return OpenAPIAuthenticatedRoute(route.ktorRoute.createChild(OAuth2RouteSelector(OAuth2RouteConfiguration(scopes.map { it.name }.toSet()))), route.provider.child().also { it.registerModule(this) }, this).throws(
                APIException.apiException(HttpStatusCode.Unauthorized) { ex: InvalidAccessTokenException ->
                    ex.localizedMessage
                }
        )
    }

    override val security: Iterable<Iterable<AuthProvider.Security<*>>> = listOf(listOf(AuthProvider.Security(scheme, scopes)))
}
