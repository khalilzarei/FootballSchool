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
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.DateUtils.toPersianDigits
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

// ═════════════════════════════════════════════
// انتخاب تاریخ شمسی به سبک NumberPicker / Spinner
// — سه چرخ اسکرولی (روز | ماه | سال)
// بهینه‌سازی روانی:
//  • فلینگ اسنپ اختصاصی: یک حرکت پیوسته تا آیتم هدف
//  • اندازه فونت ثابت برای همه آیتم‌ها (فقط رنگ عوض می‌شود)
//  • خواندن state مرکز فقط داخل WheelItemText
//
// انتخاب با تقویم فارسی، نمایش هر دو تاریخ، ارسال میلادی (YYYY-MM-DD) به سرور
// ═════════════════════════════════════════════

private val JALALI_MONTHS = listOf(
    "فروردین",
    "اردیبهشت",
    "خرداد",
    "تیر",
    "مرداد",
    "شهریور",
    "مهر",
    "آبان",
    "آذر",
    "دی",
    "بهمن",
    "اسفند"
)

private const val MIN_YEAR = 1360
private const val MAX_YEAR = 1415

// ثابت زمان اصطکاک فلینگ (ثانیه) — مسافت تخمینی = سرعت × این مقدار
private const val FLING_TIME_CONSTANT = 0.25f

/**
 * اجزای شمسی (سال، ماه، روز) از تاریخ میلادی YYYY-MM-DD
 */
internal fun jalaliPartsFromGregorian(gregorian: String?): Triple<Int, Int, Int>? {
    if (gregorian.isNullOrBlank()) return null
    val j = DateUtils.gregorianToJalali(gregorian) // "1405/06/21"
    val p = j.split("/")
    if (p.size != 3) return null
    val y = p[0].toIntOrNull()
            ?: return null
    val m = p[1].toIntOrNull()
            ?: return null
    val d = p[2].toIntOrNull()
            ?: return null
    if (m !in 1..12 || d !in 1..31) return null
    return Triple(
        y,
        m,
        d
    )
}

/**
 * تعداد روزهای ماه شمسی
 * (اسفند سال کبیسه ۳۰ روزه است — همان قاعده‌ی کبیسه‌ی DateUtils)
 */
internal fun jalaliMonthLength(
    jy: Int,
    jm: Int
): Int {
    if (jm in 1..6) return 31
    if (jm in 7..11) return 30
    val r = (jy - 979) % 33
    val isLeap = r in intArrayOf(
        0,
        4,
        8,
        12,
        16,
        20,
        24,
        28
    )
    return if (isLeap) 30 else 29
}

/**
 * فیلد انتخاب تاریخ شمسی — با لمس، دیالوگ اسپینری را باز می‌کند.
 * تاریخ شمسی (درشت) + میلادی (کوچک) را کنار هم نمایش می‌دهد و
 * تاریخ میلادی YYYY-MM-DD را برمی‌گرداند (همان چیزی که سرور می‌خواهد).
 */
@Composable
fun JalaliDateField(
    label: String,
    gregorianValue: String?,
    onDatePicked: (gregorian: String) -> Unit,
    modifier: Modifier = Modifier,
    minYear: Int = MIN_YEAR,
    maxYear: Int = MAX_YEAR
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.5f),
            modifier = Modifier.padding(
                start = 4.dp,
                bottom = 4.dp
            )
        )
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(0.08f))
                .clickable { showDialog = true }
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                )) {
            val valid = gregorianValue != null && Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(gregorianValue)
            if (valid) {
                Column {
                    Text(
                        "${toPersianDigits(DateUtils.gregorianToJalali(gregorianValue!!))} شمسی",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "$gregorianValue میلادی",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                }
            } else {
                Text(
                    "برای انتخاب تاریخ، لمس کنید",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.4f)
                )
            }
        }
    }

    if (showDialog) {
        JalaliDatePickerDialog(
            initialGregorian = gregorianValue,
            onDismiss = { showDialog = false },
            onPick = { gregorian ->
                onDatePicked(gregorian)
                showDialog = false
            },
            minYear = minYear,
            maxYear = maxYear
        )
    }
}

