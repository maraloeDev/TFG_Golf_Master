package com.maraloedev.golfmaster.view.preferencias

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Jugadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferenciasViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<Jugadores?>(null)
    val jugador: StateFlow<Jugadores?> = _jugador

    private val uid = auth.currentUser?.uid

    init {
        cargarPreferencias()
    }

    fun cargarPreferencias() {
        uid ?: return
        db.collection("jugadores").document(uid).get()
            .addOnSuccessListener { doc ->
                _jugador.value = doc.toObject(Jugadores::class.java)
            }
    }

    fun guardarPreferencias(
        idioma: String,
        dias: List<String>,
        intereses: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        uid ?: return onError("Usuario no autenticado")

        db.collection("jugadores").document(uid)
            .update(
                mapOf(
                    "idioma" to idioma,
                    "dias_juego" to dias,
                    "intereses" to intereses
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Error desconocido") }
    }
}
