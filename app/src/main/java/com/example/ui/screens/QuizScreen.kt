package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestionEntity
import com.example.ui.viewmodel.LmsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: LmsViewModel,
    quizId: Int,
    onBack: () -> Unit
) {
    var questions by remember { mutableStateOf<List<QuizQuestionEntity>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf<MutableMap<Int, Int>>(mutableMapOf()) }
    var isSubmitted by remember { mutableStateOf(false) }
    var calculatedScore by remember { mutableStateOf(0) }

    // Timer simulation
    var secondsRemaining by remember { mutableStateOf(900) } // 15 minutes

    LaunchedEffect(quizId) {
        questions = viewModel.getQuestionsForQuiz(quizId)
    }

    LaunchedEffect(isSubmitted) {
        if (!isSubmitted) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kuis Evaluation Otomatis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("quiz_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            val mins = secondsRemaining / 60
                            val secs = secondsRemaining % 60
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (isSubmitted) {
            // QUIZ RESULT SCREEN - INSTANT AUTOMATIC SCORE DISPLAY
            val total = questions.size
            val scorePercent = (calculatedScore.toDouble() / total * 100).toInt()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (scorePercent >= 70) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (scorePercent >= 70) Icons.Default.EmojiEvents else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (scorePercent >= 70) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (scorePercent >= 70) "Sangat Baik! Kuis Lulus Otomatis" else "Perlu Remedial / Ulangi Materi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Nilai Anda langsung disinkronkan ke Kolom Nilai Akademik API",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$calculatedScore / $total",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "PERSENTASE KELULUSAN: $scorePercent%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("finish_quiz_result_button")
                ) {
                    Text("KEMBALI KE MATERI", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // ACTIVE QUESTION CARD
            val q = questions[currentQuestionIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Soal Ke ${currentQuestionIndex + 1} Dari ${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kuis Pilihan Ganda",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
                        options.forEachIndexed { index, optionText ->
                            val isSelected = selectedAnswers[currentQuestionIndex] == index

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        val newAnswers = selectedAnswers.toMutableMap()
                                        newAnswers[currentQuestionIndex] = index
                                        selectedAnswers = newAnswers
                                    }
                                    .testTag("quiz_option_${currentQuestionIndex}_$index")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            val newAnswers = selectedAnswers.toMutableMap()
                                            newAnswers[currentQuestionIndex] = index
                                            selectedAnswers = newAnswers
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${('A' + index)}. $optionText",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentQuestionIndex > 0) {
                        OutlinedButton(
                            onClick = { currentQuestionIndex-- },
                            modifier = Modifier.testTag("quiz_prev_button")
                        ) {
                            Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = null)
                            Text("Sebelumnya")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (currentQuestionIndex < questions.size - 1) {
                        Button(
                            onClick = { currentQuestionIndex++ },
                            modifier = Modifier.testTag("quiz_next_button")
                        ) {
                            Text("Berikutnya")
                            Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = {
                                // Calculate score automatically
                                var scoreCount = 0
                                questions.forEachIndexed { idx, question ->
                                    if (selectedAnswers[idx] == question.correctOption) {
                                        scoreCount++
                                    }
                                }
                                calculatedScore = scoreCount
                                isSubmitted = true
                                viewModel.submitQuizResult(quizId, scoreCount, questions.size)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("quiz_submit_answers_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SELESAI & HITUNG NILAI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
