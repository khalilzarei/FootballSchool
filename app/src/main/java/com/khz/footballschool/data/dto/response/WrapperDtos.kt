package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

// ═════════════════════════════════════════════
// User Wrappers
// ═════════════════════════════════════════════
data class UserResponseDto(
    @SerializedName("user") val user: UserDto
)

// ═════════════════════════════════════════════
// Guardian Wrapper
// ═════════════════════════════════════════════
data class GuardianResponseDto(
    @SerializedName("guardian") val guardian: GuardianDto
)

// ═════════════════════════════════════════════
// Player Wrapper
// ═════════════════════════════════════════════
data class PlayerResponseDto(
    @SerializedName("player") val player: PlayerDto
)

// ═════════════════════════════════════════════
// Season Wrapper
// ═════════════════════════════════════════════
data class SeasonResponseDto(
    @SerializedName("season") val season: SeasonDto
)

// ═════════════════════════════════════════════
// Age Group Wrapper
// ═════════════════════════════════════════════
data class AgeGroupResponseDto(
    @SerializedName("age_group") val ageGroup: AgeGroupDto
)

// ═════════════════════════════════════════════
// Coach Wrapper
// ═════════════════════════════════════════════
data class CoachResponseDto(
    @SerializedName("coach") val coach: CoachDto
)

// ═════════════════════════════════════════════
// Class Wrapper
// ═════════════════════════════════════════════
data class ClassResponseDto(
    @SerializedName("class") val classItem: ClassDto
)

// ═════════════════════════════════════════════
// Class Schedule Wrapper
// ═════════════════════════════════════════════
data class ClassScheduleResponseDto(
    @SerializedName("schedule") val schedule: ClassScheduleDto
)

// ═════════════════════════════════════════════
// Enrollment Wrapper
// ═════════════════════════════════════════════
data class EnrollmentResponseDto(
    @SerializedName("enrollment") val enrollment: EnrollmentDto
)

// ═════════════════════════════════════════════
// Session Wrapper
// ═════════════════════════════════════════════
data class SessionResponseDto(
    @SerializedName("session") val session: SessionDto
)

// ═════════════════════════════════════════════
// Evaluation Wrapper
// ═════════════════════════════════════════════
data class EvaluationResponseDto(
    @SerializedName("evaluation") val evaluation: EvaluationDto
)

// ═════════════════════════════════════════════
// Invoice Wrapper
// ═════════════════════════════════════════════
data class InvoiceResponseDto(
    @SerializedName("invoice") val invoice: InvoiceDto
)

// ═════════════════════════════════════════════
// Invoice Item Wrapper
// ═════════════════════════════════════════════
data class InvoiceItemResponseDto(
    @SerializedName("item") val item: InvoiceItemDto
)

// ═════════════════════════════════════════════
// Installment Wrapper
// ═════════════════════════════════════════════
data class InstallmentResponseDto(
    @SerializedName("installment") val installment: InstallmentDto
)

// ═════════════════════════════════════════════
// Discount Wrapper
// ═════════════════════════════════════════════
data class DiscountResponseDto(
    @SerializedName("discount") val discount: DiscountDto
)

// ═════════════════════════════════════════════
// Payment Wrapper
// ═════════════════════════════════════════════
data class PaymentResponseDto(
    @SerializedName("payment") val payment: PaymentDto
)

// ═════════════════════════════════════════════
// Media Wrapper
// ═════════════════════════════════════════════
data class MediaResponseDto(
    @SerializedName("media") val media: MediaDto
)

// ═════════════════════════════════════════════
// News Wrapper
// ═════════════════════════════════════════════
data class NewsResponseDto(
    @SerializedName("news") val news: NewsDto
)

// ═════════════════════════════════════════════
// Match Wrapper
// ═════════════════════════════════════════════
data class MatchResponseDto(
    @SerializedName("match") val match: MatchDto
)

// ═════════════════════════════════════════════
// Match Player Wrapper
// ═════════════════════════════════════════════
data class MatchPlayerResponseDto(
    @SerializedName("match_player") val matchPlayer: MatchPlayerDto
)

