package com.khz.malekadmin.ui.players

import android.net.Uri
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassDropdown
import com.khz.malekadmin.ui.components.GlassTextField3D
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.components.JalaliDateField
import com.khz.malekadmin.ui.theme.GoldPrimary
import com.khz.malekadmin.ui.users.AvatarPicker
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
    val container = (context.applicationContext as MalekAdminApp).container
    val repo = container.playerRepository
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var nationalCode by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    // موبایل اولیه‌ی بازیکن (برای تشخیص پاک‌کردن فیلد در حالت ویرایش)
    var initialMobile by remember { mutableStateOf<String?>(null) }

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
                    mobile = p.mobile
                            ?: ""
                    initialMobile = p.mobile
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
                            label = if (isEditMode) "کد ملی" else "کد ملی (الزامی)",
                            keyboardType = KeyboardType.Number,
                            supportingText = if (isEditMode) {
                                null
                            } else {
                                "شناسه و رمز اولیه‌ی ورود بازیکن همان کد ملی است (در اولین ورود تغییر می‌کند)"
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Key,
                                    null
                                )
                            })

                        GlassTextField3D(
                            value = mobile,
                            onValueChange = { v ->
                                mobile = v.filter { it.isDigit() }
                                    .take(15)
                            },
                            label = "شماره موبایل (اختیاری)",
                            keyboardType = KeyboardType.Phone,
                            supportingText = "برای چت و تماس با بازیکن استفاده می‌شود",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    null
                                )
                            })

                        // تاریخ تولد — انتخاب با تقویم شمسی، ارسال میلادی به سرور
                        JalaliDateField(
                            label = "تاریخ تولد",
                            gregorianValue = birthDateGregorian.takeIf { it.isNotBlank() },
                            onDatePicked = { g ->
                                birthDateGregorian = g
                                ageDisplay = DateUtils.calculateAgeFromGregorian(g)
                            },
                            minYear = 1360,
                            maxYear = DateUtils.gregorianToJalali(java.time.LocalDate.now().toString())
                                .split("/").first().toIntOrNull() ?: 1415
                        )


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
                        if (birthDateGregorian > java.time.LocalDate.now().toString()) {
                            error = "تاریخ تولد نمی‌تواند در آینده باشد"; return@GlassButton
                        }
                        if (nationalCode.isNotBlank() && nationalCode.length != 10) {
                            error = "کد ملی باید ۱۰ رقم باشد"; return@GlassButton
                        }
                        if (!isEditMode && nationalCode.isBlank()) {
                            error = "کد ملی الزامی است (شناسه و رمز اولیه‌ی ورود، همان کد ملی می‌شود)"; return@GlassButton
                        }
                        if (mobile.isNotBlank() && (mobile.length < 10 || mobile.length > 15)) {
                            error = "شماره موبایل معتبر نیست (۱۰ تا ۱۵ رقم)"; return@GlassButton
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
                                    // اگر بازیکن قبلاً موبایل داشته و حالا خالی شده → رشته خالی (پاک شود)
                                    mobile = if (initialMobile != null && mobile.isBlank()) ""
                                    else mobile.takeIf { it.isNotBlank() },
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
                                    mobile = mobile.takeIf { it.isNotBlank() },
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
                                is NetworkResult.Success -> {
                                    // پیام راهنما: رمز اولیه همان کد ملی شد
                                    if (!isEditMode) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "بازیکن ساخته شد — رمز اولیه همان کد ملی است و در اولین ورود باید تغییر کند",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    onSaved()
                                }
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