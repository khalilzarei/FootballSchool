package com.khz.malekadmin.ui.users

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassDropdown
import com.khz.malekadmin.ui.components.GlassTextField3D
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

// ─── ثوابت ───
private val ROLES = listOf(
    "admin" to "مدیر",
    "coach" to "مربی"
)

private val STATUSES = listOf(
    "active" to "فعال",
    "inactive" to "غیرفعال"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    userId: Int? = null,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val isEditMode = userId != null
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.userRepository
    val scope = rememberCoroutineScope()

    // ─── State های فرم ───
    var fullName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var nationalCode by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("coach") }
    var status by remember { mutableStateOf("active") }

    // ─── State های آواتار ───
    var currentAvatarUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var avatarUploading by remember { mutableStateOf(false) }

    // ─── State های کلی ───
    var loading by remember { mutableStateOf(false) }
    var initialLoading by remember { mutableStateOf(isEditMode) }
    var error by remember { mutableStateOf<String?>(null) }

    // ─── بارگذاری اطلاعات کاربر در حالت ویرایش ───
    LaunchedEffect(userId) {
        if (userId != null) {
            when (val r = repo.getUser(userId)) {
                is NetworkResult.Success -> {
                    val user = r.data
                    fullName = user.fullName
                    mobile = user.mobile
                            ?: ""
                    nationalCode = user.nationalCode
                            ?: ""
                    selectedRole = user.role
                    status = user.status
                    currentAvatarUrl = user.avatarUrl
                }

                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            initialLoading = false
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = if (isEditMode) "ویرایش کاربر" else "افزودن کاربر",
                onBack = onBack
            )
        }) { padding ->
        if (initialLoading) {
            // ─── لودینگ اولیه (فقط در حالت ویرایش) ───
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

                // ═════════════════════════════════════════
                // بخش آواتار
                // ═════════════════════════════════════════
                AvatarPicker(
                    displayName = fullName.ifBlank { "کاربر جدید" },
                    currentAvatarUrl = currentAvatarUrl,
                    selectedImageUri = selectedImageUri,
                    isUploading = avatarUploading,
                    isEditMode = isEditMode,
                    onImageSelected = { uri -> selectedImageUri = uri },
                    onRemoveImage = {
                        selectedImageUri = null
                        // اگر در حالت ویرایش آواتار فعلی وجود دارد، از سرور حذف کن
                        if (isEditMode && currentAvatarUrl != null) {
                            scope.launch {
                                avatarUploading = true
                                when (repo.deleteAvatar(userId!!)) {
                                    is NetworkResult.Success -> currentAvatarUrl = null
                                    is NetworkResult.Error   -> { /* خطا نادیده گرفته می‌شود */
                                    }

                                    else                     -> {}
                                }
                                avatarUploading = false
                            }
                        }
                    })

                // ═════════════════════════════════════════
                // بخش اطلاعات کاربر
                // ═════════════════════════════════════════
                GlassCard3D() {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ─── نقش (فقط در حالت ایجاد قابل تغییر) ───
                        GlassDropdown(
                            label = "نقش",
                            options = ROLES,
                            selectedValue = selectedRole,
                            onSelect = { selectedRole = it },
                            enabled = !isEditMode
                        )

                        // ─── نام و نام خانوادگی ───
                        GlassTextField3D(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "نام و نام خانوادگی",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    null
                                )
                            })

                        // ─── شماره موبایل ───
                        GlassTextField3D(
                            value = mobile,
                            onValueChange = { v ->
                                mobile = v.filter { it.isDigit() }
                                    .take(11)
                            },
                            label = "شماره موبایل",
                            keyboardType = KeyboardType.Phone,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    null
                                )
                            })

                        // ─── کد ملی ───
                        GlassTextField3D(
                            value = nationalCode,
                            onValueChange = { v ->
                                nationalCode = v.filter { it.isDigit() }
                                    .take(10)
                            },
                            label = if (!isEditMode) "کد ملی (الزامی)" else "کد ملی",
                            supportingText = if (!isEditMode) {
                                "شناسه و رمز اولیه‌ی ورود، همان کد ملی می‌شود (در اولین ورود تغییر می‌کند)"
                            } else null,
                            keyboardType = KeyboardType.Number,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Key,
                                    null
                                )
                            })

                        // ─── وضعیت (فقط در حالت ویرایش) ───
                        if (isEditMode) {
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
                // دکمه ثبت / ذخیره
                // ═════════════════════════════════════════
                GlassButton(
                    text = if (isEditMode) "ذخیره تغییرات" else "ثبت کاربر",
                    onClick = {
                        // ─── اعتبارسنجی ───
                        if (fullName.isBlank()) {
                            error = "نام و نام خانوادگی الزامی است"
                            return@GlassButton
                        }
                        if (mobile.isBlank()) {
                            error = "شماره موبایل الزامی است"
                            return@GlassButton
                        }
                        if (mobile.length != 11) {
                            error = "شماره موبایل باید ۱۱ رقم باشد"
                            return@GlassButton
                        }
                        if (nationalCode.isNotBlank() && nationalCode.length != 10) {
                            error = "کد ملی باید ۱۰ رقم باشد"
                            return@GlassButton
                        }
                        if (!isEditMode && nationalCode.isBlank()) {
                            error = "کد ملی الزامی است (شناسه و رمز اولیه‌ی ورود، همان کد ملی می‌شود)"
                            return@GlassButton
                        }

                        loading = true
                        error = null
                        // ─── لاگ قبل از ارسال ───
                        android.util.Log.d(
                            "UserForm",
                            "=== Submitting Form ==="
                        )
                        android.util.Log.d(
                            "UserForm",
                            "fullName: '$fullName'"
                        )
                        android.util.Log.d(
                            "UserForm",
                            "mobile: '$mobile'"
                        )
                        android.util.Log.d(
                            "UserForm",
                            "nationalCode: '$nationalCode'"
                        )
                        android.util.Log.d(
                            "UserForm",
                            "selectedRole: '$selectedRole'"
                        )
                        android.util.Log.d(
                            "UserForm",
                            "selectedImageUri: $selectedImageUri"
                        )

                        scope.launch {
                            val result: NetworkResult<*> = if (isEditMode) {
                                // ─── ویرایش کاربر ───
                                repo.updateUser(
                                    id = userId,
                                    fullName = fullName,
                                    mobile = mobile,
                                    nationalCode = nationalCode.takeIf { it.isNotBlank() },
                                    status = status,
                                    avatarUri = selectedImageUri,
                                    context = context
                                )
                            } else {
                                // ─── ایجاد کاربر ───
                                // رمز عبور دیگر از فرم نمی‌آید؛
                                // سرور آن را از کد ملی می‌سازد
                                // (must_change_password = 1)
                                repo.createUser(
                                    fullName = fullName,
                                    mobile = mobile,
                                    nationalCode = nationalCode.takeIf { it.isNotBlank() },
                                    role = selectedRole,
                                    password = "",
                                    status = "active",
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
                                            "کاربر ساخته شد — رمز اولیه همان کد ملی است و در اولین ورود باید تغییر کند",
                                            android.widget.Toast.LENGTH_LONG
                                        )
                                            .show()
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

                // ═════════════════════════════════════════
                // نمایش خطا
                // ═════════════════════════════════════════
                error?.let { errorMessage ->
                    GlassCard3D(glowColor = Color(0xFFA50044),) {
                        Text(
                            errorMessage,
                            color = Color(0xFFFF8A80),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}