// Repositorio para la entidad Jugador. Permite guardar y leer jugadores en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Jugador

class JugadorRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda un jugador en Firestore
    fun guardar(jugador: Jugador) {
        db.collection("jugadores").document(jugador.id).set(jugador)
            .addOnSuccessListener { Log.d("Firestore", "Jugador guardado") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee un jugador por su id y lo retorna por callback
    fun leer(id: String, onResult: (Jugador?) -> Unit) {
        db.collection("jugadores").document(id).get()
            .addOnSuccessListener { doc ->
                val jugador = doc.toObject(Jugador::class.java)
                onResult(jugador)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
