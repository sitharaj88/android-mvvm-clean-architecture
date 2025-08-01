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

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Notes app.
 *
 * Initializes global application state and is the entry point for app-wide configuration.
 *
 * @constructor Creates an instance of [NotesApp].
 */
@HiltAndroidApp
class NotesApp : Application()
