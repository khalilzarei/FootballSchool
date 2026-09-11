package com.khz.footballschool.ui.news

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(onBack: () -> Unit, onAdd: () -> Unit = {}) {
    val vm: NewsListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "اخبار",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, "افزودن", tint = GoldPrimary)
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            Modifier.padding(padding)
        ) {
            GenericListScreen(
                title = "اخبار",
                state = state,
                onRefresh = vm::refresh
            ) { n ->
                InfoCard(
                    title = n.title,
                    subtitle = n.body.take(80),
                    trailing = n.status
                )
            }
        }
    }
}