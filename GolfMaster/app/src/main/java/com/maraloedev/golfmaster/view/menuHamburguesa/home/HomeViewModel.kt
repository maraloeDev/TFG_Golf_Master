package com.maraloedev.golfmaster.view.menuHamburguesa.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel de la pantalla Home.
 *
 * - Carga los datos básicos del jugador desde la colección "jugadores".
 * - Expone el estado del jugador y un flag de carga.
 */
class HomeViewModel : ViewModel() {

    /**
     * Modelo interno con los datos que nos interesan mostrar en Home.
     */
    data class Jugador(
        val id: String = "",
        val nombre: String = "",
        val correo: String = "",
        val telefono: String = "",
        val handicap: String? = null
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estado observable del jugador
    private val _jugador = MutableStateFlow<Jugador?>(null)
    val jugador: StateFlow<Jugador?> get() = _jugador

    // Indicador de carga (por si quieres mostrar un shimmer/spinner)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        cargarDatosJugador()
    }

    /**
     * Carga los datos del jugador actual desde Firestore.
     *
     * - Si el documento existe en "jugadores", usamos sus campos.
     * - Si no existe, se rellena con la información básica de FirebaseAuth.
     */
    fun cargarDatosJugador() {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = db.collection("jugadores")
                    .document(user.uid)
                    .get()
                    .await()

                val jugador = if (doc.exists()) {
                    // El handicap puede venir como número o como String
                    val handicapField = doc.get("handicap_jugador")
                    val handicap = when (handicapField) {
                        is Number -> handicapField.toString()
                        is String -> handicapField
                        else      -> ""
                    }

                    Jugador(
                        id = doc.id,
                        nombre = doc.getString("nombre_jugador") ?: (user.displayName ?: ""),
                        correo = doc.getString("correo_jugador") ?: (user.email ?: ""),
                        telefono = doc.getString("telefono_jugador") ?: "",
                        handicap = handicap
                    )
                } else {
                    // Si no hay documento en Firestore, creamos un Jugador mínimo
                    Jugador(
                        id = user.uid,
                        nombre = user.displayName ?: "Jugador",
                        correo = user.email ?: "Sin correo"
                    )
                }

                _jugador.value = jugador
            } catch (_: Exception) {
                // En caso de error en Firestore, al menos mostramos datos básicos de Auth
                _jugador.value = Jugador(
                    id = user.uid,
                    nombre = user.displayName ?: "Jugador",
                    correo = user.email ?: "Sin correo"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}
