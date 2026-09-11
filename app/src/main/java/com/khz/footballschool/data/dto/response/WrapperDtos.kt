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
// Chat Room Wrapper
// ═════════════════════════════════════════════
data class ChatRoomResponseDto(
    @SerializedName("room") val room: ChatRoomDto
)

// ═════════════════════════════════════════════
// Chat Message Wrapper
// ═════════════════════════════════════════════
data class ChatMessageResponseDto(
    @SerializedName("message") val message: ChatMessageDto
)