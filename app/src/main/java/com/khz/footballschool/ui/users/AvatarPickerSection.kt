package com.khz.footballschool.ui.users

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun AvatarPicker(
    displayName: String,
    currentAvatarUrl: String?,
    selectedImageUri: Uri?,
    isUploading: Boolean,
    isEditMode: Boolean,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImageSelected(uri)
    }

    GlassCard3D(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "تصویر پروفایل",
                style = MaterialTheme.typography.titleSmall,
                color = GoldPrimary
            )

            Box {
                AvatarView(
                    name = displayName,
                    avatarUrl = currentAvatarUrl,
                    localUri = selectedImageUri,
                    size = 150.dp
                )

                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                GoldPrimary,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            null,
                            tint = Color(0xFF1A0533),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                GlassButton(
//                    text = "انتخاب عکس",
//                    onClick = { imagePickerLauncher.launch("image/*") },
//                    primary = false,
//                    modifier = Modifier.weight(1f)
//                )
//
                if (currentAvatarUrl != null || selectedImageUri != null) {
                    IconButton(onClick = onRemoveImage) {
                        Icon(
                            Icons.Default.Delete,
                            "حذف",
                            tint = Color(0xFFFF8A80)
                        )
                    }
                }
//            }

                if (selectedImageUri != null) {
                    Text(
                        if (isEditMode) "عکس جدید همراه با ذخیره تغییرات، آپلود می‌شود"
                        else "عکس همراه با ثبت کاربر، آپلود می‌شود",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )
                }
            }
        }
    }
}