package com.scto.mcs.core.di

import com.scto.mcs.core.FileSystemUtils
import com.scto.mcs.core.TerminalEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreUtilsModule {

    @Provides
    @Singleton
    fun provideFileSystemUtils(): FileSystemUtils {
        return FileSystemUtils()
    }

    @Provides
    @Singleton
    fun provideTerminalEnvironment(): TerminalEnvironment {
        return TerminalEnvironment()
    }
}
