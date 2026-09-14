package com.khz.footballschool.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.khz.footballschool.core.util.DateUtils.toPersianDigits
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

// ═════════════════════════════════════════════
// انتخاب ساعت به سبک NumberPicker / Spinner
// — دو چرخ اسکرولی (ساعت | دقیقه)
// همان بهینه‌سازی‌های JalaliDatePicker:
//  • فلینگ اسنپ اختصاصی (تخمین فیزیکی ساده — بدون APIهای نسخه‌دار)
//  • فونت ثابت آیتم‌ها (فقط رنگ عوض می‌شود)
//  • خواندن state مرکز فقط داخل WheelItemText
//
// مقدار ورودی/خروجی همیشه "HH:mm" است (همان چیزی که سرور می‌خواهد)
// ═════════════════════════════════════════════

// ثابت زمان اصطکاک فلینگ (ثانیه) — مسافت تخمینی = سرعت × این مقدار
private const val FLING_TIME_CONSTANT = 0.25f

private val HOUR_ITEMS = (0..23).map { toPersianDigits(it.toString().padStart(2, '0')) }
private val MINUTE_ITEMS = (0..11).map { toPersianDigits((it * 5).toString().padStart(2, '0')) }

/**
 * اجزای ساعت از رشته‌ی "HH:mm" — null اگر نامعتبر باشد
 */
internal fun timeParts(value: String?): Pair<Int, Int>? {
    if (value.isNullOrBlank()) return null
    val m = Regex("^(\\d{1,2}):(\\d{2})$").find(value.trim()) ?: return null
    val h = m.groupValues[1].toIntOrNull() ?: return null
    val min = m.groupValues[2].toIntOrNull() ?: return null
    if (h !in 0..23 || min !in 0..59) return null
    return h to min
}

/** گرد کردن دقیقه به نزدیک‌ترین مضرب ۵ (چرخ دقیقه ۵تایی است) */
internal fun snapToStep5(minute: Int): Int =
    ((minute + 2) / 5).let { if (it > 11) 0 else it * 5 }

internal fun formatTime(hour: Int, minute: Int): String =
    hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

/**
 * فیلد انتخاب ساعت — با لمس، دیالوگ اسپینری را باز می‌کند.
 * مقدار value و خروجی onTimePicked هر دو "HH:mm" هستند.
 */
@Composable
fun TimeWheelField(
    label: String,
    value: String?,
    onTimePicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.5f),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(0.08f))
                .clickable { showDialog = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val valid = timeParts(value)
            if (valid != null) {
                Text(
                    toPersianDigits(formatTime(valid.first, valid.second)),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Text(
                    "برای انتخاب ساعت، لمس کنید",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.4f)
                )
            }
        }
    }

    if (showDialog) {
        TimeWheelPickerDialog(
            initialTime = value,
            onDismiss = { showDialog = false },
            onPick = { t ->
                onTimePicked(t)
                showDialog = false
            }
        )
    }
}

/**
 * دیالوگ انتخاب ساعت به سبک NumberPicker (اسپینر):
 * هدر طلایی با ساعت انتخاب‌شده + دو چرخ (ساعت | دقیقه) + اکنون / انصراف / تایید
 * خروجی onPick همیشه "HH:mm" است.
 */
@Composable
fun TimeWheelPickerDialog(
    initialTime: String?,
    onDismiss: () -> Unit,
    onPick: (time: String) -> Unit
) {
    val init = remember { timeParts(initialTime) ?: (17 to 0) }

    var hour by remember { mutableStateOf(init.first) }
    var minute by remember { mutableStateOf(snapToStep5(init.second)) }

    Dialog(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                Modifier
                    .width(328.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF241040))
            ) {

                // ─── هدر: ساعت انتخاب‌شده ───
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(GoldPrimary, GoldPrimary.copy(alpha = 0.72f))
                            )
                        )
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            toPersianDigits(formatTime(hour, minute)),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A0533)
                        )
                        Text(
                            "ساعت انتخاب‌شده",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1A0533).copy(0.75f)
                        )
                    }
                }

                // ─── دو چرخ اسپینری: ساعت | دقیقه ───
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberWheel(
                        label = "دقیقه",
                        items = MINUTE_ITEMS,
                        selectedIndex = minute / 5,
                        onSelected = { minute = it * 5 },
                        modifier = Modifier.weight(1f)
                    )
                    NumberWheel(
                        label = "ساعت",
                        items = HOUR_ITEMS,
                        selectedIndex = hour,
                        onSelected = { hour = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                // ─── پایین: اکنون / انصراف / تایید ───
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        val now = Calendar.getInstance()
                        hour = now.get(Calendar.HOUR_OF_DAY)
                        minute = snapToStep5(now.get(Calendar.MINUTE))
                    }) {
                        Text("اکنون", color = Color.White.copy(0.7f))
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text("انصراف", color = Color.White.copy(0.7f))
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(GoldPrimary)
                            .clickable { onPick(formatTime(hour, minute)) }
                            .padding(horizontal = 22.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "تایید",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A0533)
                        )
                    }
                }
            }
        }
    }
}

