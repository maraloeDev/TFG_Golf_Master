package com.maraloedev.golfmaster.vm

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Jugadores

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error al iniciar sesión") }
    }

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

    fun logout() {
        auth.signOut()
    }

    fun eliminarCuenta(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        val uid = user?.uid ?: return onError("Usuario no autenticado")

        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                db.collection("jugadores").document(uid)
                    .delete()
                    .addOnSuccessListener {
                        user.delete()
                            .addOnSuccessListener {
                                auth.signOut()
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "Error al eliminar usuario de Auth")
                            }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Error al eliminar jugador en Firestore")
                    }
            }
            .addOnFailureListener { e ->
                onError("Debes iniciar sesión de nuevo: ${e.message}")
            }
    }

}
