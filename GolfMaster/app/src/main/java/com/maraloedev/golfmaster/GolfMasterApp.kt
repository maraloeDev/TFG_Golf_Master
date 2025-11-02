package com.maraloedev.golfmaster

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Clase de aplicación base.
 * Se ejecuta antes que cualquier actividad.
 * Aquí inicializamos Firebase (una sola vez).
 */
class GolfMasterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
