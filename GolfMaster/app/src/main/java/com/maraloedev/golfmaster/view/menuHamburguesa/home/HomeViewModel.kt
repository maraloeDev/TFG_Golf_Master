package com.maraloedev.golfmaster.view.menuHamburguesa.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Jugador(
    val id_jugador: String = "",
    val nombre_jugador: String = "",
    val correo_jugador: String = "",
    val telefono_jugador: String = "",
    val handicap_jugador: String? = null
)

class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<Jugador?>(null)
    val jugador: StateFlow<Jugador?> = _jugador

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        cargarDatosJugador()
    }

    /** 🔹 Carga la información del jugador desde Firestore o desde FirebaseAuth */
    fun cargarDatosJugador() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            _isLoading.emit(true)
            try {
                db.collection("jugadores")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {

                            // ✅ Manejo seguro del tipo de dato
                            val handicapField = doc.get("handicap_jugador")
                            val handicap = when (handicapField) {
                                is Number -> handicapField.toString()
                                is String -> handicapField
                                else -> ""
                            }

                            val jugador = Jugador(
                                id_jugador = doc.id,
                                nombre_jugador = doc.getString("nombre_jugador") ?: (user.displayName ?: ""),
                                correo_jugador = doc.getString("correo_jugador") ?: (user.email ?: ""),
                                telefono_jugador = doc.getString("telefono_jugador") ?: "",
                                handicap_jugador = handicap
                            )
                            _jugador.value = jugador

                        } else {
                            // Si no existe en Firestore, creamos con los datos del Auth
                            _jugador.value = Jugador(
                                id_jugador = user.uid,
                                nombre_jugador = user.displayName ?: "Jugador",
                                correo_jugador = user.email ?: "Sin correo"
                            )
                        }
                        viewModelScope.launch { _isLoading.emit(false) }
                    }
                    .addOnFailureListener {
                        _jugador.value = Jugador(
                            id_jugador = user.uid,
                            nombre_jugador = user.displayName ?: "Jugador",
                            correo_jugador = user.email ?: "Sin correo"
                        )
                        viewModelScope.launch { _isLoading.emit(false) }
                    }
            } catch (e: Exception) {
                _jugador.value = Jugador(
                    id_jugador = user.uid,
                    nombre_jugador = user.displayName ?: "Jugador",
                    correo_jugador = user.email ?: "Sin correo"
                )
                _isLoading.emit(false)
            }
        }
    }

    /** 🔹 Cierra sesión y limpia el estado */
    fun cerrarSesion(onLogout: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            _jugador.value = null
            onLogout()
        }
    }
}
