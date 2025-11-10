package com.maraloedev.golfmaster

import android.app.Application
import com.google.firebase.FirebaseApp

class GolfMasterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
