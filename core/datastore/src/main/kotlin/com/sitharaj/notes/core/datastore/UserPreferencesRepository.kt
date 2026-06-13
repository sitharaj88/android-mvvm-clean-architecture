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
 */

package com.sitharaj.notes.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads and writes [UserPreferences] backed by Preferences DataStore. Single source of truth
 * for user settings; exposes a reactive [userPreferences] stream and suspend setters.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            darkThemeConfig = prefs[DARK_THEME_CONFIG]
                ?.let { runCatching { DarkThemeConfig.valueOf(it) }.getOrNull() }
                ?: DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = prefs[USE_DYNAMIC_COLOR] ?: true,
        )
    }

    suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        dataStore.edit { it[DARK_THEME_CONFIG] = config.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[USE_DYNAMIC_COLOR] = enabled }
    }

    private companion object {
        val DARK_THEME_CONFIG = stringPreferencesKey("dark_theme_config")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
    }
}
