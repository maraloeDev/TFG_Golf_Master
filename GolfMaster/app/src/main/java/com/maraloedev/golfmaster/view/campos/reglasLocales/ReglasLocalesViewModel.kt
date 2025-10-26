package com.maraloedev.golfmaster.view.campos.reglasLocales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReglaLocal(val texto: String = "")

data class ReglasLocalesState(
    val reglas: List<ReglaLocal> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class ReglasLocalesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _state = MutableStateFlow(ReglasLocalesState(cargando = true))
    val state: StateFlow<ReglasLocalesState> = _state

    init {
        cargarReglas()
    }

    fun cargarReglas() {
        viewModelScope.launch {
            _state.value = _state.value.copy(cargando = true)
            db.collection("reglas_locales")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.map { ReglaLocal(it.getString("texto") ?: "") }
                    _state.value = ReglasLocalesState(
                        reglas = if (lista.isEmpty()) mockReglas() else lista,
                        cargando = false
                    )
                }
                .addOnFailureListener {
                    _state.value = _state.value.copy(cargando = false, error = it.message)
                }
        }
    }

    private fun mockReglas() = listOf(
        ReglaLocal("Bola injugable: drop con 1 golpe de penalización."),
        ReglaLocal("Zonas de protección de fauna: alivio obligatorio."),
        ReglaLocal("Fuera de límites: líneas blancas y vallas."),
        ReglaLocal("Prohibido circular con buggies por los greenes.")
    )
}
