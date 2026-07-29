package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EssaySubmissionEntity
import com.example.ui.viewmodel.LmsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssayExamScreen(
    viewModel: LmsViewModel,
    essayExamId: Int,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val submissions by viewModel.allSubmissions.collectAsState()
    val context = LocalContext.current

    var uploadFileNameInput by remember { mutableStateOf("") }
    var selectedFileType by remember { mutableStateOf("PDF") }

    // Instructor Grading Dialog State
    var selectedSubmissionForGrading by remember { mutableStateOf<EssaySubmissionEntity?>(null) }
    var inputGrade by remember { mutableStateOf("") }
    var inputFeedback by remember { mutableStateOf("") }

    val userRole = currentUser?.role ?: "SISWA"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ujian Esai & Portofolio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("essay_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ujian Esai Studi Kasus: Perangkat Asesmen MPA",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tenggat Waktu: 5 Agustus 2026, 23:59 WIB",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Unduh file lembar kerja ujian esai, ketik rancangan dokumen asesmen portofolio Anda, lalu unggah jawaban dalam format PDF atau DOC.",
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.testTag("download_essay_pdf_question_button")
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unduh Soal Ujian Esai (PDF)")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (userRole == "SISWA") {
                // SISWA SECTION: UPLOAD JAWABAN & LIHAT NILAI
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Unggah Jawaban Ujian Esai Siswa",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Format yang didukung: PDF, DOC, DOCX",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = uploadFileNameInput,
                                onValueChange = { uploadFileNameInput = it },
                                label = { Text("Nama File Jawaban (Contoh: Jawaban_Esai_Budi.pdf)") },
                                leadingIcon = { Icon(imageVector = Icons.Default.AttachFile, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("essay_filename_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("PDF", "DOC").forEach { fType ->
                                    FilterChip(
                                        selected = selectedFileType == fType,
                                        onClick = { selectedFileType = fType },
                                        label = { Text("Format $fType", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val fName = if (uploadFileNameInput.isNotBlank()) uploadFileNameInput
                                    else "Jawaban_Esai_${currentUser?.name?.replace(" ", "_")}.$selectedFileType"
                                    viewModel.submitEssayAnswer(essayExamId, fName)
                                    uploadFileNameInput = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_essay_button")
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("UNGGAH JAWABAN SEKARANG", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Riwayat Pengiriman & KOLOM NILAI Anda",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val mySubmissions = submissions.filter { it.userId == currentUser?.id }
                if (mySubmissions.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada file jawaban esai yang diunggah.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(mySubmissions) { sub ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = sub.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (sub.status == "DINILAI") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = sub.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(8.dp))

                                // KOLOM NILAI DISPLAY
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("KOLOM NILAI INSTRUKTUR:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = if (sub.grade.isNotBlank()) sub.grade else "Menunggu Penilaian Instruktur",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (sub.grade.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (sub.instructorFeedback.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Catatan Instruktur: ${sub.instructorFeedback}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                // INSTRUKTUR & ADMIN SECTION: BERIKAN NILAI PADA KOLOM NILAI
                item {
                    Text(
                        text = "Manajemen Penilaian Ujian Esai Siswa (Instruktur Portal)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Instruktur dapat meninjau file jawaban siswa dan memberikan nilai pada KOLOM NILAI serta catatan perbaikan.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(submissions) { sub ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = sub.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Dikirim: ${sub.submittedAt} • File: ${sub.fileName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (sub.status == "DINILAI") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = sub.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("KOLOM NILAI SEKARANG:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (sub.grade.isNotBlank()) sub.grade else "Belum Dinilai",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedSubmissionForGrading = sub
                                        inputGrade = sub.grade
                                        inputFeedback = sub.instructorFeedback
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("grade_submission_button_${sub.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ISI KOLOM NILAI", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Instructor Grading Modal Dialog
        selectedSubmissionForGrading?.let { sub ->
            AlertDialog(
                onDismissRequest = { selectedSubmissionForGrading = null },
                title = { Text("Input Pada KOLOM NILAI - ${sub.userName}", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Masukkan Nilai Kuantitatif / Kualitatif Ujian Esai Siswa:", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = inputGrade,
                            onValueChange = { inputGrade = it },
                            label = { Text("KOLOM NILAI (Contoh: 88/100 atau A)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_grade_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputFeedback,
                            onValueChange = { inputFeedback = it },
                            label = { Text("Catatan / Masukan Instruktur") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.gradeEssaySubmission(sub.id, inputGrade, inputFeedback)
                            selectedSubmissionForGrading = null
                        },
                        modifier = Modifier.testTag("save_grade_button")
                    ) {
                        Text("SIMPAN NILAI REAL-TIME")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedSubmissionForGrading = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}
