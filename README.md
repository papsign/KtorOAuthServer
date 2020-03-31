[![](https://jitpack.io/v/papsign/KtorOAuthServer.svg)](https://jitpack.io/#papsign/KtorOAuthServer)
# KtorOAuthServer
Ktor OAuth Authentication and resource server library

## Install

1. Add jitpack in your root build.gradle at the end of repositories:
```gradle

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```

2. Add the dependency

```gradle
	dependencies {
	        implementation 'com.github.papsign:KtorOAuthServer:-SNAPSHOT'
	}
```
3. Target the realease explicitly, during the experimental staes snapshots will likely break between versions.

## Configuration

1. Implement a configuration like in the [example](https://github.com/papsign/KtorOAuthServer/tree/master/src/test/kotlin/com/papsign/oauth2/example)
2. Register the auth server endpoints with `Application.registerTestAuth()`
3. Register the resource server auth on your endpoint
- If you use KtorOenAPIGen, 
```kotlin
inline fun NormalOpenAPIRoute.oauth2(vararg scopes: OAuthScope, crossinline route: OpenAPIAuthenticatedRoute<APIPrincipal>.()->Unit = {}): OpenAPIAuthenticatedRoute<APIPrincipal> {
    return TestOpenAPIOAuthProvider(scopes.asList()).apply(this).apply {
        route()
    }
}
```
- If you use vanilla Ktor use `TestResourceServer.validateAuthCall`, if it is null it failed, else it has properly validated the call.
4. If you use Exposed, you may need to implement the `requestWrapper` parameters with a database transaction
