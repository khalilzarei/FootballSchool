package com.khz.malekadmin.ui.players

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.CreateGuardianForPlayerRequest
import com.khz.malekadmin.data.dto.request.UpdateGuardianRequest
import com.khz.malekadmin.data.dto.response.AttachNewGuardianResponseDto
import com.khz.malekadmin.domain.model.GuardianPlayer
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassDropdown
import com.khz.malekadmin.ui.components.GlassSectionTitle
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

private val RELATIONS = listOf(
    "father" to "پدر",
    "mother" to "مادر",
    "grandfather" to "پدربزرگ",
    "grandmother" to "مادربزرگ",
    "uncle" to "عمو/دایی",
    "aunt" to "عمه/خاله",
    "other" to "سایر"
)

/** اعتبارسنجی ساده موبایل ایران */
private fun isValidMobile(m: String): Boolean = Regex("^09\\d{9}$").matches(m.trim())

/** اعتبارسنجی کد ملی — اختیاری، ولی اگر وارد شود باید ۱۰ رقم باشد */
private fun isValidNationalCode(nc: String): Boolean =
    nc.trim().isBlank() || Regex("^\\d{10}$").matches(nc.trim())

/**
 * مدیریت سرپرستِ (تک‌سرپرستِ) بازیکن:
 * - اگر بازیکن سرپرست داشته باشد: فقط سرپرستِ همین بازیکن نمایش داده می‌شود (نه لیست همه سرپرست‌ها)
 *   همراه با دکمه‌های ویرایش و حذف.
 * - اگر نداشته باشد: فرم ثبت سرپرست (نام + موبایل الزامی + کد ملی اختیاری + نسبت و دسترسی‌ها).
 *   اگر سرپرستی با این موبایل از قبل در سیستم ثبت شده باشد، همان حساب به بازیکن متصل می‌شود.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachGuardianToPlayerScreen(
    playerId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container
    val playerRepo = container.playerRepository
    val scope = rememberCoroutineScope()

    // ─── سرپرست‌های فعلی همین بازیکن ───
    var guardians by remember { mutableStateOf<List<GuardianPlayer>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // ─── فرم ثبت سرپرست (وقتی بازیکن سرپرست ندارد) ───
    var newName by remember { mutableStateOf("") }
    var newMobile by remember { mutableStateOf("") }
    var newNationalCode by remember { mutableStateOf("") }
    var selectedRelation by remember { mutableStateOf("father") }
    var canViewReports by remember { mutableStateOf(true) }
    var canPay by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }

    // ─── دیالوگ ویرایش ───
    var editing by remember { mutableStateOf<GuardianPlayer?>(null) }
    var editName by remember { mutableStateOf("") }
    var editMobile by remember { mutableStateOf("") }
    var editNationalCode by remember { mutableStateOf("") }
    var editRelation by remember { mutableStateOf("father") }
    var editViewReports by remember { mutableStateOf(true) }
    var editCanPay by remember { mutableStateOf(true) }
    var editSaving by remember { mutableStateOf(false) }
    var editError by remember { mutableStateOf<String?>(null) }

    // ─── دیالوگ تأیید حذف ───
    var deleting by remember { mutableStateOf<GuardianPlayer?>(null) }
    var deleteSaving by remember { mutableStateOf(false) }

    // ─── دیالوگ نتیجه ثبت (رمز اولیه یا اتصال سرپرست موجود) ───
    var attachResult by remember { mutableStateOf<AttachNewGuardianResponseDto?>(null) }

    fun resetCreateForm() {
        newName = ""
        newMobile = ""
        newNationalCode = ""
        selectedRelation = "father"
        canViewReports = true
        canPay = true
    }

    fun reload() {
        loading = true
        scope.launch {
            when (val r = playerRepo.getPlayerGuardians(playerId)) {
                is NetworkResult.Success -> guardians = r.data
                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) { reload() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "سرپرست بازیکن",
                onBack = onBack
            )
        }) { padding ->
        if (loading) {
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
                    .verticalScroll(rememberScrollState())
            ) {
                if (guardians.isEmpty()) {
                    // ═══ بازیکن سرپرست ندارد → فرم ثبت ═══
                    GlassSectionTitle("ثبت سرپرست")
                    Spacer(Modifier.height(8.dp))

                    GlassCard3D() {
                        Text(
                            "هر بازیکن فقط یک سرپرست دارد. اگر سرپرستی با این شماره موبایل قبلاً ثبت شده باشد، همان حساب به بازیکن متصل می‌شود.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.55f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    GlassSectionTitle("مشخصات سرپرست")
                    Spacer(Modifier.height(8.dp))

                    GlassCard3D() {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassTextField(
                                value = newName,
                                onValueChange = { newName = it },
                                label = "نام و نام خانوادگی سرپرست *"
                            )
                            GlassTextField(
                                value = newMobile,
                                onValueChange = { newMobile = it },
                                label = "شماره موبایل * (نام کاربری و شماره تماس)",
                                keyboardType = KeyboardType.Phone
                            )
                            GlassTextField(
                                value = newNationalCode,
                                onValueChange = { newNationalCode = it },
                                label = "کد ملی (اختیاری)",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    GlassSectionTitle("نسبت و دسترسی‌ها")
                    Spacer(Modifier.height(8.dp))

                    GlassCard3D() {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassDropdown(
                                label = "نسبت",
                                options = RELATIONS,
                                selectedValue = selectedRelation,
                                onSelect = { selectedRelation = it })
                            CheckboxRow(
                                "دسترسی به گزارش‌ها",
                                canViewReports
                            ) { canViewReports = it }
                            CheckboxRow(
                                "اجازه پرداخت",
                                canPay
                            ) { canPay = it }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    GlassButton(
                        text = "ثبت سرپرست",
                        onClick = {
                            if (newName.trim().length < 3) {
                                error = "نام و نام خانوادگی سرپرست را کامل وارد کنید"; return@GlassButton
                            }
                            if (!isValidMobile(newMobile)) {
                                error = "موبایل باید ۱۱ رقم و با ۰۹ شروع شود (مثلاً 09123456789)"; return@GlassButton
                            }
                            if (!isValidNationalCode(newNationalCode)) {
                                error = "کد ملی باید ۱۰ رقم باشد"; return@GlassButton
                            }
                            saving = true
                            error = null
                            scope.launch {
                                val r = playerRepo.attachNewGuardian(
                                    playerId,
                                    CreateGuardianForPlayerRequest(
                                        fullName = newName.trim(),
                                        mobile = newMobile.trim(),
                                        nationalCode = newNationalCode.trim().takeIf { it.isNotBlank() },
                                        relation = selectedRelation,
                                        isPrimary = true,
                                        canViewReports = canViewReports,
                                        canPay = canPay
                                    )
                                )
                                saving = false
                                when (r) {
                                    is NetworkResult.Success -> {
                                        resetCreateForm()
                                        attachResult = r.data
                                    }
                                    is NetworkResult.Error -> error = r.message
                                    else -> {}
                                }
                            }
                        },
                        loading = saving,
                        enabled = !saving,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // ═══ بازیکن سرپرست دارد → نمایش سرپرستِ همین بازیکن + ویرایش/حذف ═══
                    GlassSectionTitle("سرپرست بازیکن")
                    Spacer(Modifier.height(8.dp))

                    GlassCard3D() {
                        Text(
                            "هر بازیکن فقط یک سرپرست دارد. برای تعویض سرپرست، ابتدا سرپرست فعلی را حذف کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.55f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    guardians.forEach { g ->
                        GuardianCard(
                            guardianPlayer = g,
                            onEdit = {
                                editing = g
                                editName = g.guardian?.user?.fullName ?: ""
                                editMobile = g.guardian?.user?.mobile ?: ""
                                editNationalCode = g.guardian?.user?.nationalCode ?: ""
                                editRelation = if (RELATIONS.any { it.first == g.relation }) g.relation else "other"
                                editViewReports = g.canViewReports
                                editCanPay = g.canPay
                                editError = null
                            },
                            onDelete = { deleting = g }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    GlassCard3D() {
                        Text(
                            it,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
            }
        }
    }

    // ─── دیالوگ ویرایش سرپرست ───
    editing?.let { g ->
        AlertDialog(
            onDismissRequest = { if (!editSaving) { editing = null; editError = null } },
            title = { Text("ویرایش سرپرست") },
            text = {
                Column(
                    Modifier
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = "نام و نام خانوادگی *"
                    )
                    GlassTextField(
                        value = editMobile,
                        onValueChange = { editMobile = it },
                        label = "شماره موبایل *",
                        keyboardType = KeyboardType.Phone
                    )
                    GlassTextField(
                        value = editNationalCode,
                        onValueChange = { editNationalCode = it },
                        label = "کد ملی (اختیاری)",
                        keyboardType = KeyboardType.Number
                    )
                    GlassDropdown(
                        label = "نسبت",
                        options = RELATIONS,
                        selectedValue = editRelation,
                        onSelect = { editRelation = it })
                    CheckboxRow(
                        "دسترسی به گزارش‌ها",
                        editViewReports
                    ) { editViewReports = it }
                    CheckboxRow(
                        "اجازه پرداخت",
                        editCanPay
                    ) { editCanPay = it }
                    Text(
                        "نام کاربری ورود سرپرست = شماره موبایل او",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.45f)
                    )
                    editError?.let {
                        Text(
                            it,
                            color = Color(0xFFFF8A80),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editName.trim().length < 3) {
                        editError = "نام و نام خانوادگی را کامل وارد کنید"; return@TextButton
                    }
                    if (!isValidMobile(editMobile)) {
                        editError = "موبایل باید ۱۱ رقم و با ۰۹ شروع شود (مثلاً 09123456789)"; return@TextButton
                    }
                    if (!isValidNationalCode(editNationalCode)) {
                        editError = "کد ملی باید ۱۰ رقم باشد"; return@TextButton
                    }
                    editSaving = true
                    editError = null
                    scope.launch {
                        val r = playerRepo.updateGuardian(
                            playerId,
                            g.guardianId,
                            UpdateGuardianRequest(
                                fullName = editName.trim(),
                                mobile = editMobile.trim(),
                                nationalCode = editNationalCode.trim().takeIf { it.isNotBlank() },
                                relation = editRelation,
                                canViewReports = editViewReports,
                                canPay = editCanPay
                            )
                        )
                        editSaving = false
                        when (r) {
                            is NetworkResult.Success -> {
                                guardians = guardians.map { if (it.guardianId == g.guardianId) r.data else it }
                                editing = null
                                editError = null
                                Toast.makeText(context, "ذخیره شد", Toast.LENGTH_SHORT).show()
                            }
                            is NetworkResult.Error -> editError = r.message
                            else -> {}
                        }
                    }
                }, enabled = !editSaving) {
                    Text(
                        if (editSaving) "در حال ذخیره…" else "ذخیره",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!editSaving) { editing = null; editError = null } }) {
                    Text("انصراف", color = Color.White.copy(0.7f))
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ─── دیالوگ تأیید حذف ───
    deleting?.let { g ->
        AlertDialog(
            onDismissRequest = { if (!deleteSaving) deleting = null },
            title = { Text("حذف سرپرست") },
            text = {
                Text(
                    "ارتباط «${g.guardianName}» با این بازیکن قطع شود؟\n\n" +
                            "حساب کاربری سرپرست حذف نمی‌شود و پس از حذف می‌توانید سرپرست جدیدی ثبت کنید."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteSaving = true
                    scope.launch {
                        val r = playerRepo.detachGuardian(playerId, g.guardianId)
                        deleteSaving = false
                        when (r) {
                            is NetworkResult.Success -> {
                                deleting = null
                                guardians = guardians.filterNot { it.guardianId == g.guardianId }
                                resetCreateForm()
                                Toast.makeText(context, "سرپرست حذف شد", Toast.LENGTH_SHORT).show()
                            }
                            is NetworkResult.Error -> {
                                deleting = null
                                error = r.message
                            }
                            else -> {}
                        }
                    }
                }, enabled = !deleteSaving) {
                    Text(
                        if (deleteSaving) "…" else "حذف",
                        color = Color(0xFFFF8A80),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!deleteSaving) deleting = null }) {
                    Text("انصراف", color = Color.White.copy(0.7f))
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ─── دیالوگ نتیجه ثبت ───
    attachResult?.let { res ->
        AlertDialog(
            onDismissRequest = {},
            title = { Text(if (res.attachedExisting) "سرپرست متصل شد" else "سرپرست ساخته شد") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (res.attachedExisting) {
                        Text(
                            "سرپرستِ موجود «${res.guardianPlayer?.guardian?.user?.fullName ?: ""}» " +
                                    "با این شماره موبایل به بازیکن متصل شد."
                        )
                    } else {
                        Text(
                            "حساب سرپرست با موفقیت ساخته و به بازیکن متصل شد." +
                                    (res.initialPassword?.takeIf { it.isNotBlank() }?.let {
                                        "\n\nرمز اولیه حساب (به سرپرست اعلام کنید):\n$it"
                                    } ?: "")
                        )
                    }
                    Text(
                        "نام کاربری برای ورود سرپرست: شماره موبایل او",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    attachResult = null
                    reload()
                }) {
                    Text("باشه", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF241040)
        )
    }
}

/** کارت سرپرستِ متصل به بازیکن (نمایش + ویرایش + حذف) */
@Composable
private fun GuardianCard(
    guardianPlayer: GuardianPlayer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val user = guardianPlayer.guardian?.user
    val mobile = user?.mobile

    GlassCard3D() {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarView(
                    name = guardianPlayer.guardianName,
                    avatarUrl = user?.avatarUrl,
                    size = 52.dp,
                    accentColor = GoldPrimary
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            guardianPlayer.guardianName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        RelationChip(guardianPlayer.relationLabel)
                    }
                    if (!mobile.isNullOrBlank()) {
                        Text(
                            mobile,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.75f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (!user?.nationalCode.isNullOrBlank()) {
                        Text(
                            "کد ملی: ${user?.nationalCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.5f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PermissionChip(
                    "مشاهده گزارش‌ها",
                    guardianPlayer.canViewReports
                )
                PermissionChip(
                    "اجازه پرداخت",
                    guardianPlayer.canPay
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassButton(
                    text = "ویرایش",
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                )
                GlassButton(
                    text = "حذف سرپرست",
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    primary = false
                )
            }
        }
    }
}

@Composable
private fun RelationChip(label: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GoldPrimary.copy(alpha = 0.22f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            label,
            color = GoldPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PermissionChip(label: String, enabled: Boolean) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (enabled) Color(0xFF81C784).copy(alpha = 0.16f)
                else Color.White.copy(0.05f)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            (if (enabled) "✓ " else "✕ ") + label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) Color(0xFFA5D6A7) else Color.White.copy(0.4f)
        )
    }
}

@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = GoldPrimary,
                checkmarkColor = Color(0xFF1A0533),
                uncheckedColor = Color.White.copy(0.4f)
            )
        )
        Text(
            label,
            color = Color.White.copy(0.85f)
        )
    }
}