package com.maraloedev.golfmaster.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SimpleAuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        errorMessage = null
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                // mensaje claro para credenciales malas
                errorMessage = "El correo o la contraseña no son correctos."
                onResult(false)
            }
    }

    fun register(email: String, pass: String, onResult: (Boolean) -> Unit) {
        errorMessage = null
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener {
                errorMessage = it.localizedMessage ?: "No se pudo registrar."
                onResult(false)
            }
    }
}
