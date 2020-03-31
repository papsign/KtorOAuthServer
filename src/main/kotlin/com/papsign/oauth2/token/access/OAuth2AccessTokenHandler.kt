package com.papsign.oauth2.token.access

import com.papsign.oauth2.client.OAuth2Client
import com.papsign.oauth2.owner.OAuth2ResourceOwner

interface OAuth2AccessTokenHandler<T: OAuth2AccessToken<T, R, O, C>, R: OAuth2RefreshToken<T, R, O, C>, O: OAuth2ResourceOwner, C: OAuth2Client<O>>: OAuth2AccessTokenCreator<T, R, O, C>, OAuth2AccessTokenAccessor<T, R, O, C>
