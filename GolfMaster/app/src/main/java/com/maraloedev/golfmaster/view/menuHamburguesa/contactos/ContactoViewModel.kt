package com.maraloedev.golfmaster.view.menuHamburguesa.contactos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

/**
 * ViewModel encargado de gestionar el envío de mensajes de contacto.
 *
 * - Valida los campos del formulario.
 * - Inserta el mensaje en la colección "contacto" de Firestore.
 * - Incluye fecha de envío y UID del usuario (si está logueado).
 */
class ContactoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Envía el mensaje de contacto a Firestore.
     *
     * @param nombre Nombre del usuario.
     * @param correo Correo del usuario.
     * @param mensaje Contenido del mensaje.
     * @param onSuccess Se ejecuta cuando el guardado en Firestore se completa correctamente.
     * @param onError Se ejecuta cuando ocurre cualquier error, con un mensaje para mostrar en UI.
     */
    fun enviarMensaje(
        nombre: String,
        correo: String,
        mensaje: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 1️⃣ Validación básica de campos
        if (nombre.isBlank() || correo.isBlank() || mensaje.isBlank()) {
            onError("Por favor, completa todos los campos antes de enviar.")
            return
        }

        // 2️⃣ Construimos el objeto de datos a guardar
        val datos = mapOf(
            "nombre" to nombre.trim(),
            "correo" to correo.trim(),
            "mensaje" to mensaje.trim(),
            "fecha_envio" to Date(),
            "uid" to (auth.currentUser?.uid ?: "anónimo")
        )

        // 3️⃣ Guardamos en la colección "contacto" de Firestore
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
