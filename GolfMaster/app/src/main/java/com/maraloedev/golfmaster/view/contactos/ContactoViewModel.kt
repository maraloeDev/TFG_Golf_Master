package com.maraloedev.golfmaster.view.contactos

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ContactoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun enviarMensaje(
        nombre: String,
        correo: String,
        mensaje: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.isBlank() || correo.isBlank() || mensaje.isBlank()) {
            onError("Por favor, completa todos los campos")
            return
        }

        val datos = mapOf(
            "nombre" to nombre,
            "correo" to correo,
            "mensaje" to mensaje,
            "fecha" to Date()
        )

        db.collection("contacto")
            .add(datos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Error al enviar el mensaje") }
    }
}
