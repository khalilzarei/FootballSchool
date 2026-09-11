package com.khz.footballschool.ui.players

import android.graphics.Color
import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassDropdown
import com.khz.footballschool.ui.components.GlassTextField3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import com.khz.footballschool.ui.users.AvatarPicker
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import kotlinx.coroutines.launch

private val GENDERS = listOf(
    "male" to "پسر",
    "female" to "دختر"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerFormScreen(
    playerId: Int? = null,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val isEditMode = playerId != null
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.playerRepository
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var nationalCode by remember { mutableStateOf("") }

    var birthDateJalali by remember { mutableStateOf("") }
    var birthDateGregorian by remember { mutableStateOf("") }
    var ageDisplay by remember { mutableStateOf("") }

    var gender by remember { mutableStateOf("male") }
    var notes by remember { mutableStateOf("") }

    var currentAvatarUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var loading by remember { mutableStateOf(false) }
    var initialLoading by remember { mutableStateOf(isEditMode) }
    var error by remember { mutableStateOf<String?>(null) }
    var avatarUploading by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val picker = PersianDatePickerDialog(context).setPositiveButtonString("باشه")
        .setTodayButton("امروز")
        .setTodayButtonVisible(true)
        .setMinYear(1300)
        .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
        .setMaxMonth(PersianDatePickerDialog.THIS_MONTH)
        .setMaxDay(PersianDatePickerDialog.THIS_DAY)
        .setInitDate(
            1370,
            3,
            13
        ) // می‌توانید این را داینامیک کنید
        .setActionTextColor(Color.GRAY) // از android.graphics.Color استفاده می‌شود
        // .setTypeFace(typeface) // اگر فونت خاصی دارید، اینجا ست کنید
        .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
        .setShowInBottomSheet(true)
        .setListener(object : PersianPickerListener {
            override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                val year = persianPickerDate.persianYear
                val month = persianPickerDate.persianMonth
                val day = persianPickerDate.persianDay

                Log.d(
                    "DatePicker",
                    "Year: $year, Month: $month, Day: $day"
                )
                Log.d(
                    "DatePicker",
                    "Gregorian: ${persianPickerDate.gregorianDate}"
                )
                Log.d(
                    "DatePicker",
                    "Persian Long: ${persianPickerDate.persianLongDate}"
                )

                // ذخیره تاریخ شمسی برای نمایش
                birthDateJalali = "$year/${
                    String.format(
                        "%02d",
                        month
                    )
                }/${
                    String.format(
                        "%02d",
                        day
                    )
                }"

                // تبدیل به میلادی برای ارسال به سرور
                birthDateGregorian = DateUtils.jalaliToGregorian(
                    year,
                    month,
                    day
                )

                // محاسبه و نمایش سن
                ageDisplay = DateUtils.calculateAgeFromGregorian(birthDateGregorian)

                showDatePicker = false
            }

            override fun onDismissed() {
                showDatePicker = false
            }
        })

    // ═══════════════════════════════════════════════════════
    // مدیریت نمایش دیالوگ تاریخ
    // ═══════════════════════════════════════════════════════
    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            val picker = PersianDatePickerDialog(context)
                .setPositiveButtonString("باشه")
