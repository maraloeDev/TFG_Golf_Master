package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio central para operaciones con Firebase:
 *  - Autenticación
 *  - Jugadores
 *  - Reservas
 *  - Invitaciones
 *  - Eventos
 */
class FirebaseRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // ============================================================
    //  Constantes de colecciones
    // ============================================================

    private companion object {
        const val COL_JUGADORES = "jugadores"
        const val COL_RESERVAS = "reservas"
        const val COL_INVITACIONES = "invitaciones"
        const val COL_EVENTOS = "eventos"
    }

    // Acceso rápido al UID actual (null si no hay sesión)
    val currentUid: String?
        get() = auth.currentUser?.uid

    // ============================================================
    //  AUTENTICACIÓN
    // ============================================================

    /**
     * Inicia sesión con email y contraseña usando FirebaseAuth.
     * Lanza excepción con mensaje claro en caso de error.
     */
    suspend fun login(email: String, pass: String) {
        try {
            auth.signInWithEmailAndPassword(email, pass).await()
        } catch (_: FirebaseAuthInvalidUserException) {
            throw Exception("El usuario no existe o ha sido eliminado.")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            throw Exception("La contraseña es incorrecta.")
        } catch (e: Exception) {
            throw Exception("Error al iniciar sesión: ${e.message}")
        }
    }

    // ============================================================
    //  JUGADORES
    // ============================================================

    /**
     * Busca jugadores cuyo nombre comience por el texto indicado.
     */
    suspend fun buscarJugadoresPorNombre(nombre: String): List<Jugadores> {
        val snap = db.collection(COL_JUGADORES)
            .whereGreaterThanOrEqualTo("nombre_jugador", nombre)
            .whereLessThanOrEqualTo("nombre_jugador", nombre + "\uf8ff")
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Jugadores::class.java) }
    }

    // ============================================================
    //  RESERVAS
    // ============================================================

    /**
     * Crea una nueva reserva asignándole un ID generado por Firestore.
     * Devuelve el ID para que lo puedas usar en la UI.
     */
    suspend fun crearReserva(reserva: Reserva): String {
        val docRef = db.collection(COL_RESERVAS).document()
        val reservaConId = reserva.copy(id = docRef.id)
        docRef.set(reservaConId).await()
        return docRef.id
    }

    /**
     * Elimina una reserva por ID.
     */
    suspend fun eliminarReserva(id: String) {
        db.collection(COL_RESERVAS)
            .document(id)
            .delete()
            .await()
    }

    /**
     * Añade un participante a la lista de participantes de una reserva.
     * Se hace mediante transacción para evitar condiciones de carrera.
     */
    suspend fun anadirParticipanteAReserva(
        reservaId: String,
        userId: String
    ) {
        val reservaRef = db.collection(COL_RESERVAS).document(reservaId)

        db.runTransaction { tx ->
            val snap = tx.get(reservaRef)

            // Si la reserva no existe, puedes decidir lanzar error o simplemente no hacer nada
            if (!snap.exists()) return@runTransaction null

            val actuales = (snap.get("participantesIds") as? List<*>)?.filterIsInstance<String>()
                ?: emptyList()

            if (!actuales.contains(userId)) {
                tx.update(reservaRef, "participantesIds", actuales + userId)
            }

            null
        }.await()
    }

    // ============================================================
    //  INVITACIONES
    // ============================================================

    /**
     * Crea una invitación de un jugador a otro para una reserva.
     *
     * @param de UID del jugador que invita
     * @param para UID del jugador invitado
     * @param reservaId ID de la reserva
     * @param fecha Fecha/hora de la reserva (opcional)
     * @return ID de la invitación creada
     */
    suspend fun crearInvitacion(
        de: String,
        para: String,
        reservaId: String,
        fecha: Timestamp?
    ): String {

        // 1️⃣ Obtener nombre del jugador que invita
        val jugadorSnap = db.collection(COL_JUGADORES)
            .document(de)
            .get()
            .await()

        val nombreDe = jugadorSnap.getString("nombre_jugador") ?: "Un jugador"

        // 2️⃣ Crear documento de invitación
        val docRef = db.collection(COL_INVITACIONES).document()

        val invitacion = mapOf(
            "id" to docRef.id,
            "deId" to de,
            "paraId" to para,
            "reservaId" to reservaId,
            "nombreDe" to nombreDe,
            "fecha" to fecha,
            "estado" to "pendiente",
            "creadaEn" to Timestamp.now()
        )

        docRef.set(invitacion).await()
        return docRef.id
    }

    /**
     * Actualiza el estado de una invitación (pendiente/aceptada/rechazada).
     */
    suspend fun actualizarEstadoInvitacion(
        invitacionId: String,
        nuevoEstado: String
    ) {
        db.collection(COL_INVITACIONES)
            .document(invitacionId)
            .update("estado", nuevoEstado)
            .await()
    }

    // ============================================================
    //  EVENTOS
    // ============================================================

    /**
     * Devuelve un Flow que emite la lista de eventos en tiempo real.
     * Cada cambio en la colección "eventos" provoca una nueva emisión.
     */
    fun getEventosFlow(): Flow<List<Evento>> = callbackFlow {
        val listener: ListenerRegistration = db.collection(COL_EVENTOS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Evento::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(lista).isSuccess
            }

        // Se ejecuta cuando el Flow se cancela (por ejemplo, cuando se destruye la pantalla)
        awaitClose { listener.remove() }
    }

    /**
     * Crea un evento nuevo en Firestore.
     * Si quieres garantizar que siempre tenga ID, puedes generar document()
     * y hacer copy(id = doc.id) como en reservas.
     */
    suspend fun addEvento(evento: Evento) {
        db.collection(COL_EVENTOS)
            .add(evento)
            .await()
    }

    /**
     * Inscribe al usuario (uid) en el evento indicado.
     * Usa transacción para evitar duplicados y condiciones de carrera.
     */
    suspend fun inscribirseEnEvento(eventoId: String, uid: String) {
        val ref = db.collection(COL_EVENTOS).document(eventoId)

        db.runTransaction { tx ->
            val snap = tx.get(ref)
            if (!snap.exists()) return@runTransaction null

            val actuales = (snap.get("inscritos") as? List<*>)?.filterIsInstance<String>()
                ?: emptyList()

            if (!actuales.contains(uid)) {
                tx.update(ref, "inscritos", actuales + uid)
            }

            null
        }.await()
    }

    /**
     * Elimina un evento por ID.
     */
    suspend fun deleteEvento(id: String) {
        db.collection(COL_EVENTOS)
            .document(id)
            .delete()
            .await()
    }
}
