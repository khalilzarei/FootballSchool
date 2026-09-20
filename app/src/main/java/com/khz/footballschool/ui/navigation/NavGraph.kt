package com.khz.footballschool.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.di.ViewModelFactory
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.ui.age_groups.AgeGroupListScreen
import com.khz.footballschool.ui.attendance.AttendanceScreen
import com.khz.footballschool.ui.auth.ChangePasswordScreen
import com.khz.footballschool.ui.auth.LoginScreen
import com.khz.footballschool.ui.chat.ChatContactsScreen
import com.khz.footballschool.ui.chat.ChatRoomListScreen
import com.khz.footballschool.ui.chat.ChatScreen
import com.khz.footballschool.ui.chat.CreateGroupRoomDialog
import com.khz.footballschool.ui.classes.ClassFormScreen
import com.khz.footballschool.ui.classes.ClassListScreen
import com.khz.footballschool.ui.classes.EnrollPlayerScreen
import com.khz.footballschool.ui.classes.EnrollmentManagerScreen
import com.khz.footballschool.ui.classes.ScheduleManagerScreen
import com.khz.footballschool.ui.coaches.CoachListScreen
import com.khz.footballschool.ui.dashboard.DashboardScreen
import com.khz.footballschool.ui.discounts.DiscountListScreen
import com.khz.footballschool.ui.guardians.GuardianDetailScreen
import com.khz.footballschool.ui.guardians.GuardianListScreen
import com.khz.footballschool.ui.invoices.InvoiceListScreen
import com.khz.footballschool.ui.matches.MatchListScreen
import com.khz.footballschool.ui.matches.MatchPlayersScreen
import com.khz.footballschool.ui.matches.SetMatchResultScreen
import com.khz.footballschool.ui.media.MediaListScreen
import com.khz.footballschool.ui.news.NewsListScreen
import com.khz.footballschool.ui.notifications.NotificationListScreen
import com.khz.footballschool.ui.payments.PaymentListScreen
import com.khz.footballschool.ui.players.AttachGuardianToPlayerScreen
import com.khz.footballschool.ui.players.PlayerDetailScreen
import com.khz.footballschool.ui.players.PlayerFormScreen
import com.khz.footballschool.ui.players.PlayerListScreen
import com.khz.footballschool.ui.reports.ReportsScreen
import com.khz.footballschool.ui.seasons.SeasonListScreen
import com.khz.footballschool.ui.sessions.GenerateSessionsScreen
import com.khz.footballschool.ui.sessions.MySessionsScreen
import com.khz.footballschool.ui.sessions.SessionEvaluationsScreen
import com.khz.footballschool.ui.sessions.SessionListScreen
import com.khz.footballschool.ui.settings.SettingsScreen
import com.khz.footballschool.ui.splash.SplashScreen
import com.khz.footballschool.ui.users.UserDetailScreen
import com.khz.footballschool.ui.users.UserFormScreen
import com.khz.footballschool.ui.users.UserListScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {

    val nav = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val scope = rememberCoroutineScope()

    var startDestination by remember { mutableStateOf<String?>(null) }

    /*
     * بررسی وضعیت لاگین در شروع اپ
     */
    LaunchedEffect(Unit) {
        val rememberMe = container.sessionManager.rememberMe.first()
        val token = container.sessionManager.authToken.first()

        if (!rememberMe) {
            container.sessionManager.clearSession()
            startDestination = Screen.Login.route
        } else {
            startDestination = if (!token.isNullOrBlank()) {
                Screen.Dashboard.route
            } else {
                Screen.Login.route
            }
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

    NavHost(
        navController = nav,
        startDestination = start
    ) {

        // ═════════════════════════════════════════════
        // Auth & Splash
        // ═════════════════════════════════════════════

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onRequirePasswordChange = {
                    nav.navigate(Screen.ChangePassword.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                })
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onPasswordChanged = {
                    nav.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.ChangePassword.route) {
                            inclusive = true
                        }
                    }
                })
        }

        // ═════════════════════════════════════════════
        // Dashboard & Settings
        // ═════════════════════════════════════════════

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onRequirePasswordChange = {
                    nav.navigate(Screen.ChangePassword.route) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                },

                onNavigateToUsers = {
                    nav.navigate(Screen.UserList.route)
                },

                onNavigateToPlayers = {
                    nav.navigate(Screen.PlayerList.route)
                },

                onNavigateToClasses = {
                    nav.navigate(Screen.ClassList.route)
                },

                onNavigateToSessions = {
                    nav.navigate(Screen.SessionList.route)
                },

                onNavigateToMySessions = {
                    nav.navigate(Screen.MySessions.route)
                },

                onNavigateToInvoices = {
                    nav.navigate(Screen.InvoiceList.route)
                },

                onNavigateToPayments = {
                    nav.navigate(Screen.PaymentList.route)
                },

                onNavigateToReports = {
                    nav.navigate(Screen.Reports.route)
                },

                onNavigateToNews = {
                    nav.navigate(Screen.NewsList.route)
                },

                onNavigateToChat = {
                    nav.navigate(Screen.ChatRoomList.route)
                },

                onNavigateToAgeGroups = {
                    nav.navigate(Screen.AgeGroupList.route)
                },

                onNavigateToSeasons = {
                    nav.navigate(Screen.SeasonList.route)
                },

                onNavigateToCoaches = {
                    nav.navigate(Screen.CoachList.route)
                },

                onNavigateToDiscounts = {
                    nav.navigate(Screen.DiscountList.route)
                },

                onNavigateToMatches = {
                    nav.navigate(Screen.MatchList.route)
                },

                onNavigateToMedia = {
                    nav.navigate(Screen.MediaList.route)
                },

                onNavigateToNotifications = {
                    nav.navigate(Screen.NotificationList.route)
                },

                onNavigateToSettings = {
                    nav.navigate(Screen.Settings.route)
                },

                onOpenChat = { roomId ->
                    nav.navigate(
                        Screen.Chat.createRoute(roomId)
                    )
                })
        }

        // ═════════════════════════════════════════════
        // News
        // ═════════════════════════════════════════════

        composable(Screen.NewsList.route) {
            NewsListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Chat Room List
        // ═════════════════════════════════════════════

        composable(Screen.ChatRoomList.route) {

            var showCreateGroup by remember {
                mutableStateOf(false)
            }

            ChatRoomListScreen(
                onBack = {
                    nav.popBackStack()
                },

                onOpenChat = { roomId ->
                    nav.navigate(
                        Screen.Chat.createRoute(roomId)
                    )
                },

                onOpenContacts = {
                    nav.navigate(Screen.ChatContacts.route)
                },

                onOpenCreateGroup = {
                    showCreateGroup = true
                })

            if (showCreateGroup) {
                CreateGroupRoomDialog(
                    onDismiss = { showCreateGroup = false },
                    onCreated = { roomId ->
                        showCreateGroup = false
                        nav.navigate(Screen.Chat.createRoute(roomId))
                    })
            }
        }

        // ═════════════════════════════════════════════
        // Chat Contacts (شروع گفتگوی جدید — ادمین)
        // ═════════════════════════════════════════════

        composable(Screen.ChatContacts.route) {
            ChatContactsScreen(
                onBack = {
                    nav.popBackStack()
                },

                onPick = { userId ->

                    scope.launch {

                        when (val result = container.chatRepository.createPrivateRoom(
                            targetUserId = userId
                        )) {

                            is NetworkResult.Success -> {
                                nav.navigate(
                                    Screen.Chat.createRoute(
                                        result.data.id
                                    )
                                )
                            }

                            is NetworkResult.Error   -> {
                                // خطا در صفحه تماس/پروفایل مدیریت می‌شود
                            }

                            is NetworkResult.Loading -> Unit
                        }
                    }
                })
        }

        // ═════════════════════════════════════════════
        // Chat Room
        // ═════════════════════════════════════════════

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.IntType
                })) { backStackEntry ->

            val roomId = backStackEntry.arguments?.getInt("roomId")
                    ?: return@composable

            ChatScreen(
                roomId = roomId,
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Age Groups
        // ═════════════════════════════════════════════

        composable(Screen.AgeGroupList.route) {
            AgeGroupListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Seasons
        // ═════════════════════════════════════════════

        composable(Screen.SeasonList.route) {
            SeasonListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Coaches
        // ═════════════════════════════════════════════

        composable(Screen.CoachList.route) {
            CoachListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Discounts
        // ═════════════════════════════════════════════

        composable(Screen.DiscountList.route) {
            DiscountListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Matches
        // ═════════════════════════════════════════════

        composable(Screen.MatchList.route) {
            MatchListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Media
        // ═════════════════════════════════════════════

        composable(Screen.MediaList.route) {
            MediaListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Notifications
        // ═════════════════════════════════════════════

        composable(Screen.NotificationList.route) {
            NotificationListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Settings
        // ═════════════════════════════════════════════

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    nav.popBackStack()
                },

                onLoggedOut = {
                    nav.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                })
        }

        // ═════════════════════════════════════════════
        // Users
        // ═════════════════════════════════════════════

        composable(Screen.UserList.route) {
            UserListScreen(
                onUserClick = { id ->
                    nav.navigate(
                        Screen.UserDetail.createRoute(id)
                    )
                },

                onAddUser = {
                    nav.navigate(Screen.UserForm.route)
                },

                onChat = { userId ->

                    scope.launch {

                        when (val result = container.chatRepository.createPrivateRoom(
                            targetUserId = userId
                        )) {

                            is NetworkResult.Success -> {
                                nav.navigate(
                                    Screen.Chat.createRoute(
                                        result.data.id
                                    )
                                )
                            }

                            is NetworkResult.Error   -> {
                                // خطا توسط UI فعلی مدیریت شود
                            }

                            is NetworkResult.Loading -> Unit
                        }
                    }
                })
        }

        // ═════════════════════════════════════════════
        // User Form
        // ═════════════════════════════════════════════

        composable(Screen.UserForm.route) {
            UserFormScreen(
                userId = null,
                onSaved = {
                    nav.popBackStack()
                },
                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // User Detail
        // ═════════════════════════════════════════════

        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                })) { backStack ->

            val userId = backStack.arguments?.getInt("userId")
                    ?: 0

            UserDetailScreen(
                userId = userId,

                onBack = {
                    nav.popBackStack()
                },

                onEdit = {
                    nav.navigate(
                        Screen.UserEdit.createRoute(userId)
                    )
                })
        }

        // ═════════════════════════════════════════════
        // User Edit
        // ═════════════════════════════════════════════

        composable(
            route = Screen.UserEdit.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                })) { backStack ->

            val userId = backStack.arguments?.getInt("userId")
                    ?: 0

            UserFormScreen(
                userId = userId,

                onSaved = {
                    nav.popBackStack()
                },

                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Players
        // ═════════════════════════════════════════════

        composable(Screen.PlayerList.route) {
            PlayerListScreen(

                onPlayerClick = { id ->
                    nav.navigate(
                        Screen.PlayerDetail.createRoute(id)
                    )
                },

                onAddPlayer = {
                    nav.navigate(
                        Screen.PlayerForm.route
                    )
                },

                onChat = { userId ->

                    scope.launch {

                        when (val result = container.chatRepository.createPrivateRoom(
                            targetUserId = userId
                        )) {

                            is NetworkResult.Success -> {
                                nav.navigate(
                                    Screen.Chat.createRoute(
                                        result.data.id
                                    )
                                )
                            }

                            is NetworkResult.Error   -> {
                                // خطا توسط UI فعلی مدیریت شود
                            }

                            is NetworkResult.Loading -> Unit
                        }
                    }
                })
        }

        // ═════════════════════════════════════════════
        // Player Form
        // ═════════════════════════════════════════════

        composable(Screen.PlayerForm.route) {
            PlayerFormScreen(
                playerId = null,

                onSaved = {
                    nav.popBackStack()
                },

                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Player Detail
        // ═════════════════════════════════════════════

        composable(
            route = Screen.PlayerDetail.route,
            arguments = listOf(
                navArgument("playerId") {
                    type = NavType.IntType
                })) { backStack ->

            val playerId = backStack.arguments?.getInt("playerId")
                    ?: 0

            PlayerDetailScreen(
                playerId = playerId,

                onBack = {
                    nav.popBackStack()
                },

                onEdit = {
                    nav.navigate(
                        Screen.PlayerEdit.createRoute(playerId)
                    )
                },

                onAttachGuardian = {
                    nav.navigate(
                        Screen.AttachGuardianToPlayer.createRoute(
                            playerId
                        )
                    )
                },

                onChat = { userId ->

                    scope.launch {

                        when (val result = container.chatRepository.createPrivateRoom(
                            targetUserId = userId
                        )) {

                            is NetworkResult.Success -> {
                                nav.navigate(
                                    Screen.Chat.createRoute(
                                        result.data.id
                                    )
                                )
                            }

                            is NetworkResult.Error   -> {
                                // خطا توسط UI فعلی مدیریت شود
                            }

                            is NetworkResult.Loading -> Unit
                        }
                    }
                })
        }

        // ═════════════════════════════════════════════
        // Player Edit
        // ═════════════════════════════════════════════

        composable(
            route = Screen.PlayerEdit.route,
            arguments = listOf(
                navArgument("playerId") {
                    type = NavType.IntType
                })) { backStack ->

            val playerId = backStack.arguments?.getInt("playerId")
                    ?: 0

            PlayerFormScreen(
                playerId = playerId,

                onSaved = {
                    nav.popBackStack()
                },

                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Attach Guardian
        // ═════════════════════════════════════════════

        composable(
            route = Screen.AttachGuardianToPlayer.route,
            arguments = listOf(
                navArgument("playerId") {
                    type = NavType.IntType
                })) { backStack ->

            val playerId = backStack.arguments?.getInt("playerId")
                    ?: 0

            AttachGuardianToPlayerScreen(
                playerId = playerId,

                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Guardians
        // ═════════════════════════════════════════════

        composable(Screen.GuardianList.route) {
            GuardianListScreen(
                onGuardianClick = { id ->
                    nav.navigate(
                        Screen.GuardianDetail.createRoute(id)
                    )
                })
        }

        composable(
            route = Screen.GuardianDetail.route,
            arguments = listOf(
                navArgument("guardianId") {
                    type = NavType.IntType
                })) { backStack ->

            val guardianId = backStack.arguments?.getInt("guardianId")
                    ?: 0

            GuardianDetailScreen(
                guardianId = guardianId,

                onBack = {
                    nav.popBackStack()
                },

                onEdit = {
                    // TODO: Guardian Edit
                })
        }

        // ═════════════════════════════════════════════
        // Classes & Schedules
        // ═════════════════════════════════════════════

        composable(
            route = Screen.ScheduleManager.route,
            arguments = listOf(
                navArgument("classId") {
                    type = NavType.IntType
                })) { backStack ->

            val classId = backStack.arguments?.getInt("classId")
                    ?: 0

            ScheduleManagerScreen(
                classId = classId,

                onBack = {
                    nav.popBackStack()
                })
        }

        composable(
            route = Screen.EnrollmentManager.route,
            arguments = listOf(
                navArgument("classId") {
                    type = NavType.IntType
                })) { backStack ->

            val classId = backStack.arguments?.getInt("classId")
                    ?: 0

            EnrollmentManagerScreen(
                classId = classId,

                onBack = {
                    nav.popBackStack()
                },

                onEnrollPlayer = {
                    nav.navigate(
                        Screen.EnrollPlayer.createRoute(
                            classId
                        )
                    )
                })
        }

        composable(
            route = Screen.EnrollPlayer.route,
            arguments = listOf(
                navArgument("classId") {
                    type = NavType.IntType
                })) { backStack ->

            val classId = backStack.arguments?.getInt("classId")
                    ?: 0

            EnrollPlayerScreen(
                classId = classId,

                onBack = {
                    nav.popBackStack()
                },

                onEnrolled = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Sessions & Attendance
        // ═════════════════════════════════════════════

        composable(Screen.SessionList.route) {
            SessionListScreen(
                onSessionClick = { sessionId, classId ->
                    nav.navigate(
                        Screen.Attendance.createRoute(
                            sessionId,
                            classId
                        )
                    )
                })
        }

        composable(Screen.MySessions.route) {
            MySessionsScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        composable(
            route = Screen.Attendance.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.IntType
                },
                navArgument("classId") {
                    type = NavType.IntType
                })) { backStack ->

            val sessionId = backStack.arguments?.getInt("sessionId")
                    ?: 0

            val classId = backStack.arguments?.getInt("classId")
                    ?: 0

            AttendanceScreen(
                sessionId = sessionId,
                classId = classId,

                onBack = {
                    nav.popBackStack()
                })
        }

        composable(Screen.GenerateSessions.route) {
            GenerateSessionsScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        composable(
            route = Screen.SessionEvaluations.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.IntType
                })) { backStack ->

            val sessionId = backStack.arguments?.getInt("sessionId")
                    ?: 0

            SessionEvaluationsScreen(
                sessionId = sessionId,

                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Finance
        // ═════════════════════════════════════════════

        composable(Screen.InvoiceList.route) {
            InvoiceListScreen()
        }

        composable(Screen.PaymentList.route) {
            PaymentListScreen()
        }

        // ═════════════════════════════════════════════
        // Matches
        // ═════════════════════════════════════════════

        composable(Screen.MatchList.route) {
            MatchListScreen(
                onBack = {
                    nav.popBackStack()
                })
        }

        composable(
            route = Screen.MatchPlayers.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                })) { backStack ->

            val matchId = backStack.arguments?.getInt("matchId")
                    ?: 0

            MatchPlayersScreen(
                matchId = matchId,

                onBack = {
                    nav.popBackStack()
                })
        }

        composable(
            route = Screen.SetMatchResult.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                })) { backStack ->

            val matchId = backStack.arguments?.getInt("matchId")
                    ?: 0

            SetMatchResultScreen(
                matchId = matchId,

                onBack = {
                    nav.popBackStack()
                },

                onSaved = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Reports
        // ═════════════════════════════════════════════

        composable(Screen.Reports.route) {
            ReportsScreen()
        }

        // ═════════════════════════════════════════════
        // Classes
        // ═════════════════════════════════════════════

        composable(Screen.ClassList.route) {
            ClassListScreen(
                onClassClick = { id ->
                    nav.navigate(
                        Screen.ClassEdit.createRoute(id)
                    )
                },

                onAddClass = {
                    nav.navigate(
                        Screen.ClassForm.route
                    )
                })
        }

        // ═════════════════════════════════════════════
        // Class Form
        // ═════════════════════════════════════════════

        composable(Screen.ClassForm.route) {
            ClassFormScreen(
                classId = null,

                onSaved = {
                    nav.popBackStack()
                },

                onBack = {
                    nav.popBackStack()
                })
        }

        // ═════════════════════════════════════════════
        // Class Edit
        // ═════════════════════════════════════════════

        composable(
            route = Screen.ClassEdit.route,
            arguments = listOf(
                navArgument("classId") {
                    type = NavType.IntType
                })) { backStack ->

            val classId = backStack.arguments?.getInt("classId")
                    ?: 0

            ClassFormScreen(
                classId = classId,

                onSaved = {
                    nav.popBackStack()
                },

                onBack = {
                    nav.popBackStack()
                },

                onManageSchedules = { id ->
                    nav.navigate(
                        Screen.ScheduleManager.createRoute(id)
                    )
                },

                onManageEnrollments = { id ->
                    nav.navigate(
                        Screen.EnrollmentManager.createRoute(id)
                    )
                })
        }
    }
}