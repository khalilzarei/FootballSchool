package com.khz.footballschool.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.theme.ErrorGlow
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun ChangePasswordScreen(onPasswordChanged: () -> Unit) {
    val viewModel: AuthViewModel = appViewModel()
    val state by viewModel.changePasswordState.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is ChangePasswordState.Success) {
            onPasswordChanged()
            viewModel.resetChangePasswordState()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "تغییر رمز عبور",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "برای امنیت بیشتر، رمز عبور پیش‌فرض را تغییر دهید.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(0.7f)
        )
        Spacer(Modifier.height(24.dp))

        GlassCard3D {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = "رمز عبور فعلی",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            Icons.Default.Lock,
                            null
                        )
                    })
                GlassTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "رمز عبور جدید",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    supportingText = "حداقل ۶ کاراکتر"
                )
                GlassTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "تکرار رمز عبور جدید",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        GlassButton(
            text = "ثبت رمز جدید",
            onClick = {
                viewModel.changePassword(
                    oldPassword,
                    newPassword,
                    confirmPassword
                )
            },
            loading = state is ChangePasswordState.Loading,
            enabled = state !is ChangePasswordState.Loading && oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        if (state is ChangePasswordState.Error) {
            Spacer(Modifier.height(16.dp))
            GlassCard3D {
                Text(
                    (state as ChangePasswordState.Error).message,
                    color = Color(0xFFFF8A80)
                )
            }
        }
    }
}