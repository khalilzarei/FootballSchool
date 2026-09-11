package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateSessionRequest(
    @SerializedName("class_id") val classId: Int,
    @SerializedName("session_date") val sessionDate: String,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String,
    @SerializedName("topic") val topic: String?,
    @SerializedName("notes") val notes: String?
)

data class GenerateSessionsRequest(
    @SerializedName("class_id") val classId: Int,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String
)

data class UpdateSessionRequest(
    @SerializedName("session_date") val sessionDate: String?,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("topic") val topic: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("notes") val notes: String?
)

data class AttendanceItemRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("is_billable") val isBillable: Boolean,
    @SerializedName("note") val note: String?
)

data class SaveBulkAttendanceRequest(
    @SerializedName("items") val items: List<AttendanceItemRequest>
)

data class CreateEvaluationRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("session_id") val sessionId: Int?,
    @SerializedName("coach_id") val coachId: Int,
    @SerializedName("evaluation_type") val evaluationType: String,
    @SerializedName("technical_score") val technicalScore: Int?,
    @SerializedName("discipline_score") val disciplineScore: Int?,
    @SerializedName("physical_score") val physicalScore: Int?,
    @SerializedName("teamwork_score") val teamworkScore: Int?,
    @SerializedName("overall_score") val overallScore: Int?,
    @SerializedName("strengths") val strengths: String?,
    @SerializedName("weaknesses") val weaknesses: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("status") val status: String
)

data class UpdateEvaluationRequest(
    @SerializedName("technical_score") val technicalScore: Int?,
    @SerializedName("discipline_score") val disciplineScore: Int?,
    @SerializedName("physical_score") val physicalScore: Int?,
    @SerializedName("teamwork_score") val teamworkScore: Int?,
    @SerializedName("overall_score") val overallScore: Int?,
    @SerializedName("strengths") val strengths: String?,
    @SerializedName("weaknesses") val weaknesses: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("status") val status: String?
)