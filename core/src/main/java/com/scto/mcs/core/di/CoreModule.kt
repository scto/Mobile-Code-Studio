package com.scto.mcs.core.di

import android.content.Context
import com.scto.mcs.core.BuildManager
import com.scto.mcs.core.EditorConfigManager
import com.scto.mcs.core.TerminalEnvironment
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
    fun provideTerminalEnvironment(): TerminalEnvironment {
        return TerminalEnvironment()
    }

    @Provides
    @Singleton
    fun provideEditorConfigManager(@ApplicationContext context: Context): EditorConfigManager {
        return EditorConfigManager(context)
    }

    @Provides
    @Singleton
    fun provideBuildManager(
        terminalEnvironment: TerminalEnvironment,
        @ApplicationContext context: Context
    ): BuildManager {
        return BuildManager(terminalEnvironment, context)
    }
}
