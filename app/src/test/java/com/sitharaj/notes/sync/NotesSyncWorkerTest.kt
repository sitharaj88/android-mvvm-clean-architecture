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
        coEvery { repository.syncNotes() } returns Unit
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

