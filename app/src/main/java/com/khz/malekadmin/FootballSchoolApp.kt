package com.khz.malekadmin

import android.app.Application
import com.khz.malekadmin.core.di.AppContainer

class FootballSchoolApp : Application() {

    // ظرف اصلی وابستگی‌های پروژه
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)


    }
}