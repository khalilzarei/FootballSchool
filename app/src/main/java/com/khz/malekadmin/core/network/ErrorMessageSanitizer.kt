package com.khz.malekadmin.core.network

/**
 * پاک‌سازی پیام خطا قبل از نمایش به کاربر.
 *
 * هدف: کاربر فقط «پیام» را ببیند — نه آدرس API، نه هاست/پورت،
 * نه مسیر فایل‌های سمت سرور و نه پیام‌های فنیِ انگلیسیِ کتابخانه‌ها.
 *
 * نمونه ورودی/خروجی:
 *  - "unexpected end of stream on https://api.malekashtarkaz.ir/api/v1/players"
 *    → "خطا در برقراری ارتباط با سرور. لطفاً دوباره تلاش کنید."
 *  - "خطای داخلی سرور — اتصال به https://api.malekashtarkaz.ir برقرار نشد"
 *    → "خطای داخلی سرور — اتصال برقرار نشد"
 *
 * این کلاس تنها نقطه‌ی تصمیم‌گیری است؛ هیچ پیامی بدون عبور از
 * [sanitize] نباید به UI برسد (رجوع کنید به NetworkResult.Error).
 */
object ErrorMessageSanitizer {

    /** پیام عمومیِ شبکه — وقتی خطا ماهیت ارتباطی دارد (استثناهای OkHttp/Retrofit) */
    const val GENERIC_MESSAGE = "خطا در برقراری ارتباط با سرور. لطفاً دوباره تلاش کنید."

    /** پیام عمومیِ خنثی — برای پیام‌هایی که از سمت سرور آمده‌اند و قابل‌نمایش نیستند */
    const val NEUTRAL_MESSAGE = "خطا در انجام عملیات. لطفاً دوباره تلاش کنید."

    /** حداکثر طول پیام نمایش‌داده‌شده */
    private const val MAX_LENGTH = 180

    // ─────────────────────────────────────────────
    // الگوهای حذف
    // ─────────────────────────────────────────────

    /** تگ‌های HTML — خطاهای PHP در حالت debug خروجی HTML دارند */
    private val HTML_TAG = Regex("<[^>]*>")

    /** Connection{api.malekashtarkaz.ir:443, proxy=DIRECT ...} — پیام‌های OkHttp */
    private val CONNECTION_BLOCK = Regex("(?i)Connection\\s*\\{[^}]*\\}")

    /** آدرس کامل http/https */
    private val URL = Regex("""(?i)\bhttps?://[^\s،,)؛;"'<>\[\]{}«»]+""")

    /** آدرس بدون اسکیم: www.example.com/path */
    private val WWW = Regex("""(?i)\bwww\.[^\s،,)؛;"'<>\[\]{}«»]+""")

    /** آدرس IP (با پورت اختیاری) */
    private val IPV4 = Regex("""\b\d{1,3}(?:\.\d{1,3}){3}(?::\d{1,5})?\b""")

    /**
     * هاست (با پورت و مسیر اختیاری):
     * api.malekashtarkaz.ir  |  api.malekashtarkaz.ir:443  |  host.com/api/v1/x
     * برای این‌که روی اعداد اعشاری/نسخه‌ها (مثل 1.0 یا 2.5) حساس نباشد،
     * بخش آخر باید حداقل دو حرف انگلیسی باشد (TLD).
     */
    private val HOST = Regex(
        """\b[A-Za-z0-9][A-Za-z0-9.\-]*\.[A-Za-z]{2,}(?::\d{1,5})?(?:/[^\s،,)؛;"'<>\[\]{}«»]*)?"""
    )

    /** مسیر فایل‌های سمت سرور / کلاس‌های جاوا-کاتلین */
    private val CODE_LOCATION = Regex(
        """(?:/[\w\-./@]+|[\w.\-]+\.(?:php|java|kt|kts|json|sql|xml|log))\b"""
    )

    /** شماره خط: "on line 123" / ":123" */
    private val LINE_NUMBER = Regex("""(?i)\b(?:on\s+line|line)\s*:?\s*\d+""")

    private val MULTI_SPACE = Regex("""[ \t ]{2,}""")

