package com.khz.malekadmin.ui.classes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassChip3D
import com.khz.malekadmin.ui.components.GlassSearchField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(
    onClassClick: (Int) -> Unit,
    onAddClass: () -> Unit
) {
    val viewModel: ClassListViewModel = appViewModel()
    val state by viewModel.state.collectAsState()
    val query by viewModel.query.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    var classToDelete by remember { mutableStateOf<FootballClass?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "کلاس‌ها",
                actions = {
                    IconButton(onClick = onAddClass) {
                        Icon(Icons.Default.Add, "افزودن کلاس", tint = GoldPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // جستجو
            GlassSearchField(
                value = query,
                onValueChange = { viewModel.search(it) },
                label = "جستجوی کلاس"
            )

            Spacer(Modifier.height(12.dp))

            // فیلتر وضعیت
            Text(
                "وضعیت",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(0.7f)
            )
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                GlassChip3D(
                    label = "همه",
                    selected = statusFilter == null,
                    onClick = { viewModel.setStatusFilter(null) }
                )
                GlassChip3D(
                    label = "فعال",
                    selected = statusFilter == "active",
                    onClick = { viewModel.setStatusFilter("active") },
                    accentColor = Color(0xFF81C784)
                )
                GlassChip3D(
                    label = "غیرفعال",
                    selected = statusFilter == "inactive",
                    onClick = { viewModel.setStatusFilter("inactive") },
                    accentColor = Color(0xFFFF8A80)
                )
            }

            Spacer(Modifier.height(16.dp))

            when (val s = state) {
                is ClassListState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }

                is ClassListState.Error -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GlassCard3D(glowColor = Color(0x66A50044),) {
                        Text(
                            s.message,
                            color = Color.White.copy(0.85f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    GlassButton(text = "تلاش مجدد", onClick = { viewModel.load() })
                }

                is ClassListState.Success -> {
                    if (s.classes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "کلاسی یافت نشد",
                                    color = Color.White.copy(0.6f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "کلاس جدید ایجاد کنید",
                                    color = Color.White.copy(0.4f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else {
                        Text(
                            "${s.classes.size} کلاس",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(0.5f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(s.classes, key = { it.id }) { cls ->
                                ClassRow(
                                    cls = cls,
                                    onClick = { onClassClick(cls.id) },
                                    onToggle = { viewModel.toggleStatus(cls) },
                                    onDelete = { classToDelete = cls }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // دیالوگ حذف
    classToDelete?.let { cls ->
        AlertDialog(
            onDismissRequest = { classToDelete = null },
            icon = {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFFF8A80))
            },
            title = { Text("حذف کلاس") },
            text = {
                Text("آیا از حذف کلاس «${cls.title}» اطمینان دارید؟ این عمل قابل بازگشت نیست.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(cls)
                    classToDelete = null
                }) {
                    Text("بله، حذف کن", color = Color(0xFFFF8A80))
                }
            },
            dismissButton = {
                TextButton(onClick = { classToDelete = null }) {
                    Text("انصراف", color = Color.White.copy(0.7f))
                }
            },
            containerColor = Color(0xFF241040)
        )
    }
}