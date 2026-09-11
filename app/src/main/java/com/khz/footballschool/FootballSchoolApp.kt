package com.khz.footballschool

import android.app.Application
import com.khz.footballschool.core.di.AppContainer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FootballSchoolApp : Application() {

    // ظرف اصلی وابستگی‌های پروژه
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)


    }
}