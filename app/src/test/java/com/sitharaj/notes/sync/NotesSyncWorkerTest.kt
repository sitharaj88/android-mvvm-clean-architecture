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
import com.sitharaj.notes.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NotesSyncWorkerTest {
    private lateinit var repository: NoteRepository
    private lateinit var worker: NotesSyncWorker
    private val context = mockk<Context>(relaxed = true)
    private val params = mockk<WorkerParameters>(relaxed = true)

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        worker = NotesSyncWorker(context, params, repository)
    }

    @Test
    fun `doWork returns success when sync succeeds`() = runBlocking {
        coEvery { repository.syncNotes() } returns com.sitharaj.notes.core.common.Result.success(Unit)
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
}

