package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CourseEntity
import com.example.ui.viewmodel.LmsViewModel

@Composable
fun CourseListScreen(
    viewModel: LmsViewModel,
    onSelectCourse: (Int) -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val filtered = remember(courses, searchQuery, selectedCategory) {
        courses.filter { course ->
            val matchesCategory = selectedCategory == "SEMUA" || course.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    course.title.contains(searchQuery, ignoreCase = true) ||
                    course.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
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
                text = "Modul & Materi Pembelajaran",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Akses Video, Dokumen PDF, Gambar, Kuis, dan Ujian Esai",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(filtered) { course ->
            CourseItemCard(
                course = course,
                onSelect = { onSelectCourse(course.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    viewModel: LmsViewModel,
    courseId: Int,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var course by remember { mutableStateOf<CourseEntity?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Materi (Video, PDF, Gambar), 1 = Kuis Otomatis, 2 = Ujian Esai

    LaunchedEffect(courseId) {
        course = viewModel.getCourseById(courseId)
    }

    val currentCourse = course ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentCourse.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
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
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val bannerRes = if (currentCourse.bannerImageRes != 0) currentCourse.bannerImageRes else R.drawable.img_hero_banner_1785305486488
                    Image(
                        painter = painterResource(id = bannerRes),
                        contentDescription = currentCourse.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = currentCourse.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentCourse.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Instruktur Pengampu: ${currentCourse.instructorName}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentCourse.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Row
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Materi (Media)") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Kuis Otomatis") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Ujian Esai") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (selectedTab == 0) {
                // TAB 0: Materi Pembelajaran (Video, PDF, Gambar)
                item {
                    Text("1. Video Pembelajaran Interaktif", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Video Modul Utama API", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Format MP4 / Live Stream", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentCourse.videoUrl))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("play_video_button")
                            ) {
                                Text("Putar Video", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("2. Dokumen Modul Standar & Portofolio (PDF)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Buku Panduan Asesmen (PDF)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("File Dokumen Resmi API", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentCourse.pdfUrl))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("view_pdf_button")
                            ) {
                                Text("Buka PDF", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("3. Diagram & Infografis Prosedur (JPG)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.img_ad_sertifikasi_1785305501941),
                                contentDescription = "Infografis Prosedur",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Infografis Standar Operasional Prosedur Uji Kompetensi API (JPG)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // TAB 1: Kuis Otomatis
                item {
                    Text("Daftar Kuis Evaluasi Otomatis", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Nilai akan langsung dihitung dan muncul secara otomatis setelah kuis selesai dikerjakan.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Kuis 1: Prinsip Dasas Asesmen & Uji Kompetensi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Durasi: 15 Menit • 3 Soal Pilihan Ganda", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.selectQuiz(1) },
                                modifier = Modifier.fillMaxWidth().testTag("start_quiz_1_button")
                            ) {
                                Icon(imageVector = Icons.Default.Quiz, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("KERJAKAN KUIS OTOMATIS", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // TAB 2: Ujian Esai
                item {
                    Text("Ujian Esai & Tugas Portofolio", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Unggah jawaban esai Anda dalam bentuk file PDF atau DOC untuk dinilai oleh Instruktur.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ujian Esai Studi Kasus: Penyusunan Perangkat Asesmen MPA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Tenggat Waktu: 5 Agustus 2026", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Instruksi: Unduh lembar soal PDF, ketik jawaban Anda lalu simpan sebagai file PDF/DOC dan unggah pada tombol di bawah.", fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.selectEssayExam(1) },
                                modifier = Modifier.fillMaxWidth().testTag("open_essay_exam_button")
                            ) {
                                Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (currentUser?.role == "INSTRUKTUR") "PENILAIAN KOLOM NILAI (INSTRUKTUR)"
                                    else "UNGGAH JAWABAN ESAI (PDF/DOC)",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
