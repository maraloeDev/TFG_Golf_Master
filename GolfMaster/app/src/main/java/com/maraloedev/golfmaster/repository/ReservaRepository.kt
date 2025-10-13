// Repositorio para la entidad Reserva. Permite guardar y leer reservas en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Reserva

class ReservaRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda una reserva en Firestore
    fun guardar(reserva: Reserva) {
        db.collection("reservas").document(reserva.id).set(reserva)
            .addOnSuccessListener { Log.d("Firestore", "Reserva guardada") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee una reserva por su id y la retorna por callback
    fun leer(id: String, onResult: (Reserva?) -> Unit) {
        db.collection("reservas").document(id).get()
            .addOnSuccessListener { doc ->
                val reserva = doc.toObject(Reserva::class.java)
                onResult(reserva)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
