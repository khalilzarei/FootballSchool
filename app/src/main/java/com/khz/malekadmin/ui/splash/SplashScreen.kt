package com.khz.malekadmin.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.R
import com.khz.malekadmin.ui.components.GlassBackground
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        // تاخیر کوتاه برای نمایش splash
        delay(600.milliseconds)
        if (isLoggedIn) {
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = "لوگو",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    "مدرسه فوتبال - مالک‌اشتر",
                    color = GoldPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "نسخه ادمین",
                    style = MaterialTheme.typography.headlineLarge,
                    color = GoldPrimary
                )
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(
                    color = GoldPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    // فقط بخش UI رو بدون LaunchedEffect و container نشون بده
    GlassBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = "لوگو",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(18.dp))
                Text(
                    "مدرسه فوتبال - مالک‌اشتر",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "نسخه ادمین",
                    style = MaterialTheme.typography.bodyLarge,
                    color =  Color.White,
                )
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(
                    color = GoldPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}