package com.scto.mcs.core.ui.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UiModule {
    // Hilt-Modul für UI-Komponenten, falls zukünftig ThemeManager oder ähnliches benötigt wird.
}
