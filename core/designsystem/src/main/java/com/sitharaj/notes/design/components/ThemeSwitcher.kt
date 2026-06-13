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

package com.sitharaj.notes.design.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sitharaj.notes.design.R

/**
 * A theme switcher component for toggling between light and dark themes.
 *
 * This component displays a switch with labels for light and dark modes.
 *
 * @param isDarkTheme Whether the current theme is dark.
 * @param onThemeChange Callback invoked when the theme is toggled.
 * @param modifier The modifier to apply to the switcher.
 */
@Composable
fun ThemeSwitcher(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = if (isDarkTheme) R.drawable.ic_dark_mode else R.drawable.ic_light_mode),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isDarkTheme) "Dark" else "Light",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isDarkTheme,
            onCheckedChange = onThemeChange
        )
    }
}

@Preview
@Composable
fun ThemeSwitcherPreview() {
    ThemeSwitcher(
        isDarkTheme = false,
        onThemeChange = {}
    )
}
