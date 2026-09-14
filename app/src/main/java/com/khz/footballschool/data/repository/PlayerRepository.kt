package com.khz.footballschool.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.core.util.MultipartHelper
import com.khz.footballschool.data.dto.request.AttachGuardianToPlayerRequest
import com.khz.footballschool.data.dto.request.UpdatePlayerRequest
import com.khz.footballschool.data.dto.response.GuardianPlayerDto
import com.khz.footballschool.data.dto.response.PlayerDto
import com.khz.footballschool.data.remote.PlayerApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.*
import okhttp3.MultipartBody

class PlayerRepository(private val api: PlayerApi) {

    companion object {
        private const val TAG = "PlayerRepository"
    }

    // ═════════════════════════════════════════════
    // List & Detail
    // ═════════════════════════════════════════════
    suspend fun getPlayers(
        page: Int = 1,
        perPage: Int = 20,
        query: String? = null,
        status: String? = null
    ): NetworkResult<PaginatedResponse<Player>> = try {
        val r = api.getPlayers(
            page,
            perPage,
            query,
            status
        )
        if (r.success && r.data != null) {
            NetworkResult.Success(
                PaginatedResponse(
                    items = r.data.items.map { it.toDomain() },
                    total = r.data.total,
                    page = r.data.page,
                    perPage = r.data.perPage
                )
            )
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت بازیکنان"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getPlayer(id: Int): NetworkResult<Player> = try {
        val r = api.getPlayer(id)
        val dto = r.data.unwrap<PlayerDto>("player")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت بازیکن"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Create Player (multipart با آواتار اختیاری)
    // ═════════════════════════════════════════════
    suspend fun createPlayer(
        firstName: String,
        lastName: String,
        nationalCode: String?,
        birthDate: String?,
        gender: String,
        status: String = "active",
        medicalNotes: String? = null,
        notes: String? = null,
        avatarUri: Uri? = null,
        context: Context
    ): NetworkResult<Player> = try {
        val parts = mutableListOf<MultipartBody.Part>()

        MultipartHelper.textPart(
            "first_name",
            firstName
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "last_name",
            lastName
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "national_code",
            nationalCode
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "birth_date",
            birthDate
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "gender",
            gender
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "status",
            status
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "medical_notes",
            medicalNotes
        )
            ?.let { parts.add(it) }
        MultipartHelper.textPart(
            "notes",
            notes
        )
            ?.let { parts.add(it) }

        if (avatarUri != null) {
            MultipartHelper.filePart(
                context,
                avatarUri,
                "avatar"
            )
                ?.let { parts.add(it) }
        }

        Log.d(
            TAG,
            "createPlayer: ${parts.size} parts"
        )

        val r = api.createPlayer(parts)
        val dto = r.data.unwrap<PlayerDto>("player")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ایجاد بازیکن"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createPlayer error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Update Player (multipart با آواتار اختیاری)
    // ═════════════════════════════════════════════
    suspend fun updatePlayer(
        id: Int,
        firstName: String,
        lastName: String,
        nationalCode: String?,
        birthDate: String?,
        gender: String,
        status: String,
        medicalNotes: String? = null,
        notes: String? = null,
        avatarUri: Uri? = null,
        context: Context
    ): NetworkResult<Player> = try {
        // ─── مرحله ۱: آپدیت اطلاعات با JSON ───
        val request = UpdatePlayerRequest(
            firstName = firstName,
            lastName = lastName,
            nationalCode = nationalCode,
            birthDate = birthDate,
            gender = gender,
            status = status,
            medicalNotes = medicalNotes,
            notes = notes
        )

        Log.d(
            TAG,
            "updatePlayer: updating info for id=$id"
        )
        val updateResult = api.updatePlayer(
            id,
            request
        )

        if (!updateResult.success) {
            return NetworkResult.Error(
                updateResult.message
                        ?: "خطا در به‌روزرسانی بازیکن"
            )
        }

        // ─── مرحله ۲: آپلود آواتار اگر انتخاب شده ───
        if (avatarUri != null) {
            Log.d(
                TAG,
                "updatePlayer: uploading avatar for id=$id"
            )
            val part = MultipartHelper.filePart(
                context,
                avatarUri,
                "avatar"
            )
            if (part != null) {
                val avatarResult = api.uploadPlayerAvatar(
                    id,
                    part
                )
                if (!avatarResult.success) {
                    Log.w(
                        TAG,
                        "Avatar upload failed (but player updated): ${avatarResult.message}"
                    )
                }
            } else {
                Log.w(
                    TAG,
                    "updatePlayer: cannot read avatar file"
                )
            }
        }

        // ─── مرحله ۳: دریافت اطلاعات به‌روز بازیکن ───
        getPlayer(id)

    } catch (e: Exception) {
        Log.e(
            TAG,
            "updatePlayer error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Avatar Management
    // ═════════════════════════════════════════════
    suspend fun uploadAvatar(
        playerId: Int,
        imageUri: Uri,
        context: Context
    ): NetworkResult<Player> = try {
        val part = MultipartHelper.filePart(
            context,
            imageUri,
            "avatar"
        )
                ?: return NetworkResult.Error("خطا در خواندن تصویر")

        val r = api.uploadPlayerAvatar(
            playerId,
            part
        )
        val dto = r.data.unwrap<PlayerDto>("player")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در آپلود آواتار"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun deleteAvatar(playerId: Int): NetworkResult<Player> = try {
        val r = api.deletePlayerAvatar(playerId)
        if (r.success) getPlayer(playerId)
        else NetworkResult.Error(
            r.message
                    ?: "خطا در حذف آواتار"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Status Management
    // ═════════════════════════════════════════════
    suspend fun toggleStatus(
        id: Int,
        activate: Boolean
    ): NetworkResult<Unit> = try {
        val r = if (activate) api.activatePlayer(id) else api.deactivatePlayer(id)
        if (r.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در تغییر وضعیت"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Guardians Relations
    // ═════════════════════════════════════════════
    suspend fun getPlayerGuardians(id: Int): NetworkResult<List<GuardianPlayer>> = try {
        val r = api.getPlayerGuardians(id)
        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.map { it.toDomain() })
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت سرپرست‌ها"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun attachGuardian(
        id: Int,
        request: AttachGuardianToPlayerRequest
    ): NetworkResult<GuardianPlayer> = try {
        val r = api.attachGuardian(
            id,
            request
        )
        val dto = r.data.unwrap<GuardianPlayerDto>(
            "guardian_player",
            "player",
            "guardian"
        )
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در اتصال سرپرست"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun detachGuardian(
        playerId: Int,
        guardianId: Int
    ): NetworkResult<Unit> = try {
        val r = api.detachGuardian(
            playerId,
            guardianId
        )
        if (r.success) NetworkResult.Success(Unit)
        else NetworkResult.Error(
            r.message
                    ?: "خطا در قطع ارتباط"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Related Resources
    // ═════════════════════════════════════════════
    suspend fun getPlayerAttendances(id: Int): NetworkResult<List<Attendance>> = try {
        val r = api.getPlayerAttendances(id)
        // سرور لیست را داخل کلید "attendances" برمی‌گرداند
        if (r.success && r.data != null) {
            NetworkResult.Success(
                (r.data.attendances
                        ?: emptyList()).map { it.toDomain() })
        } else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت حضور و غیاب"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getPlayerEvaluations(id: Int): NetworkResult<List<Evaluation>> = try {
        val r = api.getPlayerEvaluations(id)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت ارزیابی‌ها"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getPlayerInvoices(id: Int): NetworkResult<List<Invoice>> = try {
        val r = api.getPlayerInvoices(id)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت فاکتورها"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getPlayerPayments(id: Int): NetworkResult<List<Payment>> = try {
        val r = api.getPlayerPayments(id)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت پرداخت‌ها"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getPlayerBalance(id: Int): NetworkResult<PlayerBalance> = try {
        val r = api.getPlayerBalance(id)
        if (r.success && r.data != null) NetworkResult.Success(r.data.toDomain())
        else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت مانده حساب"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}