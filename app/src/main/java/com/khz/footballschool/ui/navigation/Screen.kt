package com.khz.footballschool.ui.navigation

sealed class Screen(val route: String) {

    object AttachGuardianToPlayer : Screen("players/{playerId}/attach-guardian") {
        fun createRoute(playerId: Int) = "players/$playerId/attach-guardian"
    }

    object GuardianDetail : Screen("guardians/{guardianId}") {
        fun createRoute(guardianId: Int) = "guardians/$guardianId"
    }

    object Splash : Screen("splash") // ← جدید

    // ═════════════════════════════════════════════
    // احراز هویت
    // ═════════════════════════════════════════════
    object Login : Screen("login")
    object ChangePassword : Screen("change_password")

    // ═════════════════════════════════════════════
    // داشبورد
    // ═════════════════════════════════════════════
    object Dashboard : Screen("dashboard")

    // ═════════════════════════════════════════════
    // کاربران (مدیر، مربی، سرپرست)
    // ═════════════════════════════════════════════
    object UserList : Screen("users")
    object UserDetail : Screen("users/{userId}") {
        fun createRoute(userId: Int) = "users/$userId"
    }

    object UserForm : Screen("users/form")
    object UserEdit : Screen("users/{userId}/edit") {
        fun createRoute(userId: Int) = "users/$userId/edit"
    }

    // ═════════════════════════════════════════════
    // سرپرست‌ها
    // ═════════════════════════════════════════════
    object GuardianList : Screen("guardians")

    object GuardianEdit : Screen("guardians/{guardianId}/edit") {
        fun createRoute(guardianId: Int) = "guardians/$guardianId/edit"
    }

    // ═════════════════════════════════════════════
    // بازیکنان
    // ═════════════════════════════════════════════
    object PlayerList : Screen("players")
    object PlayerDetail : Screen("players/{playerId}") {
        fun createRoute(playerId: Int) = "players/$playerId"
    }

    object PlayerForm : Screen("players/form")
    object PlayerEdit : Screen("players/{playerId}/edit") {
        fun createRoute(playerId: Int) = "players/$playerId/edit"
    }

    // ═════════════════════════════════════════════
    // فصل‌ها
    // ═════════════════════════════════════════════
    object SeasonList : Screen("seasons")
    object SeasonForm : Screen("seasons/form")
    object SeasonEdit : Screen("seasons/{seasonId}/edit") {
        fun createRoute(seasonId: Int) = "seasons/$seasonId/edit"
    }

    // ═════════════════════════════════════════════
    // گروه‌های سنی
    // ═════════════════════════════════════════════
    object AgeGroupList : Screen("age_groups")
    object AgeGroupForm : Screen("age_groups/form")
    object AgeGroupEdit : Screen("age_groups/{ageGroupId}/edit") {
        fun createRoute(ageGroupId: Int) = "age_groups/$ageGroupId/edit"
    }

    // ═════════════════════════════════════════════
    // مربیان
    // ═════════════════════════════════════════════
    object CoachList : Screen("coaches")
    object CoachDetail : Screen("coaches/{coachId}") {
        fun createRoute(coachId: Int) = "coaches/$coachId"
    }

    object CoachEdit : Screen("coaches/{coachId}/edit") {
        fun createRoute(coachId: Int) = "coaches/$coachId/edit"
    }

    // ═════════════════════════════════════════════
    // کلاس‌ها، برنامه‌ها و ثبت‌نام‌ها
    // ═════════════════════════════════════════════
    object ClassList : Screen("classes")
    object ClassDetail : Screen("classes/{classId}") {
        fun createRoute(classId: Int) = "classes/$classId"
    }

    object ClassForm : Screen("classes/form")
    object ClassEdit : Screen("classes/{classId}/edit") {
        fun createRoute(classId: Int) = "classes/$classId/edit"
    }

    object ScheduleManager : Screen("classes/{classId}/schedules") {
        fun createRoute(classId: Int) = "classes/$classId/schedules"
    }

    object EnrollmentManager : Screen("classes/{classId}/enrollments") {
        fun createRoute(classId: Int) = "classes/$classId/enrollments"
    }

    object EnrollPlayer : Screen("classes/{classId}/enroll") {
        fun createRoute(classId: Int) = "classes/$classId/enroll"
    }

    // ═════════════════════════════════════════════
    // جلسات تمرین و حضور و غیاب
    // ═════════════════════════════════════════════
    object SessionList : Screen("sessions")
    object MySessions : Screen("my-sessions")
    object SessionDetail : Screen("sessions/{sessionId}") {
        fun createRoute(sessionId: Int) = "sessions/$sessionId"
    }

    object SessionForm : Screen("sessions/form")
    object GenerateSessions : Screen("sessions/generate")
    object Attendance : Screen("attendance/{sessionId}/{classId}") {
        fun createRoute(
            sessionId: Int,
            classId: Int
        ) = "attendance/$sessionId/$classId"
    }

