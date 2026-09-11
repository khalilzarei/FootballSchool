package com.khz.footballschool.domain.model

data class Coach(
    val id: Int,
    val userId: Int,
    val user: User,
    val specialty: String?,
    val licenseLevel: String?,
    val bio: String?,
    val classes: List<FootballClass> = emptyList()
)