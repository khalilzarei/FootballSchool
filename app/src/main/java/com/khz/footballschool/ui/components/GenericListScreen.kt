package com.khz.footballschool.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericListScreen(
    title: String,
    state: ListState<T>,
    onRefresh: () -> Unit,
    onBack: (() -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
    itemContent: @Composable (T) -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = title,
                onBack = onBack,
                actions = {
                    if (onAdd != null) {
                        IconButton(onClick = onAdd) {
                            Icon(Icons.Default.Add, "افزودن", tint = GoldPrimary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            when (state) {
                is ListState.Loading -> {
                    CircularProgressIndicator(
                        color = GoldPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ListState.Error -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlassCard3D() {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Warning,
                                    null,
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    state.message,
                                    color = Color.White.copy(0.85f)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        GlassButton(text = "تلاش مجدد", onClick = onRefresh)
                    }
                }
                is ListState.Success -> {
                    if (state.items.isEmpty()) {
                        Column(
                            Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Inbox,
                                null,
                                tint = Color.White.copy(0.35f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("موردی یافت نشد", color = Color.White.copy(0.6f))
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.items) { itemContent(it) }
                        }
                    }
                }
            }
        }
    }
}