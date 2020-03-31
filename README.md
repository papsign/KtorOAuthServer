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
