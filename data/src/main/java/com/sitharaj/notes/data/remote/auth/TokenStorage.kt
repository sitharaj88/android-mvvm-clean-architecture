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

package com.sitharaj.notes.data.remote.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Pluggable auth-token store. Swap the backing store (SharedPreferences today, EncryptedSharedPrefs
 * or DataStore tomorrow) by binding a different implementation — the [AuthInterceptor] and the rest
 * of the app are unaffected.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
interface TokenStorage {
    /** The current bearer access token, or null if signed out. */
    fun accessToken(): String?

    /** Persists the tokens after a successful sign-in / refresh. */
    fun saveTokens(accessToken: String, refreshToken: String?)

    /** Clears all stored tokens (sign-out). */
    fun clear()
}

/**
 * Default [TokenStorage] backed by private [android.content.SharedPreferences].
 *
 * Swap this binding for an `EncryptedSharedPreferences`-backed implementation to harden at-rest
 * storage — no other code changes required.
 */
class SharedPrefsTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) : TokenStorage {
    private val prefs = context.getSharedPreferences("auth_tokens", Context.MODE_PRIVATE)

    override fun accessToken(): String? = prefs.getString(KEY_ACCESS, null)

    override fun saveTokens(accessToken: String, refreshToken: String?) {
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
    }
}
