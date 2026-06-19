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

package com.sitharaj.notes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sitharaj.notes.core.navigation.FeatureNavGraph

/**
 * The application's navigation host. It is feature-agnostic: it assembles the graph purely from the
 * injected [featureNavGraphs] set, so it never imports or references any feature module. Plug a
 * feature in by adding its module + `@IntoSet FeatureNavGraph` binding; nothing here changes.
 *
 * @param featureNavGraphs All registered feature graphs (injected in [MainActivity]).
 * @param innerPadding Padding from the hosting scaffold.
 */
@Composable
@Suppress("FunctionNaming")
fun AppNavHost(
    featureNavGraphs: Set<FeatureNavGraph>,
    innerPadding: PaddingValues = PaddingValues()
) {
    val navController = rememberNavController()
    val startDestination = featureNavGraphs.firstNotNullOfOrNull { it.startDestination } ?: "home"
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        featureNavGraphs.forEach { graph -> graph.register(this, navController) }
    }
}
