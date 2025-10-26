package com.maraloedev.golfmaster.view.campos.correspondencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CampoInfo(
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val email: String = ""
)

data class CorrespondenciaCamposState(
    val campos: List<CampoInfo> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class CorrespondenciaCamposViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _state = MutableStateFlow(CorrespondenciaCamposState(cargando = true))
    val state: StateFlow<CorrespondenciaCamposState> = _state

    init {
        cargarCampos()
    }

    fun cargarCampos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true, error = null)
            db.collection("campos")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.map { doc ->
                        CampoInfo(
                            nombre = doc.getString("nombre") ?: "",
                            direccion = doc.getString("direccion") ?: "",
                            telefono = doc.getString("telefono") ?: "",
                            email = doc.getString("email") ?: ""
                        )
                    }
                    val datos = if (lista.isEmpty()) mockCampos() else lista
                    _state.value = CorrespondenciaCamposState(campos = datos, cargando = false)
                }
                .addOnFailureListener {
                    _state.value = _state.value.copy(
                        cargando = false,
                        error = it.message ?: "Error al cargar campos"
                    )
                }
        }
    }

    private fun mockCampos() = listOf(
        CampoInfo(
            nombre = "Club de Golf Valleverde",
            direccion = "Ctra. Vieja s/n, 28000 Madrid",
            telefono = "609 048 714",
            email = "club@valleverde.com"
        ),
        CampoInfo(
            nombre = "Golf Sierra Norte",
            direccion = "Km 15, M-607, Colmenar Viejo",
            telefono = "915 000 111",
            email = "info@sierranorte.golf"
        )
    )
}
