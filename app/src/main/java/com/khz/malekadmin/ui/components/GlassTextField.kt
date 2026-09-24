package com.khz.malekadmin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.theme.GoldPrimary

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    placeholder: String? = null
) {
    val shape = RoundedCornerShape(18.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let {
            {
                Text(
                    it,
                    color = Color.White.copy(0.35f)
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        readOnly = readOnly,
        enabled = enabled,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.10f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedBorderColor = GoldPrimary.copy(alpha = 0.85f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
            errorBorderColor = Color(0xFFA50044).copy(alpha = 0.8f),
            focusedLabelColor = GoldPrimary,
            unfocusedLabelColor = Color.White.copy(alpha = 0.60f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.90f),
            cursorColor = GoldPrimary,
            focusedLeadingIconColor = GoldPrimary,
            unfocusedLeadingIconColor = Color.White.copy(alpha = 0.70f),
            disabledContainerColor = Color.White.copy(0.05f),
            disabledBorderColor = Color.White.copy(0.15f),
            disabledLabelColor = Color.White.copy(0.4f),
            disabledTextColor = Color.White.copy(0.6f)
        )
    )
}