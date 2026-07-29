package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.DynamicAdBannerSection
import com.example.ui.components.QuickCategorySearchBar
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSlateDark
import com.example.ui.theme.BentoSlateMedium
import com.example.ui.viewmodel.LmsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LmsViewModel,
    onNavigate: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val dynamicAds by viewModel.dynamicAds.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val context = LocalContext.current

    // Filter courses based on search & category
    val filteredCourses = remember(courses, searchQuery, selectedCategory) {
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
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Hero Header Banner Bento Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1785305486488),
                        contentDescription = "Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A).copy(alpha = 0.88f),
                                        Color(0xFF1E293B).copy(alpha = 0.45f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AlertOrange
                        ) {
                            Text(
                                text = "Lembaga Asesmen Profesional Indonesia",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Sistem Manajemen Pembelajaran & Uji Kompetensi",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentUser != null)
                                    "Selamat datang, ${currentUser?.name} (${currentUser?.role})"
                                else "Mewujudkan Tenaga Kerja Bersertifikasi & Profesional",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Dynamic Ads Section Managed by Admin
        item {
            Spacer(modifier = Modifier.height(12.dp))
            DynamicAdBannerSection(
                ads = dynamicAds,
                onAdClick = { ad ->
                    if (ad.mediaUrl.isNotBlank() && ad.mediaUrl.startsWith("http")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ad.mediaUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handled
                        }
                    }
                }
            )
        }

        // 3. Quick Action Bento Tile Grid
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Menu Layanan Utama LMS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.VideoCameraFront,
                    title = "Live Class",
                    color = MaterialTheme.colorScheme.primaryContainer,
                    onClick = { onNavigate("LIVE_CLASS") },
                    modifier = Modifier.weight(1f),
                    tag = "quick_live_class"
                )
                QuickActionCard(
                    icon = Icons.Default.CalendarMonth,
                    title = "Kalender",
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { onNavigate("CALENDAR") },
                    modifier = Modifier.weight(1f),
                    tag = "quick_calendar"
                )
                QuickActionCard(
                    icon = Icons.Default.Quiz,
                    title = "Kuis & Esai",
                    color = MaterialTheme.colorScheme.surface,
                    onClick = { onNavigate("COURSES") },
                    modifier = Modifier.weight(1f),
                    tag = "quick_quiz"
                )
                QuickActionCard(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Bantuan",
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { onNavigate("MESSAGES") },
                    modifier = Modifier.weight(1f),
                    tag = "quick_help"
                )
            }
        }

        // 4. Live Class Active Session Alert
        item {
            val activeLive = liveClasses.firstOrNull { it.isLiveNow }
            if (activeLive != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().testTag("active_live_class_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(10.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE CLASS SEDANG BERLANGSUNG",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = activeLive.scheduledTime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeLive.topic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Instruktur: ${activeLive.instructorName} • ${activeLive.courseTitle}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(activeLive.meetingUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    onNavigate("LIVE_CLASS")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth().testTag("join_live_class_now_button")
                        ) {
                            Icon(imageVector = Icons.Default.VideoCall, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GABUNG CLASS SEKARANG", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. Quick Category Search Bar
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Modul & Program Sertifikasi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            QuickCategorySearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                selectedCategory = selectedCategory,
                onSelectCategory = { viewModel.setSelectedCategory(it) }
            )
        }

        // 6. Course Cards List
        if (filteredCourses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tidak ditemukan materi untuk kategori atau pencarian tersebut.")
                    }
                }
            }
        } else {
            items(filteredCourses) { course ->
                CourseItemCard(
                    course = course,
                    onSelect = {
                        viewModel.selectCourse(course.id)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(26.dp), tint = BentoSlateDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoSlateDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CourseItemCard(
    course: CourseEntity,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("course_card_${course.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bannerRes = if (course.bannerImageRes != 0) course.bannerImageRes else R.drawable.img_hero_banner_1785305486488
            Image(
                painter = painterResource(id = bannerRes),
                contentDescription = course.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = course.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = course.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BentoSlateDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Instruktur: ${course.instructorName}",
                    fontSize = 11.sp,
                    color = BentoSlateMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { course.progressPercent / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = BentoPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${course.progressPercent}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary
                    )
                }
            }
        }
    }
}
