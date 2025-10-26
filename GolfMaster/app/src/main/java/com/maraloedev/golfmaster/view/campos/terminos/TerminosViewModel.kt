package com.maraloedev.golfmaster.view.campos.terminos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Termino(val titulo: String = "", val descripcion: String = "")

data class TerminosState(
    val terminos: List<Termino> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class TerminosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _state = MutableStateFlow(TerminosState(cargando = true))
    val state: StateFlow<TerminosState> = _state

    init {
        cargarTerminos()
    }

    fun cargarTerminos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true)
            db.collection("terminos_torneos")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.map {
                        Termino(
                            titulo = it.getString("titulo") ?: "",
                            descripcion = it.getString("descripcion") ?: ""
                        )
                    }
                    _state.value = TerminosState(
                        terminos = if (lista.isEmpty()) mockTerminos() else lista,
                        cargando = false
                    )
                }
                .addOnFailureListener {
                    _state.value = _state.value.copy(cargando = false, error = it.message)
                }
        }
    }

    private fun mockTerminos() = listOf(
        Termino("Inscripción y pagos", "La inscripción se realiza por la app y debe abonarse antes del cierre."),
        Termino("Handicap", "El comité validará hándicaps y categorías según la normativa vigente."),
        Termino("Cancelaciones", "Menos de 24h antes del torneo pueden implicar pérdida de la cuota."),
        Termino("Protección de datos", "Tus datos se tratarán conforme al RGPD."),
        Termino("Aceptación", "La inscripción implica aceptar todos los términos del torneo.")
    )
}
