package com.maraloedev.golfmaster.view.amigos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.maraloedev.golfmaster.model.Amigo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel para gestionar:
 *  - Lista de amigos en tiempo real (/jugadores/{uid}/amigos)
 *  - Búsqueda de jugadores
 *  - Envío de solicitudes de amistad (colección "amigo")
 *  - Eliminación de amigos en ambos sentidos
 */
class AmigosViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ============================================================
    // Constantes de colecciones y campos
    // ============================================================
    private companion object {
        const val COL_JUGADORES = "jugadores"
        const val COL_AMIGOS = "amigos"
        const val COL_SOLICITUDES_AMIGO = "amigo"

        const val FIELD_NOMBRE_JUGADOR = "nombre_jugador"
    }

    // Lista de amigos del usuario actual
    private val _amigos = MutableStateFlow<List<Amigo>>(emptyList())
    val amigos: StateFlow<List<Amigo>> = _amigos.asStateFlow()

    // Indicador de carga de la lista de amigos
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    // Resultados de búsqueda: Pair(idJugador, nombreJugador)
    private val _resultados = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val resultados: StateFlow<List<Pair<String, String>>> = _resultados.asStateFlow()

    // Indicador de si se está buscando ahora mismo
    private val _buscando = MutableStateFlow(false)
    val buscando: StateFlow<Boolean> = _buscando.asStateFlow()

    // Listener para la lista de amigos (para poder limpiarlo en onCleared)
    private var amigosListener: ListenerRegistration? = null

    init {
        suscribeAmigos()
    }

    // ============================================================
    //  Suscripción en tiempo real a los amigos
    // ============================================================

    private fun suscribeAmigos() {
        val uid = auth.currentUser?.uid ?: run {
            _loading.value = false
            return
        }

        // Por si acaso ya había un listener previo
        amigosListener?.remove()

        amigosListener = db.collection(COL_JUGADORES)
            .document(uid)
            .collection(COL_AMIGOS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Para el TFG: simplemente dejamos la lista vacía y paramos loading
                    _amigos.value = emptyList()
                    _loading.value = false
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    // id del documento = UID del amigo (tal y como lo estás usando)
                    doc.toObject(Amigo::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _amigos.value = lista
                _loading.value = false
            }
    }

    // ============================================================
    //  Utilidad privada: obtener mi nombre de la colección jugadores
    // ============================================================

    /**
     * Devuelve el nombre del jugador actual desde la colección `jugadores`.
     * Se usa al enviar solicitudes de amistad.
     */
    private suspend fun nombreActualDesdeJugadores(): String {
        val uid = auth.currentUser?.uid ?: return "Desconocido"

        return try {
            val snap = db.collection(COL_JUGADORES)
                .document(uid)
                .get()
                .await()

            snap.getString(FIELD_NOMBRE_JUGADOR) ?: "Jugador"
        } catch (_: Exception) {
            "Jugador"
        }
    }

    // ============================================================
    //  Buscar jugadores por nombre
    // ============================================================

    /**
     * Busca jugadores cuyo nombre empiece por el texto indicado
     */
    fun buscarJugador(texto: String) {
        if (texto.isBlank()) {
            _resultados.value = emptyList()
            return
        }

        val uidActual = auth.currentUser?.uid

        viewModelScope.launch {
            _buscando.value = true
            try {
                val docs = db.collection(COL_JUGADORES)
                    .whereGreaterThanOrEqualTo(FIELD_NOMBRE_JUGADOR, texto)
                    .whereLessThanOrEqualTo(FIELD_NOMBRE_JUGADOR, texto + "\uf8ff")
                    .get()
                    .await()

                _resultados.value = docs.documents.mapNotNull { d ->
                    val id = d.id
                    val nombre = d.getString(FIELD_NOMBRE_JUGADOR)
                    // No mostramos nuestro propio usuario en resultados
                    if (id != uidActual && nombre != null) id to nombre else null
                }
            } catch (_: Exception) {
                _resultados.value = emptyList()
            } finally {
                _buscando.value = false
            }
        }
    }

    // ============================================================
    //  Enviar solicitud de amistad
    // ============================================================

    /**
     * Envía una solicitud de amistad a otro jugador.
     *
     * @param idDestino UID del jugador destino.
     * @param nombreDestino Nombre del jugador destino (para mensaje y guardado).
     * @param onDone Callback para mostrar un mensaje en la UI (Snackbar).
     */
    fun enviarSolicitudAmistad(
        idDestino: String,
        nombreDestino: String,
        onDone: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val nombreActual = nombreActualDesdeJugadores()

                //  Comprobar si ya existe una solicitud pendiente
                val existentes = db.collection(COL_SOLICITUDES_AMIGO)
                    .whereEqualTo("de", uid)
                    .whereEqualTo("para", idDestino)
                    .get()
                    .await()

                if (!existentes.isEmpty) {
                    onDone("⚠️ Ya existe una solicitud pendiente a $nombreDestino")
                    return@launch
                }

                //  Crear documento de solicitud
                val doc = mapOf(
                    "tipo" to "amistad",
                    "de" to uid,
                    "nombreDe" to nombreActual,
                    "para" to idDestino,
                    "nombrePara" to nombreDestino,
                    "estado" to "pendiente",
                    "fecha" to Timestamp.now()
                )

                db.collection(COL_SOLICITUDES_AMIGO).add(doc).await()
                onDone(" Solicitud enviada a $nombreDestino")

            } catch (e: Exception) {
                onDone(" Error al enviar: ${e.message}")
            }
        }
    }

    // ============================================================
    //  Eliminar amigo
    // ============================================================

    /**
     * Elimina un amigo de ambas subcolecciones:
     *
     *  /jugadores/{yo}/amigos/{amigoId}
     *  /jugadores/{amigoId}/amigos/{yo}
     */
    fun eliminarAmigo(amigoId: String) = viewModelScope.launch {
        val uid = auth.currentUser?.uid ?: return@launch

        try {
            // Borramos desde nuestro usuario
            db.collection(COL_JUGADORES)
                .document(uid)
                .collection(COL_AMIGOS)
                .document(amigoId)
                .delete()
                .await()

            // Borramos desde el usuario amigo
            db.collection(COL_JUGADORES)
                .document(amigoId)
                .collection(COL_AMIGOS)
                .document(uid)
                .delete()
                .await()
        } catch (_: Exception) {
        }
    }

    override fun onCleared() {
        amigosListener?.remove()
        amigosListener = null
        super.onCleared()
    }
}
