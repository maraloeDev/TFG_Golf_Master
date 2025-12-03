package com.maraloedev.golfmaster.view.menuHamburguesa.preferencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Preferencias
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de gestionar las preferencias del jugador.
 *
 * Funciones principales:
 *  - Cargar preferencias desde Firestore.
 *  - Crear un documento inicial si no existe.
 *  - Guardar actualizaciones de días de juego e intereses.
 */
class PreferenciasViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val uid get() = auth.currentUser?.uid
    private val email get() = auth.currentUser?.email ?: "desconocido"

    // Estado observable con las preferencias actuales
    private val _preferencias = MutableStateFlow(Preferencias())
    val preferencias: StateFlow<Preferencias> = _preferencias

    init {
        cargarPreferencias()
    }

    /**
     * Carga las preferencias del usuario actual.
     *
     * Si el documento no existe, lo crea con un valor por defecto
     * asociando el correo del usuario.
     */
    fun cargarPreferencias() {
        val userId = uid ?: return

        db.collection("preferencias").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val prefs = doc.toObject(Preferencias::class.java)
                    if (prefs != null) {
                        _preferencias.value = prefs
                    }
                } else {
                    val prefs = Preferencias(usuario = email)
                    db.collection("preferencias").document(userId).set(prefs)
                    _preferencias.value = prefs
                }
            }
            .addOnFailureListener {
                // En caso de error, se inicializa con usuario pero sin datos de días/intereses.
                _preferencias.value = Preferencias(usuario = email)
            }
    }

    /**
     * Guarda las preferencias seleccionadas por el usuario en Firestore.
     *
     * @param dias       Lista de días de juego seleccionados.
     * @param intereses  Lista de intereses marcados.
     * @param onSuccess  Callback de éxito.
     * @param onError    Callback con mensaje de error legible para la UI.
     */
    fun guardarPreferencias(
        dias: List<String>,
        intereses: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid ?: run {
            onError("Usuario no autenticado")
            return
        }

        val prefs = Preferencias(
            usuario = email,
            dias_juego = dias,
            intereses = intereses
        )

        viewModelScope.launch {
            db.collection("preferencias").document(userId)
                .set(prefs)
                .addOnSuccessListener {
                    _preferencias.value = prefs
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error desconocido")
                }
        }
    }
}
