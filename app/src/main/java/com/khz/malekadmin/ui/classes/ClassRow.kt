package com.khz.malekadmin.ui.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.theme.GoldPrimary

@Composable
fun ClassRow(
    cls: FootballClass,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = Color(0xFF4FC3F7)

    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            bottomStart = 24.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // آیکون کلاس
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accentColor.copy(0.85f),
                                accentColor.copy(0.35f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.School,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // اطلاعات
            Column(Modifier.weight(1f)) {
                Text(
                    cls.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                cls.coachName?.let { coach ->
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = Color.White.copy(0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "مربی: $coach",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.7f)
                        )
                    }
                }

                cls.ageGroupsTitle?.let { ageGroups ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "گروه سنی: $ageGroups",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )
                }

                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        cls.billingCycleLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cls.isSeasonal) Color(0xFFFFD54F) else Color.White.copy(0.6f),
                        fontWeight = if (cls.isSeasonal) FontWeight.Bold else FontWeight.Normal
                    )
                    cls.effectiveFee?.let { fee ->
                        if (fee > 0) {
                            Text(
                                "• ${fee} تومان",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(0.5f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // نوار پیشرفت ظرفیت
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Groups,
                        null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "${cls.enrolledCount}${if (cls.capacity != null) "/${cls.capacity}" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.8f),
                        fontWeight = FontWeight.Bold
                    )

                    if (cls.capacity != null && cls.capacity > 0) {
                        Box(modifier = Modifier.weight(1f)) {
                            LinearProgressIndicator(
                                progress = { cls.fillPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (cls.isFull) Color(0xFFFF8A80) else accentColor,
                                trackColor = Color.White.copy(0.1f),
                                strokeCap = StrokeCap.Round
                            )
                        }

                        if (cls.isFull) {
                            Text(
                                "تکمیل",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF8A80),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // وضعیت و عملیات
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .background(
                            if (cls.isActive) Color(0x3381C784) else Color(0x33FF8A80),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        )
                ) {
                    Text(
                        if (cls.isActive) "فعال" else "غیرفعال",
                        color = if (cls.isActive) Color(0xFF81C784) else Color(0xFFFF8A80),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(4.dp))

                TextButton(onClick = onToggle) {
                    Text(
                        if (cls.isActive) "غیرفعال" else "فعال",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        "حذف",
                        tint = Color(0xFFFF8A80),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}