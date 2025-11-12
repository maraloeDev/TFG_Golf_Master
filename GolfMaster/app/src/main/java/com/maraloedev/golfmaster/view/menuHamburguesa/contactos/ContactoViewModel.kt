package com.maraloedev.golfmaster.view.menuHamburguesa.contactos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

class ContactoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Envía un mensaje de contacto a Firebase Firestore.
     *
     * @param nombre Nombre del usuario.
     * @param correo Correo electrónico del usuario.
     * @param mensaje Contenido del mensaje.
     * @param onSuccess Callback si se guarda correctamente.
     * @param onError Callback con mensaje de error.
     */
    fun enviarMensaje(
        nombre: String,
        correo: String,
        mensaje: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 🔹 Validación de campos
        if (nombre.isBlank() || correo.isBlank() || mensaje.isBlank()) {
            onError("Por favor, completa todos los campos antes de enviar.")
            return
        }

        // 🔹 Crear objeto de datos
        val datos = mapOf(
            "nombre" to nombre.trim(),
            "correo" to correo.trim(),
            "mensaje" to mensaje.trim(),
            "fecha_envio" to Date(),
            "uid" to (auth.currentUser?.uid ?: "anónimo")
        )

        // 🔹 Guardar en Firestore con control de errores limpio
        runCatching {
            db.collection("contacto")
                .add(datos)
        }.onSuccess {
            it.addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Error al enviar el mensaje a Firebase.")
                }
        }.onFailure {
            onError(it.localizedMessage ?: "Error interno al procesar el mensaje.")
        }
    }
}
