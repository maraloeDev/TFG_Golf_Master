package com.maraloedev.golfmaster.view.torneos

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.model.Torneos

class TorneosViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Crea un torneo completo en Firestore
     * y ejecuta el callback con el torneo creado.
     */
    fun crearTorneoCompleto(
        nombre: String,
        tipo: String,
        premio: String,
        lugar: String,
        formato: String,
        inicio: Timestamp,
        fin: Timestamp,
        onFinish: (Torneos) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return

        val torneo = Torneos(
            id = "", // se asignará luego
            nombre = nombre,
            tipo = tipo,
            premio = premio,
            lugar = lugar,
            formato = formato,
            fechaInicio = inicio,
            fechaFin = fin,
            creadorId = currentUser.uid
        )

        val torneoRef = db.collection("torneos").document()
        val torneoConId = torneo.copy(id = torneoRef.id)

        torneoRef
            .set(torneoConId.toMap())
            .addOnSuccessListener {
                onFinish(torneoConId)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