/**
 * دیالوگ انتخاب تاریخ شمسی به سبک NumberPicker (اسپینر):
 * هدر طلایی با هر دو تاریخ + سه چرخ (روز | ماه | سال) + تایید
 * خروجی onPick همیشه میلادی (YYYY-MM-DD) است.
 */
@Composable
fun JalaliDatePickerDialog(
    initialGregorian: String?,
    onDismiss: () -> Unit,
    onPick: (gregorian: String) -> Unit,
    minYear: Int = MIN_YEAR,
    maxYear: Int = MAX_YEAR
) {
    val todayParts = remember {
        jalaliPartsFromGregorian(
            LocalDate.now()
                .toString()
        )
    }
    val init = remember {
        (jalaliPartsFromGregorian(initialGregorian)
                ?: todayParts
                ?: Triple(
                    1400,
                    1,
                    1
                )).let { (y, m, d) ->
            Triple(
                y.coerceIn(
                    minYear,
                    maxYear
                ),
                m,
                d
            )
        }
    }

    var jy by remember { mutableStateOf(init.first) }
    var jm by remember { mutableStateOf(init.second) }
    var jd by remember { mutableStateOf(init.third) }

    val monthLen = jalaliMonthLength(
        jy,
        jm
    )
    val safeDay = minOf(
        jd,
        monthLen
    )

    val dayItems = remember(
        jy,
        jm
    ) {
        (1..jalaliMonthLength(
            jy,
            jm
        )).map { toPersianDigits(it.toString()) }
    }
    val yearItems = remember(
        minYear,
        maxYear
    ) {
        (minYear..maxYear).map { toPersianDigits(it.toString()) }
    }

    val selectedGregorian = DateUtils.jalaliToGregorian(
        jy,
        jm,
        safeDay
    )

    Dialog(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                Modifier
                    .width(328.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF241040))
            ) {

                // ─── هدر: تاریخ انتخاب‌شده ───
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    GoldPrimary,
                                    GoldPrimary.copy(alpha = 0.72f)
                                )
                            )
                        )
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${toPersianDigits(safeDay.toString())} ${JALALI_MONTHS[jm - 1]} ${toPersianDigits(jy.toString())}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A0533)
                        )
                        Text(
                            "$selectedGregorian میلادی",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1A0533).copy(0.75f)
                        )
                    }
                }

                // ─── سه چرخ اسپینری: روز | ماه | سال ───
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 10.dp,
                            vertical = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberWheel(
                        label = "روز",
                        items = dayItems,
                        selectedIndex = safeDay - 1,
                        onSelected = { jd = it + 1 },
                        modifier = Modifier.weight(1f)
                    )
                    NumberWheel(
                        label = "ماه",
                        items = JALALI_MONTHS,
                        selectedIndex = jm - 1,
                        onSelected = { i ->
                            jm = i + 1
                            if (jd > jalaliMonthLength(
                                        jy,
                                        jm
                                    )
                            ) jd = jalaliMonthLength(
                                jy,
                                jm
                            )
                        },
                        modifier = Modifier.weight(1.4f)
                    )
                    NumberWheel(
                        label = "سال",
                        items = yearItems,
                        selectedIndex = jy - minYear,
                        onSelected = { jy = minYear + it },
                        modifier = Modifier.weight(1.1f)
                    )
                }

                // ─── پایین: امروز / انصراف / تایید ───
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    todayParts?.let {
                        TextButton(onClick = {
                            jy = it.first; jm = it.second; jd = it.third
                        }) {
                            Text(
                                "امروز",
                                color = Color.White.copy(0.7f)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(
                            "انصراف",
                            color = Color.White.copy(0.7f)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(GoldPrimary)
                            .clickable { onPick(selectedGregorian) }
                            .padding(
                                horizontal = 22.dp,
                                vertical = 8.dp
                            )) {
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
 * یک چرخ اسکرولی به سبک NumberPicker با فلینگ اسنپ:
 * با رها کردن انگشت، حرکت به‌صورت پیوسته تا نزدیک‌ترین آیتم ادامه
 * می‌یابد و همان‌جا می‌نشیند (بدون فلینگ آزاد + انیمیشن جبرانی).
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
    val fling = rememberSnapScrollFling(
        scroll,
        itemPx
    )

    // مقادیر به‌روز برای استفاده داخل coroutineهای طولانی
    val currentSelected by rememberUpdatedState(selectedIndex)
    val currentOnSelected by rememberUpdatedState(onSelected)

    // آیتم مرکزی — فقط داخل WheelItemText خوانده می‌شود
    val centerIdx: State<Int> = remember(items.size) {
        derivedStateOf {
            if (items.isEmpty()) 0
            else floor((scroll.value + itemPx / 2) / itemPx).toInt()
                .coerceIn(
                    0,
                    items.lastIndex
                )
        }
    }

    // همگام‌سازی از بیرون (مقدار اولیه، دکمه «امروز»، تغییر طول لیست)
    LaunchedEffect(
        selectedIndex,
        items.size
    ) {
        if (items.isEmpty()) return@LaunchedEffect
        if (scroll.isScrollInProgress) return@LaunchedEffect
        val idx = selectedIndex.coerceIn(
            0,
            items.lastIndex
        )
        if (abs(scroll.value - idx * itemPx) > 1f) {
            scroll.scrollTo((idx * itemPx).toInt())
        }
    }

    // گزارش انتخاب پس از سکون کامل (پایان درگ یا فلینگ)
    LaunchedEffect(items.size) {
        snapshotFlow { scroll.isScrollInProgress to centerIdx.value }.distinctUntilChanged()
            .collect { (scrolling, idx) ->
                if (!scrolling && items.isNotEmpty() && idx != currentSelected) {
                    currentOnSelected(idx)
                }
            }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    .verticalScroll(
                        scroll,
                        flingBehavior = fling
                    ),
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
                        WheelItemText(
                            text = text,
                            isCenter = { i == centerIdx.value })
                    }
                }
                Spacer(Modifier.height(edgePad))
            }
        }
    }
}

