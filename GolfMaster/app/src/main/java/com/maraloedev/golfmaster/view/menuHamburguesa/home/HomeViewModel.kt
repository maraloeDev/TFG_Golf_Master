package com.maraloedev.golfmaster.view.menuHamburguesa.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class HomeViewModel : ViewModel() {

    data class Jugador(
        val id: String = "",
        val nombre: String = "",
        val correo: String = "",
        val telefono: String = "",
        val handicap: String? = null
    )

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<Jugador?>(null)
    val jugador: StateFlow<Jugador?> get() = _jugador

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        cargarDatosJugador()
    }

    /** 🔹 Carga los datos del jugador actual desde Firestore */
    fun cargarDatosJugador() {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("jugadores")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val jugador = if (doc.exists()) {
                            val handicapField = doc.get("handicap_jugador")
                            val handicap = when (handicapField) {
                                is Number -> handicapField.toString()
                                is String -> handicapField
                                else -> ""
                            }

                            Jugador(
                                id = doc.id,
                                nombre = doc.getString("nombre_jugador") ?: (user.displayName ?: ""),
                                correo = doc.getString("correo_jugador") ?: (user.email ?: ""),
                                telefono = doc.getString("telefono_jugador") ?: "",
                                handicap = handicap
                            )
                        } else {
                            // Si no existe en Firestore, crear datos básicos desde Auth
                            Jugador(
                                id = user.uid,
                                nombre = user.displayName ?: "Jugador",
                                correo = user.email ?: "Sin correo"
                            )
                        }

                        _jugador.value = jugador
                        _isLoading.value = false
                    }
                    .addOnFailureListener {
                        _jugador.value = Jugador(
                            id = user.uid,
                            nombre = user.displayName ?: "Jugador",
                            correo = user.email ?: "Sin correo"
                        )
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _jugador.value = Jugador(
                    id = user.uid,
                    nombre = user.displayName ?: "Jugador",
                    correo = user.email ?: "Sin correo"
                )
                _isLoading.value = false
            }
        }
    }
}
