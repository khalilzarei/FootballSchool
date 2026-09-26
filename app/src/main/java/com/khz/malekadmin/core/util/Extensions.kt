package com.khz.malekadmin.core.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.di.AppContainer
import com.khz.malekadmin.core.di.ViewModelFactory

// دسترسی به AppContainer از هر جای برنامه
fun ComponentActivity.getAppContainer(): AppContainer {
    return (application as MalekAdminApp).container
}

// دسترسی به ViewModelFactory از داخل Composable
@Composable
fun getAppViewModelFactory(): ViewModelFactory {
    val context = LocalContext.current
    val app = context.applicationContext as MalekAdminApp
    return ViewModelFactory(app.container)
}

// تابع کمکی برای دریافت ViewModel در Compose
@Composable
inline fun <reified T : ViewModel> appViewModel(): T {
    val factory = getAppViewModelFactory()
    return viewModel(factory = factory)
}

/** ارقام فارسی */
fun String.toPersianDigits(): String {
    val english = '0'..'9'
    val persian = listOf(
        '۰',
        '۱',
        '۲',
        '۳',
        '۴',
        '۵',
        '۶',
        '۷',
        '۸',
        '۹'
    )
    val builder = StringBuilder()
    for (c in this) {
        if (c in english) {
            builder.append(persian[c - '0'])
        } else {
            builder.append(c)
        }
    }
    return builder.toString()
}