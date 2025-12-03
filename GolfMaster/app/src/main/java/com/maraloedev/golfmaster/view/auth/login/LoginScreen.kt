package com.maraloedev.golfmaster.view.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maraloedev.golfmaster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    erroresCampo: Map<String, String>,
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(erroresCampo) {
        emailError = erroresCampo["email"]
        passwordError = erroresCampo["password"]
    }

    val isLoginEnabled = email.isNotBlank() && password.isNotBlank()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = stringResource(R.string.login_logo_cd),
                modifier = Modifier.size(110.dp)
            )

            Spacer(Modifier.height(24.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = { Text(stringResource(R.string.login_email_label)) },
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Email,
                        contentDescription = null,
                        tint = colors.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = {
                    emailError?.let { msg ->
                        Text(msg, color = colors.error, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground
                )
            )

            Spacer(Modifier.height(16.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                },
                label = { Text(stringResource(R.string.login_password_label)) },
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = colors.primary
                    )
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { msg ->
                        Text(msg, color = colors.error, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (isLoginEnabled) {
                            onLogin(email.trim(), password.trim())
                        }
                    }
                ),
                trailingIcon = {
                    val icon = if (passwordVisible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }
                    val desc = if (passwordVisible) {
                        stringResource(R.string.login_password_hide)
                    } else {
                        stringResource(R.string.login_password_show)
                    }

                    androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        androidx.compose.material3.Icon(
                            imageVector = icon,
                            contentDescription = desc,
                            tint = colors.primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground
                )
            )

            Spacer(Modifier.height(24.dp))

            // BOTÓN LOGIN
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLogin(email.trim(), password.trim())
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                enabled = isLoginEnabled
            ) {
                Text(stringResource(R.string.login_button))
            }

            // ERROR GENERAL
            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    errorMessage,
                    color = colors.error,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            // LINK A REGISTRO
            TextButton(onClick = onRegisterClick) {
                Text(
                    stringResource(R.string.login_no_account),
                    color = colors.primary
                )
            }
        }
    }
}
