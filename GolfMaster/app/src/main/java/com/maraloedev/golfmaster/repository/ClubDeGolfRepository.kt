
// Repositorio para la entidad ClubDeGolf. Permite guardar y leer clubes de golf en Firestore.
package com.maraloedev.golfmaster.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.maraloedev.golfmaster.model.ClubDeGolf

class ClubDeGolfRepository {
    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Guarda un club de golf en Firestore
    fun guardar(club: ClubDeGolf) {
        db.collection("clubes_de_golf").document(club.id).set(club)
            .addOnSuccessListener { Log.d("Firestore", "Club guardado") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e) }
    }

    // Lee un club de golf por su id y lo retorna por callback
    fun leer(id: String, onResult: (ClubDeGolf?) -> Unit) {
        db.collection("clubes_de_golf").document(id).get()
            .addOnSuccessListener { doc ->
                val club = doc.toObject(ClubDeGolf::class.java)
                onResult(club)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Error", e); onResult(null) }
    }
}
