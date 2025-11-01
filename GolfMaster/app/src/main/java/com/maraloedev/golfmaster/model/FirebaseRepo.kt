package com.maraloedev.golfmaster.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * FirebaseRepo
 * ----------------------------------------------------
 * Repositorio central para operaciones con Firebase:
 *  - Autenticación
 *  - Jugadores
 *  - Torneos
 *  - Reservas
 *  - Notificaciones
 *  - Inscripciones y Solicitudes
 */
class FirebaseRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUid get() = auth.currentUser?.uid

    // ============================================================
    // 🔐 AUTENTICACIÓN
    // ============================================================
    suspend fun register(email: String, pass: String): String {
        val res = auth.createUserWithEmailAndPassword(email, pass).await()
        return res.user?.uid ?: throw Exception("Error al registrar usuario.")
    }

    suspend fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    fun logout() = auth.signOut()

    // ============================================================
    // 🧍 JUGADORES
    // ============================================================
    suspend fun createOrUpdateJugador(j: Jugadores) {
        if (j.id.isBlank()) throw Exception("ID de jugador no válido.")
        db.collection("jugadores").document(j.id).set(j).await()
    }

    suspend fun createOrUpdateJugadorPorNombreYLicencia(nombre: String, licencia: String): Jugadores {
        if (nombre.isBlank()) throw Exception("El nombre no puede estar vacío.")
        if (licencia.isBlank()) throw Exception("El número de licencia no puede estar vacío.")

        val existente = db.collection("jugadores")
            .whereEqualTo("numero_licencia_jugador", licencia)
            .limit(1)
            .get().await()

        val idDoc = if (existente.isEmpty) db.collection("jugadores").document().id
        else existente.documents.first().id

        val jugador = Jugadores(
            id = idDoc,
            nombre_jugador = nombre.trim(),
            numero_licencia_jugador = licencia.trim()
        )

        db.collection("jugadores").document(idDoc).set(jugador).await()
        return jugador
    }

    suspend fun getJugador(uid: String): Jugadores? {
        val doc = db.collection("jugadores").document(uid).get().await()
        return doc.toObject(Jugadores::class.java)
    }

    suspend fun buscarJugadoresPorNombreOLicencia(query: String): List<Jugadores> {
        if (query.isBlank()) return emptyList()

        val porNombre = db.collection("jugadores")
            .whereGreaterThanOrEqualTo("nombre_jugador", query)
            .whereLessThanOrEqualTo("nombre_jugador", query + "\uf8ff")
            .get().await()
            .toObjects(Jugadores::class.java)

        val porLicencia = db.collection("jugadores")
            .whereEqualTo("numero_licencia_jugador", query)
            .get().await()
            .toObjects(Jugadores::class.java)

        return (porNombre + porLicencia).distinctBy { it.id }
    }

    fun listenJugadoresPorNombrePrefix(prefix: String): Flow<List<Jugadores>> = callbackFlow {
        if (prefix.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query: Query = db.collection("jugadores")
            .orderBy("nombre_jugador")
            .startAt(prefix)
            .endAt(prefix + "\uf8ff")

        val listener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val lista = snapshot?.toObjects(Jugadores::class.java) ?: emptyList()
            trySend(lista)
        }

        awaitClose { listener.remove() }
    }

    // ============================================================
    // 🏆 TORNEOS
    // ============================================================
    suspend fun getTorneos(): List<Torneos> {
        val snapshot = db.collection("torneos").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Torneos::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun getTorneoById(id: String): Torneos? {
        if (id.isBlank()) throw Exception("ID de torneo no válido.")
        val doc = db.collection("torneos").document(id).get().await()
        return if (doc.exists()) doc.toObject(Torneos::class.java)?.copy(id = doc.id) else null
    }

    suspend fun crearTorneo(t: Torneos) {
        val ref = db.collection("torneos").document()
        ref.set(t.copy(id = ref.id)).await()
    }

    // ============================================================
    // ✅ INSCRIPCIÓN DIRECTA
    // ============================================================
    suspend fun inscribirseEnTorneo(t: Torneos, usuarioId: String) {
        if (t.id.isBlank()) throw Exception("El torneo no tiene ID válido.")
        if (usuarioId.isBlank()) throw Exception("El usuario no es válido.")

        val yaInscrito = db.collection("inscripciones")
            .whereEqualTo("torneoId", t.id)
            .whereEqualTo("usuarioId", usuarioId)
            .get().await()

        if (!yaInscrito.isEmpty) throw Exception("Ya estás inscrito en este torneo.")

        val ref = db.collection("inscripciones").document()
        val data = mapOf(
            "id" to ref.id,
            "torneoId" to t.id,
            "usuarioId" to usuarioId,
            "fecha" to Timestamp.now()
        )
        ref.set(data).await()
    }

    // ============================================================
    // 📬 SOLICITUDES DE INSCRIPCIÓN
    // ============================================================
    suspend fun enviarSolicitudInscripcion(torneoId: String, usuarioId: String) {
        if (torneoId.isBlank() || usuarioId.isBlank()) throw Exception("Datos inválidos.")

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
