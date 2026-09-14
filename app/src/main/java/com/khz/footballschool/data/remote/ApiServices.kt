package com.khz.footballschool.data.remote

import com.google.gson.JsonElement
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.ApiResponse
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.data.dto.request.*
import com.khz.footballschool.data.dto.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

// ═══════════════════════════════════════════════════════════════
// ۰۱ - Auth
// ═══════════════════════════════════════════════════════════════
interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginDataDto>

    @GET("auth/me")
    suspend fun getCurrentUser(): ApiResponse<JsonElement>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<JsonElement>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۰۲ - Users
// ═══════════════════════════════════════════════════════════════

interface UserApi {

    // ═════════════════════════════════════════════
    // List & Detail
    // ═════════════════════════════════════════════
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("role") role: String? = null,
        @Query("status") status: String? = null,
        @Query("q") query: String? = null
    ): ApiResponse<PaginatedResponse<UserDto>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Create (multipart با آواتار)
    // ═════════════════════════════════════════════
    @Multipart
    @POST("users")
    suspend fun createUser(@Part parts: List<MultipartBody.Part>): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Update (فقط JSON - بدون فایل)
    // ═════════════════════════════════════════════
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UpdateUserRequest
    ): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Avatar Management (multipart جداگانه)
    // ═════════════════════════════════════════════
    @Multipart
    @POST("users/{id}/avatar")
    suspend fun uploadUserAvatar(
        @Path("id") id: Int,
        @Part avatar: MultipartBody.Part
    ): ApiResponse<JsonElement>

    @DELETE("users/{id}/avatar")
    suspend fun deleteUserAvatar(@Path("id") id: Int): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Self Avatar
    // ═════════════════════════════════════════════
    @Multipart
    @POST("me/avatar")
    suspend fun uploadMyAvatar(@Part avatar: MultipartBody.Part): ApiResponse<JsonElement>

    @DELETE("me/avatar")
    suspend fun deleteMyAvatar(): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Status Management
    // ═════════════════════════════════════════════
    @POST("users/{id}/activate")
    suspend fun activateUser(@Path("id") id: Int): ApiResponse<Unit>

    @POST("users/{id}/deactivate")
    suspend fun deactivateUser(@Path("id") id: Int): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Password Reset
    // ═════════════════════════════════════════════
    @POST("users/{id}/reset-password")
    suspend fun resetPassword(
        @Path("id") id: Int,
        @Body request: ResetPasswordRequest
    ): ApiResponse<JsonElement>
}

