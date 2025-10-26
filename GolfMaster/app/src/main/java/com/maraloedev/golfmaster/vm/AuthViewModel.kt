package com.maraloedev.golfmaster.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.message ?: "Error al iniciar sesión") }
        }
    }

    fun reg(
        email: String,
        password: String,
        jugador: Jugadores,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid
                    if (uid == null) {
                        onError("Error: UID nulo tras el registro")
                        return@addOnSuccessListener
                    }
                    db.collection("jugadores").document(uid)
                        .set(jugador)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onError("Error al guardar en Firestore: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    onError("Error al registrar usuario: ${e.message}")
                }
        }
    }

    fun logout() = auth.signOut()
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
