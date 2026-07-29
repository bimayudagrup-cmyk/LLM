package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSlateDark
import com.example.ui.theme.BentoSlateMedium
import com.example.ui.viewmodel.LmsViewModel

@Composable
fun AnalyticsAndTasksScreen(
    viewModel: LmsViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val dailyTasks by viewModel.dailyTasks.collectAsState()
    val quizResults by viewModel.userQuizResults.collectAsState()

    var isNotificationEnabled by remember { mutableStateOf(true) }

    val avgScore = remember(quizResults) {
        if (quizResults.isEmpty()) 0
        else {
            val totalScore = quizResults.sumOf { (it.score.toDouble() / it.totalQuestions) * 100 }
            (totalScore / quizResults.size).toInt()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Dasbor Analitik & Aktivitas Belajar",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoSlateDark
                )
            )
            Text(
                text = "Pantau Perkembangan Nilai, Statistik Kuis, dan Pengingat Tugas Harian",
                style = MaterialTheme.typography.bodySmall.copy(color = BentoSlateMedium)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Bento Metric Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Rata-Rata Kuis",
                    value = "$avgScore%",
                    icon = Icons.Default.Grade,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Hari Berturut",
                    value = "7 Hari",
                    icon = Icons.Default.LocalFireDepartment,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Progres Modul",
                    value = "65%",
                    icon = Icons.Default.Analytics,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Tugas Selesai",
                    value = "${dailyTasks.count { it.isCompleted }}/${dailyTasks.size}",
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Daily Notification Reminder Bento Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BentoBorder),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = BentoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notifikasi Kustom Pengingat Belajar", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoSlateDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kirimkan pengingat harian otomatis ke perangkat untuk jadwal kuis & ujian esai.",
                            fontSize = 11.sp,
                            color = BentoSlateMedium
                        )
                    }

                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = { isNotificationEnabled = it },
                        modifier = Modifier.testTag("notification_reminder_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pengingat Tugas Harian Siswa",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BentoSlateDark)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (dailyTasks.isEmpty()) {
            item {
                Text("Tidak ada tugas harian pending.", fontSize = 12.sp, color = BentoSlateMedium)
            }
        } else {
            items(dailyTasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTaskCompletion(task.id, task.isCompleted) },
                            modifier = Modifier.testTag("task_checkbox_${task.id}")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoSlateDark)
                            Text(text = "Kategori: ${task.category} • Tenggat: ${task.dueDate}", fontSize = 11.sp, color = BentoSlateMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BentoBorder),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = BentoSlateDark)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BentoSlateDark)
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = BentoSlateMedium)
        }
    }
}
