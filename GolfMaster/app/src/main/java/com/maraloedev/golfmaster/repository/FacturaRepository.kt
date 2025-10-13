// Repositorio para la entidad Factura. Permite guardar y leer facturas en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Factura

class FacturaRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda una factura en Firestore
    fun guardar(factura: Factura) {
        db.collection("facturas").document(factura.id).set(factura)
            .addOnSuccessListener { Log.d("Firestore", "Factura guardada") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee una factura por su id y la retorna por callback
    fun leer(id: String, onResult: (Factura?) -> Unit) {
        db.collection("facturas").document(id).get()
            .addOnSuccessListener { doc ->
                val factura = doc.toObject(Factura::class.java)
                onResult(factura)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
