package com.khz.malekadmin

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.khz.malekadmin.core.di.AppContainer

class MalekAdminApp : Application(),
    ImageLoaderFactory {

    // ظرف اصلی وابستگی‌های پروژه
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    /**
     * پیکربندی سراسری Coil برای کش آواتار و تصاویر:
     * - حافظه: ۲۵٪ رم
     * - دیسک: ۲۵۰ مگابایت، ماندگار تا پاک شدن کش یا آپدیت اپ
     * - respectCacheHeaders=false → حتی اگر سرور no-cache بدهد، تا ابد نگهداری می‌شود (برای آواتار مناسب است)
     * بعد از اولین لود، هر عکس از دیسک/حافظه خوانده می‌شود و اینترنت مصرف نمی‌شود.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_avatar_cache"))
                    .maxSizeBytes(250L * 1024 * 1024)
                    .build()
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()
    }
}