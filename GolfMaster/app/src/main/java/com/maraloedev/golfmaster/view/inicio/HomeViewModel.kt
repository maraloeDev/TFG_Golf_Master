package com.maraloedev.golfmaster.view.inicio

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class JugadorPerfil(
    val id: String = "",
    val nombre_jugador: String = "",
    val correo_jugador: String = "",
    val telefono_jugador: String = "",
    val sexo_jugador: String = "Hombre",
    val pais_jugador: String = "",
    val codigo_postal_jugador: String = "",
    val licencia_jugador: String = "",
    val handicap_jugador: String = ""
)

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<JugadorPerfil?>(null)
    val jugador: StateFlow<JugadorPerfil?> = _jugador

    init {
        cargarJugador()
    }

    fun cargarJugador() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("jugadores").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@addOnSuccessListener
                    val jugador = JugadorPerfil(
                        id = data["id"] as? String ?: uid,
                        nombre_jugador = data["nombre_jugador"] as? String ?: "",
                        correo_jugador = data["correo_jugador"] as? String ?: (auth.currentUser?.email ?: ""),
                        telefono_jugador = data["telefono_jugador"] as? String ?: "",
                        sexo_jugador = data["sexo_jugador"] as? String ?: "Hombre",
                        pais_jugador = data["pais_jugador"] as? String ?: "",
                        codigo_postal_jugador = data["codigo_postal_jugador"] as? String ?: "",
                        licencia_jugador = data["licencia_jugador"] as? String ?: "",
                        handicap_jugador = when (val valor = data["handicap_jugador"]) {
                            is Number -> valor.toString()
                            is String -> valor
                            else -> ""
                        }
                    )
                    _jugador.value = jugador
                }
            }
    }
}
