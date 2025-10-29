package com.maraloedev.golfmaster.view.preferencias

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Modelo de datos de preferencias
 */
data class Preferencias(
    val temaOscuro: Boolean = true,
    val notificaciones: Boolean = true,
    val idioma: String = "Español",
    val mostrarPerfilPublico: Boolean = true
)

/**
 * Estado de la UI
 */
data class PreferenciasUiState(
    val preferencias: Preferencias = Preferencias(),
    val loading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

class PreferenciasViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(PreferenciasUiState(loading = true))
    val ui: StateFlow<PreferenciasUiState> = _ui

    init {
        cargarPreferencias()
    }

    /**
     * Carga las preferencias del usuario autenticado
     */
    fun cargarPreferencias() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.value = PreferenciasUiState(
                loading = false,
                error = "No hay sesión activa."
            )
            return
        }

        _ui.value = _ui.value.copy(loading = true)

        db.collection("preferencias").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: emptyMap()
                    val prefs = Preferencias(
                        temaOscuro = data["temaOscuro"] as? Boolean ?: true,
                        notificaciones = data["notificaciones"] as? Boolean ?: true,
                        idioma = data["idioma"] as? String ?: "Español",
                        mostrarPerfilPublico = data["mostrarPerfilPublico"] as? Boolean ?: true
                    )
                    _ui.value = PreferenciasUiState(preferencias = prefs, loading = false)
                } else {
                    // Crear preferencias por defecto
                    val defaultPrefs = Preferencias()
                    db.collection("preferencias").document(uid).set(defaultPrefs)
                    _ui.value = PreferenciasUiState(preferencias = defaultPrefs, loading = false)
                }
            }
            .addOnFailureListener { e ->
                _ui.value = PreferenciasUiState(
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar preferencias."
                )
            }
    }

    /**
     * Actualiza las preferencias del usuario
     */
    fun guardarPreferencias(prefs: Preferencias, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onError("Usuario no autenticado.")
            return
        }

        db.collection("preferencias").document(uid)
            .set(prefs)
            .addOnSuccessListener {
                _ui.value = _ui.value.copy(preferencias = prefs, mensaje = "Preferencias guardadas ✅")
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al guardar preferencias.")
            }
    }
}
