package com.khz.malekadmin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * فیلد ورودی شیشه‌ای سه‌بعدی
 */
@Composable
fun GlassTextField3D(
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
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(18.dp)

    // گرادیان سطح
    val surfaceBrush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.06f),
            Color.White.copy(alpha = 0.03f)
        )
    )

    // گرادیان لبه
    val borderBrush = Brush.verticalGradient(
        listOf(
            if (isError) Color(0xFFA50044).copy(alpha = 0.60f) else GoldPrimary.copy(alpha = 0.50f),
            Color.White.copy(alpha = 0.20f),
            Color.White.copy(alpha = 0.08f),
            Color.Transparent
        )
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceBrush, shape)
            .border(1.dp, borderBrush, shape)
            .clip(shape)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        enabled = enabled,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            focusedLabelColor = GoldPrimary,
            unfocusedLabelColor = Color.White.copy(alpha = 0.60f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.90f),
            cursorColor = GoldPrimary,
            focusedLeadingIconColor = GoldPrimary,
            unfocusedLeadingIconColor = Color.White.copy(alpha = 0.70f)
        )
    )
}