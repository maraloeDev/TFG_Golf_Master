package com.maraloedev.golfmaster

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Clase de aplicación de GolfMaster.
 *
 * Extiende de Application para permitir inicializar componentes globales
 * antes de que cualquier Activity sea lanzada.
 */
class GolfMasterApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //  Inicializa Firebase en toda la app (solo se ejecuta una vez)
        FirebaseApp.initializeApp(this)
    }
}
