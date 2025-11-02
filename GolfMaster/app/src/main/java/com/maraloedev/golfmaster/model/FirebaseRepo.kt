package com.maraloedev.golfmaster.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * FirebaseRepo
<<<<<<< HEAD
 * ----------------------------------------------------
 * Repositorio central para operaciones con Firebase:
 *  - Autenticación
 *  - Jugadores
 *  - Torneos
 *  - Inscripciones y Solicitudes
 *  - (Reservas y Notificaciones opcionalmente)
=======
 *
 * Repositorio central de todas las operaciones con Firebase:
 * - Autenticación (Auth)
 * - Gestión de jugadores, torneos, reservas y notificaciones.
 *
 * Adaptado para estructura PLANA (colecciones raíz):
 * jugadores, torneos, reservas, notificacion, etc.
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
 */
class FirebaseRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUid get() = auth.currentUser?.uid

    // --- 🔐 AUTENTICACIÓN ---
    suspend fun register(email: String, pass: String): String {
        val res = auth.createUserWithEmailAndPassword(email, pass).await()
        return res.user?.uid ?: throw Exception("Error al registrar usuario.")
    }

    suspend fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    fun logout() = auth.signOut()

    // --- 🧍 JUGADORES ---
    suspend fun createOrUpdateJugador(j: Jugadores) {
        db.collection("jugadores").document(j.id).set(j).await()
    }

<<<<<<< HEAD
    suspend fun createOrUpdateJugadorPorNombreYLicencia(
        nombre: String,
        licencia: String
    ): Jugadores {
        if (nombre.isBlank()) throw Exception("El nombre no puede estar vacío.")
        if (licencia.isBlank()) throw Exception("El número de licencia no puede estar vacío.")

        // Buscar si ya existe un jugador con esa licencia
        val existente = db.collection("jugadores")
            .whereEqualTo("numero_licencia_jugador", licencia)
            .limit(1)
            .get().await()

        val idDoc = if (existente.isEmpty)
            db.collection("jugadores").document().id
        else
            existente.documents.first().id

        val jugador = Jugadores(
            id = idDoc,
            nombre_jugador = nombre.trim(),
            numero_licencia_jugador = licencia.trim()
        )

        db.collection("jugadores").document(idDoc).set(jugador).await()
        return jugador
=======
    suspend fun getJugadorByCorreo(correo: String): Jugadores? {
        val snapshot = db.collection("jugadores")
            .whereEqualTo("correo_jugador", correo)
            .get().await()
        return snapshot.documents.firstOrNull()?.toObject(Jugadores::class.java)
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
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
            .get().await()
            .toObjects(Jugadores::class.java)

    // --- 🏆 TORNEOS ---
    suspend fun getTorneos(): List<Torneos> =
        db.collection("torneos").get().await().toObjects(Torneos::class.java)

    suspend fun crearTorneo(t: Torneos): Torneos {
        val ref = db.collection("torneos").document()
        val torneoConId = t.copy(id = ref.id)
        ref.set(torneoConId).await()
        return torneoConId
    }

<<<<<<< HEAD
    // ============================================================
    // ✅ INSCRIPCIÓN DIRECTA EN TORNEO
    // ============================================================
    suspend fun inscribirseEnTorneo(t: Torneos, usuarioId: String) {
        if (t.id.isBlank()) throw Exception("El torneo no tiene ID válido.")
        if (usuarioId.isBlank()) throw Exception("El usuario no es válido.")

        val yaInscrito = db.collection("inscripciones")
            .whereEqualTo("torneoId", t.id)
            .whereEqualTo("usuarioId", usuarioId)
=======
    // --- 📅 RESERVAS ---
    suspend fun getReservasPorJugador(uid: String): List<Reserva> =
        db.collection("reservas")
            .whereEqualTo("id_jugador", uid)
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
            .get().await()
            .toObjects(Reserva::class.java)

    suspend fun crearReserva(r: Reserva) {
        val ref = db.collection("reservas").document()
        ref.set(r.copy(id = ref.id)).await()
    }

<<<<<<< HEAD
    // ============================================================
    // 📬 SOLICITUDES DE INSCRIPCIÓN
    // ============================================================
    suspend fun enviarSolicitudInscripcion(torneoId: String, usuarioId: String) {
        if (torneoId.isBlank() || usuarioId.isBlank()) throw Exception("Datos inválidos.")

        // Evitar solicitudes duplicadas
        val existente = db.collection("solicitudes_inscripcion")
            .whereEqualTo("torneoId", torneoId)
            .whereEqualTo("usuarioId", usuarioId)
            .limit(1)
            .get().await()

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

    // ============================================================
    // 🔄 ESCUCHAS EN TIEMPO REAL DE TORNEOS
    // ============================================================
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

    /** Obtener todas las reservas de un jugador */
    suspend fun getReservasPorJugador(uid: String): List<Reserva> {
        if (uid.isBlank()) throw Exception("UID inválido.")
        val snapshot = db.collection("reservas")
            .whereEqualTo("id_jugador", uid)
            .get()
            .await()
        return snapshot.toObjects(Reserva::class.java)
    }

    /** Crear nueva reserva */
    suspend fun crearReserva(r: Reserva) {
        val ref = db.collection("reservas").document()
        val reservaFinal = r.copy(id = ref.id)
        ref.set(reservaFinal).await()
    }

    /** Actualizar reserva existente */
    suspend fun actualizarReserva(id: String, nuevosDatos: Map<String, Any>) {
        if (id.isBlank()) throw Exception("ID de reserva no válido")
        db.collection("reservas").document(id).update(nuevosDatos).await()
    }
=======
    // --- 🔔 NOTIFICACIONES ---
    suspend fun getNotificaciones(): List<Notificacion> =
        db.collection("notificacion").get().await().toObjects(Notificacion::class.java)
>>>>>>> parent of ff2be93 (EventosScreen + Amigos Screen Success)
}

