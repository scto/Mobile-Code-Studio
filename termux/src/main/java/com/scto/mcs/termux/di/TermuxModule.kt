package com.scto.mcs.termux.di

import com.scto.mcs.termux.application.TermuxSettings
import com.scto.mcs.termux.shared.TerminalPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TermuxModule {

    @Provides
    @Singleton
    fun provideTerminalPreferences(): TerminalPreferences = TerminalPreferences()

    @Provides
    @Singleton
    fun provideTermuxSettings(): TermuxSettings = TermuxSettings()
}
