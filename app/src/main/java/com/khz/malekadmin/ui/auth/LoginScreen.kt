package com.khz.malekadmin.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.theme.GoldPrimary

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRequirePasswordChange: () -> Unit
) {
    val viewModel: AuthViewModel = appViewModel()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthState.Success -> onLoginSuccess()
            is AuthState.RequirePasswordChange -> onRequirePasswordChange()
            else -> {}
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // لوگوی سه‌بعدی
        Box(
            Modifier
                .size(96.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            GoldPrimary.copy(0.95f),
                            GoldPrimary.copy(0.35f)
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.SportsSoccer,
                null,
                tint = Color(0xFF1A0533),
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "مدرسه فوتبال مالک اشتر",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = GoldPrimary
        )
        Text(
            "پنل مدیریت",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(32.dp))

        GlassCard3D() {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                GlassTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = "شماره موبایل یا کد ملی",
                    keyboardType = KeyboardType.Number,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            null
                        )
                    })
                GlassTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "رمز عبور",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            null
                        )
                    })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GoldPrimary,
                            checkmarkColor = Color(0xFF1A0533),
                            uncheckedColor = Color.White.copy(alpha = 0.4f)
                        )
                    )
                    Text(
                        "مرا به خاطر بسپار",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        GlassButton(
            text = "ورود به پنل",
            onClick = {
                viewModel.login(
                    identifier,
                    password,
                    rememberMe
                )
            },
            loading = loginState is AuthState.Loading,
            enabled = loginState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        if (loginState is AuthState.Error) {
            Spacer(Modifier.height(14.dp))
            GlassCard3D() {
                Text(
                    (loginState as AuthState.Error).message,
                    color = Color(0xFFFF8A80),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}