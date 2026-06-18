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

package com.sitharaj.notes.sync

import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import android.content.Context
import com.sitharaj.notes.core.analytics.CompositeAnalytics
import com.sitharaj.notes.core.common.AppError
import com.sitharaj.notes.core.observability.CompositeCrashReporter
import com.sitharaj.notes.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.sitharaj.notes.core.common.Result as AppResult

class NotesSyncWorkerTest {
    private lateinit var repository: NoteRepository
    private lateinit var worker: NotesSyncWorker
    private val context = mockk<Context>(relaxed = true)
    private val params = mockk<WorkerParameters>(relaxed = true)
    private val analytics = mockk<CompositeAnalytics>(relaxed = true)
    private val crashReporter = mockk<CompositeCrashReporter>(relaxed = true)

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        worker = NotesSyncWorker(context, params, repository, analytics, crashReporter)
    }

    @Test
    fun `doWork returns success when sync succeeds`() = runBlocking {
        coEvery { repository.syncNotes() } returns AppResult.success(Unit)
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { repository.syncNotes() }
    }

    @Test
    fun `doWork returns retry when sync throws`() = runBlocking {
        coEvery { repository.syncNotes() } throws Exception("fail")
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.retry(), result)
        coVerify { repository.syncNotes() }
    }

    @Test
    fun `doWork retries on transient network error`() = runBlocking {
        coEvery { repository.syncNotes() } returns
            AppResult.failure(AppError.Network(kind = AppError.Network.Kind.Timeout))
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `doWork fails fast on terminal auth error`() = runBlocking {
        coEvery { repository.syncNotes() } returns
            AppResult.failure(AppError.Auth(kind = AppError.Auth.Kind.Unauthorized))
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `doWork fails fast on client 4xx error`() = runBlocking {
        coEvery { repository.syncNotes() } returns
            AppResult.failure(AppError.Network(kind = AppError.Network.Kind.Http4xx, code = 400))
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.failure(), result)
    }
}

