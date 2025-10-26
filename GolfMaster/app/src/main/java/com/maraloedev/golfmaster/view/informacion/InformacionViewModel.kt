package com.maraloedev.golfmaster.view.informacion

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maraloedev.golfmaster.view.core.navigation.NavRoutes
import kotlinx.coroutines.launch

class InformacionViewModel : ViewModel() {

    private val _state = mutableStateOf(InformacionState())
    val state: State<InformacionState> = _state

    init {
        cargar()
    }

    private fun cargar() = viewModelScope.launch {
        _state.value = InformacionState(
            sections = listOf(
                InfoSectionData(
                    header = "Reservas",
                    entries = listOf(
                        InfoEntry(
                            iconName = "golf",
                            title = "Reservas de Equipamiento",
                            subtitle = "Reserva de buggies, palos y carros",
                            route = NavRoutes.RESERVAS
                        )
                    )
                ),
                InfoSectionData(
                    header = "Campos",
                    entries = listOf(
                        InfoEntry(
                            iconName = "map",
                            title = "Correspondencia de Campos",
                            subtitle = "Información de contacto y ubicación",
                            route = NavRoutes.CORRESPONDENCIA_CAMPOS
                        ),
                        InfoEntry(
                            iconName = "flag",
                            title = "Reglas Locales",
                            subtitle = "Reglas locales de los campos de golf",
                            route = NavRoutes.REGLAS_LOCALES
                        )
                    )
                ),
                InfoSectionData(
                    header = "Torneos",
                    entries = listOf(
                        InfoEntry(
                            iconName = "trophy",
                            title = "Términos y Condiciones",
                            subtitle = "Términos y condiciones de los torneos",
                            route = NavRoutes.TERMINOS_CONDICIONES
                        )
                    )
                )
            )
        )
    }
}

/** Estado inmutable de la pantalla */
data class InformacionState(
    val sections: List<InfoSectionData> = emptyList()
)

/** Sección con cabecera + tarjetas */
data class InfoSectionData(
    val header: String,
    val entries: List<InfoEntry>
)

/** Tarjeta individual */
data class InfoEntry(
    val iconName: String,           // "golf" | "map" | "flag" | "trophy"
    val title: String,
    val subtitle: String,
    val route: String? = null
)
