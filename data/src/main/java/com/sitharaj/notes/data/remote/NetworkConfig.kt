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

package com.sitharaj.notes.data.remote

import com.sitharaj.notes.data.BuildConfig

/**
 * Pluggable network configuration. Swap the whole network target (base URL, timeouts) by binding
 * a different [NetworkConfig] implementation — no edit to the OkHttp/Retrofit wiring required.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface NetworkConfig {
    val baseUrl: String
    val connectTimeoutSeconds: Long
    val readTimeoutSeconds: Long
    val writeTimeoutSeconds: Long
    val oauthClientId: String
    val oauthClientSecret: String
}

/**
 * Default config sourced from the per-flavor `BuildConfig.BASE_URL` (dev vs prod). Plug a custom
 * [NetworkConfig] (e.g. a staging or feature-flag-driven endpoint) by replacing this binding.
 */
class DefaultNetworkConfig @javax.inject.Inject constructor() : NetworkConfig {
    override val baseUrl: String = BuildConfig.BASE_URL
    override val connectTimeoutSeconds: Long = 10
    override val readTimeoutSeconds: Long = 15
    override val writeTimeoutSeconds: Long = 15
    override val oauthClientId: String = BuildConfig.OAUTH_CLIENT_ID
    override val oauthClientSecret: String = BuildConfig.OAUTH_CLIENT_SECRET
}
