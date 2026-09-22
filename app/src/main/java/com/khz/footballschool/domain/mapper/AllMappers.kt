package com.khz.footballschool.domain.mapper

import com.khz.footballschool.data.dto.response.*
import com.khz.footballschool.domain.model.*
import java.lang.Boolean.parseBoolean

// ═══════════════════════════════════════════════════════════════
// Users & Guardians & Players
// ═══════════════════════════════════════════════════════════════

fun UserDto.toDomain(): User = User(
    id = id,
    fullName = fullName,
    mobile = mobile,
    nationalCode = nationalCode,
    role = role,
    status = status,
    mustChangePassword = (mustChangePassword
            ?: 0) == 1,
    failedLoginCount = failedLoginCount
            ?: 0,
    lockedUntil = lockedUntil,
    lastLoginAt = lastLoginAt,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    // ─── اولویت با URL کامل، سپس مسیر نسبی (AvatarView هر دو را پشتیبانی می‌کند) ───
    avatarUrl = avatarUrl?.takeIf { it.isNotBlank() }
            ?: avatarPath?.takeIf { it.isNotBlank() }
            ?: avatar?.takeIf { it.isNotBlank() },
)

fun GuardianDto.toDomain(): Guardian = Guardian(
    id = id,
    userId = userId,
    user = user?.toDomain(),
    address = address,
    emergencyPhone = emergencyPhone,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    players = players?.map { it.toDomain() }
            ?: emptyList())

fun GuardianPlayerDto.toDomain(): GuardianPlayer = GuardianPlayer(
    id = id,
    guardianId = guardianId,
    playerId = playerId,
    relation = relation,
    isPrimary = isPrimary,
    canViewReports = canViewReports,
    canPay = canPay,
    guardian = guardian?.toDomain(),
    player = player?.toDomain()
)

fun PlayerDto.toDomain(): Player = Player(
    id = id,
    firstName = firstName,
    userId = userId,
    lastName = lastName,
    fullName = fullName?.takeIf { it.isNotBlank() }
            ?: "$firstName $lastName".trim(),
    nationalCode = nationalCode,
    birthDate = birthDate,
    age = age,
    gender = gender,
    status = status,
    medicalNotes = medicalNotes,
    notes = notes,
    // ─── اولویت با avatar_url کامل، سپس avatar_path نسبی ───
    avatarPath = avatarUrl
            ?: avatarPath,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    guardians = guardians?.map { it.toDomain() }
            ?: emptyList(),
    currentClass = currentClass?.toDomain(),
    balance = balance?.toDomain(),
)

fun PlayerBalanceDto.toDomain(): PlayerBalance = PlayerBalance(
    playerId = playerId,
    totalInvoiced = totalInvoiced
            ?: 0,
    totalPaid = totalPaid
            ?: 0,
    balance = balance
            ?: debt
            ?: 0,
    debt = debt
            ?: balance
            ?: 0,
    pendingPayments = pendingPayments
            ?: pendingPaymentsCount?.toLong()
            ?: 0,
    pendingCount = pendingPaymentsCount
            ?: pendingPayments?.toInt()
            ?: 0,
    pendingAmount = pendingAmount
            ?: 0,
    isDebtor = isDebtor
            ?: ((debt
                    ?: balance
                    ?: 0) > 0),
    classDebts = classDebts?.map { it.toDomain() }
            ?: emptyList(),
    classFees = classFees?.map { it.toDomain() }
            ?: emptyList())

fun ClassDebtDto.toDomain(): PlayerClassDebt = PlayerClassDebt(
    classId = classId,
    classTitle = classTitle
            ?: "کلاس #${classId ?: 0}",
    ageGroupTitle = ageGroupTitle,
    total = total
            ?: 0,
    paid = paid
            ?: 0,
    remaining = remaining
            ?: 0,
    itemsCount = itemsCount
            ?: 0
)

