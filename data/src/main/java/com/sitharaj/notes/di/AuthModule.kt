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

package com.sitharaj.notes.di

import com.sitharaj.notes.data.remote.auth.AuthInterceptor
import com.sitharaj.notes.data.remote.auth.EncryptedTokenStorage
import com.sitharaj.notes.data.remote.auth.TokenAuthenticator
import com.sitharaj.notes.data.remote.auth.TokenStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Authenticator
import okhttp3.Interceptor
import javax.inject.Singleton

/**
 * Wires authentication into the app. This whole module IS the plug:
 *  - binds the [TokenStorage] implementation, and
 *  - contributes [AuthInterceptor] into the OkHttp `Set<Interceptor>`.
 *
 * Delete this module to unplug auth entirely; the network stack keeps working without it.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    /** Hardened, encrypted token store. Swap to `SharedPrefsTokenStorage` here to change backing. */
    @Binds
    @Singleton
    abstract fun bindTokenStorage(impl: EncryptedTokenStorage): TokenStorage

    /** Attaches the bearer token to outgoing requests. */
    @Binds
    @IntoSet
    abstract fun bindAuthInterceptor(impl: AuthInterceptor): Interceptor

    /** Refreshes the token and retries on 401 (makes the client's optional authenticator present). */
    @Binds
    abstract fun bindAuthenticator(impl: TokenAuthenticator): Authenticator
}
