package com.maraloedev.golfmaster.view.menuHamburguesa.preferencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Preferencias
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreferenciasViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val uid get() = auth.currentUser?.uid
    private val email get() = auth.currentUser?.email ?: "desconocido"

    private val _preferencias = MutableStateFlow(Preferencias())
    val preferencias: StateFlow<Preferencias> = _preferencias

    init {
        cargarPreferencias()
    }

    fun cargarPreferencias() {
        val userId = uid ?: return
        db.collection("preferencias").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val prefs = doc.toObject(Preferencias::class.java)
                    if (prefs != null) _preferencias.value = prefs
                } else {
                    val prefs = Preferencias(usuario = email)
                    db.collection("preferencias").document(userId).set(prefs)
                    _preferencias.value = prefs
                }
            }
            .addOnFailureListener {
                _preferencias.value = Preferencias(usuario = email)
            }
    }

    fun guardarPreferencias(
        dias: List<String>,
        intereses: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = uid ?: return onError("Usuario no autenticado")

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
