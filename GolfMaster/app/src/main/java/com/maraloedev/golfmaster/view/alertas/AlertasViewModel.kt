package com.maraloedev.golfmaster.view.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.maraloedev.golfmaster.model.AlertaAmistad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsable de:
 *  - Escuchar en tiempo real las solicitudes de amistad dirigidas al usuario actual.
 *  - Aceptar o rechazar solicitudes.
 */
class AlertasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ============================================================
    //  Constantes (colecciones / campos)
    // ============================================================
    private companion object {
        const val COL_AMIGO = "amigo"           // Colección de solicitudes de amistad
        const val COL_JUGADORES = "jugadores"   // Colección de jugadores

        const val FIELD_PARA = "para"
        const val FIELD_ESTADO = "estado"
        const val FIELD_NOMBRE_JUGADOR = "nombre_jugador"
    }

    // Lista de solicitudes de amistad pendientes/recibidas
    private val _invitaciones = MutableStateFlow<List<AlertaAmistad>>(emptyList())
    val invitaciones = _invitaciones.asStateFlow()

    // Estado de carga (para el spinner)
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // Mensaje de error que la UI puede mostrar
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Listener de Firestore para poder desconectarlo cuando se destruya el VM
    private var listener: ListenerRegistration? = null

    // ============================================================
    //  Observación de invitaciones de amistad en tiempo real
    // ============================================================

    /**
     * Empieza a observar las solicitudes de amistad donde "para" es el UID actual.
     * Se usa un snapshotListener para recibir cambios en tiempo real.
     */
    fun observarInvitaciones() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true
        _error.value = null

        // Eliminamos un listener anterior si ya estaba activo
        listener?.remove()

        listener = db.collection(COL_AMIGO)
            .whereEqualTo(FIELD_PARA, uid)
            .addSnapshotListener { snaps, e ->
                if (e != null) {
                    // En caso de error guardamos el mensaje para que la UI lo muestre
                    _error.value = e.localizedMessage
                    _loading.value = false
                    return@addSnapshotListener
                }

                val lista = snaps?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(AlertaAmistad::class.java)?.copy(id = doc.id)
                    }
                    .orEmpty()

                // Ordenamos de más reciente a más antigua
                _invitaciones.value = lista.sortedByDescending { it.fecha.seconds }
                _loading.value = false
            }
    }

    // ============================================================
    // Aceptar amistad
    // ============================================================

    /**
     * Acepta una solicitud de amistad:
     *  1) Marca la solicitud como aceptada.
     *  2) Añade a cada jugador en la subcolección "amigos" del otro.
     *  3) Borra la notificación de la colección "amigo".
     *
     * @param alertaId ID del documento de la solicitud en la colección "amigo"
     * @param deUid UID del jugador que envió la solicitud
     * @param nombreDe Nombre del jugador que envió la solicitud (para guardarlo como amigo)
     */
    fun aceptarAmistad(
        alertaId: String,
        deUid: String,
        nombreDe: String
    ) = viewModelScope.launch {
        val currentUid = auth.currentUser?.uid ?: return@launch

        try {
            //  Actualizamos el estado de la solicitud
            db.collection(COL_AMIGO).document(alertaId)
                .update(FIELD_ESTADO, "aceptada")
                .await()

            //  Obtenemos el nombre del usuario actual para almacenarlo en el otro
            val currentSnap = db.collection(COL_JUGADORES)
                .document(currentUid)
                .get()
                .await()

            val miNombre = currentSnap.getString(FIELD_NOMBRE_JUGADOR) ?: "Jugador"

            // Añadimos ambos como amigos (subcolecciones /jugadores/{uid}/amigos/{otroUid})
            db.collection(COL_JUGADORES).document(currentUid)
                .collection("amigos")
                .document(deUid)
                .set(mapOf("nombre" to nombreDe))
                .await()

            db.collection(COL_JUGADORES).document(deUid)
                .collection("amigos")
                .document(currentUid)
                .set(mapOf("nombre" to miNombre))
                .await()

            //  Borramos la notificación de la colección "amigo"
            db.collection(COL_AMIGO).document(alertaId)
                .delete()
                .await()

        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    // ============================================================
    //  Rechazar amistad
    // ============================================================

    /**
     * Rechaza una solicitud de amistad:
     *  1) Marca la solicitud como rechazada.
     *  2) Elimina el documento de la colección "amigo".
     */
    fun rechazarAmistad(alertaId: String) = viewModelScope.launch {
        try {
            db.collection(COL_AMIGO).document(alertaId)
                .update(FIELD_ESTADO, "rechazada")
                .await()

            db.collection(COL_AMIGO).document(alertaId)
                .delete()
                .await()
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    // ============================================================
    //  Limpieza
    // ============================================================

    /**
     * Al destruirse el ViewModel, se elimina el listener de Firestore para
     * evitar fugas de memoria y lecturas innecesarias.
     */
    override fun onCleared() {
        listener?.remove()
        listener = null
        super.onCleared()
    }
}
