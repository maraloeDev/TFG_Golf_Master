package com.maraloedev.golfmaster.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Pequeña envoltura para FirebaseAuth para exponer la instancia y funciones basicas.
 * Aqui solo exponemos auth y helpers para login/registro y para obtener el usuario actual.
 */
object AuthManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun signIn(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onComplete(true, null)
                else onComplete(false, task.exception)
            }
    }

    fun register(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onComplete(true, null)
                else onComplete(false, task.exception)
            }
    }

    fun signOut() { auth.signOut() }
}

