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

package com.sitharaj.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sitharaj.notes.core.datastore.DarkThemeConfig
import com.sitharaj.notes.design.NotesTheme
import com.sitharaj.notes.navigation.NotesNavDisplay
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point for the Notes application.
 *
 * Sets up the Compose UI, applies the user-selected theme (observed from DataStore) and hosts
 * the Navigation 3 display.
 *
 * @author Sitharaj Seenivasan
 * @date 22 Jun 2025
 * @version 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val preferences = (uiState as? MainUiState.Success)?.preferences
            val darkTheme = when (preferences?.darkThemeConfig) {
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
                else -> isSystemInDarkTheme()
            }
            NotesTheme(
                darkTheme = darkTheme,
                dynamicColor = preferences?.useDynamicColor ?: true
            ) {
                Scaffold { innerPadding ->
                    NotesNavDisplay(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
