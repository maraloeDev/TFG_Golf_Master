package com.maraloedev.golfmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Jugadores

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * 🔹 Iniciar sesión
     */
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

    /**
     * 🔹 Registrar nuevo jugador y almacenarlo en Firestore
     * Incluye la contraseña (solo para uso interno)
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
                val jugadorConId = jugador.copy(
                    id = uid,
                    password_jugador = password // ⚠️ Solo si se necesita almacenar
                )

                db.collection("jugadores")
                    .document(uid)
                    .set(jugadorConId)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Error al guardar jugador en Firestore")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al registrar usuario en Firebase")
            }
    }

    /**
     * 🔹 Cerrar sesión actual
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * 🔹 Eliminar cuenta del usuario autenticado
     * - Verifica si hay sesión activa
     * - Reautentica con email y contraseña antes de borrar
     * - Elimina datos del jugador en Firestore
     */
    fun eliminarCuenta(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            onError("No hay sesión activa. Inicia sesión antes de eliminar la cuenta.")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, password)

        // Reautenticamos antes de eliminar (Firebase requiere sesión reciente)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                val uid = user.uid

                // Primero eliminamos los datos del jugador en Firestore
                db.collection("jugadores").document(uid)
                    .delete()
                    .addOnSuccessListener {
                        // Luego eliminamos la cuenta del Auth
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
                        onError(e.message ?: "Error al eliminar datos del jugador en Firestore")
                    }
            }
            .addOnFailureListener { e ->
                onError("Credenciales incorrectas o sesión expirada. Inicia sesión de nuevo.\n${e.message}")
            }
    }
}
