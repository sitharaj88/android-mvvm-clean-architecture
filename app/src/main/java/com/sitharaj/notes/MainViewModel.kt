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

package com.sitharaj.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sitharaj.notes.core.datastore.UserPreferences
import com.sitharaj.notes.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Supplies the activity with the persisted theme preferences that drive [NotesTheme]. */
@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = userPreferencesRepository.userPreferences
        .map { MainUiState.Success(it) as MainUiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState.Loading,
        )
}

/** Activity-level UI state holding the resolved [UserPreferences]. */
sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val preferences: UserPreferences) : MainUiState
}
