package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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
 *  - Inscripciones
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

    suspend fun buscarJugadoresPorNombre(nombre: String): List<Jugadores> =
        db.collection("jugadores")
            .whereGreaterThanOrEqualTo("nombre_jugador", nombre)
            .whereLessThanOrEqualTo("nombre_jugador", nombre + "\uf8ff")
            .get()
            .await()
            .toObjects(Jugadores::class.java)


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
    suspend fun crearReserva(r: Reserva): String {
        val ref = db.collection("reservas").document()
        val reservaFinal = r.copy(id = ref.id)
        ref.set(reservaFinal).await()
        return ref.id
    }

    suspend fun getReservasPorJugador(uid: String): List<Reserva> {
        val snapshot = db.collection("reservas")
            .whereArrayContains("participantesIds", uid)  // 👈 AHORA POR PARTICIPANTES
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val reserva = doc.toObject(Reserva::class.java)
            reserva?.copy(id = doc.id)
        }
    }


    suspend fun getReservasUsuario(): List<Reserva> {
        val snapshot = db.collection("reservas").get().await()
        return snapshot.toObjects(Reserva::class.java)
    }

    suspend fun actualizarReserva(id: String, nuevosDatos: Map<String, Any>) {
        if (id.isBlank()) throw Exception("ID de reserva no válido")
        db.collection("reservas").document(id).update(nuevosDatos).await()
    }

    suspend fun eliminarReserva(id: String) {
        if (id.isBlank()) throw Exception("ID de reserva no válido")
        db.collection("reservas").document(id).delete().await()
    }


    // ============================================================
    // 🟩 INVITACIONES
    // ============================================================
    suspend fun crearInvitacion(de: String, para: String, reservaId: String) {
        if (de.isBlank() || para.isBlank() || reservaId.isBlank()) {
            throw Exception("Datos de invitación inválidos.")
        }

        val ref = db.collection("invitaciones").document()
        val data = hashMapOf(
            "id" to ref.id,
            "de" to de,
            "para" to para,
            "reservaId" to reservaId,
            "estado" to "pendiente",
            "fecha" to Timestamp.now()
        )
        ref.set(data).await()
    }


    // ============================================================
    // 🏆 EVENTOS
    // ============================================================
    suspend fun getEventos(): List<Evento> {
        return try {
            db.collection("eventos")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Evento::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEvento(evento: Evento) {
        val currentUser = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val data = hashMapOf(
            "nombre" to evento.nombre,
            "tipo" to evento.tipo,
            "plazas" to (evento.plazas ?: 0),
            "precioSocio" to (evento.precioSocio ?: 0.0),
            "precioNoSocio" to (evento.precioNoSocio ?: 0.0),
            "fechaInicio" to (evento.fechaInicio ?: Timestamp.now()),
            "fechaFin" to (evento.fechaFin ?: Timestamp.now()),
            "organizadorId" to currentUser.uid
        )
        db.collection("eventos").add(data).await()
    }

    suspend fun inscribirseEnEvento(evento: Evento) {
        val eventoId = evento.id ?: throw Exception("ID del evento no válido")
        val docRef = db.collection("eventos").document(eventoId)

        db.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            val plazasActuales = snapshot.getLong("plazas") ?: 0
            if (plazasActuales > 0) {
                tx.update(docRef, "plazas", plazasActuales - 1)
            } else {
                throw Exception("No quedan plazas disponibles para este evento.")
            }
        }.await()
    }

    suspend fun updateEvento(evento: Evento) {
        val currentUser = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val eventoId = evento.id ?: throw Exception("ID del evento no válido")

        val eventoRef = db.collection("eventos").document(eventoId)
        val snapshot = eventoRef.get().await()
        val organizadorId = snapshot.getString("organizadorId")

        if (organizadorId != currentUser.uid) {
            throw Exception("No tienes permisos para editar este evento.")
        }

        val updates = mapOf(
            "nombre" to evento.nombre,
            "tipo" to evento.tipo,
            "plazas" to evento.plazas,
            "precioSocio" to evento.precioSocio,
            "precioNoSocio" to evento.precioNoSocio,
            "fechaInicio" to evento.fechaInicio,
            "fechaFin" to evento.fechaFin
        )
        eventoRef.update(updates).await()
    }

    suspend fun deleteEvento(eventoId: String) {
        val currentUser = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val eventoRef = db.collection("eventos").document(eventoId)
        val snapshot = eventoRef.get().await()
        val organizadorId = snapshot.getString("organizadorId")

        if (organizadorId != currentUser.uid) {
            throw Exception("No tienes permisos para eliminar este evento.")
        }

        eventoRef.delete().await()
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
