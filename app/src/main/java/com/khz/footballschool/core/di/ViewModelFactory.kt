package com.khz.footballschool.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khz.footballschool.ui.age_groups.AgeGroupListViewModel
import com.khz.footballschool.ui.attendance.AttendanceViewModel
import com.khz.footballschool.ui.auth.AuthViewModel
import com.khz.footballschool.ui.chat.ChatRoomListViewModel
import com.khz.footballschool.ui.chat.ChatViewModel
import com.khz.footballschool.ui.classes.ClassFormViewModel
import com.khz.footballschool.ui.classes.ClassListViewModel
import com.khz.footballschool.ui.coaches.CoachListViewModel
import com.khz.footballschool.ui.dashboard.DashboardViewModel
import com.khz.footballschool.ui.discounts.DiscountListViewModel
import com.khz.footballschool.ui.guardians.GuardianListViewModel
import com.khz.footballschool.ui.invoices.InvoiceListViewModel
import com.khz.footballschool.ui.matches.MatchListViewModel
import com.khz.footballschool.ui.media.MediaListViewModel
import com.khz.footballschool.ui.news.NewsListViewModel
import com.khz.footballschool.ui.notifications.NotificationListViewModel
import com.khz.footballschool.ui.payments.PaymentListViewModel
import com.khz.footballschool.ui.players.PlayerDetailViewModel
import com.khz.footballschool.ui.players.PlayerListViewModel
import com.khz.footballschool.ui.reports.ReportsViewModel
import com.khz.footballschool.ui.seasons.SeasonListViewModel
import com.khz.footballschool.ui.sessions.SessionListViewModel
import com.khz.footballschool.ui.settings.SettingsViewModel
import com.khz.footballschool.ui.users.UserDetailViewModel
import com.khz.footballschool.ui.users.UserListViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java)             -> AuthViewModel(container.authRepository) as T
        modelClass.isAssignableFrom(DashboardViewModel::class.java)        -> DashboardViewModel(container.reportRepository) as T

        modelClass.isAssignableFrom(UserListViewModel::class.java)         -> UserListViewModel(container.userRepository) as T
        modelClass.isAssignableFrom(UserDetailViewModel::class.java)       -> UserDetailViewModel(container.userRepository) as T

        modelClass.isAssignableFrom(PlayerListViewModel::class.java)       -> PlayerListViewModel(container.playerRepository) as T
        modelClass.isAssignableFrom(PlayerDetailViewModel::class.java)     -> PlayerDetailViewModel(container.playerRepository) as T
        modelClass.isAssignableFrom(ClassFormViewModel::class.java)        -> {
            ClassFormViewModel(
                container.classRepository,
                container.ageGroupRepository,
                container.coachRepository
            ) as T
        }

        modelClass.isAssignableFrom(GuardianListViewModel::class.java)     -> GuardianListViewModel(container.guardianRepository) as T
        modelClass.isAssignableFrom(SeasonListViewModel::class.java)       -> SeasonListViewModel(container.seasonRepository) as T
        modelClass.isAssignableFrom(AgeGroupListViewModel::class.java)     -> AgeGroupListViewModel(container.ageGroupRepository) as T
        modelClass.isAssignableFrom(CoachListViewModel::class.java)        -> CoachListViewModel(container.coachRepository) as T
        modelClass.isAssignableFrom(ClassListViewModel::class.java)        -> ClassListViewModel(container.classRepository) as T
        modelClass.isAssignableFrom(SessionListViewModel::class.java)      -> SessionListViewModel(container.sessionRepository, container.classRepository) as T
        modelClass.isAssignableFrom(AttendanceViewModel::class.java)       -> AttendanceViewModel(
            container.sessionRepository,
            container.classRepository
        ) as T

        modelClass.isAssignableFrom(InvoiceListViewModel::class.java)      -> InvoiceListViewModel(container.invoiceRepository) as T
        modelClass.isAssignableFrom(DiscountListViewModel::class.java)     -> DiscountListViewModel(container.discountRepository) as T
        modelClass.isAssignableFrom(PaymentListViewModel::class.java)      -> PaymentListViewModel(container.paymentRepository) as T
        modelClass.isAssignableFrom(MediaListViewModel::class.java)        -> MediaListViewModel(container.mediaRepository) as T
        modelClass.isAssignableFrom(NewsListViewModel::class.java)         -> NewsListViewModel(container.newsRepository) as T
        modelClass.isAssignableFrom(MatchListViewModel::class.java)        -> MatchListViewModel(container.matchRepository) as T
        modelClass.isAssignableFrom(NotificationListViewModel::class.java) -> NotificationListViewModel(container.notificationRepository) as T
        modelClass.isAssignableFrom(SettingsViewModel::class.java)         -> SettingsViewModel(container.settingRepository) as T
        modelClass.isAssignableFrom(ChatRoomListViewModel::class.java)     -> ChatRoomListViewModel(container.chatRepository) as T
        modelClass.isAssignableFrom(ChatViewModel::class.java)             -> ChatViewModel(container.chatRepository) as T
        modelClass.isAssignableFrom(ReportsViewModel::class.java)          -> ReportsViewModel(container.reportRepository) as T
        else                                                               -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}