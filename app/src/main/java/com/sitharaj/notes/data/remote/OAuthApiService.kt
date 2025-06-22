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
 * @date 22 Jun 2025
 * @version 1.0.0
 */

package com.sitharaj.notes.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing the OAuth token response from the authentication server.
 *
 * @property accessToken The access token issued by the authorization server.
 * @property refreshToken The refresh token which can be used to obtain new access tokens.
 * @property expiresIn The lifetime in seconds of the access token.
 * @property tokenType The type of the token issued.
 * @property scope The scope of the access token as described by the authorization server.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@Serializable
data class OAuthTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("token_type") val tokenType: String,
    @SerialName("scope") val scope: String? = null
)

/**
 * Retrofit API service for OAuth token operations.
 *
 * Provides a method to obtain an OAuth token from the authentication server.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
interface OAuthApiService {
    /**
     * Requests an OAuth token from the authentication server.
     *
     * @param grantType The type of grant being used (e.g., "authorization_code").
     * @param clientId The client identifier issued to the client.
     * @param clientSecret The client secret issued to the client.
     * @param code The authorization code received from the authorization server (if applicable).
     * @param redirectUri The redirect URI used in the authorization request (if applicable).
     * @param refreshToken The refresh token to obtain a new access token (if applicable).
     * @return [OAuthTokenResponse] containing the access and refresh tokens and related information.
     */
    @Suppress("LongParameterList")
    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String?,
        @Field("redirect_uri") redirectUri: String?,
        @Field("refresh_token") refreshToken: String? = null
    ): OAuthTokenResponse
}
