package com.scto.mcs.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    // Hier können bei Bedarf Provider-Methoden hinzugefügt werden, 
    // falls Klassen nicht direkt via @Inject konstruiert werden können.
}
