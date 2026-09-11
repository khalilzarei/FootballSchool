package com.khz.footballschool.core.di

import android.content.Context
import com.khz.footballschool.core.local.SessionManager
import com.khz.footballschool.core.network.AuthInterceptor
import com.khz.footballschool.core.util.Constants
import com.khz.footballschool.data.remote.*
import com.khz.footballschool.data.repository.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

/**
 * ظرف اصلی وابستگی‌های پروژه (Manual Dependency Injection)
 * تمام سرویس‌ها و ریپازیتوری‌ها به صورت Lazy ساخته می‌شوند
 * تا تنها در صورت نیاز، نمونه‌سازی انجام شود.
 */
class AppContainer(context: Context) {

    // ═════════════════════════════════════════════
    // Local Storage
    // ═════════════════════════════════════════════
    val sessionManager: SessionManager by lazy { SessionManager(context) }

    // ═════════════════════════════════════════════
    // Network Core
    // ═════════════════════════════════════════════
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(sessionManager)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(
                Constants.CONNECT_TIMEOUT,
                TimeUnit.SECONDS
            )
            .readTimeout(
                Constants.READ_TIMEOUT,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                Constants.WRITE_TIMEOUT,
                TimeUnit.SECONDS
            )
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ═════════════════════════════════════════════
    // API Services (منطبق بر routes/api.php)
    // ═════════════════════════════════════════════
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val guardianApi: GuardianApi by lazy { retrofit.create(GuardianApi::class.java) }
    val playerApi: PlayerApi by lazy { retrofit.create(PlayerApi::class.java) }
    val seasonApi: SeasonApi by lazy { retrofit.create(SeasonApi::class.java) }
    val ageGroupApi: AgeGroupApi by lazy { retrofit.create(AgeGroupApi::class.java) }
    val coachApi: CoachApi by lazy { retrofit.create(CoachApi::class.java) }
    val classApi: ClassApi by lazy { retrofit.create(ClassApi::class.java) }
    val classScheduleApi: ClassScheduleApi by lazy { retrofit.create(ClassScheduleApi::class.java) }
    val enrollmentApi: EnrollmentApi by lazy { retrofit.create(EnrollmentApi::class.java) }
    val sessionApi: SessionApi by lazy { retrofit.create(SessionApi::class.java) }
    val attendanceApi: AttendanceApi by lazy { retrofit.create(AttendanceApi::class.java) }
    val evaluationApi: EvaluationApi by lazy { retrofit.create(EvaluationApi::class.java) }
    val invoiceApi: InvoiceApi by lazy { retrofit.create(InvoiceApi::class.java) }
    val discountApi: DiscountApi by lazy { retrofit.create(DiscountApi::class.java) }
    val paymentApi: PaymentApi by lazy { retrofit.create(PaymentApi::class.java) }
    val mediaApi: MediaApi by lazy { retrofit.create(MediaApi::class.java) }
    val newsApi: NewsApi by lazy { retrofit.create(NewsApi::class.java) }
    val matchApi: MatchApi by lazy { retrofit.create(MatchApi::class.java) }
    val notificationApi: NotificationApi by lazy { retrofit.create(NotificationApi::class.java) }
    val settingApi: SettingApi by lazy { retrofit.create(SettingApi::class.java) }
    val chatApi: ChatApi by lazy { retrofit.create(ChatApi::class.java) }
    val reportApi: ReportApi by lazy { retrofit.create(ReportApi::class.java) }
    val clientApi: ClientApi by lazy { retrofit.create(ClientApi::class.java) }

    // ═════════════════════════════════════════════
    // Repositories
    // ═════════════════════════════════════════════
    val authRepository: AuthRepository by lazy {
        AuthRepository(
            authApi,
            sessionManager
        )
    }
    val userRepository: UserRepository by lazy { UserRepository(userApi) }
    val guardianRepository: GuardianRepository by lazy { GuardianRepository(guardianApi) }
    val playerRepository: PlayerRepository by lazy { PlayerRepository(playerApi) }
    val seasonRepository: SeasonRepository by lazy { SeasonRepository(seasonApi) }
    val ageGroupRepository: AgeGroupRepository by lazy { AgeGroupRepository(ageGroupApi) }
    val coachRepository: CoachRepository by lazy { CoachRepository(coachApi) }

    val classRepository: ClassRepository by lazy { ClassRepository(classApi) }
    val sessionRepository: SessionRepository by lazy {
        SessionRepository(
            sessionApi,
            attendanceApi
        )
    }
    val evaluationRepository: EvaluationRepository by lazy { EvaluationRepository(evaluationApi) }
    val invoiceRepository: InvoiceRepository by lazy { InvoiceRepository(invoiceApi) }
    val discountRepository: DiscountRepository by lazy { DiscountRepository(discountApi) }
    val paymentRepository: PaymentRepository by lazy { PaymentRepository(paymentApi) }
    val mediaRepository: MediaRepository by lazy { MediaRepository(mediaApi) }
    val newsRepository: NewsRepository by lazy { NewsRepository(newsApi) }
    val matchRepository: MatchRepository by lazy { MatchRepository(matchApi) }
    val notificationRepository: NotificationRepository by lazy { NotificationRepository(notificationApi) }
    val settingRepository: SettingRepository by lazy { SettingRepository(settingApi) }
    val chatRepository: ChatRepository by lazy { ChatRepository(chatApi) }
    val reportRepository: ReportRepository by lazy { ReportRepository(reportApi) }
    val clientRepository: ClientRepository by lazy { ClientRepository(clientApi) }
}