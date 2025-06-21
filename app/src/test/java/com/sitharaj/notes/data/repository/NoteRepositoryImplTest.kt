package com.sitharaj.notes.data.repository

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sitharaj.notes.common.Logger
import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.local.NotesDatabase
import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.entity.NoteEntity
import com.sitharaj.notes.data.local.entity.SyncState
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import com.sitharaj.notes.domain.model.Note
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
class NoteRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: NotesDatabase
    private lateinit var noteDao: NoteDao
    private lateinit var local: NoteLocalDataSource
    private lateinit var remote: NoteRemoteDataSource
    private lateinit var logger: Logger
    private lateinit var repository: NoteRepositoryImpl

    @Before
    fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, NotesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        noteDao = db.noteDao()
        local = NoteLocalDataSource(noteDao)
        remote = mock(NoteRemoteDataSource::class.java)
        logger = mock(Logger::class.java)
        repository = NoteRepositoryImpl(local, remote, logger)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `getNotes filters out deleted notes`() = runBlocking {
        val keep = NoteEntity(1, "t1", "c1", syncState = SyncState.SYNCED)
        val gone = NoteEntity(2, "t2", "c2", syncState = SyncState.DELETED)
        noteDao.insertNote(keep)
        noteDao.insertNote(gone)

        val notes = repository.getNotes().first()
        assertEquals(1, notes.size)
        assertEquals(1, notes[0].id)
    }

    @Test
    fun `addNote calls local insertNote`() = runBlocking {
        val note = Note(3, "title", "content")
        repository.addNote(note)

        val all = noteDao.getAllNotes().first()
        assertTrue(all.any { it.title == "title" && it.content == "content" })
    }

//    @Test
//    fun `deleteNote removes note from local database`() = runBlocking {
//        val note = Note(4, "delete", "me")
//        repository.addNote(note)
//
//        assertTrue(noteDao.getAllNotes().first().any { it.id == 4 })
//
//        repository.deleteNote(note)
//        val after = noteDao.getAllNotes().first()
//        assertFalse(after.any { it.id == 4 })
//    }

    @Test
    fun `updateNote updates existing note`() = runBlocking {
        val original = Note(5, "old", "content")
        repository.addNote(original)

        val updated = original.copy(title = "new", content = "updated")
        repository.updateNote(updated)

        val list = noteDao.getAllNotes().first()
        assertTrue(list.any {
            it.id == 5 &&
                    it.title == "new" &&
                    it.content == "updated"
        })
    }

    @Test
    fun `getNotes does not return deleted entries`() = runBlocking {
        val keep = NoteEntity(6, "keep", "me", syncState = SyncState.SYNCED)
        val remove = NoteEntity(7, "remove", "me", syncState = SyncState.DELETED)
        noteDao.insertNote(keep)
        noteDao.insertNote(remove)

        val notes = repository.getNotes().first()
        assertTrue(notes.none { it.id == 7 })
        assertTrue(notes.any    { it.id == 6 })
    }
}
