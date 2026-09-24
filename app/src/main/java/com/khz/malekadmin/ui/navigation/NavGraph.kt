package com.khz.malekadmin.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.di.ViewModelFactory
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.ui.age_groups.AgeGroupListScreen
import com.khz.malekadmin.ui.attendance.AttendanceScreen
import com.khz.malekadmin.ui.auth.ChangePasswordScreen
import com.khz.malekadmin.ui.auth.LoginScreen
import com.khz.malekadmin.ui.chat.ChatContactsScreen
import com.khz.malekadmin.ui.chat.ChatRoomListScreen
import com.khz.malekadmin.ui.chat.ChatScreen
import com.khz.malekadmin.ui.chat.CreateGroupRoomDialog
import com.khz.malekadmin.ui.classes.ClassFormScreen
import com.khz.malekadmin.ui.classes.ClassListScreen
import com.khz.malekadmin.ui.classes.EnrollPlayerScreen
import com.khz.malekadmin.ui.classes.EnrollmentManagerScreen
import com.khz.malekadmin.ui.classes.ScheduleManagerScreen
import com.khz.malekadmin.ui.components.NetworkStatusBanner
import com.khz.malekadmin.ui.dashboard.DashboardScreen
import com.khz.malekadmin.ui.discounts.DiscountListScreen
import com.khz.malekadmin.ui.guardians.GuardianDetailScreen
import com.khz.malekadmin.ui.guardians.GuardianListScreen
import com.khz.malekadmin.ui.invoices.InvoiceDetailScreen
import com.khz.malekadmin.ui.invoices.InvoiceListScreen
import com.khz.malekadmin.ui.matches.MatchDetailScreen
import com.khz.malekadmin.ui.matches.MatchFormScreen
import com.khz.malekadmin.ui.matches.MatchListScreen
import com.khz.malekadmin.ui.matches.MatchPlayersScreen
import com.khz.malekadmin.ui.matches.MatchRefreshBus
import com.khz.malekadmin.ui.matches.SetMatchResultScreen
import com.khz.malekadmin.ui.media.MediaListScreen
import com.khz.malekadmin.ui.news.NewsDetailScreen
import com.khz.malekadmin.ui.news.NewsFormScreen
import com.khz.malekadmin.ui.news.NewsListScreen
import com.khz.malekadmin.ui.news.NewsRefreshBus
import com.khz.malekadmin.ui.notifications.NotificationListScreen
import com.khz.malekadmin.ui.payments.PaymentListScreen
import com.khz.malekadmin.ui.players.AttachGuardianToPlayerScreen
import com.khz.malekadmin.ui.players.PlayerDetailScreen
import com.khz.malekadmin.ui.players.PlayerFormScreen
import com.khz.malekadmin.ui.players.PlayerListScreen
import com.khz.malekadmin.ui.reports.ReportsScreen
import com.khz.malekadmin.ui.seasons.SeasonListScreen
import com.khz.malekadmin.ui.sessions.GenerateSessionsScreen
import com.khz.malekadmin.ui.sessions.MySessionsScreen
import com.khz.malekadmin.ui.sessions.SessionEvaluationsScreen
import com.khz.malekadmin.ui.sessions.SessionListScreen
import com.khz.malekadmin.ui.settings.SettingsScreen
import com.khz.malekadmin.ui.splash.SplashScreen
import com.khz.malekadmin.ui.users.UserDetailScreen
import com.khz.malekadmin.ui.users.UserFormScreen
import com.khz.malekadmin.ui.users.UserListScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {

    val nav = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val scope = rememberCoroutineScope()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val rememberMe = container.sessionManager.rememberMe.first()
        val token = container.sessionManager.authToken.first()
        if (!rememberMe) {
            container.sessionManager.clearSession()
            startDestination = Screen.Login.route
        } else {
            startDestination = if (!token.isNullOrBlank()) Screen.Dashboard.route else Screen.Login.route
        }
    }

    val start = startDestination
    if (start == null) {
        SplashScreen(
            isLoggedIn = false,
            onNavigateToLogin = {},
            onNavigateToDashboard = {})
        return
    }

    // Box دور NavHost برای نمایش بنر وضعیت شبکه (اینترنت/فیلترشکن) روی همه‌ی صفحات
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = nav,
            startDestination = start
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { nav.navigate(Screen.Dashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                    onRequirePasswordChange = { nav.navigate(Screen.ChangePassword.route) { popUpTo(Screen.Login.route) { inclusive = true } } })
            }

            composable(Screen.ChangePassword.route) {
                ChangePasswordScreen(onPasswordChanged = { nav.navigate(Screen.Dashboard.route) { popUpTo(Screen.ChangePassword.route) { inclusive = true } } })
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onRequirePasswordChange = { nav.navigate(Screen.ChangePassword.route) { popUpTo(Screen.Dashboard.route) { inclusive = true } } },
                    onNavigateToUsers = { nav.navigate(Screen.UserList.route) },
                    onNavigateToPlayers = { nav.navigate(Screen.PlayerList.route) },
                    onNavigateToClasses = { nav.navigate(Screen.ClassList.route) },
                    onNavigateToSessions = { nav.navigate(Screen.SessionList.route) },
                    onNavigateToMySessions = { nav.navigate(Screen.MySessions.route) },
                    onNavigateToInvoices = { nav.navigate(Screen.InvoiceList.route) },
                    onNavigateToPayments = { nav.navigate(Screen.PaymentList.route) },
                    onNavigateToReports = { nav.navigate(Screen.Reports.route) },
                    onNavigateToNews = { nav.navigate(Screen.NewsList.route) },
                    onNavigateToChat = { nav.navigate(Screen.ChatRoomList.route) },
                    onNavigateToAgeGroups = { nav.navigate(Screen.AgeGroupList.route) },
                    onNavigateToSeasons = { nav.navigate(Screen.SeasonList.route) },
                    onNavigateToDiscounts = { nav.navigate(Screen.DiscountList.route) },
                    onNavigateToMatches = { nav.navigate(Screen.MatchList.route) },
                    onNavigateToMedia = { nav.navigate(Screen.MediaList.route) },
                    onNavigateToNotifications = { nav.navigate(Screen.NotificationList.route) },
                    onNavigateToSettings = { nav.navigate(Screen.Settings.route) },
                    onOpenChat = { roomId -> nav.navigate(Screen.Chat.createRoute(roomId)) })
            }

            composable(Screen.NewsList.route) {
                NewsListScreen(
                    onBack = { nav.popBackStack() },
                    onAdd = { nav.navigate(Screen.NewsForm.route) },
                    onEdit = { id -> nav.navigate(Screen.NewsEdit.createRoute(id)) },
                    onDetail = { id -> nav.navigate(Screen.NewsDetail.createRoute(id)) })
            }

            composable(Screen.NewsForm.route) {
                NewsFormScreen(
                    newsId = null,
                    onBack = { nav.popBackStack() },
                    onSaved = { NewsRefreshBus.refresh(); nav.popBackStack() })
            }

            composable(
                route = Screen.NewsDetail.route,
                arguments = listOf(navArgument("newsId") { type = NavType.IntType })
            ) { backStack ->
                val newsId = backStack.arguments?.getInt("newsId")
                        ?: 0
                NewsDetailScreen(
                    newsId = newsId,
                    onBack = { nav.popBackStack() },
                    onEdit = { id -> nav.navigate(Screen.NewsEdit.createRoute(id)) },
                    onDeleted = { NewsRefreshBus.refresh(); nav.popBackStack() })
            }

            composable(
                route = Screen.NewsEdit.route,
                arguments = listOf(navArgument("newsId") { type = NavType.IntType })
            ) { backStack ->
                val newsId = backStack.arguments?.getInt("newsId")
                        ?: 0
                NewsFormScreen(
                    newsId = newsId,
                    onBack = { nav.popBackStack() },
                    onSaved = { NewsRefreshBus.refresh(); nav.popBackStack() })
            }

            composable(Screen.ChatRoomList.route) {
                var showCreateGroup by remember { mutableStateOf(false) }
                ChatRoomListScreen(
                    onBack = { nav.popBackStack() },
                    onOpenChat = { roomId -> nav.navigate(Screen.Chat.createRoute(roomId)) },
                    onOpenContacts = { nav.navigate(Screen.ChatContacts.route) },
                    onOpenCreateGroup = { showCreateGroup = true })
                if (showCreateGroup) {
                    CreateGroupRoomDialog(
                        onDismiss = { showCreateGroup = false },
                        onCreated = { roomId -> showCreateGroup = false; nav.navigate(Screen.Chat.createRoute(roomId)) })
                }
            }

            composable(Screen.ChatContacts.route) {
                ChatContactsScreen(
                    onBack = { nav.popBackStack() },
                    onPick = { userId ->
                        scope.launch {
                            when (val result = container.chatRepository.createPrivateRoom(targetUserId = userId)) {
                                is NetworkResult.Success -> nav.navigate(Screen.Chat.createRoute(result.data.id))
                                else                     -> {}
                            }
                        }
                    })
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(navArgument("roomId") { type = NavType.IntType })
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getInt("roomId")
                        ?: return@composable
                ChatScreen(
                    roomId = roomId,
                    onBack = { nav.popBackStack() })
            }

            composable(Screen.AgeGroupList.route) { AgeGroupListScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.SeasonList.route) { SeasonListScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.DiscountList.route) { DiscountListScreen(onBack = { nav.popBackStack() }) }

            // Matches - با رفرش باس
            composable(Screen.MatchList.route) {
                MatchListScreen(
                    onBack = { nav.popBackStack() },
                    onAdd = { nav.navigate(Screen.MatchForm.route) },
                    onDetail = { id -> nav.navigate(Screen.MatchDetail.createRoute(id)) })
            }
            composable(
                route = Screen.MatchDetail.route,
                arguments = listOf(navArgument("matchId") { type = NavType.IntType })
            ) { backStack ->
                val matchId = backStack.arguments?.getInt("matchId")
                        ?: 0
                MatchDetailScreen(
                    matchId = matchId,
                    onBack = { nav.popBackStack() },
                    onEdit = { id -> nav.navigate(Screen.MatchEdit.createRoute(id)) },
                    onPlayers = { id -> nav.navigate(Screen.MatchPlayers.createRoute(id)) },
                    onSetResult = { id -> nav.navigate(Screen.SetMatchResult.createRoute(id)) })
            }
            composable(Screen.MatchForm.route) {
                MatchFormScreen(
                    matchId = null,
                    onBack = { nav.popBackStack() },
                    onSaved = { MatchRefreshBus.refresh(); nav.popBackStack() })
            }
            composable(
                route = Screen.MatchEdit.route,
                arguments = listOf(navArgument("matchId") { type = NavType.IntType })
            ) { backStack ->
                val matchId = backStack.arguments?.getInt("matchId")
                        ?: 0
                MatchFormScreen(
                    matchId = matchId,
                    onBack = { nav.popBackStack() },
                    onSaved = { MatchRefreshBus.refresh(); nav.popBackStack() })
            }
            composable(
                route = Screen.MatchPlayers.route,
                arguments = listOf(navArgument("matchId") { type = NavType.IntType })
            ) { backStack ->
                val matchId = backStack.arguments?.getInt("matchId")
                        ?: 0
                MatchPlayersScreen(
                    matchId = matchId,
                    onBack = { MatchRefreshBus.refresh(); nav.popBackStack() })
            }
            composable(
                route = Screen.SetMatchResult.route,
                arguments = listOf(navArgument("matchId") { type = NavType.IntType })
            ) { backStack ->
                val matchId = backStack.arguments?.getInt("matchId")
                        ?: 0
                SetMatchResultScreen(
                    matchId = matchId,
                    onBack = { nav.popBackStack() },
                    onSaved = { MatchRefreshBus.refresh(); nav.popBackStack() })
            }

            composable(Screen.MediaList.route) { MediaListScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.NotificationList.route) { NotificationListScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { nav.popBackStack() },
                    onLoggedOut = { nav.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } })
            }

            composable(Screen.UserList.route) {
                UserListScreen(
                    onUserClick = { id -> nav.navigate(Screen.UserDetail.createRoute(id)) },
                    onAddUser = { nav.navigate(Screen.UserForm.route) },
                    onChat = { userId ->
                        scope.launch {
                            when (val result = container.chatRepository.createPrivateRoom(targetUserId = userId)) {
                                is NetworkResult.Success -> nav.navigate(Screen.Chat.createRoute(result.data.id))
                                else                     -> {}
                            }
                        }
                    })
            }

            composable(Screen.UserForm.route) {
                UserFormScreen(
                    userId = null,
                    onSaved = { nav.popBackStack() },
                    onBack = { nav.popBackStack() })
            }

            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStack ->
                val userId = backStack.arguments?.getInt("userId")
                        ?: 0
                UserDetailScreen(
                    userId = userId,
                    onBack = { nav.popBackStack() },
                    onEdit = { nav.navigate(Screen.UserEdit.createRoute(userId)) })
            }

            composable(
                route = Screen.UserEdit.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStack ->
                val userId = backStack.arguments?.getInt("userId")
                        ?: 0
                UserFormScreen(
                    userId = userId,
                    onSaved = { nav.popBackStack() },
                    onBack = { nav.popBackStack() })
            }

            composable(Screen.PlayerList.route) {
                PlayerListScreen(
                    onPlayerClick = { id -> nav.navigate(Screen.PlayerDetail.createRoute(id)) },
                    onAddPlayer = { nav.navigate(Screen.PlayerForm.route) },
                    onChat = { userId ->
                        scope.launch {
                            when (val result = container.chatRepository.createPrivateRoom(targetUserId = userId)) {
                                is NetworkResult.Success -> nav.navigate(Screen.Chat.createRoute(result.data.id))
                                else                     -> {}
                            }
                        }
                    })
            }

            composable(Screen.PlayerForm.route) {
                PlayerFormScreen(
                    playerId = null,
                    onSaved = { nav.popBackStack() },
                    onBack = { nav.popBackStack() })
            }

            composable(
                route = Screen.PlayerDetail.route,
                arguments = listOf(navArgument("playerId") { type = NavType.IntType })
            ) { backStack ->
                val playerId = backStack.arguments?.getInt("playerId")
                        ?: 0
                PlayerDetailScreen(
                    playerId = playerId,
                    onBack = { nav.popBackStack() },
                    onEdit = { nav.navigate(Screen.PlayerEdit.createRoute(playerId)) },
                    onAttachGuardian = { nav.navigate(Screen.AttachGuardianToPlayer.createRoute(playerId)) },
                    onChat = { userId ->
                        scope.launch {
                            when (val result = container.chatRepository.createPrivateRoom(targetUserId = userId)) {
                                is NetworkResult.Success -> nav.navigate(Screen.Chat.createRoute(result.data.id))
                                else                     -> {}
                            }
                        }
                    },
                    onInvoiceDetail = { invId -> nav.navigate(Screen.InvoiceDetail.createRoute(invId)) })
            }

            composable(
                route = Screen.PlayerEdit.route,
                arguments = listOf(navArgument("playerId") { type = NavType.IntType })
            ) { backStack ->
                val playerId = backStack.arguments?.getInt("playerId")
                        ?: 0
                PlayerFormScreen(
                    playerId = playerId,
                    onSaved = { nav.popBackStack() },
                    onBack = { nav.popBackStack() })
            }

            composable(
                route = Screen.AttachGuardianToPlayer.route,
                arguments = listOf(navArgument("playerId") { type = NavType.IntType })
            ) { backStack ->
                val playerId = backStack.arguments?.getInt("playerId")
                        ?: 0
                AttachGuardianToPlayerScreen(
                    playerId = playerId,
                    onBack = { nav.popBackStack() })
            }

            composable(Screen.GuardianList.route) { GuardianListScreen(onGuardianClick = { id -> nav.navigate(Screen.GuardianDetail.createRoute(id)) }) }

            composable(
                route = Screen.GuardianDetail.route,
                arguments = listOf(navArgument("guardianId") { type = NavType.IntType })
            ) { backStack ->
                val guardianId = backStack.arguments?.getInt("guardianId")
                        ?: 0
                GuardianDetailScreen(
                    guardianId = guardianId,
                    onBack = { nav.popBackStack() },
                    onEdit = { })
            }

            composable(
                route = Screen.ScheduleManager.route,
                arguments = listOf(navArgument("classId") { type = NavType.IntType })
            ) { backStack ->
                val classId = backStack.arguments?.getInt("classId")
                        ?: 0
                ScheduleManagerScreen(
                    classId = classId,
                    onBack = { nav.popBackStack() })
            }

            composable(
                route = Screen.EnrollmentManager.route,
                arguments = listOf(navArgument("classId") { type = NavType.IntType })
            ) { backStack ->
                val classId = backStack.arguments?.getInt("classId")
                        ?: 0
                EnrollmentManagerScreen(
                    classId = classId,
                    onBack = { nav.popBackStack() },
                    onEnrollPlayer = { nav.navigate(Screen.EnrollPlayer.createRoute(classId)) })
            }

            composable(
                route = Screen.EnrollPlayer.route,
                arguments = listOf(navArgument("classId") { type = NavType.IntType })
            ) { backStack ->
                val classId = backStack.arguments?.getInt("classId")
                        ?: 0
                EnrollPlayerScreen(
                    classId = classId,
                    onBack = { nav.popBackStack() },
                    onEnrolled = { nav.popBackStack() })
            }

            composable(Screen.SessionList.route) {
                SessionListScreen(onSessionClick = { sessionId, classId ->
                    nav.navigate(
                        Screen.Attendance.createRoute(
                            sessionId,
                            classId
                        )
                    )
                })
            }

            composable(Screen.MySessions.route) { MySessionsScreen(onBack = { nav.popBackStack() }) }

            composable(
                route = Screen.Attendance.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType },
                    navArgument("classId") { type = NavType.IntType })
            ) { backStack ->
                val sessionId = backStack.arguments?.getInt("sessionId")
                        ?: 0
                val classId = backStack.arguments?.getInt("classId")
                        ?: 0
                AttendanceScreen(
                    sessionId = sessionId,
                    classId = classId,
                    onBack = { nav.popBackStack() })
            }

            composable(Screen.GenerateSessions.route) { GenerateSessionsScreen(onBack = { nav.popBackStack() }) }

            composable(
                route = Screen.SessionEvaluations.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
            ) { backStack ->
                val sessionId = backStack.arguments?.getInt("sessionId")
                        ?: 0
                SessionEvaluationsScreen(
                    sessionId = sessionId,
                    onBack = { nav.popBackStack() })
            }

            // Finance
            composable(Screen.InvoiceList.route) {
                InvoiceListScreen(onDetail = { id -> nav.navigate(Screen.InvoiceDetail.createRoute(id)) })
            }
            composable(
                route = Screen.InvoiceDetail.route,
                arguments = listOf(navArgument("invoiceId") { type = NavType.IntType })
            ) { backStack ->
                val invoiceId = backStack.arguments?.getInt("invoiceId")
                        ?: 0
                InvoiceDetailScreen(
                    invoiceId = invoiceId,
                    onBack = { nav.popBackStack() })
            }
            composable(Screen.PaymentList.route) { PaymentListScreen() }

            composable(Screen.Reports.route) { ReportsScreen() }

            composable(Screen.ClassList.route) {
                ClassListScreen(
                    onClassClick = { id -> nav.navigate(Screen.ClassEdit.createRoute(id)) },
                    onAddClass = { nav.navigate(Screen.ClassForm.route) })
            }

            composable(Screen.ClassForm.route) {
                ClassFormScreen(
                    classId = null,
                    onSaved = { nav.popBackStack() },
                    onBack = { nav.popBackStack() })
            }

            composable(
                route = Screen.ClassEdit.route,
                arguments = listOf(navArgument("classId") { type = NavType.IntType })
            ) { backStack ->
                val classId = backStack.arguments?.getInt("classId")
                        ?: 0
                ClassFormScreen(
                    classId = classId,
                    onSaved = { nav.popBackStack() },
                    onBack = { nav.popBackStack() },
                    onManageSchedules = { id -> nav.navigate(Screen.ScheduleManager.createRoute(id)) },
                    onManageEnrollments = { id -> nav.navigate(Screen.EnrollmentManager.createRoute(id)) })
            }
        }

        // بنر وضعیت شبکه: اینترنت قطع / فیلترشکن روشن (روی همه‌ی صفحات)
        NetworkStatusBanner(
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
