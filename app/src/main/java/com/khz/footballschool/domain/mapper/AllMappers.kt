package com.khz.footballschool.domain.mapper

import com.khz.footballschool.data.dto.response.*
import com.khz.footballschool.domain.model.*

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
    avatarUrl = avatarUrl,
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
    lastName = lastName,
    fullName = fullName?.takeIf { it.isNotBlank() } ?: "$firstName $lastName".trim(),
    nationalCode = nationalCode,
    birthDate = birthDate,
    age = age,
    gender = gender,
    status = status,
    medicalNotes = medicalNotes,
    notes = notes,
    // ─── اولویت با avatar_url کامل، سپس avatar_path نسبی ───
    avatarPath = avatarUrl ?: avatarPath,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    guardians = guardians?.map { it.toDomain() } ?: emptyList(),
    currentClass = currentClass?.toDomain(),
    balance = balance?.toDomain(),
)

fun PlayerBalanceDto.toDomain(): PlayerBalance = PlayerBalance(
    playerId = playerId,
    totalInvoiced = totalInvoiced,
    totalPaid = totalPaid,
    balance = balance,
    pendingPayments = pendingPayments
)

fun CoachDto.toDomain(): Coach = Coach(
    id = id,
    userId = userId,
    user = user.toDomain(),
    specialty = specialty,
    licenseLevel = licenseLevel,
    bio = bio,
    classes = classes.map { it.toDomain() })

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
    seasonId = seasonId,
    season = season?.toDomain(),
    title = title,
    birthDateFrom = birthDateFrom,
    birthDateTo = birthDateTo,
    minAgeAtCutoff = minAgeAtCutoff,
    maxAgeAtCutoff = maxAgeAtCutoff,
    sortOrder = sortOrder,
    status = status,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ClassDto.toDomain(): FootballClass = FootballClass(
    id = id,
    title = title,
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
    schedules = schedules.map { it.toDomain() })

fun ClassScheduleDto.toDomain(): ClassSchedule = ClassSchedule(
    id = id,
    classId = classId,
    weekday = weekday.toString(),
    startTime = startTime,
    endTime = endTime,
    location = location,
    status = status,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun EnrollmentDto.toDomain(): Enrollment = Enrollment(
    id = id,
    classId = classId,
    classItem = classItem.toDomain(),
    playerId = playerId,
    player = player.toDomain(),
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
    startTime = startTime,
    endTime = endTime,
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
    items = items.map { it.toDomain() },
    discounts = discounts.map { it.toDomain() },
    installments = installments.map { it.toDomain() },
    payments = payments.map { it.toDomain() },
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
    fileType = fileType,
    fileSize = fileSize,
    mimeType = mimeType,
    url = url,
    thumbnailUrl = thumbnailUrl,
    visibility = visibility,
    relatedType = relatedType,
    relatedId = relatedId,
    description = description,
    status = status,
    uploadedBy = uploadedBy,
    createdAt = createdAt,
    updatedAt = updatedAt
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
    createdAt = createdAt,
    updatedAt = updatedAt
)

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
    notes = notes,
    players = players.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MatchPlayerDto.toDomain(): MatchPlayer = MatchPlayer(
    id = id,
    matchId = matchId,
    playerId = playerId,
    player = player?.toDomain(),
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
    updatedAt = updatedAt
)

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

fun ChatRoomDto.toDomain(): ChatRoom = ChatRoom(
    id = id,
    roomType = roomType,
    targetUserId = targetUserId,
    playerId = playerId,
    classId = classId,
    subject = subject,
    lastMessage = lastMessage?.toDomain(),
    unreadCount = unreadCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    roomId = roomId,
    senderId = senderId,
    sender = sender?.toDomain(),
    messageType = messageType,
    body = body,
    mediaId = mediaId,
    media = media?.toDomain(),
    isRead = isRead,
    readAt = readAt,
    createdAt = createdAt,
    senderName = sender?.fullName
)

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
    overdueDebt = overdueDebt,
    oldestInvoiceDate = oldestInvoiceDate
)

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
    classId = classId,
    classTitle = classTitle,
    weekday = weekday,
    startTime = startTime,
    endTime = endTime,
    location = location,
    coachName = coachName
)

fun MyFinanceDto.toDomain(): MyFinance = MyFinance(
    playerId = playerId,
    playerName = playerName,
    totalInvoiced = totalInvoiced,
    totalPaid = totalPaid,
    balance = balance,
    pendingPayments = pendingPayments,
    invoices = invoices.map { it.toDomain() })