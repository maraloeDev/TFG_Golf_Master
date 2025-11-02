package com.maraloedev.golfmaster.view.amigos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AmigosViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _amigos = MutableStateFlow<List<String>>(emptyList())
    val amigos: StateFlow<List<String>> = _amigos.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _resultados = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val resultados: StateFlow<List<Pair<String, String>>> = _resultados.asStateFlow()

    private val _buscando = MutableStateFlow(false)
    val buscando: StateFlow<Boolean> = _buscando.asStateFlow()

    init {
        suscribeAmigos()
    }

    private fun suscribeAmigos() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid)
            .collection("amigos")
            .addSnapshotListener { snapshot, _ ->
                _amigos.value = snapshot?.documents?.mapNotNull { it.getString("nombre") } ?: emptyList()
                _loading.value = false
            }
    }

    fun buscarPorNombre(nombre: String) {
        if (nombre.isBlank()) {
            _resultados.value = emptyList()
            return
        }
        _buscando.value = true
        val uidActual = auth.currentUser?.uid
        db.collection("usuarios")
            .whereEqualTo("nombre", nombre)
            .get()
            .addOnSuccessListener { docs ->
                _resultados.value = docs.documents.mapNotNull { d ->
                    val id = d.id
                    val nom = d.getString("nombre")
                    if (id != uidActual && nom != null) id to nom else null
                }
                _buscando.value = false
            }
            .addOnFailureListener {
                _buscando.value = false
            }
    }

    fun addAmigo(id: String, nombre: String, onDone: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios")
            .document(uid)
            .collection("amigos")
            .document(id)
            .set(mapOf("nombre" to nombre))
            .addOnSuccessListener { onDone() }
    }
}
