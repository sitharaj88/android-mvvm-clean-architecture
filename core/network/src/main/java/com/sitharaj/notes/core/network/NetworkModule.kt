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

package com.sitharaj.notes.core.network

import com.sitharaj.notes.core.network.auth.RefreshClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Optional
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing network-related dependencies such as OkHttpClient, Retrofit,
 * and API services for the Notes app.
 *
 * This module is installed in the [SingletonComponent] and provides singletons for network
 * layer components.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Contributes the HTTP logging interceptor into the pluggable interceptor set.
     * Remove this binding to unplug logging; add new `@Provides @IntoSet` interceptors
     * (auth, headers, …) to plug them in — no edit to [provideOkHttpClient] needed.
     */
    @Provides
    @IntoSet
    @Singleton
    fun provideLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    /**
     * Provides the OkHttp client, applying every registered [Interceptor] from the multibound set
     * and the pluggable [NetworkConfig] timeouts.
     *
     * @return The [OkHttpClient] instance.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptors: Set<@JvmSuppressWildcards Interceptor>,
        authenticator: Optional<Authenticator>,
        config: NetworkConfig
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .apply {
                interceptors.forEach { addInterceptor(it) }
                // Optional 401-refresh authenticator — present only when an auth plug is installed.
                if (authenticator.isPresent) authenticator(authenticator.get())
            }
            .build()

    /**
     * Bare client used solely for token refresh — no interceptors/authenticator, which breaks the
     * dependency cycle between the authenticated client and the OAuth refresh call.
     */
    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(config: NetworkConfig): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .build()

    /**
     * Provides the OAuth token API, used by the 401 authenticator to refresh tokens.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideOAuthApiService(
        @RefreshClient okHttpClient: OkHttpClient,
        config: NetworkConfig
    ): OAuthApiService =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()!!)
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(OAuthApiService::class.java)

    /**
     * Provides the Retrofit instance for API calls, using the pluggable [NetworkConfig] base URL.
     *
     * @param okHttpClient The [OkHttpClient] to use for network requests.
     * @param config The pluggable network configuration.
     * @return The [Retrofit] instance.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, config: NetworkConfig): Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()!!)
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
}
