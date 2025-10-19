package com.maraloedev.golfmaster.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * FirebaseRepo
 *
 * Repositorio central de todas las operaciones con Firebase:
 * - Autenticación (Auth)
 * - Gestión de jugadores, torneos, reservas y notificaciones.
 *
 * Adaptado para estructura PLANA (colecciones raíz):
 * jugadores, torneos, reservas, notificacion, etc.
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

    suspend fun getJugadorByCorreo(correo: String): Jugadores? {
        val snapshot = db.collection("jugadores")
            .whereEqualTo("correo_jugador", correo)
            .get().await()
        return snapshot.documents.firstOrNull()?.toObject(Jugadores::class.java)
    }

    suspend fun getJugador(uid: String): Jugadores? {
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

    suspend fun crearTorneo(t: Torneos) {
        val ref = db.collection("torneos").document()
        ref.set(t.copy(id = ref.id)).await()
    }

    // --- 📅 RESERVAS ---
    suspend fun getReservasPorJugador(uid: String): List<Reservas> =
        db.collection("reservas")
            .whereEqualTo("id_jugador", uid)
            .get().await()
            .toObjects(Reservas::class.java)

    suspend fun crearReserva(r: Reservas) {
        val ref = db.collection("reservas").document()
        ref.set(r.copy(id = ref.id)).await()
    }

    // --- 🔔 NOTIFICACIONES ---
    suspend fun getNotificaciones(): List<Notificacion> =
        db.collection("notificacion").get().await().toObjects(Notificacion::class.java)
}
