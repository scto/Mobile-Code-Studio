package com.scto.mcs.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventManager @Inject constructor() {

    fun post(event: Any) {
        // Einfache Event-Bus Implementierung
    }
}
