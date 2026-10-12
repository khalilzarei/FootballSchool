package com.khz.malekadmin.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.khz.malekadmin.core.util.Constants
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * آواتار دایره‌ای شیشه‌ای با پشتیبانی از:
 *
 * ۱. عکس انتخاب‌شده محلی (Uri) - پیش‌نمایش آنی در فرم‌ها
 * ۲. عکس از سرور (URL کامل یا مسیر نسبی)
 * ۳. حرف اول نام به عنوان حالت پیش‌فرض
 *
 * اولویت نمایش:
 * localUri > avatarUrl > حرف اول نام
 *
 * لمسِ آواتار (وقتی عکسی وجود داشته باشد) تصویر را در [FullScreenImageViewer]
 * تمام‌صفحه با امکان زوم نمایش می‌دهد.
 *
 * @param name نام کاربر/بازیکن برای حالت پیش‌فرض
 * @param avatarUrl مسیر یا URL آواتار از سرور
 * @param localUri آدرس محلی عکس انتخاب‌شده (برای پیش‌نمایش آنی)
 * @param size اندازه آواتار
 * @param accentColor رنگ گرادیان پس‌زمینه
 * @param enableFullscreenPreview با لمس، عکس بزرگ و تمام‌صفحه نمایش داده شود؟
 *        (در جاهایی که خودِ آواتار نقش دکمه‌ی انتخاب دارد، false بدهید)
 * @param onAvatarClick اکشن دلخواهِ اضافه هنگام لمس (اختیاری)
 */
@Composable
fun AvatarView(
    name: String,
    avatarUrl: String? = null,
    localUri: Uri? = null,
    size: Dp = 48.dp,
    accentColor: Color = GoldPrimary,
    modifier: Modifier = Modifier,
    enableFullscreenPreview: Boolean = true,
    onAvatarClick: (() -> Unit)? = null
) {
    // ساخت URL کامل از مسیر نسبی (با کش)
    val fullUrl = remember(avatarUrl) { buildFullUrl(avatarUrl) }

    // تعیین منبع تصویر بر اساس اولویت
    val imageSource: Any? = when {
        localUri != null -> localUri              // ← اولویت اول: عکس محلی انتخاب‌شده
        !fullUrl.isNullOrBlank() -> fullUrl       // ← اولویت دوم: آواتار سرور
        else -> null                              // ← حالت پیش‌فرض: حرف اول نام
    }

    // فقط وقتی عکسی برای نمایش وجود دارد لمس معنا دارد
    val isClickable = imageSource != null && (enableFullscreenPreview || onAvatarClick != null)

    var showFullscreen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        accentColor.copy(alpha = 0.90f),
                        accentColor.copy(alpha = 0.40f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = CircleShape
            )
            .then(
                if (isClickable) {
                Modifier.clickable {
                    onAvatarClick?.invoke()
                    if (enableFullscreenPreview) showFullscreen = true
                }
            } else {
                Modifier
            })
            // تموجِ (ripple) لمس هم به‌شکل دایره برش می‌خورد
        .clip(CircleShape),
        contentAlignment = Alignment.Center) {
        if (imageSource != null) {
            // ─── نمایش عکس با Coil ───
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageSource)                    // ← Uri یا String هر دو کار می‌کنند
                    .crossfade(true)                      // ← انیمیشن محو شدن
                    .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                    .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                    .build(),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            // ─── حالت پیش‌فرض: حرف اول نام ───
            Text(
                text = name.take(1)
                    .uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    // ─── نمایش تمام‌صفحه با زوم ───
    if (showFullscreen) {
        FullScreenImageViewer(
            model = imageSource,
            title = name,
            onDismiss = { showFullscreen = false })
    }
}

/**
 * ساخت URL کامل برای آواتار
 *
 * حالت‌های ورودی:
 * - `null` یا خالی → `null` (حالت حرف اول)
 * - `http://...` یا `https://...` → همان URL (بدون تغییر)
 * - `avatars/users/1_abc.jpg` → `STORAGE_URL/avatars/users/1_abc.jpg`
 * - `/storage/avatars/users/1_abc.jpg` → `STORAGE_URL/storage/avatars/users/1_abc.jpg`
 */
private fun buildFullUrl(avatarUrl: String?): String? {
    if (avatarUrl.isNullOrBlank()) return null

    return when {
        // URL کامل - بدون تغییر
        avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://") -> avatarUrl

        // مسیر نسبی - اضافه کردن STORAGE_URL
        else                                                                -> {
            val storageUrl = Constants.STORAGE_URL.trimEnd('/')
            val path = avatarUrl.trimStart('/')
            "$storageUrl/$path"
        }
    }
}
