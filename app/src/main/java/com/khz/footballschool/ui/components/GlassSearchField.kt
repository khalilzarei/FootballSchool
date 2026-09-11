package com.khz.footballschool.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun GlassSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "جستجو",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        modifier = modifier
            .fillMaxWidth()
            ,
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.10f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedBorderColor = GoldPrimary.copy(alpha = 0.85f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
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