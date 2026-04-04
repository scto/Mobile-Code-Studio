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
abstract class CoreDataModule {

    @Binds
    @Singleton
    abstract fun bindEditorRepository(
        editorRepositoryImpl: EditorRepositoryImpl
    ): EditorRepository

    @Binds
    @Singleton
    abstract fun bindGitRepository(
        gitRepositoryImpl: GitRepositoryImpl
    ): GitRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository
}
