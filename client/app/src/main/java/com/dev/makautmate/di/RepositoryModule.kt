package com.dev.makautmate.di

import com.dev.makautmate.data.repository.AIRepositoryImpl
import com.dev.makautmate.data.repository.AuthRepositoryImpl
import com.dev.makautmate.data.repository.NoteRepositoryImpl
import com.dev.makautmate.data.repository.PortalRepositoryImpl
import com.dev.makautmate.domain.repository.AIRepository
import com.dev.makautmate.domain.repository.AuthRepository
import com.dev.makautmate.domain.repository.NoteRepository
import com.dev.makautmate.domain.repository.PortalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPortalRepository(
        portalRepositoryImpl: PortalRepositoryImpl
    ): PortalRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindAIRepository(
        aiRepositoryImpl: AIRepositoryImpl
    ): AIRepository
}
