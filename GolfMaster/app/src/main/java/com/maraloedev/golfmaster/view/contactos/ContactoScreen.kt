package com.maraloedev.golfmaster.view.contacto

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maraloedev.golfmaster.view.contactos.ContactoViewModel
import kotlinx.coroutines.launch

private const val CONTACT_PHONE = "609 048 714"
private const val CONTACT_EMAIL = "martinsonsecaeduardo@gmail.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(vm: ContactoViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Obtener jugador autenticado
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    // Cargar datos del jugador desde Firestore
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("jugadores").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    nombre = doc.getString("nombre_jugador") ?: ""
                    correo = doc.getString("correo_jugador") ?: ""
                }
                .addOnFailureListener {
                    scope.launch { snackbarHostState.showSnackbar("❌ Error al cargar tus datos") }
                }
        }
    }

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
        } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar("❌ No se encontró app de correo") }
        }
    }

    fun abrirTelefono() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$CONTACT_PHONE"))
        try {
            context.startActivity(intent)
            scope.launch { snackbarHostState.showSnackbar("📞 Abriendo marcador telefónico...") }
        } catch (_: Exception) {
            scope.launch { snackbarHostState.showSnackbar("❌ No se pudo abrir el marcador") }
        }
    }

    Scaffold(
        containerColor = Color(0xFF0B3D2E),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Contacto", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B3D2E))
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
            // 🧍 Nombre (solo lectura)
            OutlinedTextField(
                value = nombre,
                onValueChange = {},
                label = { Text("Nombre") },
                singleLine = true,
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            // 📧 Correo (solo lectura)
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

            // ✍️ Mensaje
            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Mensaje") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(20.dp))

            // 📨 Enviar
            Button(
                onClick = {
                    vm.enviarMensaje(
                        nombre = nombre,
                        correo = correo,
                        mensaje = mensaje,
                        onSuccess = {
                            scope.launch { snackbarHostState.showSnackbar("✅ Mensaje enviado correctamente") }
                            abrirEmailCliente()
                            mensaje = ""
                        },
                        onError = {
                            scope.launch { snackbarHostState.showSnackbar("❌ Error: $it") }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF77))
            ) {
                Text("Enviar", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))

            // ℹ️ Información de contacto
            Text("Información de contacto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        }
    }
}

@Composable
private fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF00FF77).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF00FF77))
        }
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White, fontSize = 15.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00FF77),
    unfocusedBorderColor = Color(0xFF1F4D3E),
    focusedContainerColor = Color(0xFF173E34),
    unfocusedContainerColor = Color(0xFF173E34),
    focusedLabelColor = Color(0xFF00FF77),
    unfocusedLabelColor = Color.Gray,
    cursorColor = Color(0xFF00FF77),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
