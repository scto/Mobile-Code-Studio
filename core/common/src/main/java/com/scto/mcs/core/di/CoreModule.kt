package com.scto.mcs.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    // Da BootstrapConfig und TerminalEnvironment mit @Inject constructor() 
    // annotiert sind, werden sie automatisch von Hilt bereitgestellt.
}
