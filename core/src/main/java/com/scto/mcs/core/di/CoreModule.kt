package com.scto.mcs.core.di

import android.content.Context
import com.scto.mcs.core.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideTerminalEnvironment(): TerminalEnvironment = TerminalEnvironment()

    @Provides
    @Singleton
    fun provideTerminalSessionManager(terminalEnvironment: TerminalEnvironment): TerminalSessionManager = 
        TerminalSessionManager(terminalEnvironment)

    @Provides
    @Singleton
    fun provideEditorConfigManager(@ApplicationContext context: Context): EditorConfigManager = 
        EditorConfigManager(context)

    @Provides
    @Singleton
    fun provideFileSystemUtils(@ApplicationContext context: Context): FileSystemUtils = 
        FileSystemUtils(context)

    @Provides
    @Singleton
    fun provideGitManager(): GitManager = GitManager()

    @Provides
    @Singleton
    fun provideTemplateEngine(): TemplateEngine = TemplateEngine()

    @Provides
    @Singleton
    fun provideLogcatManager(): LogcatManager = LogcatManager()
}
