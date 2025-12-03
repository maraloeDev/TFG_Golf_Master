package com.maraloedev.golfmaster.vm

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Jugadores

/**
 * ViewModel de autenticación y registro.
 *
 * Responsabilidades:
 *  - Iniciar sesión con email y contraseña.
 *  - Registrar un nuevo usuario y crear su documento en "jugadores".
 */
class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Realiza el login contra Firebase Authentication.
     */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al iniciar sesión")
            }
    }

    /**
     * Registra un nuevo jugador:
     *  1. Crea el usuario en Firebase Auth.
     *  2. Guarda el documento correspondiente en la colección "jugadores".
     */
    fun registerJugador(
        email: String,
        password: String,
        jugador: Jugadores,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                // Se copia el id generado por Firebase como identificador del modelo Jugadores
                val jugadorConId = jugador.copy(id = uid)

                db.collection("jugadores")
                    .document(uid)
                    .set(jugadorConId)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Error al guardar jugador en Firestore")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al registrar usuario")
            }
    }
}
