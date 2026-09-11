package com.khz.footballschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.khz.footballschool.core.di.ViewModelFactory
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.navigation.AppNavigation
import com.khz.footballschool.ui.theme.FootballSchoolTheme

class MainActivity : ComponentActivity() {

    // Factory مشترک برای تمام ViewModelها
    lateinit var viewModelFactory: ViewModelFactory
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // دسترسی به AppContainer از Application
        val app = application as FootballSchoolApp
        viewModelFactory = ViewModelFactory(app.container)

        setContent {
            FootballSchoolTheme(darkTheme = true) {
                // قفل دائمی جهت روی راست‌به‌چپ
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    GlassBackground {
                        AppNavigation(viewModelFactory = viewModelFactory)
                    }
                }
            }
        }
    }
}