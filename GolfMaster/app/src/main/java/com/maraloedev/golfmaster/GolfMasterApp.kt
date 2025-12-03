package com.maraloedev.golfmaster

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Clase de aplicación de GolfMaster.
 *
 * Extiende de Application para permitir inicializar componentes globales
 * antes de que cualquier Activity sea lanzada.
 *
 * En este caso, se inicializa Firebase de forma explícita para asegurar
 * que todos los módulos (auth, Firestore, storage, etc.) estén disponibles
 * desde el inicio de la aplicación.
 *
 * Se declara en AndroidManifest.xml:
 * <application android:name=".GolfMasterApp" ... >
 */
class GolfMasterApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 🔹 Inicializa Firebase en toda la app (solo se ejecuta una vez)
        FirebaseApp.initializeApp(this)
    }
}
