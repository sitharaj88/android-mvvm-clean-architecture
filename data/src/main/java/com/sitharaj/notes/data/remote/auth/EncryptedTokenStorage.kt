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
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * [TokenStorage] backed by Jetpack Security's [EncryptedSharedPreferences] (AES-256, keys held in
 * the Android Keystore). This is the hardened default; plug [SharedPrefsTokenStorage] instead for
 * environments where the keystore is unavailable.
 *
 * Falls back to plain prefs if encrypted prefs cannot be created on a given device so the app keeps
 * working rather than crashing at startup.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
class EncryptedTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) : TokenStorage {

    private val prefs: SharedPreferences = runCatching {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "auth_tokens_encrypted",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        context.getSharedPreferences("auth_tokens", Context.MODE_PRIVATE)
    }

    override fun accessToken(): String? = prefs.getString(KEY_ACCESS, null)

    override fun refreshToken(): String? = prefs.getString(KEY_REFRESH, null)

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
