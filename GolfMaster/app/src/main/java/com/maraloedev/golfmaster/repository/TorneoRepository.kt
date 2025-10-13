// Repositorio para la entidad Torneo. Permite guardar y leer torneos en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Torneo

class TorneoRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda un torneo en Firestore
    fun guardar(torneo: Torneo) {
        db.collection("torneos").document(torneo.id).set(torneo)
            .addOnSuccessListener { Log.d("Firestore", "Torneo guardado") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee un torneo por su id y lo retorna por callback
    fun leer(id: String, onResult: (Torneo?) -> Unit) {
        db.collection("torneos").document(id).get()
            .addOnSuccessListener { doc ->
                val torneo = doc.toObject(Torneo::class.java)
                onResult(torneo)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
