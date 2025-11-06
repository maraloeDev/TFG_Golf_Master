package com.maraloedev.golfmaster.view.menuHamburguesa.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Modelo de datos del jugador.
 */
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
 */
class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _jugador = MutableStateFlow<JugadorPerfil?>(null)
    val jugador: StateFlow<JugadorPerfil?> = _jugador

    init {
        cargarPerfil()
    }

    /**
     * Carga el perfil desde Firestore.
     * Si no existe, crea un nuevo perfil con licencia generada.
     */
    fun cargarPerfil() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        db.collection("jugadores").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@addOnSuccessListener
                    _jugador.value = JugadorPerfil(
                        id = data["id"] as? String ?: uid,
                        nombre_jugador = data["nombre_jugador"] as? String ?: "",
                        correo_jugador = data["correo_jugador"] as? String ?: user.email.orEmpty(),
                        telefono_jugador = data["telefono_jugador"] as? String ?: "",
                        sexo_jugador = data["sexo_jugador"] as? String ?: "Hombre",
                        ciudad_jugador = data["ciudad_jugador"] as? String ?: "",
                        provincia_jugador = data["provincia_jugador"] as? String ?: "",
                        codigo_postal_jugador = data["codigo_postal_jugador"] as? String ?: "",
                        licencia_jugador = data["licencia_jugador"] as? String ?: generarLicencia(),
                        handicap_jugador = when (val valor = data["handicap_jugador"]) {
                            is Number -> valor.toString()
                            is String -> valor
                            else -> ""
                        }
                    )
                } else {
                    // Si el documento no existe, crear uno nuevo con licencia generada
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
     * Genera una licencia aleatoria de 6 cifras numéricas.
     * Ejemplo: 777305 o 565512
     */
    private fun generarLicencia(): String {
        val numero = Random.nextInt(100000, 999999)
        return numero.toString()
    }

    /**
     * Actualiza el perfil del jugador en Firestore.
     */
    fun actualizarPerfil(
        perfil: JugadorPerfil,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onError("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            db.collection("jugadores").document(uid)
                .set(perfil)
                .addOnSuccessListener {
                    _jugador.value = perfil
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error al actualizar perfil")
                }
        }
    }

    /**
     * Elimina la cuenta del usuario (documentos + autenticación).
     */
    fun eliminarCuenta(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")
        val user = auth.currentUser

        viewModelScope.launch {
            // Eliminar documentos asociados
            db.collection("jugadores").document(uid).delete()
            db.collection("preferencias").document(uid).delete()

            // Eliminar autenticación
            user?.delete()
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error al eliminar cuenta")
                }
        }
    }
}