// ═══════════════════════════════════════════════════════════════
// ۰۳ - Guardians
// ═══════════════════════════════════════════════════════════════
interface GuardianApi {
    @GET("guardians")
    suspend fun getGuardians(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<GuardianDto>>

    @GET("guardians/{id}")
    suspend fun getGuardian(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("guardians/{id}")
    suspend fun updateGuardian(
        @Path("id") id: Int,
        @Body request: UpdateGuardianRequest
    ): ApiResponse<JsonElement>

    @GET("guardians/{id}/players")
    suspend fun getGuardianPlayers(@Path("id") id: Int): ApiResponse<List<GuardianPlayerDto>>

    @POST("guardians/{id}/players")
    suspend fun attachPlayer(
        @Path("id") id: Int,
        @Body request: AttachPlayerToGuardianRequest
    ): ApiResponse<JsonElement>

    @DELETE("guardians/{id}/players/{playerId}")
    suspend fun detachPlayer(
        @Path("id") id: Int,
        @Path("playerId") playerId: Int
    ): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۰۴ - Players
// ═══════════════════════════════════════════════════════════════

interface PlayerApi {

    // ═════════════════════════════════════════════
    // List & Detail
    // ═════════════════════════════════════════════
    @GET("players")
    suspend fun getPlayers(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<PlayerDto>>

    @GET("players/{id}")
    suspend fun getPlayer(@Path("id") id: Int): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Create (multipart با آواتار)
    // ═════════════════════════════════════════════
    @Multipart
    @POST("players")
    suspend fun createPlayer(@Part parts: List<MultipartBody.Part>): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Update (فقط JSON - بدون فایل)
    // ═════════════════════════════════════════════
    @PUT("players/{id}")
    suspend fun updatePlayer(
        @Path("id") id: Int,
        @Body request: UpdatePlayerRequest
    ): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Avatar Management (multipart جداگانه)
    // ═════════════════════════════════════════════
    @Multipart
    @POST("players/{id}/avatar")
    suspend fun uploadPlayerAvatar(
        @Path("id") id: Int,
        @Part avatar: MultipartBody.Part
    ): ApiResponse<JsonElement>

    @DELETE("players/{id}/avatar")
    suspend fun deletePlayerAvatar(@Path("id") id: Int): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Status Management
    // ═════════════════════════════════════════════
    @POST("players/{id}/activate")
    suspend fun activatePlayer(@Path("id") id: Int): ApiResponse<Unit>

    @POST("players/{id}/deactivate")
    suspend fun deactivatePlayer(@Path("id") id: Int): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Guardians
    // ═════════════════════════════════════════════
    @GET("players/{id}/guardians")
    suspend fun getPlayerGuardians(@Path("id") id: Int): ApiResponse<List<GuardianPlayerDto>>

    @POST("players/{id}/guardians")
    suspend fun attachGuardian(
        @Path("id") id: Int,
        @Body request: AttachGuardianToPlayerRequest
    ): ApiResponse<JsonElement>

    @DELETE("players/{id}/guardians/{guardianId}")
    suspend fun detachGuardian(
        @Path("id") id: Int,
        @Path("guardianId") guardianId: Int
    ): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Related Resources
    // ═════════════════════════════════════════════
    @GET("players/{id}/attendances")
    suspend fun getPlayerAttendances(@Path("id") id: Int): ApiResponse<AttendanceListResponse>

    @GET("players/{id}/evaluations")
    suspend fun getPlayerEvaluations(@Path("id") id: Int): ApiResponse<List<EvaluationDto>>

    @GET("players/{id}/invoices")
    suspend fun getPlayerInvoices(@Path("id") id: Int): ApiResponse<List<InvoiceDto>>

    @GET("players/{id}/payments")
    suspend fun getPlayerPayments(@Path("id") id: Int): ApiResponse<List<PaymentDto>>

    @GET("players/{id}/balance")
    suspend fun getPlayerBalance(@Path("id") id: Int): ApiResponse<PlayerBalanceDto>
}

// ═══════════════════════════════════════════════════════════════
// ۰۵ - Seasons
// ═══════════════════════════════════════════════════════════════
interface SeasonApi {
    @GET("seasons")
    suspend fun getSeasons(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<SeasonDto>>

    @POST("seasons")
    suspend fun createSeason(@Body request: CreateSeasonRequest): ApiResponse<JsonElement>

    @GET("seasons/{id}")
    suspend fun getSeason(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("seasons/{id}")
    suspend fun updateSeason(
        @Path("id") id: Int,
        @Body request: UpdateSeasonRequest
    ): ApiResponse<JsonElement>

    @POST("seasons/{id}/activate")
    suspend fun activateSeason(@Path("id") id: Int): ApiResponse<Unit>

    @POST("seasons/{id}/deactivate")
    suspend fun deactivateSeason(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۰۶ - Age Groups
// ═══════════════════════════════════════════════════════════════
interface AgeGroupApi {
    @GET("age-groups")
    suspend fun getAgeGroups(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("season_id") seasonId: Int? = null,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<AgeGroupDto>>

    @POST("age-groups")
    suspend fun createAgeGroup(@Body request: CreateAgeGroupRequest): ApiResponse<JsonElement>

    @GET("age-groups/{id}")
    suspend fun getAgeGroup(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("age-groups/{id}")
    suspend fun updateAgeGroup(
        @Path("id") id: Int,
        @Body request: UpdateAgeGroupRequest
    ): ApiResponse<JsonElement>

    @POST("age-groups/{id}/activate")
    suspend fun activateAgeGroup(@Path("id") id: Int): ApiResponse<Unit>

    @POST("age-groups/{id}/deactivate")
    suspend fun deactivateAgeGroup(@Path("id") id: Int): ApiResponse<Unit>

    /**
     * بازیکنان عضو گروه سنی (بر اساس بازه تاریخ تولد گروه)
     * پاسخ سرور: data = {"players": [...]}
     */
    @GET("age-groups/{id}/players")
    suspend fun getAgeGroupPlayers(@Path("id") id: Int): ApiResponse<JsonElement>
}

// ═══════════════════════════════════════════════════════════════
// ۰۷ - Coaches
// ═══════════════════════════════════════════════════════════════
interface CoachApi {
    @GET("coaches")
    suspend fun getCoaches(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<CoachDto>>

    @GET("coaches/{id}")
    suspend fun getCoach(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("coaches/{id}")
    suspend fun updateCoach(
        @Path("id") id: Int,
        @Body request: UpdateCoachRequest
    ): ApiResponse<JsonElement>
}

// ═══════════════════════════════════════════════════════════════
// ۰۸ - Classes
// ═══════════════════════════════════════════════════════════════

interface ClassApi {

    // ═════════════════════════════════════════════
    // List & Detail
    // ═════════════════════════════════════════════
    @GET("classes")
    suspend fun getClasses(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<ClassDto>>

    @GET("classes/{id}")
    suspend fun getClass(@Path("id") id: Int): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Create / Update (JSON)
    // ═════════════════════════════════════════════
    @POST("classes")
    suspend fun createClass(@Body request: CreateClassRequest): ApiResponse<JsonElement>

    @PUT("classes/{id}")
    suspend fun updateClass(
        @Path("id") id: Int,
        @Body request: UpdateClassRequest
    ): ApiResponse<JsonElement>

    // ═════════════════════════════════════════════
    // Status Management
    // ═════════════════════════════════════════════
    @POST("classes/{id}/activate")
    suspend fun activateClass(@Path("id") id: Int): ApiResponse<Unit>

    @POST("classes/{id}/deactivate")
    suspend fun deactivateClass(@Path("id") id: Int): ApiResponse<Unit>

    // ═════════════════════════════════════════════
    // Delete
    // ═════════════════════════════════════════════
    @DELETE("classes/{id}")
    suspend fun deleteClass(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۰۹ - Class Schedules
// ═══════════════════════════════════════════════════════════════
interface ClassScheduleApi {
    @GET("classes/{classId}/schedules")
    suspend fun getSchedules(@Path("classId") classId: Int): ApiResponse<SchedulesListResponseDto>

    @POST("classes/{classId}/schedules")
    suspend fun createSchedule(
        @Path("classId") classId: Int,
        @Body request: CreateScheduleRequest
    ): ApiResponse<JsonElement>

    @PUT("schedules/{id}")
    suspend fun updateSchedule(
        @Path("id") id: Int,
        @Body request: UpdateScheduleRequest
    ): ApiResponse<JsonElement>

    @POST("schedules/{id}/activate")
    suspend fun activateSchedule(@Path("id") id: Int): ApiResponse<Unit>

    @POST("schedules/{id}/deactivate")
    suspend fun deactivateSchedule(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۰ - Enrollments
// ═══════════════════════════════════════════════════════════════
interface EnrollmentApi {
    @GET("classes/{classId}/players")
    suspend fun getClassPlayers(
        @Path("classId") classId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<EnrollmentDto>>

    @POST("classes/{classId}/players")
    suspend fun enrollPlayer(
        @Path("classId") classId: Int,
        @Body request: EnrollPlayerRequest
    ): ApiResponse<JsonElement>

    @POST("classes/{classId}/enroll-age-group")
    suspend fun enrollAgeGroup(@Path("classId") classId: Int): ApiResponse<EnrollAgeGroupResultDto>

    @GET("enrollments/{id}")
    suspend fun getEnrollment(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("enrollments/{id}")
    suspend fun updateEnrollment(
        @Path("id") id: Int,
        @Body request: UpdateEnrollmentRequest
    ): ApiResponse<JsonElement>

    @POST("enrollments/{id}/activate")
    suspend fun activateEnrollment(@Path("id") id: Int): ApiResponse<Unit>

    @POST("enrollments/{id}/deactivate")
    suspend fun deactivateEnrollment(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۱ - Sessions
// ═══════════════════════════════════════════════════════════════
interface SessionApi {
    @GET("sessions")
    suspend fun getSessions(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("class_id") classId: Int? = null,
        @Query("status") status: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): ApiResponse<PaginatedResponse<SessionDto>>

    @POST("sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): ApiResponse<JsonElement>

    @POST("sessions/generate")
    suspend fun generateSessions(@Body request: GenerateSessionsRequest): ApiResponse<GenerateSessionsResultDto>

    @GET("sessions/{id}")
    suspend fun getSession(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("sessions/{id}")
    suspend fun updateSession(
        @Path("id") id: Int,
        @Body request: UpdateSessionRequest
    ): ApiResponse<JsonElement>

    @POST("sessions/{id}/cancel")
    suspend fun cancelSession(@Path("id") id: Int): ApiResponse<Unit>

    @POST("sessions/{id}/complete")
    suspend fun completeSession(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۲ - Attendance
// ═══════════════════════════════════════════════════════════════
interface AttendanceApi {
    @GET("sessions/{sessionId}/attendance-sheet")
    suspend fun getAttendanceSheet(@Path("sessionId") sessionId: Int): ApiResponse<AttendanceSheetResponse>

    @GET("sessions/{sessionId}/attendance")
    suspend fun getSessionAttendance(@Path("sessionId") sessionId: Int): ApiResponse<AttendanceListResponse>

    @POST("sessions/{sessionId}/attendance")
    suspend fun saveBulkAttendance(
        @Path("sessionId") sessionId: Int,
        @Body request: SaveBulkAttendanceRequest
    ): ApiResponse<BulkAttendanceResultDto>
}

// ═══════════════════════════════════════════════════════════════
// ۱۳ - Evaluations
// ═══════════════════════════════════════════════════════════════
interface EvaluationApi {
    @GET("evaluations")
    suspend fun getEvaluations(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("player_id") playerId: Int? = null,
        @Query("session_id") sessionId: Int? = null,
        @Query("coach_id") coachId: Int? = null
    ): ApiResponse<PaginatedResponse<EvaluationDto>>

    @POST("evaluations")
    suspend fun createEvaluation(@Body request: CreateEvaluationRequest): ApiResponse<JsonElement>

    @GET("evaluations/{id}")
    suspend fun getEvaluation(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("evaluations/{id}")
    suspend fun updateEvaluation(
        @Path("id") id: Int,
        @Body request: UpdateEvaluationRequest
    ): ApiResponse<JsonElement>

    @GET("sessions/{sessionId}/evaluations")
    suspend fun getSessionEvaluations(@Path("sessionId") sessionId: Int): ApiResponse<List<EvaluationDto>>
}

// ═══════════════════════════════════════════════════════════════
// ۱۴ - Invoices
// ═══════════════════════════════════════════════════════════════
interface InvoiceApi {
    @GET("invoices")
    suspend fun getInvoices(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("player_id") playerId: Int? = null,
        @Query("status") status: String? = null,
        @Query("invoice_type") invoiceType: String? = null,
        @Query("q") query: String? = null
    ): ApiResponse<PaginatedResponse<InvoiceDto>>

    @POST("invoices")
    suspend fun createInvoice(@Body request: CreateInvoiceRequest): ApiResponse<JsonElement>

    @GET("invoices/{id}")
    suspend fun getInvoice(@Path("id") id: Int): ApiResponse<JsonElement>

    @POST("invoices/{id}/cancel")
    suspend fun cancelInvoice(@Path("id") id: Int): ApiResponse<Unit>

    @POST("invoices/{id}/items")
    suspend fun addItem(
        @Path("id") id: Int,
        @Body request: AddInvoiceItemRequest
    ): ApiResponse<JsonElement>

    @PUT("invoice-items/{id}")
    suspend fun updateItem(
        @Path("id") id: Int,
        @Body request: UpdateInvoiceItemRequest
    ): ApiResponse<JsonElement>

    @DELETE("invoice-items/{id}")
    suspend fun deleteItem(@Path("id") id: Int): ApiResponse<Unit>

    @POST("invoices/{id}/discounts")
    suspend fun applyDiscount(
        @Path("id") id: Int,
        @Body request: ApplyDiscountToInvoiceRequest
    ): ApiResponse<JsonElement>

    @POST("invoices/{id}/installments")
    suspend fun addInstallment(
        @Path("id") id: Int,
        @Body request: AddInstallmentRequest
    ): ApiResponse<JsonElement>

    @GET("invoices/{id}/installments")
    suspend fun listInstallments(@Path("id") id: Int): ApiResponse<List<InstallmentDto>>
}

// ═══════════════════════════════════════════════════════════════
// ۱۵ - Discounts
// ═══════════════════════════════════════════════════════════════
interface DiscountApi {
    @GET("discounts")
    suspend fun getDiscounts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<DiscountDto>>

    @POST("discounts")
    suspend fun createDiscount(@Body request: CreateDiscountRequest): ApiResponse<JsonElement>

    @GET("discounts/{id}")
    suspend fun getDiscount(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("discounts/{id}")
    suspend fun updateDiscount(
        @Path("id") id: Int,
        @Body request: UpdateDiscountRequest
    ): ApiResponse<JsonElement>

    @POST("discounts/{id}/activate")
    suspend fun activateDiscount(@Path("id") id: Int): ApiResponse<Unit>

    @POST("discounts/{id}/deactivate")
    suspend fun deactivateDiscount(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۶ - Payments
// ═══════════════════════════════════════════════════════════════
interface PaymentApi {
    @GET("payments")
    suspend fun getPayments(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("player_id") playerId: Int? = null,
        @Query("invoice_id") invoiceId: Int? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<PaymentDto>>

    @POST("payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): ApiResponse<JsonElement>

    @GET("payments/{id}")
    suspend fun getPayment(@Path("id") id: Int): ApiResponse<JsonElement>

    @POST("payments/{id}/approve")
    suspend fun approvePayment(@Path("id") id: Int): ApiResponse<Unit>

    @POST("payments/{id}/reject")
    suspend fun rejectPayment(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۷ - Media
// ═══════════════════════════════════════════════════════════════
interface MediaApi {
    @GET("media")
    suspend fun getMedia(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null,
        @Query("visibility") visibility: String? = null,
        @Query("file_type") fileType: String? = null,
        @Query("related_type") relatedType: String? = null,
        @Query("related_id") relatedId: Int? = null,
        @Query("q") query: String? = null
    ): ApiResponse<PaginatedResponse<MediaDto>>

    @Multipart
    @POST("media/upload")
    suspend fun uploadMedia(
        @Part file: MultipartBody.Part,
        @Part("visibility") visibility: RequestBody,
        @Part("related_type") relatedType: RequestBody,
        @Part("related_id") relatedId: RequestBody? = null,
        @Part("description") description: RequestBody? = null
    ): ApiResponse<JsonElement>

    @GET("media/{id}")
    suspend fun getMedia(@Path("id") id: Int): ApiResponse<JsonElement>

    @Streaming
    @GET("media/{id}/download")
    suspend fun downloadMedia(@Path("id") id: Int): okhttp3.ResponseBody

    @POST("media/{id}/audiences")
    suspend fun setAudiences(
        @Path("id") id: Int,
        @Body request: SetMediaAudiencesRequest
    ): ApiResponse<Unit>

    @DELETE("media/{id}")
    suspend fun deleteMedia(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۸ - News
// ═══════════════════════════════════════════════════════════════
interface NewsApi {
    @GET("news")
    suspend fun getNews(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<NewsDto>>

    @POST("news")
    suspend fun createNews(@Body request: CreateNewsRequest): ApiResponse<JsonElement>

    @GET("news/{id}")
    suspend fun getNews(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("news/{id}")
    suspend fun updateNews(
        @Path("id") id: Int,
        @Body request: UpdateNewsRequest
    ): ApiResponse<JsonElement>

    @POST("news/{id}/publish")
    suspend fun publishNews(@Path("id") id: Int): ApiResponse<Unit>

    @POST("news/{id}/archive")
    suspend fun archiveNews(@Path("id") id: Int): ApiResponse<Unit>

    @POST("news/{id}/audiences")
    suspend fun setAudiences(
        @Path("id") id: Int,
        @Body request: SetNewsAudiencesRequest
    ): ApiResponse<Unit>

    @DELETE("news/{id}")
    suspend fun deleteNews(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۱۹ - Matches
// ═══════════════════════════════════════════════════════════════
interface MatchApi {
    @GET("matches")
    suspend fun getMatches(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q") query: String? = null,
        @Query("status") status: String? = null,
        @Query("class_id") classId: Int? = null,
        @Query("age_group_id") ageGroupId: Int? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): ApiResponse<PaginatedResponse<MatchDto>>

    @POST("matches")
    suspend fun createMatch(@Body request: CreateMatchRequest): ApiResponse<JsonElement>

    @GET("matches/{id}")
    suspend fun getMatch(@Path("id") id: Int): ApiResponse<JsonElement>

    @PUT("matches/{id}")
    suspend fun updateMatch(
        @Path("id") id: Int,
        @Body request: UpdateMatchRequest
    ): ApiResponse<JsonElement>

    @POST("matches/{id}/cancel")
    suspend fun cancelMatch(@Path("id") id: Int): ApiResponse<Unit>

    @POST("matches/{id}/result")
    suspend fun setResult(
        @Path("id") id: Int,
        @Body request: SetMatchResultRequest
    ): ApiResponse<JsonElement>

    @POST("matches/{id}/players")
    suspend fun addPlayer(
        @Path("id") id: Int,
        @Body request: AddMatchPlayerRequest
    ): ApiResponse<JsonElement>

    @PUT("match-players/{id}")
    suspend fun updatePlayer(
        @Path("id") id: Int,
        @Body request: UpdateMatchPlayerRequest
    ): ApiResponse<JsonElement>

    @DELETE("match-players/{id}")
    suspend fun removePlayer(@Path("id") id: Int): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۲۰ - Notifications
// ═══════════════════════════════════════════════════════════════
interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("unread_only") unreadOnly: Boolean? = null,
        @Query("type") type: String? = null
    ): ApiResponse<PaginatedResponse<NotificationDto>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): ApiResponse<UnreadCountDto>

    @POST("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int): ApiResponse<Unit>

    @POST("notifications/read-all")
    suspend fun markAllAsRead(): ApiResponse<Unit>

    @POST("notifications/send")
    suspend fun send(@Body request: SendNotificationRequest): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۲۱ - Settings
// ═══════════════════════════════════════════════════════════════
interface SettingApi {
    @GET("settings")
    suspend fun getSettings(): ApiResponse<List<SettingDto>>

    @PUT("settings")
    suspend fun updateSettings(@Body request: UpdateSettingsRequest): ApiResponse<List<SettingDto>>
}

// ═══════════════════════════════════════════════════════════════
// ۲۲ - Chat
// ═══════════════════════════════════════════════════════════════

interface ChatApi {

    // ═════════════════════════════════════════════
    // Chat Rooms
    // پاسخ سرور: data = {"rooms": [...]} یا {"room": {...}}
    // ═════════════════════════════════════════════
    @GET("chat/rooms")
    suspend fun getRooms(): ApiResponse<ChatRoomsResponseDto>

    @GET("chat/rooms/{id}")
    suspend fun getRoom(@Path("id") roomId: Int): ApiResponse<ChatRoomResponseDto>

    /**
     * ایجاد (یا دریافت) اتاق چت بین کاربر فعلی و کاربر دیگر.
     * سرور با unique_key اتاق موجود را برمی‌گرداند (idempotent).
     */
    @POST("chat/rooms")
    suspend fun createRoom(@Body request: CreateChatRoomRequest): ApiResponse<ChatRoomResponseDto>

    // ═════════════════════════════════════════════
    // Messages
    // پاسخ سرور: data = {"messages": [...]} یا {"message": {...}}
    // ═════════════════════════════════════════════
    @GET("chat/rooms/{id}/messages")
    suspend fun getMessages(
        @Path("id") roomId: Int,
        @Query("limit") limit: Int = 50,
        @Query("before") before: Int? = null
    ): ApiResponse<ChatMessagesResponseDto>

    @POST("chat/rooms/{id}/messages")
    suspend fun sendMessage(
        @Path("id") roomId: Int,
        @Body request: SendChatMessageRequest
    ): ApiResponse<ChatMessageResponseDto>

    /**
     * علامت‌گذاری پیام‌های اتاق به‌عنوان خوانده‌شده
     * بدنه: { "last_read_message_id": <شناسه آخرین پیام> }
     * پاسخ سرور: data = {"room_id":1,"last_read_message_id":42}
     */
    @POST("chat/rooms/{id}/read")
    suspend fun markAsRead(
        @Path("id") roomId: Int,
        @Body request: MarkChatReadRequest
    ): ApiResponse<JsonElement>

    @DELETE("chat/rooms/{id}/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("id") roomId: Int,
        @Path("messageId") messageId: Int
    ): ApiResponse<Unit>
}

// ═══════════════════════════════════════════════════════════════
// ۲۳ - Reports
// ═══════════════════════════════════════════════════════════════
interface ReportApi {
    @GET("reports/dashboard")
    suspend fun getDashboardReport(): ApiResponse<DashboardReportDto>

    @GET("reports/finance")
    suspend fun getFinanceReport(): ApiResponse<FinanceReportDto>

    @GET("reports/debts")
    suspend fun getDebtsReport(): ApiResponse<List<DebtsReportDto>>

    @GET("reports/attendance")
    suspend fun getAttendanceReport(
        @Query("session_id") sessionId: Int? = null,
        @Query("class_id") classId: Int? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): ApiResponse<List<AttendanceReportDto>>

    @GET("reports/classes")
    suspend fun getClassesReport(): ApiResponse<List<ClassesReportDto>>
}

// ═══════════════════════════════════════════════════════════════
// ۲۴ - Client / Me (برای اپ سرپرست و مربی)
// ═══════════════════════════════════════════════════════════════
interface ClientApi {
    // سرور همه را داخل کلید نام‌دار برمی‌گرداند: children/sessions/news/media/finance
    @GET("me/children")
    suspend fun getMyChildren(): ApiResponse<MyChildrenResponseDto>

    @GET("me/schedule")
    suspend fun getMySchedule(): ApiResponse<MyScheduleResponseDto>

    @GET("me/news")
    suspend fun getMyNews(): ApiResponse<MyNewsResponseDto>

    @GET("me/media")
    suspend fun getMyMedia(): ApiResponse<MyMediaResponseDto>

    @GET("me/finance")
    suspend fun getMyFinance(): ApiResponse<MyFinanceResponseDto>
}

// ═══════════════════════════════════════════════════════════════
// ۰۰ - Ping (Health Check)
// ═══════════════════════════════════════════════════════════════
interface PingApi {
    @GET("ping")
    suspend fun ping(): ApiResponse<String>
}