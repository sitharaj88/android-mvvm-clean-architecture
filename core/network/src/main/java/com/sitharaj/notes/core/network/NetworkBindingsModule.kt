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

package com.sitharaj.notes.core.network

import com.sitharaj.notes.core.network.DefaultNetworkConfig
import com.sitharaj.notes.core.network.NetworkConfig
import dagger.Binds
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import okhttp3.Authenticator
import okhttp3.Interceptor
import javax.inject.Singleton

/**
 * Declares the pluggable network extension points:
 *  - `Set<Interceptor>` — contribute OkHttp interceptors with `@Provides @IntoSet` (auth, logging,
 *    headers, …) and the client picks them up automatically. Plug/unplug with one binding.
 *  - [NetworkConfig] — swap the base URL/timeouts by replacing this `@Binds`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingsModule {
    @Multibinds
    abstract fun interceptors(): Set<Interceptor>

    @Binds
    @Singleton
    abstract fun bindNetworkConfig(impl: DefaultNetworkConfig): NetworkConfig

    /**
     * Declares the OkHttp [Authenticator] as optional. The client uses it only when an auth plug
     * (e.g. [com.sitharaj.notes.di.AuthModule]) binds a concrete [Authenticator].
     */
    @BindsOptionalOf
    abstract fun optionalAuthenticator(): Authenticator
}