    /** علائم نگارشیِ یتیم که بعد از حذف URL جا می‌مانند: « — () » */
    private val DANGLING_PUNCT = Regex("""[\s]*(?:[:\-–—])\s*(?=[،,)؛;\-–—:\s]|$)""")

    // ─────────────────────────────────────────────
    // API
    // ─────────────────────────────────────────────

    /**
     * پیام را برای نمایش امن می‌کند.
     *
     * @param raw پیام خام (از استثنا یا از سرور)
     * @param fallback پیام جایگزین وقتی بعد از پاک‌سازی چیز قابل‌نمایشی نمانده باشد
     * @return پیام امنِ فارسی؛ هرگز خالی یا null نیست
     */
    fun sanitize(
        raw: String?,
        fallback: String = GENERIC_MESSAGE
    ): String {
        var text = raw
                ?: return fallback

        text = HTML_TAG.replace(
            text,
            " "
        )
        text = CONNECTION_BLOCK.replace(
            text,
            " "
        )
        text = URL.replace(
            text,
            " "
        )
        text = WWW.replace(
            text,
            " "
        )
        text = IPV4.replace(
            text,
            " "
        )
        text = HOST.replace(
            text,
            " "
        )
        text = CODE_LOCATION.replace(
            text,
            " "
        )
        text = LINE_NUMBER.replace(
            text,
            " "
        )
        text = DANGLING_PUNCT.replace(
            text,
            " "
        )
        text = MULTI_SPACE.replace(
            text,
            " "
        )

        text = text.trim()
        text = trimEdges(text)

        // چیز مفیدی باقی نمانده → پیام عمومی
        if (text.length < 3) return fallback

        // پیام کاملاً لاتین (خطای فنیِ کتابخانه) → پیام عمومی فارسی
        if (isTechnical(text)) return fallback

        return if (text.length > MAX_LENGTH) text.take(MAX_LENGTH)
            .trimEnd() + "…" else text
    }

    /** علائم ساده‌ای که همیشه از لبه‌ها حذف می‌شوند */
    private val PLAIN_EDGE = charArrayOf(
        ' ',
        '\t',
        '\n',
        '\r',
        ':',
        '،',
        ',',
        ';',
        '؛',
        '-',
        '–',
        '—',
        '.',
        '…',
        '/',
        '"',
        '\''
    )

    /** براکت بسته → براکت بازِ متناظر */
    private val CLOSERS = mapOf(
        ')' to '(',
        ']' to '[',
        '}' to '{',
        '»' to '«'
    )

    /**
     * حذف علائم یتیم از ابتدا و انتهای رشته.
     *
     * نکته‌ی مهم: براکتِ بسته (مثل پرانتزِ پایانیِ یک جمله‌ی سالم)
     * فقط زمانی حذف می‌شود که جفتِ بازِ آن بعد از حذف URL از بین رفته باشد؛
     * وگرنه پیام‌هایی مثل «(حداکثر ۲۰۰ مگابایت)» ناقص می‌شوند.
     */
    private fun trimEdges(text: String): String {
        var s = text.trim(*PLAIN_EDGE)
            .trim()

        // براکت‌های بسته‌ی بدون جفت در انتها
        while (s.isNotEmpty()) {
            val opener = CLOSERS[s.last()]
                    ?: break
            if (s.count { it == opener } > 0) break
            s = s.dropLast(1)
                .trim(*PLAIN_EDGE)
                .trim()
        }

        // براکت‌های بازِ بدون جفت در ابتدا
        while (s.isNotEmpty()) {
            val first = s.first()
            val closer = CLOSERS.entries.firstOrNull { it.value == first }?.key
                    ?: break
            if (s.count { it == closer } > 0) break
            s = s.drop(1)
                .trim(*PLAIN_EDGE)
                .trim()
        }

        return s
    }

    /**
     * آیا پیام ماهیت فنی دارد؟
     * ملاک ساده و امن: اگر هیچ کاراکتر فارسی/عربی نداشته باشد،
     * پیامِ کاربر‌پسندی نیست و نباید نمایش داده شود.
     */
    private fun isTechnical(text: String): Boolean {
        var persian = 0
        for (c in text) {
            if (c in '\u0600'..'\u06FF' || c in '\uFB8A'..'\uFEFF' || c in '\u0750'..'\u077F') {
                persian++
            }
        }
        return persian == 0
    }
}