/**
 * متن یک آیتم چرخ — state مرکز فقط اینجا خوانده می‌شود تا هنگام اسکرول
 * فقط همان یک آیتم recompose شود، نه کل ستون.
 * اندازه فونت ثابت است (فقط رنگ تغییر می‌کند) تا layout دوباره حساب نشود.
 */
@Composable
private fun WheelItemText(
    text: String,
    isCenter: () -> Boolean
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = if (isCenter()) Color.White else Color.White.copy(alpha = 0.40f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

/**
 * فلینگ اسنپ برای ScrollState عمودی:
 * با رها کردن انگشت، نقطه‌ی توقف حرکت تخمین زده می‌شود (اصطکاک نمایی ساده)،
 * به نزدیک‌ترین آیتم گرد می‌شود و انیمیشن از همان لحظه به‌سمت هدف می‌رود —
 * یک حرکت پیوسته، بدون انیمیشن جبرانی دوم.
 *
 * پیاده‌سازی عمداً بدون APIهای نسخه‌دار (spline decay و ...)
 * تا روی همه‌ی نسخه‌های Compose بدون خطا کامپایل شود.
 */
@Composable
private fun rememberSnapScrollFling(
    state: ScrollState,
    itemPx: Float
): FlingBehavior {
    return remember(
        state,
        itemPx
    ) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                // state.value (Int) — به‌جای value خودِ ScrollScope که در همه نسخه‌ها موجود نیست
                val projected = state.value + initialVelocity * FLING_TIME_CONSTANT
                val target = (round(projected / itemPx) * itemPx).roundToInt()
                    .coerceIn(
                        0,
                        state.maxValue
                    )
                if (target != state.value) {
                    state.animateScrollTo(target)
                }
                return 0f
            }
        }
    }
}
