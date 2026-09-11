package com.khz.footballschool.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.domain.model.DashboardReport
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import com.khz.footballschool.ui.components.ErrorContent
import com.khz.footballschool.ui.components.LoadingContent
import com.khz.footballschool.ui.dashboard.components.DashboardMenuItem
import com.khz.footballschool.ui.dashboard.components.QuickAccessGrid
import com.khz.footballschool.ui.dashboard.components.RecentMessagesSection



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onRequirePasswordChange: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToSessions: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToAgeGroups: () -> Unit,
    onNavigateToSeasons: () -> Unit,
    onNavigateToCoaches: () -> Unit,
    onNavigateToDiscounts: () -> Unit,
    onNavigateToMatches: () -> Unit,
    onNavigateToMedia: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenChat: (Int) -> Unit
) {
    val viewModel: DashboardViewModel = appViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DashboardUiState.RequirePasswordChange) {
            onRequirePasswordChange()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "پنل مدیریت",
                actions = {
                    IconButton(onClick = { viewModel.loadDashboardData() }) {
                        Icon(
                            Icons.Default.Refresh,
                            "بروزرسانی",
                            tint = Color.White.copy(0.8f)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            "تنظیمات",
                            tint = GoldPrimary
                        )
                    }
                })
        }) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val s = uiState) {
                is DashboardUiState.Loading -> LoadingContent()
                is DashboardUiState.RequirePasswordChange -> LoadingContent()
                is DashboardUiState.Error -> ErrorContent(
                    message = s.message,
                    onRetry = { viewModel.loadDashboardData() })

                is DashboardUiState.Success -> DashboardContent(
                    data = s.data,
                    onNavigateToUsers = onNavigateToUsers,
                    onNavigateToPlayers = onNavigateToPlayers,
                    onNavigateToClasses = onNavigateToClasses,
                    onNavigateToSessions = onNavigateToSessions,
                    onNavigateToInvoices = onNavigateToInvoices,
                    onNavigateToPayments = onNavigateToPayments,
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToNews = onNavigateToNews,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToAgeGroups = onNavigateToAgeGroups,
                    onNavigateToSeasons = onNavigateToSeasons,
                    onNavigateToCoaches = onNavigateToCoaches,
                    onNavigateToDiscounts = onNavigateToDiscounts,
                    onNavigateToMatches = onNavigateToMatches,
                    onNavigateToMedia = onNavigateToMedia,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onOpenChat = onOpenChat
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardReport,
    onNavigateToUsers: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToSessions: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToAgeGroups: () -> Unit,
    onNavigateToSeasons: () -> Unit,
    onNavigateToCoaches: () -> Unit,
    onNavigateToDiscounts: () -> Unit,
    onNavigateToMatches: () -> Unit,
    onNavigateToMedia: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOpenChat: (Int) -> Unit
) {
    val peopleColor = Color(0xFFFF8A65)
    val blueColor = Color(0xFF4FC3F7)
    val greenColor = Color(0xFF81C784)
    val purpleColor = Color(0xFFBA68C8)
    val cyanColor = Color(0xFF4DD0E1)

    val menuItems = listOf(
        DashboardMenuItem(
            "کاربران",
            Icons.Default.ManageAccounts,
            GoldPrimary,
            data.usersStats.totalUsers.toString(),
            onNavigateToUsers
        ),
        DashboardMenuItem(
            "بازیکنان",
            Icons.Default.SportsSoccer,
            blueColor,
            data.activePlayers.toString(),
            onNavigateToPlayers
        ),
        DashboardMenuItem(
            "مربیان",
            Icons.Default.Groups,
            cyanColor,
            data.usersStats.totalCoaches.toString(),
            onNavigateToCoaches
        ),
        DashboardMenuItem(
            "کلاس‌ها",
            Icons.Default.School,
            blueColor,
            data.activeClasses.toString(),
            onNavigateToClasses
        ),
        DashboardMenuItem(
            "جلسات",
            Icons.Default.Event,
            greenColor,
            data.sessionsToday.toString(),
            onNavigateToSessions
        ),
        DashboardMenuItem(
            "گروه‌بندی سنی",
            Icons.Default.Category,
            purpleColor,
            null,
            onNavigateToAgeGroups
        ),
        DashboardMenuItem(
            "فصل‌ها",
            Icons.Default.CalendarMonth,
            GoldPrimary,
            null,
            onNavigateToSeasons
        ),
        DashboardMenuItem(
            "فاکتورها",
            Icons.Default.ReceiptLong,
            purpleColor,
            null,
            onNavigateToInvoices
        ),
        DashboardMenuItem(
            "پرداخت‌ها",
            Icons.Default.AccountBalanceWallet,
            cyanColor,
            data.pendingPayments.toString(),
            onNavigateToPayments
        ),
        DashboardMenuItem(
            "تخفیف‌ها",
            Icons.Default.Discount,
            greenColor,
            null,
            onNavigateToDiscounts
        ),
        DashboardMenuItem(
            "مسابقات",
            Icons.Default.Star,
            GoldPrimary,
            null,
            onNavigateToMatches
        ),
        DashboardMenuItem(
            "اخبار",
            Icons.Default.Article,
            blueColor,
            null,
            onNavigateToNews
        ),
        DashboardMenuItem(
            "رسانه‌ها",
            Icons.Default.Image,
            purpleColor,
            null,
            onNavigateToMedia
        ),
        DashboardMenuItem(
            "چت",
            Icons.Default.Chat,
            greenColor,
            null,
            onNavigateToChat
        ),
        DashboardMenuItem(
            "اعلان‌ها",
            Icons.Default.Notifications,
            peopleColor,
            null,
            onNavigateToNotifications
        ),
        DashboardMenuItem(
            "گزارش‌ها",
            Icons.Default.Assessment,
            GoldPrimary,
            null,
            onNavigateToReports
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ─── پیام‌های دریافتی (چت) ───
        item {
            RecentMessagesSection(
                onViewAll = onNavigateToChat,
                onOpenChat = onOpenChat
            )
        }

        item {
            QuickAccessGrid(items = menuItems)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}





