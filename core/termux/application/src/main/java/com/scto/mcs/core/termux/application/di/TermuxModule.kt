package com.scto.mcs.core.termux.application.di

import android.content.Context
import com.scto.mcs.core.termux.application.TerminalSessionManager
import com.scto.mcs.core.termux.application.TermuxInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TermuxModule {

    @Provides
    @Singleton
    fun provideTermuxInstaller(@ApplicationContext context: Context): TermuxInstaller {
        return TermuxInstaller(context)
    }

    @Provides
    @Singleton
    fun provideTerminalSessionManager(@ApplicationContext context: Context): TerminalSessionManager {
        return TerminalSessionManager(context)
    }
}
