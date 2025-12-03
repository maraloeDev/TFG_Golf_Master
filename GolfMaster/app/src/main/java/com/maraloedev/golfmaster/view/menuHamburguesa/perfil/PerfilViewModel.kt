package com.maraloedev.golfmaster.view.menuHamburguesa.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class JugadorPerfil(
    val id: String = "",
    val nombre_jugador: String = "",
    val telefono_jugador: String = "",
    val sexo_jugador: String = "",
    val ciudad_jugador: String? = null,
    val provincia_jugador: String? = null,
    val codigo_postal_jugador: String = "",
    val licencia_jugador: String = "",
    val handicap_jugador: String = "",
    val correo_jugador: String = ""
)

/**
 * ViewModel del perfil del jugador.
 * Gestiona la carga, actualización y eliminación de su información.
 */
class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<JugadorPerfil?>(null)
    val jugador: StateFlow<JugadorPerfil?> get() = _jugador

    init {
        cargarPerfil()
    }

    /**
     * 🔹 Carga el perfil del jugador desde Firestore.
     * Si no existe o no tiene licencia, la genera automáticamente y la guarda.
     */
    fun cargarPerfil() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        db.collection("jugadores").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@addOnSuccessListener

                    // Si no tiene licencia, generar y actualizar
                    val licencia = (data["licencia_jugador"] as? String)
                        ?: generarLicencia().also { nueva ->
                            db.collection("jugadores").document(uid)
                                .update("licencia_jugador", nueva)
                        }

                    _jugador.value = JugadorPerfil(
                        id = uid,
                        nombre_jugador = data["nombre_jugador"] as? String ?: "",
                        correo_jugador = data["correo_jugador"] as? String ?: user.email.orEmpty(),
                        telefono_jugador = data["telefono_jugador"] as? String ?: "",
                        sexo_jugador = data["sexo_jugador"] as? String ?: "",
                        ciudad_jugador = data["ciudad_jugador"] as? String ?: "",
                        provincia_jugador = data["provincia_jugador"] as? String ?: "",
                        codigo_postal_jugador = data["codigo_postal_jugador"] as? String ?: "",
                        licencia_jugador = licencia,
                        handicap_jugador = when (val h = data["handicap_jugador"]) {
                            is Number -> h.toString()
                            is String -> h
                            else -> ""
                        }
                    )
                } else {
                    // 🔸 Si el documento no existe, crear uno nuevo con licencia generada
                    val nuevaLicencia = generarLicencia()
                    val nuevo = JugadorPerfil(
                        id = uid,
                        correo_jugador = user.email.orEmpty(),
                        licencia_jugador = nuevaLicencia
                    )
                    db.collection("jugadores").document(uid).set(nuevo)
                    _jugador.value = nuevo
                }
            }
            .addOnFailureListener {
                _jugador.value = null
            }
    }

    /**
     * 🔹 Genera una licencia única de 6 dígitos aleatorios.
     * Ejemplo: 583201
     */
    private fun generarLicencia(): String = Random.nextInt(100000, 999999).toString()

    /**
     * 🔹 Actualiza los datos del perfil en Firestore.
     */
    fun actualizarPerfil(
        perfil: JugadorPerfil,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")

        viewModelScope.launch {
            runCatching {
                db.collection("jugadores").document(uid).set(perfil)
            }.onSuccess {
                _jugador.value = perfil
                onSuccess()
            }.onFailure {
                onError(it.localizedMessage ?: "Error al actualizar perfil")
            }
        }
    }

    /**
     * 🔹 Elimina completamente la cuenta del usuario (documento + autenticación).
     */
    fun eliminarCuenta(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")
        val user = auth.currentUser

        viewModelScope.launch {
            runCatching {
                db.collection("jugadores").document(uid).delete()
                db.collection("preferencias").document(uid).delete()
                user?.delete()
            }.onSuccess { onSuccess() }
                .onFailure { e -> onError(e.localizedMessage ?: "Error al eliminar cuenta") }
        }
    }
}
