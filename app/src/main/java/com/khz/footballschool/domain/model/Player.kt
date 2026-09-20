package com.khz.footballschool.domain.model

data class Player(
    val id: Int,
    val userId: Int?,
    val firstName: String,
    val lastName: String,
    val fullName: String,            // در Mapper ساخته می‌شود
    val nationalCode: String?,
    val birthDate: String?,
    val age: Int?,
    val gender: String?,
    val status: String,
    val medicalNotes: String?,
    val notes: String?,
    val avatarPath: String?,
    val createdBy: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    val guardians: List<GuardianPlayer>,
    val currentClass: FootballClass?,
    val balance: PlayerBalance?
)