package com.khz.footballschool.ui.classes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TextFields
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
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassDropdown
import com.khz.footballschool.ui.components.GlassTextField3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener

private val STATUSES = listOf(
    "active" to "فعال",
    "inactive" to "غیرفعال"
)

private val PRICING_TYPES = listOf(
    "" to "بدون قیمت‌گذاری",
    "monthly" to "ماهانه",
    "session" to "جلسه‌ای",
    "registration" to "ثبت‌نام"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassFormScreen(
    classId: Int? = null,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val isEditMode = classId != null
    val context = LocalContext.current
    val vm: ClassFormViewModel = appViewModel()

    val loading by vm.loading.collectAsState()
    val initialLoading by vm.initialLoading.collectAsState()
//    var error by vm.error.collectAsState()
    val cls by vm.cls.collectAsState()

    // ─── State های فرم ───
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var ageGroupId by remember { mutableStateOf("") }
    var coachId by remember { mutableStateOf("") }
    var assistantCoachId by remember { mutableStateOf("") }
    var pricingType by remember { mutableStateOf("") }
    var monthlyFee by remember { mutableStateOf("") }
    var sessionFee by remember { mutableStateOf("") }
    var registrationFee by remember { mutableStateOf("") }
    var startDateJalali by remember { mutableStateOf("") }
    var startDateGregorian by remember { mutableStateOf("") }
    var endDateJalali by remember { mutableStateOf("") }
    var endDateGregorian by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("active") }

    // ─── State های تقویم ───
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

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
            ageGroupId = it.ageGroupId?.toString() ?: ""
            coachId = it.coachId?.toString() ?: ""
            assistantCoachId = it.assistantCoachId?.toString() ?: ""
            pricingType = it.pricingType ?: ""
            monthlyFee = it.monthlyFee?.toString() ?: ""
            sessionFee = it.sessionFee?.toString() ?: ""
            registrationFee = it.registrationFee?.toString() ?: ""
            startDateGregorian = it.startDate ?: ""
            endDateGregorian = it.endDate ?: ""
            status = it.status
        }
    }

    // ─── دیالوگ انتخاب تاریخ شروع ───
    if (showStartDatePicker) {
        val picker = PersianDatePickerDialog(context)
            .setPositiveButtonString("باشه")
            .setTodayButton("امروز")
            .setTodayButtonVisible(true)
            .setMinYear(1400)
            .setMaxYear(PersianDatePickerDialog.THIS_YEAR + 5)
            .setInitDate(1403, 1, 1)
            .setActionTextColor(android.graphics.Color.GRAY)
            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                    val y = persianPickerDate.persianYear
                    val m = persianPickerDate.persianMonth
                    val d = persianPickerDate.persianDay
                    startDateJalali = "$y/${String.format("%02d", m)}/${String.format("%02d", d)}"
                    startDateGregorian = DateUtils.jalaliToGregorian(y, m, d)
                    showStartDatePicker = false
                }
                override fun onDismissed() {
                    showStartDatePicker = false
                }
            })
        picker.show()
    }

    // ─── دیالوگ انتخاب تاریخ پایان ───
    if (showEndDatePicker) {
        val picker = PersianDatePickerDialog(context)
            .setPositiveButtonString("باشه")
            .setTodayButton("امروز")
            .setTodayButtonVisible(true)
            .setMinYear(1400)
            .setMaxYear(PersianDatePickerDialog.THIS_YEAR + 5)
            .setInitDate(1403, 1, 1)
            .setActionTextColor(android.graphics.Color.GRAY)
            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                    val y = persianPickerDate.persianYear
                    val m = persianPickerDate.persianMonth
                    val d = persianPickerDate.persianDay
                    endDateJalali = "$y/${String.format("%02d", m)}/${String.format("%02d", d)}"
                    endDateGregorian = DateUtils.jalaliToGregorian(y, m, d)
                    showEndDatePicker = false
                }
                override fun onDismissed() {
                    showEndDatePicker = false
                }
            })
        picker.show()
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
                GlassCard3D {
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
                            leadingIcon = { Icon(Icons.Default.School, null) }
                        )

                        // توضیحات
                        GlassTextField3D(
                            value = description,
                            onValueChange = { description = it },
                            label = "توضیحات (اختیاری)",
                            leadingIcon = { Icon(Icons.Default.TextFields, null) },
                            singleLine = false
                        )

                        // محل برگزاری
                        GlassTextField3D(
                            value = location,
                            onValueChange = { location = it },
                            label = "محل برگزاری (اختیاری)",
                            leadingIcon = { Icon(Icons.Default.LocationOn, null) }
                        )

                        // ظرفیت
                        GlassTextField3D(
                            value = capacity,
                            onValueChange = { v -> capacity = v.filter { it.isDigit() }.take(3) },
                            label = "ظرفیت (اختیاری)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Groups, null) }
                        )
                    }
                }

                // ═════════════════════════════════════════
                // مربیان و گروه سنی
                // ═════════════════════════════════════════
                GlassCard3D {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "مربیان و گروه سنی",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // گروه سنی ID
                        GlassTextField3D(
                            value = ageGroupId,
                            onValueChange = { v -> ageGroupId = v.filter { it.isDigit() }.take(5) },
                            label = "شناسه گروه سنی (اختیاری)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Groups, null) }
                        )

                        // مربی ID
                        GlassTextField3D(
                            value = coachId,
                            onValueChange = { v -> coachId = v.filter { it.isDigit() }.take(5) },
                            label = "شناسه مربی (اختیاری)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Person, null) }
                        )

                        // مربی کمکی ID
                        GlassTextField3D(
                            value = assistantCoachId,
                            onValueChange = { v -> assistantCoachId = v.filter { it.isDigit() }.take(5) },
                            label = "شناسه مربی کمکی (اختیاری)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Person, null) }
                        )
                    }
                }

                // ═════════════════════════════════════════
                // قیمت‌گذاری
                // ═════════════════════════════════════════
                GlassCard3D {
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

                        // نوع قیمت‌گذاری
                        GlassDropdown(
                            label = "نوع قیمت‌گذاری",
                            options = PRICING_TYPES,
                            selectedValue = pricingType,
                            onSelect = { pricingType = it }
                        )

                        // شهریه ماهانه
                        GlassTextField3D(
                            value = monthlyFee,
                            onValueChange = { v -> monthlyFee = v.filter { it.isDigit() } },
                            label = "شهریه ماهانه (ریال)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Money, null) }
                        )

                        // هزینه هر جلسه
                        GlassTextField3D(
                            value = sessionFee,
                            onValueChange = { v -> sessionFee = v.filter { it.isDigit() } },
                            label = "هزینه هر جلسه (ریال)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Money, null) }
                        )

                        // هزینه ثبت‌نام
                        GlassTextField3D(
                            value = registrationFee,
                            onValueChange = { v -> registrationFee = v.filter { it.isDigit() } },
                            label = "هزینه ثبت‌نام (ریال)",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = { Icon(Icons.Default.Money, null) }
                        )
                    }
                }

                // ═════════════════════════════════════════
                // تاریخ‌ها
                // ═════════════════════════════════════════
                GlassCard3D {
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

                        // تاریخ شروع
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStartDatePicker = true }
                        ) {
                            GlassTextField3D(
                                value = startDateJalali.ifBlank { "انتخاب تاریخ شروع" },
                                onValueChange = { },
                                label = "تاریخ شروع (اختیاری)",
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                                enabled = true
                            )
                        }

                        // تاریخ پایان
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showEndDatePicker = true }
                        ) {
                            GlassTextField3D(
                                value = endDateJalali.ifBlank { "انتخاب تاریخ پایان" },
                                onValueChange = { },
                                label = "تاریخ پایان (اختیاری)",
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                                enabled = true
                            )
                        }
                    }
                }

                // ═════════════════════════════════════════
                // وضعیت (فقط در حالت ویرایش)
                // ═════════════════════════════════════════
                if (isEditMode) {
                    GlassCard3D {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GlassDropdown(
                                label = "وضعیت",
                                options = STATUSES,
                                selectedValue = status,
                                onSelect = { status = it }
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
                        if (title.isBlank()) {
//                            error = "عنوان کلاس الزامی است"
                            return@GlassButton
                        }

                        val cap = capacity.toIntOrNull()
                        val ageGrp = ageGroupId.toIntOrNull()
                        val coach = coachId.toIntOrNull()
                        val assistantCoach = assistantCoachId.toIntOrNull()
                        val mFee = monthlyFee.toLongOrNull()
                        val sFee = sessionFee.toLongOrNull()
                        val rFee = registrationFee.toLongOrNull()

                        if (isEditMode) {
                            vm.update(
                                id = classId,
                                title = title,
                                ageGroupId = ageGrp,
                                coachId = coach,
                                assistantCoachId = assistantCoach,
                                capacity = cap,
                                location = location.takeIf { it.isNotBlank() },
                                description = description.takeIf { it.isNotBlank() },
                                pricingType = pricingType.takeIf { it.isNotBlank() },
                                monthlyFee = mFee,
                                sessionFee = sFee,
                                registrationFee = rFee,
                                startDate = startDateGregorian.takeIf { it.isNotBlank() },
                                endDate = endDateGregorian.takeIf { it.isNotBlank() },
                                status = status,
                                onSuccess = onSaved
                            )
                        } else {
                            vm.create(
                                title = title,
                                ageGroupId = ageGrp,
                                coachId = coach,
                                assistantCoachId = assistantCoach,
                                capacity = cap,
                                location = location.takeIf { it.isNotBlank() },
                                description = description.takeIf { it.isNotBlank() },
                                pricingType = pricingType.takeIf { it.isNotBlank() },
                                monthlyFee = mFee,
                                sessionFee = sFee,
                                registrationFee = rFee,
                                startDate = startDateGregorian.takeIf { it.isNotBlank() },
                                endDate = endDateGregorian.takeIf { it.isNotBlank() },
                                onSuccess = onSaved
                            )
                        }
                    },
                    loading = loading,
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                )

                // ═════════════════════════════════════════
                // نمایش خطا
                // ═════════════════════════════════════════
//                error?.let {
//                    GlassCard3D(glowColor = Color(0xFFA50044)) {
//                        Text(it, color = Color(0xFFFF8A80), modifier = Modifier.padding(12.dp))
//                    }
//                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}