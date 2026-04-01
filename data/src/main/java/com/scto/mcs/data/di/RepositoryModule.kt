package com.scto.mcs.data.di

import com.scto.mcs.data.repository.EditorRepositoryImpl
import com.scto.mcs.data.repository.GitRepositoryImpl
import com.scto.mcs.data.repository.ProjectRepositoryImpl
import com.scto.mcs.domain.repository.EditorRepository
import com.scto.mcs.domain.repository.GitRepository
import com.scto.mcs.domain.repository.ProjectRepository
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
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindGitRepository(impl: GitRepositoryImpl): GitRepository

    @Binds
    @Singleton
    abstract fun bindEditorRepository(impl: EditorRepositoryImpl): EditorRepository
}
