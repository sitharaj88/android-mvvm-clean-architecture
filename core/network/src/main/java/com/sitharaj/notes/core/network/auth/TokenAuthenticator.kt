/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Sitharaj Seenivasan
 * @version 1.0.0
 */

package com.sitharaj.notes.core.network.auth

import com.sitharaj.notes.core.network.NetworkConfig
import com.sitharaj.notes.core.network.OAuthApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * OkHttp [Authenticator] that transparently refreshes the access token on a 401 using the OAuth
 * refresh-token grant, then retries the original request with the new token.
 *
 * Like [AuthInterceptor], this is contributed purely via DI ([com.sitharaj.notes.di.AuthModule]):
 * binding it makes the OkHttp client's optional authenticator present; unbinding it removes 401
 * handling — no edit to the client builder.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val oauthApi: OAuthApiService,
    private val config: NetworkConfig
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Give up after a single refresh attempt to avoid infinite 401 loops.
        if (responseCount(response) >= MAX_ATTEMPTS) return null

        val refreshToken = tokenStorage.refreshToken() ?: return null

        val newTokens = runCatching {
            runBlocking {
                oauthApi.getToken(
                    grantType = "refresh_token",
                    clientId = config.oauthClientId,
                    clientSecret = config.oauthClientSecret,
                    code = null,
                    redirectUri = null,
                    refreshToken = refreshToken
                )
            }
        }.getOrNull() ?: return null

        tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken ?: refreshToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var prior = response.priorResponse
        var count = 1
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
    }
}