// ═════════════════════════════════════════════
// Notification Wrapper
// ═════════════════════════════════════════════
data class NotificationResponseDto(
    @SerializedName("notification") val notification: NotificationDto
)

// ═════════════════════════════════════════════
// Class Schedules Wrapper
// سرور: {"success":true,"data":{"schedules":[...]}}
// ═════════════════════════════════════════════
data class SchedulesListResponseDto(
    @SerializedName("schedules") val schedules: List<ClassScheduleDto>? = null
)

// ═════════════════════════════════════════════
// Generate Sessions Result
// سرور: {"success":true,"data":{"created_sessions":12,"from_date":"...","to_date":"...","class_id":3}}
// ═════════════════════════════════════════════
data class GenerateSessionsResultDto(
    @SerializedName("created_sessions") val createdSessions: Int = 0,
    @SerializedName("from_date") val fromDate: String? = null,
    @SerializedName("to_date") val toDate: String? = null,
    @SerializedName("class_id") val classId: Int? = null
)

// ═════════════════════════════════════════════
// Attendance Wrappers
// سرور: {"data":{"attendances":[...]}} و {"data":{"session_id":1,"saved_items":4}}
// ═════════════════════════════════════════════
data class AttendanceListResponse(
    @SerializedName("attendances") val attendances: List<AttendanceDto>? = null
)

data class BulkAttendanceResultDto(
    @SerializedName("session_id") val sessionId: Int? = null,
    @SerializedName("saved_items") val savedItems: Int? = null
)

// ═════════════════════════════════════════════
// Chat Wrappers
// سرور پاسخ‌های چت را داخل کلیدهای نام‌دار برمی‌گرداند:
// {"success":true,"data":{"rooms":[...]}} / {"room":{...}}
// / {"messages":[...]} / {"message":{...}}
// ═════════════════════════════════════════════
data class ChatRoomsResponseDto(
    @SerializedName("rooms") val rooms: List<ChatRoomDto>? = null
)

data class ChatRoomResponseDto(
    @SerializedName("room") val room: ChatRoomDto? = null
)

data class ChatMessagesResponseDto(
    @SerializedName("messages") val messages: List<ChatMessageDto>? = null
)

data class ChatMessageResponseDto(
    @SerializedName("message") val message: ChatMessageDto? = null
)

/**
 * برگه حضور و غیاب: بازیکنان ثبت‌نام‌شده کلاس + وضعیت ذخیره‌شده، در یک ریکوئست
 * سرور: {"data":{"session_id":1,"class_id":3,"players":[...]}}
 */
data class AttendanceSheetResponse(
    @SerializedName("session_id") val sessionId: Int? = null,
    @SerializedName("class_id") val classId: Int? = null,
    @SerializedName("players") val players: List<AttendanceSheetPlayerDto>? = null
)

data class AttendanceSheetPlayerDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("avatar_path") val avatarPath: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("is_billable") val isBillable: Boolean? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("recorded_at") val recordedAt: String? = null
)

// ═════════════════════════════════════════════
// Me / Client Wrappers
// سرور پاسخ‌های بخش «من» را داخل کلیدهای نام‌دار برمی‌گرداند:
// {"data":{"children":[...]}} / {"sessions":[...]} / {"news":[...]}
// / {"media":[...]} / {"finance":[...]}
// ═════════════════════════════════════════════
data class MyChildrenResponseDto(
    @SerializedName("children") val children: List<MyChildrenDto>? = null
)

data class MyScheduleResponseDto(
    @SerializedName("sessions") val sessions: List<MyScheduleDto>? = null
)

data class MyNewsResponseDto(
    @SerializedName("news") val news: List<NewsDto>? = null
)

data class MyMediaResponseDto(
    @SerializedName("media") val media: List<MediaDto>? = null
)

data class MyFinanceResponseDto(
    @SerializedName("finance") val finance: List<MyFinanceDto>? = null
)