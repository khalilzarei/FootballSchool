package com.khz.malekadmin.core.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.di.AppContainer
import com.khz.malekadmin.core.di.ViewModelFactory

// دسترسی به AppContainer از هر جای برنامه
fun ComponentActivity.getAppContainer(): AppContainer {
    return (application as FootballSchoolApp).container
}

// دسترسی به ViewModelFactory از داخل Composable
@Composable
fun getAppViewModelFactory(): ViewModelFactory {
    val context = LocalContext.current
    val app = context.applicationContext as FootballSchoolApp
    return ViewModelFactory(app.container)
}

// تابع کمکی برای دریافت ViewModel در Compose
@Composable
inline fun <reified T : ViewModel> appViewModel(): T {
    val factory = getAppViewModelFactory()
    return viewModel(factory = factory)
}