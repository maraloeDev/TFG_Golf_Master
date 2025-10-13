// Repositorio para la entidad Handicap. Permite guardar y leer handicaps en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Handicap

class HandicapRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda un handicap en Firestore
    fun guardar(handicap: Handicap) {
        db.collection("handicaps").document(handicap.id).set(handicap)
            .addOnSuccessListener { Log.d("Firestore", "Handicap guardado") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee un handicap por su id y lo retorna por callback
    fun leer(id: String, onResult: (Handicap?) -> Unit) {
        db.collection("handicaps").document(id).get()
            .addOnSuccessListener { doc ->
                val handicap = doc.toObject(Handicap::class.java)
                onResult(handicap)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