/**
 * یک چرخ اسکرولی به سبک NumberPicker با فلینگ اسنپ —
 * همان پیاده‌سازی JalaliDatePicker (کپی مستقل تا آن فایل دست‌نخورده بماند).
 */
@Composable
private fun NumberWheel(
    label: String,
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 40.dp,
    visibleRows: Int = 5
) {
    val density = LocalDensity.current
    val itemPx = with(density) { itemHeight.toPx() }
    // ضرب Dp × Int در همه نسخه‌های Compose تعریف نشده — محاسبه عددی امن
    val edgePad = (itemHeight.value * (visibleRows / 2)).dp
    val wheelHeight = (itemHeight.value * visibleRows).dp

    val scroll = rememberScrollState()
    val fling = rememberSnapScrollFling(scroll, itemPx)

    // مقادیر به‌روز برای استفاده داخل coroutineهای طولانی
    val currentSelected by rememberUpdatedState(selectedIndex)
    val currentOnSelected by rememberUpdatedState(onSelected)

    // آیتم مرکزی — فقط داخل WheelItemText خوانده می‌شود
    val centerIdx: State<Int> = remember(items.size) {
        derivedStateOf {
            if (items.isEmpty()) 0
            else floor((scroll.value + itemPx / 2) / itemPx).toInt().coerceIn(0, items.lastIndex)
        }
    }

    // همگام‌سازی از بیرون (مقدار اولیه، دکمه «اکنون»)
    LaunchedEffect(selectedIndex, items.size) {
        if (items.isEmpty()) return@LaunchedEffect
        if (scroll.isScrollInProgress) return@LaunchedEffect
        val idx = selectedIndex.coerceIn(0, items.lastIndex)
        if (abs(scroll.value - idx * itemPx) > 1f) {
            scroll.scrollTo((idx * itemPx).toInt())
        }
    }

    // گزارش انتخاب پس از سکون کامل (پایان درگ یا فلینگ)
    LaunchedEffect(items.size) {
        snapshotFlow { scroll.isScrollInProgress to centerIdx.value }
            .distinctUntilChanged()
            .collect { (scrolling, idx) ->
                if (!scrolling && items.isNotEmpty() && idx != currentSelected) {
                    currentOnSelected(idx)
                }
            }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = GoldPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(wheelHeight)
        ) {
            // باند مرکزی (پشت آیتم‌ها)
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(itemHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.10f))
            )

            // آیتم‌ها
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll, flingBehavior = fling),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(edgePad))
                items.forEachIndexed { i, text ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(itemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        WheelItemText(text = text, isCenter = { i == centerIdx.value })
                    }
                }
                Spacer(Modifier.height(edgePad))
            }
        }
    }
}

/**
 * متن یک آیتم چرخ — state مرکز فقط اینجا خوانده می‌شود؛ فونت ثابت.
 */
@Composable
private fun WheelItemText(text: String, isCenter: () -> Boolean) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = if (isCenter()) Color.White else Color.White.copy(alpha = 0.40f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

/**
 * فلینگ اسنپ برای ScrollState عمودی — بدون APIهای نسخه‌دار.
 */
@Composable
private fun rememberSnapScrollFling(state: ScrollState, itemPx: Float): FlingBehavior {
    return remember(state, itemPx) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                // state.value (Int) — به‌جای value خودِ ScrollScope که در همه نسخه‌ها موجود نیست
                val projected = state.value + initialVelocity * FLING_TIME_CONSTANT
                val target = (round(projected / itemPx) * itemPx).roundToInt()
                    .coerceIn(0, state.maxValue)
                if (target != state.value) {
                    state.animateScrollTo(target)
                }
                return 0f
            }
        }
    }
}
