// Repositorio para la entidad Precio. Permite guardar y leer precios en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.Precio

class PrecioRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda un precio en Firestore
    fun guardar(precio: Precio) {
        db.collection("precios").document(precio.id).set(precio)
            .addOnSuccessListener { Log.d("Firestore", "Precio guardado") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee un precio por su id y lo retorna por callback
    fun leer(id: String, onResult: (Precio?) -> Unit) {
        db.collection("precios").document(id).get()
            .addOnSuccessListener { doc ->
                val precio = doc.toObject(Precio::class.java)
                onResult(precio)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
