package com.maraloedev.golfmaster.view.amigos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Amigo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class AmigosViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _amigos = MutableStateFlow<List<Amigo>>(emptyList())
    val amigos: StateFlow<List<Amigo>> = _amigos.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _resultados = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val resultados: StateFlow<List<Pair<String, String>>> = _resultados.asStateFlow()

    private val _buscando = MutableStateFlow(false)
    val buscando: StateFlow<Boolean> = _buscando.asStateFlow()

    init {
        suscribeAmigos()
    }

    /** 🔄 Escucha lista de amigos en tiempo real */
    private fun suscribeAmigos() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("jugadores").document(uid)
            .collection("amigos")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    val nombre = doc.getString("nombre") ?: return@mapNotNull null
                    val numeroLicencia = doc.getString("licencia_jugador")
                        ?: doc.getString("numeroLicencia")
                        ?: ""
                    val fecha = doc.getTimestamp("fecha")
                        ?: doc.getTimestamp("fechaAmistad")
                        ?: com.google.firebase.Timestamp.now()

                    Amigo(
                        id = doc.id,
                        nombre = nombre,
                        numero_licencia = numeroLicencia,
                        fechaAmistad = fecha
                    )
                } ?: emptyList()
                _amigos.value = lista
                _loading.value = false
            }
    }

    /** 🔍 Buscar jugador */
    fun buscarJugador(texto: String) {
        if (texto.isBlank()) {
            _resultados.value = emptyList()
            return
        }
        _buscando.value = true
        val uidActual = auth.currentUser?.uid

        db.collection("jugadores")
            .whereGreaterThanOrEqualTo("nombre_jugador", texto)
            .whereLessThanOrEqualTo("nombre_jugador", texto + "\uf8ff")
            .get()
            .addOnSuccessListener { docs ->
                _resultados.value = docs.documents.mapNotNull { d ->
                    val id = d.id
                    val nombre = d.getString("nombre_jugador")
                    if (id != uidActual && nombre != null) id to nombre else null
                }
                _buscando.value = false
            }
            .addOnFailureListener { _buscando.value = false }
    }

    /** 🗑️ Eliminar amigo */
    fun eliminarAmigo(amigoId: String) = viewModelScope.launch {
        val uid = auth.currentUser?.uid ?: return@launch
        try {
            db.collection("jugadores").document(uid)
                .collection("amigos").document(amigoId).delete().await()

            db.collection("jugadores").document(amigoId)
                .collection("amigos").document(uid).delete().await()
        } catch (_: Exception) { }
    }

    /** 📨 Enviar solicitud de amistad */
    fun enviarSolicitudAmistad(idDestino: String, nombreDestino: String, onDone: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                // 🔹 Obtener el nombre del jugador actual
                val nombreActual = nombreActualDesdeJugadores()

                // 🔹 Evitar solicitudes duplicadas
                val existentes = db.collection("amigo")
                    .whereEqualTo("de", uid)
                    .whereEqualTo("para", idDestino)
                    .get()
                    .await()

                if (!existentes.isEmpty) {
                    onDone("⚠️ Ya existe una solicitud pendiente a $nombreDestino")
                    return@launch
                }

                // 🔹 Crear solicitud
                val doc = mapOf(
                    "tipo" to "amistad",
                    "de" to uid,
                    "nombreDe" to nombreActual,
                    "para" to idDestino,
                    "nombrePara" to nombreDestino,
                    "estado" to "pendiente",
                    "fecha" to com.google.firebase.Timestamp.now()
                )

                db.collection("amigo").add(doc).await()
                onDone("📩 Solicitud enviada a $nombreDestino")
            } catch (e: Exception) {
                onDone("❌ Error al enviar: ${e.message}")
            }
        }
    }

    /** 🟢 Obtiene el nombre del jugador actual desde la colección 'jugadores' */
    private suspend fun nombreActualDesdeJugadores(): String {
        val uid = auth.currentUser?.uid ?: return "Desconocido"

        return try {
            val snap = db.collection("jugadores").document(uid).get().await()
            snap.getString("nombre_jugador") ?: "Jugador"
        } catch (e: Exception) {
            "Jugador"
        }
    }


}
