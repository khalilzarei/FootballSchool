package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.remote.ReportApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.AttendanceReport
import com.khz.malekadmin.domain.model.ClassesReport
import com.khz.malekadmin.domain.model.DashboardReport
import com.khz.malekadmin.domain.model.DebtsReport
import com.khz.malekadmin.domain.model.FinanceReport
import retrofit2.HttpException

class ReportRepository(
    private val reportApi: ReportApi
) {
    suspend fun getDashboardReport(): NetworkResult<DashboardReport> {
        return try {
            val response = reportApi.getDashboardReport()
            if (response.success && response.data != null) {
                NetworkResult.Success(response.data.toDomain())
            } else {
                NetworkResult.Error(
                    response.message
                            ?: "خطا در دریافت گزارش داشبورد"
                )
            }
        } catch (e: HttpException) {
            if (e.code() == 423) {
                NetworkResult.Error("423: ابتدا باید رمز عبور را تغییر دهید")
            } else {
                NetworkResult.Error(
                    e.message
                            ?: "خطای شبکه"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e.message
                        ?: "خطای شبکه"
            )
        }
    }

    suspend fun getFinanceReport(): NetworkResult<FinanceReport> {
        return try {
            val response = reportApi.getFinanceReport()
            if (response.success && response.data != null) {
                NetworkResult.Success(response.data.toDomain())
            } else {
                NetworkResult.Error(
                    response.message
                            ?: "خطا در دریافت گزارش مالی"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e.message
                        ?: "خطای شبکه"
            )
        }
    }

    suspend fun getDebtsReport(): NetworkResult<List<DebtsReport>> {
        return try {
            val response = reportApi.getDebtsReport()
            if (response.success && response.data != null) {
                NetworkResult.Success(response.data.map { it.toDomain() })
            } else {
                NetworkResult.Error(
                    response.message
                            ?: "خطا در دریافت گزارش بدهی‌ها"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e.message
                        ?: "خطای شبکه"
            )
        }
    }

    suspend fun getAttendanceReport(
        sessionId: Int? = null,
        classId: Int? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): NetworkResult<List<AttendanceReport>> {
        return try {
            val response = reportApi.getAttendanceReport(
                sessionId,
                classId,
                dateFrom,
                dateTo
            )
            if (response.success && response.data != null) {
                NetworkResult.Success(response.data.map { it.toDomain() })
            } else {
                NetworkResult.Error(
                    response.message
                            ?: "خطا در دریافت گزارش حضور"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e.message
                        ?: "خطای شبکه"
            )
        }
    }

    suspend fun getClassesReport(): NetworkResult<List<ClassesReport>> {
        return try {
            val response = reportApi.getClassesReport()
            if (response.success && response.data != null) {
                NetworkResult.Success(response.data.map { it.toDomain() })
            } else {
                NetworkResult.Error(
                    response.message
                            ?: "خطا در دریافت گزارش کلاس‌ها"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e.message
                        ?: "خطای شبکه"
            )
        }
    }
}