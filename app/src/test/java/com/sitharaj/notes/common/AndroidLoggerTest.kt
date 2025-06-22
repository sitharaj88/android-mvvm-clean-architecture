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

package com.sitharaj.notes.common

import android.os.Build
import android.util.Log
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
class AndroidLoggerTest {
    @Before
    fun setUp() {
        mockkStatic(Log::class)
    }

    @Test
    fun `log debug calls Log d`() {
        val logger = AndroidLogger()
        logger.d("TAG", "debug message")
        verify { Log.d("TAG", "debug message") }
    }

    @Test
    fun `log info calls Log i`() {
        val logger = AndroidLogger()
        logger.i("TAG", "info message")
        verify { Log.i("TAG", "info message") }
    }

    @Test
    fun `log warn calls Log w`() {
        val logger = AndroidLogger()
        logger.w("TAG", "warn message", null)
        verify { Log.w("TAG", "warn message", null) }
    }

    @Test
    fun `log error calls Log e`() {
        val logger = AndroidLogger()
        logger.e("TAG", "error message", null)
        verify { Log.e("TAG", "error message", null) }
    }
}
