package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * ============================================================
 * 🧩 FirebaseRepo
 * ------------------------------------------------------------
 * Repositorio central para operaciones con Firebase:
 *  - Autenticación
 *  - Jugadores
 *  - Torneos
 *  - Reservas
 *  - Invitaciones
 *  - Eventos
 *  - Solicitudes de inscripción
 * ============================================================
 */
class FirebaseRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUid get() = auth.currentUser?.uid

    // ============================================================
    // 🔐 AUTENTICACIÓN
    // ============================================================
    suspend fun login(email: String, pass: String) {
        try {
            auth.signInWithEmailAndPassword(email, pass).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw Exception("El usuario no existe o ha sido eliminado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("La contraseña es incorrecta.")
        } catch (e: Exception) {
            throw Exception("Error al iniciar sesión: ${e.message}")
        }
    }

    suspend fun register(email: String, pass: String): String {
        val res = auth.createUserWithEmailAndPassword(email, pass).await()
        return res.user?.uid ?: throw Exception("Error al registrar usuario.")
    }

    fun logout() = auth.signOut()

    // ============================================================
    // 🧍 JUGADORES
    // ============================================================
    suspend fun createOrUpdateJugador(j: Jugadores) {
        db.collection("jugadores").document(j.id).set(j).await()
    }

    suspend fun getJugador(uid: String): Jugadores? {
        if (uid.isBlank()) return null
        val doc = db.collection("jugadores").document(uid).get().await()
        return doc.toObject(Jugadores::class.java)
    }

    suspend fun buscarJugadoresPorNombre(nombre: String): List<Jugadores> {
        val snap = db.collection("jugadores")
            .whereGreaterThanOrEqualTo("nombre_jugador", nombre)
            .whereLessThanOrEqualTo("nombre_jugador", nombre + "\uf8ff")
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Jugadores::class.java) }
    }

    // ============================================================
    // 🏌️ TORNEOS
    // ============================================================
    suspend fun getTorneos(): List<Torneos> =
        db.collection("torneos").get().await().toObjects(Torneos::class.java)

    suspend fun crearTorneo(t: Torneos): Torneos {
        val ref = db.collection("torneos").document()
        val torneoConId = t.copy(id = ref.id)
        ref.set(torneoConId).await()
        return torneoConId
    }

    suspend fun getTorneoById(id: String): Torneos? {
        if (id.isBlank()) throw Exception("ID de torneo no válido")
        val doc = db.collection("torneos").document(id).get().await()
        if (!doc.exists()) throw Exception("El torneo no existe")
        return doc.toObject(Torneos::class.java)?.copy(id = doc.id)
    }

    fun listenTorneos(): Flow<List<Torneos>> = callbackFlow {
        val listener = db.collection("torneos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val lista = snapshot?.toObjects(Torneos::class.java) ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    // ============================================================
    // 📅 RESERVAS
    // ============================================================
    suspend fun crearReserva(reserva: Reserva): String {
        val docRef = db.collection("reservas").document()
        val reservaConId = reserva.copy(id = docRef.id)
        docRef.set(reservaConId).await()
        return docRef.id
    }

    suspend fun actualizarReserva(id: String, nuevosDatos: Map<String, Any>) {
        db.collection("reservas")
            .document(id)
            .update(nuevosDatos)
            .await()
    }

    suspend fun eliminarReserva(id: String) {
        db.collection("reservas")
            .document(id)
            .delete()
            .await()
    }

    // Solo reservas donde el usuario sea PARTICIPANTE
    suspend fun getReservasPorJugador(uid: String): List<Reserva> {
        val snap = db.collection("reservas")
            .whereArrayContains("participantesIds", uid)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Reserva::class.java) }
    }

    suspend fun anadirParticipanteAReserva(
        reservaId: String,
        userId: String
    ) {
        val reservaRef = db.collection("reservas").document(reservaId)
        db.runTransaction { tx ->
            val snap = tx.get(reservaRef)
            val actuales = (snap.get("participantesIds") as? List<String>).orEmpty()
            if (!actuales.contains(userId)) {
                tx.update(reservaRef, "participantesIds", actuales + userId)
            }
        }.await()
    }

    // ============================================================
    // 💌 INVITACIONES
    // ============================================================
    suspend fun crearInvitacion(
        de: String,
        para: String,
        reservaId: String,
        fecha: Timestamp?
    ): String {
        // 1️⃣ Obtener nombre del jugador que invita
        val jugadorSnap = db.collection("jugadores").document(de).get().await()
        val nombreDe = jugadorSnap.getString("nombre_jugador") ?: "Un jugador"

        // 2️⃣ Crear doc de invitación
        val docRef = db.collection("invitaciones").document()
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

    suspend fun getInvitacionesPendientes(paraId: String): List<Invitacion> {
        val snap = db.collection("invitaciones")
            .whereEqualTo("paraId", paraId)
            .whereEqualTo("estado", "pendiente")
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Invitacion::class.java) }
    }

    suspend fun actualizarEstadoInvitacion(
        invitacionId: String,
        nuevoEstado: String
    ) {
        db.collection("invitaciones")
            .document(invitacionId)
            .update("estado", nuevoEstado)
            .await()
    }

    // ============================================================
// 🏆 EVENTOS
// ============================================================
    private val eventosRef = db.collection("eventos")

    suspend fun getEventosDeUsuario(uid: String): List<Evento> {
        return eventosRef
            .whereEqualTo("creadorId", uid)  // 👈 sólo eventos creados por ese usuario
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.toObject(Evento::class.java)?.copy(id = doc.id)
            }
    }

    suspend fun getEventos(): List<Evento> {
        // Si aún quieres tener un "get all" genérico, lo dejas aquí
        return eventosRef.get().await().documents.mapNotNull { doc ->
            doc.toObject(Evento::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun addEvento(evento: Evento) {
        val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

        val eventoConCreador = evento.copy(creadorId = uid)
        eventosRef.add(eventoConCreador).await()
    }

    // Inscribir varios usuarios: va acumulando en el array "inscritos"
    suspend fun inscribirseEnEvento(eventoId: String, uid: String) {
        eventosRef.document(eventoId).update(
            "inscritos", FieldValue.arrayUnion(uid)
        ).await()
    }

    suspend fun updateEvento(evento: Evento) {
        val id = evento.id ?: return
        eventosRef.document(id).set(evento).await()
    }

    suspend fun deleteEvento(id: String) {
        eventosRef.document(id).delete().await()
    }


    // ============================================================
    // 📬 SOLICITUDES DE INSCRIPCIÓN (TORNEOS)
    // ============================================================
    suspend fun enviarSolicitudInscripcion(torneoId: String, usuarioId: String) {
        if (torneoId.isBlank() || usuarioId.isBlank()) throw Exception("Datos inválidos.")

        val existente = db.collection("solicitudes_inscripcion")
            .whereEqualTo("torneoId", torneoId)
            .whereEqualTo("usuarioId", usuarioId)
            .limit(1)
            .get()
            .await()

        if (!existente.isEmpty) throw Exception("Ya enviaste una solicitud para este torneo.")

        val ref = db.collection("solicitudes_inscripcion").document()
        val data = mapOf(
            "id" to ref.id,
            "torneoId" to torneoId,
            "usuarioId" to usuarioId,
            "fecha" to Timestamp.now(),
            "estado" to "pendiente"
        )
        ref.set(data).await()
    }
}