    // ═════════════════════════════════════════════
    // ارزیابی‌ها
    // ═════════════════════════════════════════════
    object EvaluationList : Screen("evaluations")
    object EvaluationForm : Screen("evaluations/form")
    object EvaluationEdit : Screen("evaluations/{evaluationId}/edit") {
        fun createRoute(evaluationId: Int) = "evaluations/$evaluationId/edit"
    }

    object Chat : Screen("chat/{roomId}") {
        fun createRoute(roomId: Int): String = "chat/$roomId"
    }

    object SessionEvaluations : Screen("sessions/{sessionId}/evaluations") {
        fun createRoute(sessionId: Int) = "sessions/$sessionId/evaluations"
    }

    // ═════════════════════════════════════════════
    // مالی: فاکتورها
    // ═════════════════════════════════════════════
    object InvoiceList : Screen("invoices")
    object InvoiceDetail : Screen("invoices/{invoiceId}") {
        fun createRoute(invoiceId: Int) = "invoices/$invoiceId"
    }

    object InvoiceForm : Screen("invoices/form")
    object InvoiceEdit : Screen("invoices/{invoiceId}/edit") {
        fun createRoute(invoiceId: Int) = "invoices/$invoiceId/edit"
    }

    object InvoiceItemsManager : Screen("invoices/{invoiceId}/items") {
        fun createRoute(invoiceId: Int) = "invoices/$invoiceId/items"
    }

    object InstallmentsManager : Screen("invoices/{invoiceId}/installments") {
        fun createRoute(invoiceId: Int) = "invoices/$invoiceId/installments"
    }

    // ═════════════════════════════════════════════
    // تخفیف‌ها
    // ═════════════════════════════════════════════
    object DiscountList : Screen("discounts")
    object DiscountForm : Screen("discounts/form")
    object DiscountEdit : Screen("discounts/{discountId}/edit") {
        fun createRoute(discountId: Int) = "discounts/$discountId/edit"
    }

    // ═════════════════════════════════════════════
    // پرداخت‌ها
    // ═════════════════════════════════════════════
    object PaymentList : Screen("payments")
    object PaymentDetail : Screen("payments/{paymentId}") {
        fun createRoute(paymentId: Int) = "payments/$paymentId"
    }

    object PaymentForm : Screen("payments/form")

    // ═════════════════════════════════════════════
    // رسانه‌ها
    // ═════════════════════════════════════════════
    object MediaList : Screen("media")
    object MediaUpload : Screen("media/upload")
    object MediaDetail : Screen("media/{mediaId}") {
        fun createRoute(mediaId: Int) = "media/$mediaId"
    }

    // ═════════════════════════════════════════════
    // اخبار
    // ═════════════════════════════════════════════
    object NewsList : Screen("news")
    object NewsDetail : Screen("news/{newsId}") {
        fun createRoute(newsId: Int) = "news/$newsId"
    }

    object NewsForm : Screen("news/form")
    object NewsEdit : Screen("news/{newsId}/edit") {
        fun createRoute(newsId: Int) = "news/$newsId/edit"
    }

    // ═════════════════════════════════════════════
    // مسابقات
    // ═════════════════════════════════════════════
    object MatchList : Screen("matches")
    object MatchDetail : Screen("matches/{matchId}") {
        fun createRoute(matchId: Int) = "matches/$matchId"
    }

    object MatchForm : Screen("matches/form")
    object MatchEdit : Screen("matches/{matchId}/edit") {
        fun createRoute(matchId: Int) = "matches/$matchId/edit"
    }

    object MatchPlayers : Screen("matches/{matchId}/players") {
        fun createRoute(matchId: Int) = "matches/$matchId/players"
    }

    object SetMatchResult : Screen("matches/{matchId}/result") {
        fun createRoute(matchId: Int) = "matches/$matchId/result"
    }

    // ═════════════════════════════════════════════
    // اعلان‌ها
    // ═════════════════════════════════════════════
    object NotificationList : Screen("notifications")
    object NotificationDetail : Screen("notifications/{notificationId}") {
        fun createRoute(notificationId: Int) = "notifications/$notificationId"
    }

    object SendNotification : Screen("notifications/send")

    // ═════════════════════════════════════════════
    // تنظیمات
    // ═════════════════════════════════════════════
    object Settings : Screen("settings")

    // ═════════════════════════════════════════════
    // چت
    // ═════════════════════════════════════════════
    object ChatRoomList : Screen("chat_rooms")
    object CreateChatRoom : Screen("chat_rooms/create")

    // ═════════════════════════════════════════════
    // گزارش‌ها
    // ═════════════════════════════════════════════
    object Reports : Screen("reports")
    object FinanceReport : Screen("reports/finance")
    object DebtsReport : Screen("reports/debts")
    object AttendanceReport : Screen("reports/attendance")
    object ClassesReport : Screen("reports/classes")

}