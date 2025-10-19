package com.maraloedev.golfmaster.view.perfil

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<Jugadores?>(null)
    val jugador: StateFlow<Jugadores?> = _jugador

    private val uid: String? = auth.currentUser?.uid

    init {
        cargarJugadorActual()
    }

    fun cargarJugadorActual() {
        uid ?: return
        db.collection("jugadores").document(uid).get()
            .addOnSuccessListener { doc ->
                _jugador.value = doc.toObject(Jugadores::class.java)
            }
            .addOnFailureListener {
                _jugador.value = null
            }
    }

    fun actualizarJugador(jugador: Jugadores, onSuccess: () -> Unit, onError: (String) -> Unit) {
        uid ?: return onError("Usuario no autenticado")

        db.collection("jugadores").document(uid).set(jugador)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Error al actualizar") }
    }
}
