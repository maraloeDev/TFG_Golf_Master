package com.maraloedev.golfmaster.view.menuHamburguesa.contactos

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/* 🎨 Paleta GolfMaster (idealmente iría en un Theme común) */
private val ScreenBg = Color(0xFF00281F)
private val Accent = Color(0xFF00FF77)

/* Datos de contacto del club / app */
private const val CONTACT_PHONE = "609 048 714"
private const val CONTACT_EMAIL = "martinsonsecaeduardo@gmail.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(
    vm: ContactoViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Obtenemos instancias de Firebase una sola vez por composición
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    // Estado de los campos
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf(false) }

    // ============================================================
    // 🔹 Cargar datos del jugador actual al entrar en la pantalla
    //    (nombre y correo se completan automáticamente)
    // ============================================================
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        try {
            val doc = db.collection("jugadores").document(uid).get().await()
            nombre = doc.getString("nombre_jugador") ?: ""
            correo = doc.getString("correo_jugador") ?: ""
        } catch (_: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("❌ Error al cargar tus datos")
            }
        }
    }

    // ============================================================
    // 🔹 Funciones de acción: abrir email / teléfono
    // ============================================================

    // Abrir el cliente de correo con un email pre-rellenado
    fun abrirEmailCliente() {
        val uri = Uri.parse("mailto:$CONTACT_EMAIL")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Contacto GolfMaster - $nombre")
            putExtra(
                Intent.EXTRA_TEXT,
                """
                Hola equipo GolfMaster,

                $mensaje

                ---
                Datos de contacto:
                • Nombre: $nombre
                • Email: $correo
                """.trimIndent()
            )
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Enviar email"))
            scope.launch { snackbarHostState.showSnackbar("📧 Abriendo cliente de correo...") }
        } catch (_: Exception) {
            scope.launch { snackbarHostState.showSnackbar("❌ No se encontró app de correo") }
        }
    }

    // Abrir la app de teléfono con el número del club
    fun abrirTelefono() {
        // Para el intent tel: es más seguro eliminar espacios
        val numeroLimpio = CONTACT_PHONE.filter { it.isDigit() }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numeroLimpio"))
        try {
            context.startActivity(intent)
            scope.launch { snackbarHostState.showSnackbar("📞 Abriendo marcador telefónico...") }
        } catch (_: Exception) {
            scope.launch { snackbarHostState.showSnackbar("❌ No se pudo abrir el marcador") }
        }
    }

    // ============================================================
    // 🔹 UI principal
    // ============================================================
    Scaffold(
        containerColor = ScreenBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contacto",
                        color = Accent,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== Campos de información del usuario (solo lectura) =====
            OutlinedTextField(
                value = nombre,
                onValueChange = {}, // readOnly → no actualizamos
                label = { Text("Nombre") },
                singleLine = true,
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = {},
                label = { Text("Correo electrónico") },
                singleLine = true,
                readOnly = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            // Campo de mensaje que escribe el usuario
            OutlinedTextField(
                value = mensaje,
                onValueChange = {
                    mensaje = it
                    errorMensaje = false
                },
                label = { Text("Mensaje") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                colors = fieldColors(),
                isError = errorMensaje
            )

            // Mensaje de error bajo el TextField si está vacío
            AnimatedVisibility(errorMensaje) {
                Text(
                    text = "El mensaje no puede estar vacío",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(Modifier.height(20.dp))

            // ===== Botón principal de envío =====
            Button(
                onClick = {
                    // Validación local: el mensaje no puede estar vacío
                    if (mensaje.isBlank()) {
                        errorMensaje = true
                        scope.launch {
                            snackbarHostState.showSnackbar("❌ Escribe un mensaje antes de enviar")
                        }
                        return@Button
                    }

                    vm.enviarMensaje(
                        nombre = nombre,
                        correo = correo,
                        mensaje = mensaje,
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Mensaje enviado correctamente")
                            }
                            // Abre cliente de email con el contenido preparado
                            abrirEmailCliente()
                            mensaje = ""
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar("❌ Error: $error")
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text(
                    text = "Enviar",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(36.dp))

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            Spacer(Modifier.height(20.dp))

            // ===== Información de contacto adicional (teléfono / email) =====
            Text(
                text = "Información de contacto",
                color = Accent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(16.dp))

            ContactInfoItem(
                icon = Icons.Default.Phone,
                text = "+34 $CONTACT_PHONE",
                onClick = { abrirTelefono() }
            )

            Spacer(Modifier.height(10.dp))

            ContactInfoItem(
                icon = Icons.Default.Email,
                text = CONTACT_EMAIL,
                onClick = { abrirEmailCliente() }
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

/* ============================================================
 * 🔹 COMPONENTES REUTILIZABLES
 * ============================================================ */

/**
 * Fila reutilizable para mostrar un dato de contacto con icono
 * (teléfono, email, etc.) que es clickable.
 */
@Composable
private fun ContactInfoItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Accent
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}

/**
 * Estilo común para todos los OutlinedTextField de esta pantalla.
 */
@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Accent,
    unfocusedBorderColor = Color(0xFF1F4D3E),
    focusedContainerColor = Color(0xFF173E34),
    unfocusedContainerColor = Color(0xFF173E34),
    focusedLabelColor = Accent,
    unfocusedLabelColor = Color.Gray,
    cursorColor = Accent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
