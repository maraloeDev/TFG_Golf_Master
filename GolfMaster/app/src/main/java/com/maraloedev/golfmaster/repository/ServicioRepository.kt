// Repositorio para la entidad Servicio. Permite guardar y leer servicios en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Servicio

class ServicioRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda un servicio en Firestore
    fun guardar(servicio: Servicio) {
        db.collection("servicios").document(servicio.id).set(servicio)
            .addOnSuccessListener { Log.d("Firestore", "Servicio guardado") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee un servicio por su id y lo retorna por callback
    fun leer(id: String, onResult: (Servicio?) -> Unit) {
        db.collection("servicios").document(id).get()
            .addOnSuccessListener { doc ->
                val servicio = doc.toObject(Servicio::class.java)
                onResult(servicio)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
