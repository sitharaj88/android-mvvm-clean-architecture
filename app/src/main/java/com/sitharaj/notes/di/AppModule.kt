package com.sitharaj.notes.di

import android.content.Context
import androidx.room.Room
import com.sitharaj.notes.common.AndroidLogger
import com.sitharaj.notes.common.Logger
import com.sitharaj.notes.data.local.NotesDatabase
import com.sitharaj.notes.data.local.dao.NoteDao
import com.sitharaj.notes.data.local.NoteLocalDataSource
import com.sitharaj.notes.data.remote.NoteRemoteDataSource
import com.sitharaj.notes.data.repository.NoteRepositoryImpl
import com.sitharaj.notes.domain.repository.NoteRepository
import com.sitharaj.notes.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase =
        Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            "notes_db"
        ).build()

    @Provides
    fun provideNoteDao(db: NotesDatabase): NoteDao = db.noteDao()
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideNoteLocalDataSource(noteDao: NoteDao): NoteLocalDataSource =
        NoteLocalDataSource(noteDao)

    @Provides
    @Singleton
    fun provideNoteRemoteDataSource(api: com.sitharaj.notes.data.remote.NotesApiService): NoteRemoteDataSource =
        NoteRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideLogger(): Logger = AndroidLogger()

    @Provides
    @Singleton
    fun provideNoteRepository(
        local: NoteLocalDataSource,
        remote: NoteRemoteDataSource,
        logger: Logger
    ): NoteRepository = NoteRepositoryImpl(local, remote, logger)

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(repository),
        getNoteById = GetNoteByIdUseCase(repository),
        addNote = AddNoteUseCase(repository),
        updateNote = UpdateNoteUseCase(repository),
        deleteNote = DeleteNoteUseCase(repository),
        syncNotes = SyncNotesUseCase(repository)
    )
}