//                .setNegativeButtonString("بیخیال") // ⚠️ نکته مهم: در نسخه‌های جدید "String" دارد
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setMaxMonth(PersianDatePickerDialog.THIS_MONTH)
                .setMaxDay(PersianDatePickerDialog.THIS_DAY)
                .setInitDate(1370, 3, 13)
                .setActionTextColor(android.graphics.Color.GRAY) // استفاده از Color اندروید
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(object : PersianPickerListener {
                    override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                        val y = persianPickerDate.persianYear
                        val m = persianPickerDate.persianMonth
                        val d = persianPickerDate.persianDay

                        Log.d("DatePicker", "Selected: $y/$m/$d")

                        birthDateJalali = "$y/${String.format("%02d", m)}/${String.format("%02d", d)}"
                        birthDateGregorian = DateUtils.jalaliToGregorian(y, m, d)
                        ageDisplay = DateUtils.calculateAgeFromGregorian(birthDateGregorian)

                        showDatePicker = false
                    }

                    override fun onDismissed() {
                        showDatePicker = false
                    }
                })

            // نمایش دیالوگ
            try {
                picker.show()
            } catch (e: Exception) {
                android.util.Log.e("DatePicker", "Error showing dialog: ${e.message}")
            }
        }
    }
    // بارگذاری اطلاعات در حالت ویرایش
    LaunchedEffect(playerId) {
        if (playerId != null) {
            when (val r = repo.getPlayer(playerId)) {
                is NetworkResult.Success -> {
                    val p = r.data
                    firstName = p.firstName
                    lastName = p.lastName
                    nationalCode = p.nationalCode
                            ?: ""
                    birthDateGregorian = p.birthDate
                            ?: ""
                    gender = p.gender
                            ?: "male"
                    notes = p.notes
                            ?: ""
                    currentAvatarUrl = p.avatarPath

                    if (!birthDateGregorian.isNullOrBlank()) {
                        ageDisplay = DateUtils.calculateAgeFromGregorian(birthDateGregorian)
                        // نکته: اگر بخواهید تاریخ شمسی را هم در حالت ویرایش نمایش دهید، 
                        // باید یک تابع gregorianToJalali هم در DateUtils بسازید.
                        // در اینجا برای سادگی، فیلد خالی می‌ماند یا می‌توانید مقدار پیش‌فرض بگذارید.
                    }
                }

                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            initialLoading = false
        }
    }



    Scaffold(
        containerColor = ComposeColor.Transparent,
        topBar = {
            GlassTopBar(
                title = if (isEditMode) "ویرایش بازیکن" else "افزودن بازیکن",
                onBack = onBack
            )
        }) { padding ->
        if (initialLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
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
                // ─── بخش آواتار ───
                AvatarPicker(
                    displayName = "$firstName $lastName".trim()
                        .ifBlank { "بازیکن جدید" },
                    currentAvatarUrl = currentAvatarUrl,
                    selectedImageUri = selectedImageUri,
                    isUploading = avatarUploading,
                    isEditMode = isEditMode,
                    onImageSelected = { uri -> selectedImageUri = uri },
                    onRemoveImage = {
                        selectedImageUri = null
                        if (isEditMode && currentAvatarUrl != null) {
                            scope.launch {
                                avatarUploading = true
                                repo.deleteAvatar(playerId!!)
                                currentAvatarUrl = null
                                avatarUploading = false
                            }
                        }
                    })

                // ─── فرم اطلاعات ───
                GlassCard3D {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            GlassTextField3D(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "نام",
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        null
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            GlassTextField3D(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "نام خانوادگی",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        GlassTextField3D(
                            value = nationalCode,
                            onValueChange = { v ->
                                nationalCode = v.filter { it.isDigit() }
                                    .take(10)
                            },
                            label = "کد ملی",
                            keyboardType = KeyboardType.Number,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Key,
                                    null
                                )
                            })

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Log.d(
                                        "DatePicker",
                                        "Click registered! Opening dialog..."
                                    )
                                    showDatePicker = true
                                    picker.show()
                                }) {
                            GlassTextField3D(
                                value = birthDateJalali.ifBlank { "انتخاب تاریخ تولد" },
                                onValueChange = { }, // جلوگیری از تایپ دستی
                                label = "تاریخ تولد",
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        null
                                    )
                                },
                                enabled = false // باید true باشد تا ظاهر آن کمرنگ (Disabled) نشود
                            )
                        }


                        // ─── نمایش سن محاسبه‌شده ───
                        if (ageDisplay.isNotBlank() && ageDisplay != "-") {
                            Text(
                                "سن فعلی: $ageDisplay",
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 4.dp
                                )
                            )
                        }

                        GlassDropdown(
                            label = "جنسیت",
                            options = GENDERS,
                            selectedValue = gender,
                            onSelect = { gender = it })

                        GlassTextField3D(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "یادداشت (اختیاری)",
                            singleLine = false
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                GlassButton(
                    text = if (isEditMode) "ذخیره تغییرات" else "ثبت بازیکن",
                    onClick = {
                        if (firstName.isBlank()) {
                            error = "نام الزامی است"; return@GlassButton
                        }
                        if (lastName.isBlank()) {
                            error = "نام خانوادگی الزامی است"; return@GlassButton
                        }
                        if (birthDateGregorian.isBlank()) {
                            error = "تاریخ تولد الزامی است"; return@GlassButton
                        }

                        loading = true
                        error = null

                        scope.launch {
                            val result = if (isEditMode) {
                                repo.updatePlayer(
                                    id = playerId!!,
                                    firstName = firstName,
                                    lastName = lastName,
                                    nationalCode = nationalCode.takeIf { it.isNotBlank() },
                                    birthDate = birthDateGregorian, // ارسال میلادی به سرور
                                    gender = gender,
                                    status = "active",
                                    notes = notes.takeIf { it.isNotBlank() },
                                    avatarUri = selectedImageUri,
                                    context = context
                                )
                            } else {
                                repo.createPlayer(
                                    firstName = firstName,
                                    lastName = lastName,
                                    nationalCode = nationalCode.takeIf { it.isNotBlank() },
                                    birthDate = birthDateGregorian, // ارسال میلادی به سرور
                                    gender = gender,
                                    status = "active",
                                    notes = notes.takeIf { it.isNotBlank() },
                                    avatarUri = selectedImageUri,
                                    context = context
                                )
                            }

                            loading = false
                            when (result) {
                                is NetworkResult.Success -> onSaved()
                                is NetworkResult.Error   -> error = result.message
                                else                     -> {}
                            }
                        }
                    },
                    loading = loading,
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    GlassCard3D(glowColor = ComposeColor(0xFFA50044)) {
                        Text(
                            it,
                            color = ComposeColor(0xFFFF8A80),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}