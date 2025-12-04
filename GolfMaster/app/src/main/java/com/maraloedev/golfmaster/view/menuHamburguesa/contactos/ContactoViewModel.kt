package com.maraloedev.golfmaster.view.menuHamburguesa.contactos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

/**
 * ViewModel encargado de gestionar el envío de mensajes de contacto.
 *
 * - Valida los campos del formulario.
 */
class ContactoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Envía el mensaje de contacto a Firestore.

     */
    fun enviarMensaje(
        nombre: String,
        correo: String,
        mensaje: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validación básica de campos
        if (nombre.isBlank() || correo.isBlank() || mensaje.isBlank()) {
            onError("Por favor, completa todos los campos antes de enviar.")
            return
        }

        // Construimos el objeto de datos a guardar
        val datos = mapOf(
            "nombre" to nombre.trim(),
            "correo" to correo.trim(),
            "mensaje" to mensaje.trim(),
            "fecha_envio" to Date(),
            "uid" to (auth.currentUser?.uid ?: "anónimo")
        )

        //  Guardamos en la colección "contacto" de Firestore
        db.collection("contacto")
            .add(datos)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al enviar el mensaje a Firebase.")
            }
    }
}
