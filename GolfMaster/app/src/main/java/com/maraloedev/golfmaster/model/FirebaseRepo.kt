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


class FirebaseRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // ============================================================
    //  AUTENTICACIÓN
    // ============================================================
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
    suspend fun buscarJugadoresPorNombre(nombre: String): List<Jugadores> {
        val snap = db.collection("jugadores").whereGreaterThanOrEqualTo("nombre_jugador", nombre)
            .whereLessThanOrEqualTo("nombre_jugador", nombre + "\uf8ff").get().await()

        return snap.documents.mapNotNull { it.toObject(Jugadores::class.java) }
    }

    // ============================================================
    //  RESERVAS
    // ============================================================
    suspend fun crearReserva(reserva: Reserva): String {
        val docRef = db.collection("reservas").document()
        val reservaConId = reserva.copy(id = docRef.id)
        docRef.set(reservaConId).await()
        return docRef.id
    }

    suspend fun actualizarReserva(id: String, nuevosDatos: Map<String, Any>) {
        db.collection("reservas").document(id).update(nuevosDatos).await()
    }

    suspend fun eliminarReserva(id: String) {
        db.collection("reservas").document(id).delete().await()
    }

    suspend fun anadirParticipanteAReserva(
        reservaId: String, userId: String
    ) {
        val reservaRef = db.collection("reservas").document(reservaId)
        db.runTransaction { tx ->
            val snap = tx.get(reservaRef)
            val actuales = (snap.get("participantesIds") as? List<*>).orEmpty()
            if (!actuales.contains(userId)) {
                tx.update(reservaRef, "participantesIds", actuales + userId)
            }
        }.await()
    }

    // ============================================================
    //  INVITACIONES
    // ============================================================
    suspend fun crearInvitacion(
        de: String, para: String, reservaId: String, fecha: Timestamp?
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

    suspend fun actualizarEstadoInvitacion(
        invitacionId: String, nuevoEstado: String
    ) {
        db.collection("invitaciones").document(invitacionId).update("estado", nuevoEstado).await()
    }

    // ============================================================
    //  EVENTOS
    // ============================================================
    suspend fun getEventos(): List<Evento> {
        val snap = db.collection("eventos").get().await()
        return snap.documents.mapNotNull { doc ->
            doc.toObject(Evento::class.java)?.copy(id = doc.id)
        }
    }

    fun getEventosFlow(): Flow<List<Evento>> = callbackFlow {
        val listener: ListenerRegistration =
            db.collection("eventos").addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val lista = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Evento::class.java)?.copy(id = doc.id)
                    } ?: emptyList()

                    trySend(element = lista)
                }

        awaitClose { listener.remove() }
    }

    suspend fun addEvento(evento: Evento) {
        db.collection("eventos").add(evento).await()
    }

    suspend fun inscribirseEnEvento(eventoId: String, uid: String) {
        val ref = db.collection("eventos").document(eventoId)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val actuales = (snap.get("inscritos") as? List<String>) ?: emptyList()
            if (!actuales.contains(uid)) {
                tx.update(ref, "inscritos", actuales + uid)
            }
        }.await()
    }

    suspend fun updateEvento(evento: Evento) {
        val id = evento.id ?: return
        db.collection("eventos").document(id).set(evento).await()
    }

    suspend fun deleteEvento(id: String) {
        db.collection("eventos").document(id).delete().await()
    }
}