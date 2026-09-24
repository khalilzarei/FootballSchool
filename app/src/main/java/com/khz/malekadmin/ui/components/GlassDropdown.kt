package com.khz.malekadmin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * دراپ‌داون شیشه‌ای با پشتیبانی از هر نوع کلید (String, Int, ...)
 *
 * @param T نوع کلید گزینه‌ها
 * @param options لیست گزینه‌ها به صورت (کلید -> برچسب نمایشی)
 * @param selectedValue کلید گزینه انتخاب‌شده (می‌تواند نال باشد)
 * @param onSelect تابع بازگشتی با کلید گزینه انتخاب‌شده
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GlassDropdown(
    label: String,
    options: List<Pair<T, String>>,
    selectedValue: T?,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = options.find { it.first == selectedValue }?.second ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.10f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedBorderColor = GoldPrimary.copy(alpha = 0.85f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                focusedLabelColor = GoldPrimary,
                unfocusedLabelColor = Color.White.copy(alpha = 0.60f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.90f),
                focusedTrailingIconColor = GoldPrimary,
                unfocusedTrailingIconColor = Color.White.copy(alpha = 0.70f),
                disabledTextColor = Color.White.copy(alpha = 0.50f),
                disabledLabelColor = Color.White.copy(alpha = 0.40f),
                disabledBorderColor = Color.White.copy(alpha = 0.12f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFF241040)
        ) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text,
                            color = if (value == selectedValue) GoldPrimary else Color.White
                        )
                    },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}