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

package com.sitharaj.notes.di

import com.sitharaj.notes.data.remote.NotesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
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
     * Provides the OkHttp client for network requests.
     *
     * @return The [OkHttpClient] instance.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()

    /**
     * Provides the Retrofit instance for API calls.
     *
     * @param okHttpClient The [OkHttpClient] to use for network requests.
     * @return The [Retrofit] instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Use 10.0.2.2 for Android emulator localhost
            .client(okHttpClient)
            .addConverterFactory(
                Json { ignoreUnknownKeys = true }.asConverterFactory(MediaType.parse("application/json")!!)
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    /**
     * Provides the API service for notes.
     *
     * @param retrofit The [Retrofit] instance.
     * @return The [NotesApiService] instance.
     */
    @Provides
    @Singleton
    fun provideNotesApiService(retrofit: Retrofit): NotesApiService =
        retrofit.create(NotesApiService::class.java)
}
