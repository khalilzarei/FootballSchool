package com.khz.footballschool.ui.classes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassDropdown
import com.khz.footballschool.ui.components.GlassTextField3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.JalaliDateField
import com.khz.footballschool.ui.components.TimeWheelField
import com.khz.footballschool.ui.theme.GoldPrimary

// ─── مقدار sentinel برای «انتخاب نشده» در دراپ‌داون‌ها ───
private const val NO_SELECTION = -1

private val STATUSES = listOf(
    "active" to "فعال",
    "inactive" to "غیرفعال",
    "archived" to "بایگانی"
)

// ─── منطبق با سرور: monthly | session | both ───
private val PRICING_TYPES = listOf(
    "monthly" to "ماهانه",
    "session" to "جلسه‌ای",
    "both" to "ماهانه + جلسه‌ای"
)

/** جداکننده‌ی سه‌رقمی برای نمایش مبالغ — ورودی فقط رقم (10,000,000) */
private fun formatThousands(digits: String): String =
    if (digits.length <= 3) digits
    else digits.reversed().chunked(3).joinToString(",").reversed()

// ─── یک ردیف برنامه هفتگی قبل از ثبت (weekday صفر = انتخاب نشده) ───
private data class DraftSchedule(
    val weekday: Int = 0,
    val start: String = "17:00",
    val end: String = "18:30",
    val location: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassFormScreen(
    classId: Int? = null,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onManageSchedules: ((Int) -> Unit)? = null,
    onManageEnrollments: ((Int) -> Unit)? = null
) {
    val isEditMode = classId != null
    val context = LocalContext.current
    val vm: ClassFormViewModel = appViewModel()

    val loading by vm.loading.collectAsState()
    val initialLoading by vm.initialLoading.collectAsState()
    val error by vm.error.collectAsState()
    val cls by vm.cls.collectAsState()

    // ─── داده‌های مرجع ───
    val ageGroups by vm.ageGroups.collectAsState()
    val coaches by vm.coaches.collectAsState()
    val refsLoading by vm.refsLoading.collectAsState()
    val refsError by vm.refsError.collectAsState()

    // ─── State های فرم ───
    var formError by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var ageGroupId by remember { mutableStateOf<Int?>(null) }
    var coachId by remember { mutableStateOf<Int?>(null) }
    var assistantCoachId by remember { mutableStateOf<Int?>(null) }
    var pricingType by remember { mutableStateOf("monthly") }
    var monthlyFee by remember { mutableStateOf("") }
    var sessionFee by remember { mutableStateOf("") }
    var registrationFee by remember { mutableStateOf("") }
    var startDateGregorian by remember { mutableStateOf<String?>(null) }
    var endDateGregorian by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("active") }

    // ─── برنامه هفتگی و بازیکنان ───
    val schedules by vm.schedules.collectAsState()

    val draftSchedules = remember { mutableStateListOf<DraftSchedule>() }
    var autoGenerate by remember { mutableStateOf(true) }
    var preparing by remember { mutableStateOf(false) }
    var prepSummary by remember { mutableStateOf<String?>(null) }

    val container = (context.applicationContext as FootballSchoolApp).container
    val classRepo = container.classRepository
    val sessionRepo = container.sessionRepository
    val scope = rememberCoroutineScope()

    // ─── State های تقویم ───

    // ─── بارگذاری اطلاعات در حالت ویرایش ───
    LaunchedEffect(classId) {
        if (classId != null) vm.load(classId)
    }

    LaunchedEffect(cls) {
        cls?.let {
            title = it.title
            description = it.description ?: ""
            location = it.location ?: ""
            capacity = it.capacity?.toString() ?: ""
            ageGroupId = it.ageGroupId
            coachId = it.coachId
            assistantCoachId = it.assistantCoachId
            pricingType = it.pricingType
                ?.takeIf { pt -> PRICING_TYPES.any { o -> o.first == pt } }
                    ?: "monthly"
            monthlyFee = it.monthlyFee?.takeIf { f -> f > 0 }?.let { f -> formatThousands(f.toString()) } ?: ""
            sessionFee = it.sessionFee?.takeIf { f -> f > 0 }?.let { f -> formatThousands(f.toString()) } ?: ""
            registrationFee = it.registrationFee?.takeIf { f -> f > 0 }?.let { f -> formatThousands(f.toString()) } ?: ""
            startDateGregorian = it.startDate
            endDateGregorian = it.endDate
            status = it.status
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = if (isEditMode) "ویرایش کلاس" else "افزودن کلاس",
                onBack = onBack
            )
        }
    ) { padding ->
        if (initialLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ═════════════════════════════════════════
                // اطلاعات پایه
                // ═════════════════════════════════════════
                GlassCard3D() {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "اطلاعات پایه",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // عنوان
                        GlassTextField3D(
                            value = title,
                            onValueChange = { title = it },
                            label = "عنوان کلاس",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.School,
                                    null
                                )
                            },
                            isError = formError != null && title.length < 3,
                            supportingText = if (formError != null && title.length < 3) "عنوان باید حداقل ۳ کاراکتر باشد" else null
                        )

                        // توضیحات
                        GlassTextField3D(
                            value = description,
                            onValueChange = { description = it },
                            label = "توضیحات (اختیاری)",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.TextFields,
                                    null
                                )
                            },
                            singleLine = false
                        )

                        // مکان
                        GlassTextField3D(
                            value = location,
                            onValueChange = { location = it },
                            label = "مکان (اختیاری)",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null
                                )
                            })

                        // ظرفیت
                        GlassTextField3D(
                            value = capacity,
                            onValueChange = { v ->
                                capacity = v.filter { it.isDigit() }
                                    .take(4)
                            },
                            label = "ظرفیت (اختیاری)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Groups,
                                    null
                                )
                            })
                    }
                }

                // ═════════════════════════════════════════
                // گروه سنی و مربیان (دراپ‌داون از سرور)
                // ═════════════════════════════════════════
                GlassCard3D() {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "گروه سنی و مربیان",
                                style = MaterialTheme.typography.titleSmall,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (refsLoading) {
                                CircularProgressIndicator(
                                    color = GoldPrimary,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        refsError?.let {
                            Text(
                                it,
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.bodySmall
                            )
                            GlassButton(
                                text = "تلاش مجدد",
                                onClick = { vm.loadReferences() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (refsError == null) {
                            // گروه سنی — مستقل از فصل (عضویت = بازه تاریخ تولد)
                            GlassDropdown(
                                label = "گروه سنی (اختیاری)",
                                options = buildList {
                                    add(NO_SELECTION to "بدون گروه سنی")
                                    ageGroups.forEach { ag -> add(ag.id to ag.title) }
                                },
                                selectedValue = ageGroupId
                                        ?: NO_SELECTION,
                                onSelect = { v -> ageGroupId = v.takeIf { it != NO_SELECTION } })

                            // مربی اصلی
                            GlassDropdown(
                                label = "مربی اصلی (اختیاری)",
                                options = buildList {
                                    add(NO_SELECTION to "بدون مربی")
                                    coaches.forEach { c ->
                                        add(c.id to (c.user.fullName + (c.specialty?.let { " ($it)" }
                                                ?: "")))
                                    }
                                },
                                selectedValue = coachId
                                        ?: NO_SELECTION,
                                onSelect = { v ->
                                    coachId = v.takeIf { it != NO_SELECTION }
                                    // مربی کمکی اگر همان شد، ریست شود
                                    if (coachId != null && assistantCoachId == coachId) {
                                        assistantCoachId = null
                                    }
                                })

                            // مربی کمکی (بدون مربی اصلی)
                            GlassDropdown(
                                label = "مربی کمکی (اختیاری)",
                                options = buildList {
                                    add(NO_SELECTION to "بدون مربی کمکی")
                                    coaches.filter { c -> c.id != coachId }
                                        .forEach { c ->
                                            add(c.id to (c.user.fullName + (c.specialty?.let { " ($it)" }
                                                    ?: "")))
                                        }
                                },
                                selectedValue = assistantCoachId
                                        ?: NO_SELECTION,
                                onSelect = { v -> assistantCoachId = v.takeIf { it != NO_SELECTION } })

                            if (coaches.isEmpty() && !refsLoading) {
                                Text(
                                    "هنوز مربی‌ای ثبت نشده است؛ ابتدا از بخش «مربیان» کاربرِ نقش مربی بسازید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.5f)
                                )
                            }
                        }
                    }
                }

                // ═════════════════════════════════════════
                // قیمت‌گذاری
                // ═════════════════════════════════════════
                GlassCard3D() {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "قیمت‌گذاری",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // نوع قیمت‌گذاری (منطبق با سرور)
                        GlassDropdown(
                            label = "نوع قیمت‌گذاری",
                            options = PRICING_TYPES,
                            selectedValue = pricingType,
                            onSelect = { pricingType = it })

                        // شهریه ماهانه
                        if (pricingType == "monthly" || pricingType == "both") {
                            GlassTextField3D(
                                value = monthlyFee,
                                onValueChange = { v -> monthlyFee = formatThousands(v.filter { it.isDigit() }) },
                                label = "شهریه ماهانه (تومان)",
                                keyboardType = KeyboardType.Number,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Money,
                                        null
                                    )
                                })
                        }

                        // هزینه هر جلسه
                        if (pricingType == "session" || pricingType == "both") {
                            GlassTextField3D(
                                value = sessionFee,
                                onValueChange = { v -> sessionFee = formatThousands(v.filter { it.isDigit() }) },
                                label = "هزینه هر جلسه (تومان)",
                                keyboardType = KeyboardType.Number,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Money,
                                        null
                                    )
                                })
                        }

                        // هزینه ثبت‌نام (مستقل از نوع قیمت‌گذاری)
                        GlassTextField3D(
                            value = registrationFee,
                            onValueChange = { v -> registrationFee = formatThousands(v.filter { it.isDigit() }) },
                            label = "هزینه ثبت‌نام (تومان، اختیاری)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Money,
                                    null
                                )
                            })
                    }
                }

                // ═════════════════════════════════════════
                // تاریخ‌ها
                // ═════════════════════════════════════════
                GlassCard3D() {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "تاریخ‌ها",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // شروع و پایان در یک ردیف — انتخاب با تقویم شمسی، ارسال میلادی
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            JalaliDateField(
                                label = "تاریخ شروع (اختیاری)",
                                gregorianValue = startDateGregorian,
                                onDatePicked = { startDateGregorian = it },
                                modifier = Modifier.weight(1f)
                            )
                            JalaliDateField(
                                label = "تاریخ پایان (اختیاری)",
                                gregorianValue = endDateGregorian,
                                onDatePicked = { endDateGregorian = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // دکمه پاک کردن تاریخ‌ها
                        if (startDateGregorian != null || endDateGregorian != null) {
                            GlassButton(
                                text = "پاک کردن تاریخ‌ها",
                                onClick = {
                                    startDateGregorian = null
                                    endDateGregorian = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ═════════════════════════════════════════
                // وضعیت (فقط در حالت ویرایش)
                // ═════════════════════════════════════════
                if (isEditMode) {
                    GlassCard3D() {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GlassDropdown(
                                label = "وضعیت",
                                options = STATUSES,
                                selectedValue = status,
                                onSelect = { status = it })
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ═════════════════════════════════════════
                // برنامه هفتگی — چند روز قابل انتخاب؛ جلسات خودکار
                // ═════════════════════════════════════════
                val showScheduleSection = !isEditMode || schedules.isEmpty()
                if (showScheduleSection) {
                    GlassCard3D(Modifier.fillMaxWidth(),) {
                        Column(
                            Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column {
                                Text(
                                    "برنامه هفتگی (چند روز قابل انتخاب)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Text(
                                    "برای هر روز، ساعت شروع و پایان را وارد کنید؛ می‌توانید چند روز اضافه کنید (مثلاً شنبه و سه‌شنبه). " + "جلسات تا تاریخ پایان کلاس به‌صورت خودکار تولید می‌شوند" + (if (isEditMode) " — این کلاس هنوز برنامه‌ای ندارد" else ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.6f)
                                )
                            }

                            // ─── ردیف‌های برنامه هفتگی (چند روز قابل افزودن) ───
                            draftSchedules.forEachIndexed { index, draft ->
                                Column(
                                    Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "روز ${index + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = GoldPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        androidx.compose.material3.TextButton(onClick = {
                                            draftSchedules.removeAt(index)
                                        }) {
                                            Text(
                                                "حذف",
                                                color = Color(0xFFFF8A80)
                                            )
                                        }
                                    }
                                    GlassDropdown(
                                        label = "روز هفته",
                                        options = WEEKDAYS,
                                        selectedValue = draft.weekday.takeIf { it != 0 },
                                        onSelect = { draftSchedules[index] = draft.copy(weekday = it) })
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TimeWheelField(
                                            label = "از ساعت",
                                            value = draft.start,
                                            onTimePicked = { draftSchedules[index] = draft.copy(start = it) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        TimeWheelField(
                                            label = "تا ساعت",
                                            value = draft.end,
                                            onTimePicked = { draftSchedules[index] = draft.copy(end = it) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    GlassTextField3D(
                                        value = draft.location,
                                        onValueChange = { draftSchedules[index] = draft.copy(location = it) },
                                        label = "محل این روز (اختیاری — پیش‌فرض: محل کلاس)"
                                    )
                                }
                            }

                            GlassButton(
                                text = "افزودن روز +",
                                onClick = {
                                    val used = draftSchedules.map { it.weekday }
                                        .toSet()
                                    val next = (1..7).firstOrNull { it !in used }
                                            ?: 0
                                    draftSchedules.add(DraftSchedule(weekday = next))
                                },
                                primary = false
                            )

                            if (draftSchedules.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = autoGenerate,
                                        onCheckedChange = { autoGenerate = it })
                                    Column {
                                        Text(
                                            "تولید خودکار جلسات تا تاریخ پایان کلاس",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                        if (endDateGregorian == null) {
                                            Text(
                                                "برای این گزینه، تاریخ پایان کلاس را وارد کنید",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFFF8A80)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ═════════════════════════════════════════
                // نمایش خطاها (اعتبارسنجی کلاینت یا خطای سرور)
                // ═════════════════════════════════════════
                val shownError = formError ?: error
                shownError?.let {
                    GlassCard3D(glowColor = Color(0xFFA50044),) {
                        Text(
                            it,
                            color = Color(0xFFFF8A80),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // ═════════════════════════════════════════
                // مدیریت برنامه هفتگی و ثبت‌نام (فقط ویرایش) — یک ردیف
                // ═════════════════════════════════════════
                if (isEditMode && classId != null) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onManageSchedules != null) {
                            GlassButton(
                                text = "مدیریت برنامه هفتگی",
                                onClick = { onManageSchedules(classId) },
                                enabled = !loading,
                                primary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (onManageEnrollments != null) {
                            GlassButton(
                                text = "مدیریت ثبت‌نام بازیکنان",
                                onClick = { onManageEnrollments(classId) },
                                enabled = !loading,
                                primary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ═════════════════════════════════════════
                // دکمه ثبت
                // ═════════════════════════════════════════
                GlassButton(
                    text = if (isEditMode) "ذخیره تغییرات" else "ثبت کلاس",
                    onClick = {
                        // ─── اعتبارسنجی سمت کلاینت ───
                        formError = null
                        vm.clearError()

                        val cap = capacity.toIntOrNull()
                        val mFee = monthlyFee.replace(",", "").toLongOrNull()
                        val sFee = sessionFee.replace(",", "").toLongOrNull()
                        val rFee = registrationFee.replace(",", "").toLongOrNull()

                        formError = when {
                            title.trim().length < 3 ->
                                "عنوان کلاس باید حداقل ۳ کاراکتر باشد"
                            cap != null && cap < 1 ->
                                "ظرفیت باید حداقل ۱ باشد"
                            (pricingType == "monthly" || pricingType == "both") && (mFee == null || mFee <= 0L) ->
                                "شهریه ماهانه را وارد کنید (عددی بزرگ‌تر از صفر)"
                            (pricingType == "session" || pricingType == "both") && (sFee == null || sFee <= 0L) ->
                                "هزینه هر جلسه را وارد کنید (عددی بزرگ‌تر از صفر)"
                            (mFee != null && mFee < 0) || (sFee != null && sFee < 0) || (rFee != null && rFee < 0) ->
                                "مبالغ نمی‌توانند منفی باشند"
                            coachId != null && assistantCoachId != null && coachId == assistantCoachId ->
                                "مربی اصلی و کمکی نمی‌توانند یکسان باشند"
                            startDateGregorian != null && endDateGregorian != null &&
                                    endDateGregorian!! < startDateGregorian!! ->
                                "تاریخ پایان نمی‌تواند قبل از تاریخ شروع باشد"
                            draftSchedules.any { it.weekday == 0 } ->
                                "روز هفته را برای همه‌ی ردیف‌های برنامه هفتگی انتخاب کنید"
                            draftSchedules.any { !Regex("^([01]?\\d|2[0-3]):[0-5]\\d$").matches(it.start.trim()) || !Regex("^([01]?\\d|2[0-3]):[0-5]\\d$").matches(it.end.trim()) } ->
                                "ساعت‌های برنامه هفتگی باید با فرمت HH:mm باشند (مثلاً 17:00)"
                            draftSchedules.any { it.start.trim() >= it.end.trim() } ->
                                "ساعت شروع باید قبل از ساعت پایان باشد"
                            draftSchedules.groupBy { it.weekday }.any { (w, rows) -> w != 0 && rows.size > 1 } ->
                                "هر روز هفته فقط یک بار قابل انتخاب است"
                            else -> null
                        }

                        if (formError == null) {

                            // ─── زنجیره‌ی بعد از ذخیره: برنامه هفتگی + تولید جلسات ───
                            val runPostSave: (Int) -> Unit = { savedClassId ->
                                scope.launch {
                                    preparing = true
                                    val notes = mutableListOf<String>()

                                    // ۱) برنامه هفتگی — همه‌ی روزهای انتخابی (فقط اگر تازه وارد شده)
                                    if (draftSchedules.isNotEmpty() && schedules.isEmpty()) {
                                        var okDays = 0
                                        var firstError: String? = null

                                        draftSchedules.forEach { d ->
                                            when (val r = classRepo.createSchedule(
                                                savedClassId,
                                                d.weekday,
                                                d.start.trim(),
                                                d.end.trim(),
                                                d.location.takeIf { it.isNotBlank() }
                                            )) {
                                                is NetworkResult.Success -> okDays++
                                                is NetworkResult.Error -> if (firstError == null) firstError = r.message
                                                else -> {}
                                            }
                                        }

                                        if (okDays > 0) notes += "برنامه هفتگی: $okDays روز ثبت شد"
                                        firstError?.let { notes += "خطا در ثبت برنامه هفتگی: $it" }

                                        // ۲) تولید خودکار جلسات تا پایان کلاس (یک بار برای همه‌ی روزها)
                                        if (okDays > 0 && autoGenerate) {
                                            if (endDateGregorian != null) {
                                                when (val g = sessionRepo.generateSessionsForClass(savedClassId)) {
                                                    is NetworkResult.Success ->
                                                        notes += if (g.data > 0) "${g.data} جلسه تولید شد"
                                                        else "جلسه جدیدی تولید نشد (تکراری یا خارج از بازه)"
                                                    is NetworkResult.Error ->
                                                        notes += "خطا در تولید جلسات: ${g.message}"
                                                    else -> {}
                                                }
                                            } else {
                                                notes += "برای تولید جلسات، تاریخ پایان کلاس را وارد کنید"
                                            }
                                        }
                                    }

                                    preparing = false
                                    prepSummary = if (notes.isEmpty()) null else notes.joinToString("\n")
                                    if (prepSummary == null) onSaved()
                                }
                            }

                            if (classId != null) {
                                vm.update(
                                    id = classId,
                                    title = title.trim(),
                                    ageGroupId = ageGroupId,
                                    coachId = coachId,
                                    assistantCoachId = assistantCoachId,
                                    capacity = cap,
                                    location = location.takeIf { it.isNotBlank() },
                                    description = description.takeIf { it.isNotBlank() },
                                    pricingType = pricingType,
                                    monthlyFee = mFee,
                                    sessionFee = sFee,
                                    registrationFee = rFee,
                                    startDate = startDateGregorian,
                                    endDate = endDateGregorian,
                                    status = status,
                                    onSuccess = { runPostSave(classId) }
                                )
                            } else {
                                vm.create(
                                    title = title.trim(),
                                    ageGroupId = ageGroupId,
                                    coachId = coachId,
                                    assistantCoachId = assistantCoachId,
                                    capacity = cap,
                                    location = location.takeIf { it.isNotBlank() },
                                    description = description.takeIf { it.isNotBlank() },
                                    pricingType = pricingType,
                                    monthlyFee = mFee,
                                    sessionFee = sFee,
                                    registrationFee = rFee,
                                    startDate = startDateGregorian,
                                    endDate = endDateGregorian,
                                    onSuccess = { newClassId -> runPostSave(newClassId) }
                                )
                            }
                        }
                    },
                    loading = loading,
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // ─── حالت آماده‌سازی: برنامه هفتگی + تولید جلسات + ثبت‌نام ───
    if (preparing) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {},
            title = { Text("در حال آماده‌سازی کلاس") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = GoldPrimary, strokeWidth = 2.dp)
                    Text(
                        "ثبت برنامه هفتگی، تولید جلسات و ثبت‌نام بازیکنان...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            },
            confirmButton = {},
            containerColor = Color(0xFF241040)
        )
    }

    // ─── خلاصه‌ی نتیجه‌ی آماده‌سازی ───
    prepSummary?.let { summary ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { prepSummary = null },
            title = { Text("کلاس ذخیره شد") },
            text = { Text(summary) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    prepSummary = null
                    onSaved()
                }) {
                    Text("باشه", color = GoldPrimary)
                }
            },
            containerColor = Color(0xFF241040)
        )
    }
}