fun ClassFeeDto.toDomain(): PlayerClassFee = PlayerClassFee(
    classId = classId,
    classTitle = classTitle
            ?: "کلاس #${classId ?: 0}",
    ageGroupTitle = ageGroupTitle,
    monthlyFee = monthlyFee,
    sessionFee = sessionFee,
    registrationFee = registrationFee,
    debt = debt
            ?: 0
)

fun CoachDto.toDomain(): Coach = Coach(
    id = id,
    userId = userId,
    user = user.toDomain(),
    specialty = specialty,
    licenseLevel = licenseLevel,
    bio = bio,
    classes = classes.orEmpty()
        .map { it.toDomain() })

// ═══════════════════════════════════════════════════════════════
// Academic
// ═══════════════════════════════════════════════════════════════

fun SeasonDto.toDomain(): Season = Season(
    id = id,
    title = title,
    startDate = startDate,
    endDate = endDate,
    ageCutoffDate = ageCutoffDate,
    status = status,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AgeGroupDto.toDomain(): AgeGroup = AgeGroup(
    id = id,
    title = title,
    birthDateFrom = birthDateFrom,
    birthDateTo = birthDateTo,
    minAgeAtCutoff = minAgeAtCutoff,
    maxAgeAtCutoff = maxAgeAtCutoff,
    sortOrder = sortOrder,
    status = status,
    isActive = isActive,
    playersCount = playersCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ClassDto.toDomain(): FootballClass = FootballClass(
    id = id,
    title = title,
    seasonId = seasonId,
    season = season?.toDomain(),
    ageGroupId = ageGroupId,
    ageGroup = ageGroup?.toDomain(),
    coachId = coachId,
    coach = coach?.toDomain(),
    assistantCoachId = assistantCoachId,
    capacity = capacity,
    status = status,
    isActive = isActive,
    location = location,
    description = description,
    pricingType = pricingType,
    monthlyFee = monthlyFee,
    sessionFee = sessionFee,
    registrationFee = registrationFee,
    startDate = startDate,
    endDate = endDate,
    enrolledCount = enrolledCount,
    schedules = schedules.orEmpty()
        .map { it.toDomain() })

fun ClassScheduleDto.toDomain(): ClassSchedule = ClassSchedule(
    id = id,
    classId = classId,
    weekday = weekday.toString(),
    startTime = startTime.take(5), // حذف ثانیه‌ی TIME دیتابیس (17:00:00 → 17:00)
    endTime = endTime.take(5),
    location = location,
    status = status,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun EnrollmentDto.toDomain(): Enrollment = Enrollment(
    id = id,
    classId = classId,
    classItem = classItem?.toDomain(),
    playerId = playerId,
    player = player?.toDomain(),
    status = status,
    isActive = isActive,
    enrolledAt = enrolledAt,
    endedAt = endedAt,
    monthlyFeeOverride = monthlyFeeOverride,
    sessionFeeOverride = sessionFeeOverride,
    registrationFeeOverride = registrationFeeOverride,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SessionDto.toDomain(): Session = Session(
    id = id,
    classId = classId,
    classItem = classItem?.toDomain(),
    sessionDate = sessionDate,
    startTime = startTime?.take(5), // حذف ثانیه‌ی TIME دیتابیس (17:00:00 → 17:00)
    endTime = endTime?.take(5),
    location = location,
    topic = topic,
    status = status,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AttendanceDto.toDomain(): Attendance = Attendance(
    id = id,
    sessionId = sessionId,
    session = session?.toDomain(),
    playerId = playerId,
    player = player?.toDomain(),
    status = status,
    isBillable = isBillable,
    note = note,
    recordedAt = recordedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun EvaluationDto.toDomain(): Evaluation = Evaluation(
    id = id,
    playerId = playerId,
    player = player?.toDomain(),
    sessionId = sessionId,
    session = session?.toDomain(),
    coachId = coachId,
    coach = coach?.toDomain(),
    evaluationType = evaluationType,
    technicalScore = technicalScore,
    disciplineScore = disciplineScore,
    physicalScore = physicalScore,
    teamworkScore = teamworkScore,
    overallScore = overallScore,
    strengths = strengths,
    weaknesses = weaknesses,
    notes = notes,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ═══════════════════════════════════════════════════════════════
// Finance
// ═══════════════════════════════════════════════════════════════

fun InvoiceDto.toDomain(): Invoice = Invoice(
    id = id,
    playerId = playerId,
    player = player?.toDomain(),
    invoiceType = invoiceType,
    periodStartDate = periodStartDate,
    periodEndDate = periodEndDate,
    dueDate = dueDate,
    status = status,
    totalAmount = totalAmount,
    paidAmount = paidAmount,
    remainingAmount = remainingAmount,
    notes = notes,
    items = items.orEmpty()
        .map { it.toDomain() },
    discounts = discounts.orEmpty()
        .map { it.toDomain() },
    installments = installments.orEmpty()
        .map { it.toDomain() },
    payments = payments.orEmpty()
        .map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun InvoiceItemDto.toDomain(): InvoiceItem = InvoiceItem(
    id = id,
    invoiceId = invoiceId,
    title = title,
    itemType = itemType,
    amount = amount,
    quantity = quantity,
    total = total,
    classId = classId,
    classTitle = classTitle,
    ageGroupTitle = ageGroupTitle,
    sessionId = sessionId,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun InstallmentDto.toDomain(): Installment = Installment(
    id = id,
    invoiceId = invoiceId,
    amount = amount,
    paidAmount = paidAmount,
    dueDate = dueDate,
    status = status,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun DiscountDto.toDomain(): Discount = Discount(
    id = id,
    title = title,
    discountType = discountType,
    value = value,
    appliesTo = appliesTo,
    autoApply = autoApply,
    startDate = startDate,
    endDate = endDate,
    status = status,
    isActive = isActive,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PaymentDto.toDomain(): Payment = Payment(
    id = id,
    playerId = playerId,
    player = player?.toDomain(),
    invoiceId = invoiceId,
    invoice = invoice?.toDomain(),
    installmentId = installmentId,
    amount = amount,
    paymentMethod = paymentMethod,
    status = status,
    receiptMediaId = receiptMediaId,
    notes = notes,
    approvedAt = approvedAt,
    rejectedAt = rejectedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ═══════════════════════════════════════════════════════════════
// Content
// ═══════════════════════════════════════════════════════════════

fun MediaDto.toDomain(): Media = Media(
    id = id,
    fileName = fileName,
    originalName = originalName,
    fileType = fileType
            ?: "other",
    fileSize = fileSize
            ?: 0L,
    mimeType = mimeType
            ?: "application/octet-stream",
    url = url,
    thumbnailUrl = thumbnailUrl,
    visibility = visibility
            ?: "private",
    relatedType = relatedType,
    relatedId = relatedId,
    description = description,
    status = status
            ?: "active",
    uploadedBy = uploadedBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    durationSeconds = durationSeconds,
    streamUrl = streamUrl
            ?: url,
    uploaderName = uploaderName
)

fun NewsAudienceDto.toDomain(): NewsAudience = NewsAudience(
    id = id,
    newsId = newsId,
    audienceType = audienceType,
    role = role,
    targetId = targetId,
    targetTitle = targetTitle
)

fun NewsDto.toDomain(): News = News(
    id = id,
    title = title,
    body = body,
    status = status,
    publishAt = publishAt,
    publishedAt = publishedAt,
    archivedAt = archivedAt,
    createdBy = createdBy,
    createdByName = createdByName,
    media = media?.map { it.toDomain() }
            ?: emptyList(),
    audiences = audiences?.map { it.toDomain() }
            ?: emptyList(),
    createdAt = createdAt,
    updatedAt = updatedAt)

fun MatchDto.toDomain(): Match = Match(
    id = id,
    title = title,
    matchType = matchType,
    classId = classId,
    classItem = classItem?.toDomain(),
    ageGroupId = ageGroupId,
    ageGroup = ageGroup?.toDomain(),
    opponentTeam = opponentTeam,
    matchDate = matchDate,
    matchTime = matchTime,
    location = location,
    status = status,
    homeScore = homeScore,
    awayScore = awayScore,
    result = result,
    classTitle = classTitle
            ?: classItem?.title,
    ageGroupTitle = ageGroupTitle
            ?: ageGroup?.title,
    notes = notes,
    players = players.orEmpty()
        .map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MatchPlayerDto.toDomain(): MatchPlayer {
    // اگر player نال باشد ولی first_name/last_name موجود باشد، یک Player موقت بساز
    val resolvedPlayer = player?.toDomain()
            ?: run {
                val fn = firstName?.trim()
                val ln = lastName?.trim()
                val full = fullName?.trim()
                    ?.takeIf { it.isNotBlank() }
                        ?: listOfNotNull(
                            fn,
                            ln
                        ).joinToString(" ")
                            .trim()
                            .takeIf { it.isNotBlank() }
                if (full != null || fn != null || ln != null) {
                    // Player minimal برای نمایش نام
                    Player(
                        id = playerId,
                        firstName = fn
                                ?: "",
                        lastName = ln
                                ?: "",
                        fullName = full
                                ?: "بازیکن #$playerId",
                        nationalCode = null,
                        birthDate = null,
                        age = null,
                        gender = "male",
                        status = "active",
                        medicalNotes = null,
                        notes = null,
                        avatarPath = null,
                        createdBy = null,
                        createdAt = null,
                        updatedAt = null,
                        guardians = emptyList(),
                        currentClass = null,
                        balance = null,
                        userId = playerId
                    )
                } else null
            }

    return MatchPlayer(
        id = id,
        matchId = matchId,
        playerId = playerId,
        player = resolvedPlayer,
        invitationStatus = invitationStatus,
        attendanceStatus = attendanceStatus,
        jerseyNumber = jerseyNumber,
        position = position,
        goals = goals,
        assists = assists,
        yellowCards = yellowCards,
        redCards = redCards,
        minutesPlayed = minutesPlayed,
        rating = rating,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = playerId
    )
}

// ═══════════════════════════════════════════════════════════════
// Notifications, Chat, Settings
// ═══════════════════════════════════════════════════════════════

fun NotificationDto.toDomain(): Notification = Notification(
    id = id,
    userId = userId,
    title = title,
    body = body,
    type = type,
    isRead = isRead,
    data = data,
    readAt = readAt,
    createdAt = createdAt
)

fun UnreadCountDto.toDomain(): UnreadCount = UnreadCount(
    unreadCount = unreadCount
)

fun ChatRoomDto.toDomain(): ChatRoom {
    return ChatRoom(
        id = id,
        isGroup = isGroup,
        title = title.orEmpty(),
        image = image,
        users = users.map {
            ChatRoomUser(
                id = it.id,
                fullName = it.fullName.orEmpty(),
                avatar = it.avatar,
                role = it.role,
                memberRole = it.memberRole
            )
        },
        lastMessage = lastMessage?.toDomain(),
        unreadCount = unreadCount,
        status = status,
        isLocked = parseBoolean(isLocked),
        playerId = playerId,
        classId = classId,
        subject = subject,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun parseBoolean(value: Any?): Boolean {
    return when (value) {
        is Boolean -> value
        is Number  -> value.toInt() != 0
        is String  -> {
            value == "1" || value.equals(
                "true",
                ignoreCase = true
            )
        }

        else       -> false
    }
}

fun ChatContactDto.toDomain(): ChatContact = ChatContact(
    userId = userId,
    fullName = fullName,
    role = role,
    avatarUrl = avatarUrl,
    classTitle = classTitle
)

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        roomId = effectiveRoomId,
        senderId = senderId,
        sender = sender?.toDomain(),
        senderAvatar = effectiveSenderAvatar,
        messageType = messageType,
        body = body,
        mediaId = mediaId,
        media = media?.toDomain(),
        isRead = isRead,
        readAt = readAt,
        createdAt = effectiveCreatedAt,
        senderName = effectiveSenderName
    )
}

fun SettingDto.toDomain(): Setting = Setting(
    id = id,
    key = key,
    value = value,
    valueType = valueType,
    description = description,
    updatedAt = updatedAt
)

// ═══════════════════════════════════════════════════════════════
// Reports
// ═══════════════════════════════════════════════════════════════

fun DashboardReportDto.toDomain(): DashboardReport = DashboardReport(
    usersStats = report.users.toDomain(),
    activePlayers = report.activePlayers,
    activeClasses = report.activeClasses,
    sessionsToday = report.sessionsToday,
    pendingPayments = report.pendingPayments,
    totalDebt = report.totalDebt
)

fun UsersStatsDto.toDomain(): UsersStats = UsersStats(
    totalUsers = totalUsers,
    totalAdmins = totalAdmins.toIntOrNull()
            ?: 0,
    totalCoaches = totalCoaches.toIntOrNull()
            ?: 0,
    totalGuardians = totalGuardians.toIntOrNull()
            ?: 0
)

fun FinanceReportDto.toDomain(): FinanceReport = FinanceReport(
    totalInvoiced = totalInvoiced,
    totalPaid = totalPaid,
    totalPending = totalPending,
    totalDebt = totalDebt,
    monthlyRevenue = monthlyRevenue,
    dailyRevenue = dailyRevenue
)

fun DebtsReportDto.toDomain(): DebtsReport = DebtsReport(
    playerId = playerId,
    playerName = playerName,
    totalDebt = totalDebt,
    total = total
            ?: totalDebt,
    paid = paid
            ?: 0,
    overdueDebt = overdueDebt
            ?: totalDebt,
    oldestInvoiceDate = oldestInvoiceDate
            ?: oldestDueDate,
    classDebts = classDebts?.map { it.toDomain() }
            ?: emptyList(),
    isDebtor = isDebtor
            ?: true)

fun AttendanceReportDto.toDomain(): AttendanceReport = AttendanceReport(
    playerId = playerId,
    playerName = playerName,
    totalSessions = totalSessions,
    presentCount = presentCount,
    absentCount = absentCount,
    excusedCount = excusedCount,
    attendanceRate = attendanceRate
)

fun ClassesReportDto.toDomain(): ClassesReport = ClassesReport(
    classId = classId,
    classTitle = classTitle,
    coachName = coachName,
    enrolledCount = enrolledCount,
    capacity = capacity,
    occupancyRate = occupancyRate,
    totalRevenue = totalRevenue
)

// ═══════════════════════════════════════════════════════════════
// Client / Me
// ═══════════════════════════════════════════════════════════════

fun MyChildrenDto.toDomain(): MyChild = MyChild(
    id = id,
    firstName = firstName,
    lastName = lastName,
    fullName = fullName,
    birthDate = birthDate,
    age = age,
    currentClass = currentClass?.toDomain(),
    balance = balance?.toDomain()
)

fun MyScheduleDto.toDomain(): MyScheduleItem = MyScheduleItem(
    id = id
            ?: 0,
    classId = classId
            ?: 0,
    classTitle = classTitle
            ?: "-",
    sessionDate = sessionDate
            ?: "",
    startTime = startTime?.take(5)
            ?: "",
    endTime = endTime?.take(5)
            ?: "",
    location = location,
    status = status
            ?: "scheduled",
    topic = topic,
    notes = notes
)

fun MyFinanceDto.toDomain(): MyFinance = MyFinance(
    playerId = playerId,
    playerName = playerName,
    totalInvoiced = totalInvoiced,
    totalPaid = totalPaid,
    balance = balance,
    pendingPayments = pendingPayments,
    invoices = invoices.orEmpty()
        .map { it.toDomain() })