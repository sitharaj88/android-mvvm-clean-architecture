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

package com.sitharaj.notes.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.sitharaj.notes.core.navigation.FeatureNavGraph
import com.sitharaj.notes.presentation.ui.screens.NoteEditScreen
import com.sitharaj.notes.presentation.ui.screens.NotesListScreen
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

/**
 * The Notes feature's contribution to the app navigation graph. This class is the entire plug:
 * binding it `@IntoSet` makes the `:app` host render the Notes screens; removing the module (or the
 * binding) removes the feature, with no change to `:app`.
 *
 * @author Sitharaj Seenivasan
 * @since 1.0.0
 */
class NotesNavGraph @Inject constructor() : FeatureNavGraph {

    override val startDestination: String = ROUTE_LIST

    override fun register(builder: NavGraphBuilder, navController: NavHostController) {
        builder.composable(ROUTE_LIST) {
            NotesListScreen(navController)
        }
        builder.composable(ROUTE_EDIT) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NoteEditScreen(navController, noteId)
        }
    }

    companion object {
        const val ROUTE_LIST = "notes_list"
        const val ROUTE_EDIT = "note_edit/{noteId}"
    }
}

/** Contributes [NotesNavGraph] into the pluggable feature-navigation registry. */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotesNavGraphModule {
    @Binds
    @IntoSet
    abstract fun bindNotesNavGraph(impl: NotesNavGraph): FeatureNavGraph
}
