package com.maraloedev.golfmaster.view.informacion

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Modelo de datos para la información general del club
 */
data class Informacion(
    val nombreClub: String = "GolfMaster Club",
    val descripcion: String = "Club dedicado a fomentar el golf con pasión y comunidad.",
    val direccion: String = "Av. del Golf, 123 - Madrid, España",
    val telefono: String = "+34 600 123 456",
    val email: String = "contacto@golfmaster.com",
    val web: String = "www.golfmaster.com",
    val versionApp: String = "1.0.0",
    val politicaPrivacidad: String = "Tus datos se usan exclusivamente para gestionar tu experiencia de golf y no se comparten con terceros."
)

/**
 * Estado de la UI
 */
data class InformacionUiState(
    val info: Informacion = Informacion(),
    val loading: Boolean = false,
    val error: String? = null
)

class InformacionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(InformacionUiState(loading = true))
    val ui: StateFlow<InformacionUiState> = _ui

    init {
        cargarInformacion()
    }

    /**
     * Carga la información desde Firestore o usa valores por defecto
     */
    fun cargarInformacion() {
        _ui.value = _ui.value.copy(loading = true, error = null)

        db.collection("informacion").document("club")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: emptyMap()
                    val info = Informacion(
                        nombreClub = data["nombreClub"] as? String ?: "GolfMaster Club",
                        descripcion = data["descripcion"] as? String ?: "Club dedicado al golf.",
                        direccion = data["direccion"] as? String ?: "",
                        telefono = data["telefono"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        web = data["web"] as? String ?: "",
                        versionApp = data["versionApp"] as? String ?: "1.0.0",
                        politicaPrivacidad = data["politicaPrivacidad"] as? String
                            ?: "Tus datos están protegidos según la normativa vigente."
                    )
                    _ui.value = InformacionUiState(info = info, loading = false)
                } else {
                    // Documento no encontrado, usar valores por defecto
                    _ui.value = InformacionUiState(info = Informacion(), loading = false)
                }
            }
            .addOnFailureListener { e ->
                _ui.value = InformacionUiState(
                    info = Informacion(),
                    loading = false,
                    error = e.localizedMessage ?: "Error al cargar la información."
                )
            }
    }
}
