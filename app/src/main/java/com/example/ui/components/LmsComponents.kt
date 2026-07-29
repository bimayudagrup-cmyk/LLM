package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DynamicAdEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.BentoBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmsTopAppBar(
    currentUser: UserEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    onLoginRegisterClick: () -> Unit,
    onCloudSyncClick: () -> Unit,
    isCloudSyncing: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("open_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu Sidebar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.img_lms_logo_1785305469960),
                    contentDescription = "Logo API",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Asesmen Profesional",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Indonesia",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Cloud sync indicator
                IconButton(
                    onClick = onCloudSyncClick,
                    modifier = Modifier.testTag("cloud_sync_button")
                ) {
                    if (isCloudSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sinkronisasi Awan",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Dark mode toggle
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier.testTag("dark_mode_toggle")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Mode Gelap Toggle",
                        tint = if (isDarkMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Profile / Login Top Right Header Menu
                if (currentUser != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { onProfileClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("profile_top_right_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.name.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.widthIn(max = 110.dp)) {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${currentUser.role} • ${currentUser.program}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onLoginRegisterClick,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("login_top_button")
                    ) {
                        Text("Masuk", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    currentUser: UserEntity?,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_lms_logo_1785305469960),
                            contentDescription = "Logo API",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Lembaga Asesmen",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Profesional Indonesia",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentUser != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = currentUser.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Akses: ${currentUser.role} (${currentUser.program})",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Silakan login untuk mengakses fitur lengkap LMS",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DrawerMenuItem(
                    icon = Icons.Default.Home,
                    label = "Beranda Utama",
                    isSelected = currentRoute == "HOME",
                    onClick = { onNavigate("HOME"); onCloseDrawer() },
                    tag = "drawer_home"
                )

                DrawerMenuItem(
                    icon = Icons.Default.MenuBook,
                    label = "Materi & Modul",
                    isSelected = currentRoute == "COURSES",
                    onClick = { onNavigate("COURSES"); onCloseDrawer() },
                    tag = "drawer_courses"
                )

                DrawerMenuItem(
                    icon = Icons.Default.VideoCameraFront,
                    label = "Live Class (Video Conf)",
                    isSelected = currentRoute == "LIVE_CLASS",
                    onClick = { onNavigate("LIVE_CLASS"); onCloseDrawer() },
                    tag = "drawer_live_class"
                )

                DrawerMenuItem(
                    icon = Icons.Default.CalendarMonth,
                    label = "Kalender Akademik",
                    isSelected = currentRoute == "CALENDAR",
                    onClick = { onNavigate("CALENDAR"); onCloseDrawer() },
                    tag = "drawer_calendar"
                )

                DrawerMenuItem(
                    icon = Icons.Default.Analytics,
                    label = "Dasbor Analitik & Tugas",
                    isSelected = currentRoute == "ANALYTICS",
                    onClick = { onNavigate("ANALYTICS"); onCloseDrawer() },
                    tag = "drawer_analytics"
                )

                DrawerMenuItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    label = "Pesan & Bantuan",
                    isSelected = currentRoute == "MESSAGES",
                    onClick = { onNavigate("MESSAGES"); onCloseDrawer() },
                    tag = "drawer_messages"
                )

                // Role-specific items
                if (currentUser?.role == "ADMIN") {
                    DrawerMenuItem(
                        icon = Icons.Default.Campaign,
                        label = "Kelola Iklan Dinamis",
                        isSelected = currentRoute == "ADMIN_ADS",
                        onClick = { onNavigate("ADMIN_ADS"); onCloseDrawer() },
                        tag = "drawer_admin_ads"
                    )
                }

                if (currentUser != null) {
                    DrawerMenuItem(
                        icon = Icons.Default.Person,
                        label = "Profil & Pengaturan",
                        isSelected = currentRoute == "PROFILE",
                        onClick = { onNavigate("PROFILE"); onCloseDrawer() },
                        tag = "drawer_profile"
                    )
                }
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            if (currentUser != null) {
                Button(
                    onClick = { onLogout(); onCloseDrawer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drawer_logout_button")
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar Akun")
                }
            } else {
                Button(
                    onClick = { onNavigate("LOGIN"); onCloseDrawer() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drawer_login_button")
                ) {
                    Icon(imageVector = Icons.Default.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Masuk / Registrasi")
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        label = {
            Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
        },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .testTag(tag)
    )
}

@Composable
fun QuickCategorySearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf("SEMUA", "Teknologi", "Sertifikasi K3", "Keuangan", "Manajemen")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_search_input"),
            placeholder = { Text("Cari materi, kuis, atau sertifikasi...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Cari")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(category) },
                    label = { Text(category, fontSize = 12.sp) },
                    leadingIcon = if (isSelected) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }
    }
}

@Composable
fun DynamicAdBannerSection(
    ads: List<DynamicAdEntity>,
    onAdClick: (DynamicAdEntity) -> Unit
) {
    if (ads.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pengumuman & Iklan Resmi API",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Dinamis",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(ads) { ad ->
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(160.dp)
                        .clickable { onAdClick(ad) }
                        .testTag("ad_card_${ad.id}"),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val bannerRes = if (ad.bannerImageRes != 0) ad.bannerImageRes else R.drawable.img_hero_banner_1785305486488
                        Image(
                            painter = painterResource(id = bannerRes),
                            contentDescription = ad.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (ad.mediaType) {
                                    "VIDEO" -> MaterialTheme.colorScheme.error
                                    "PDF" -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.tertiary
                                }
                            ) {
                                Text(
                                    text = ad.mediaType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ad.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = ad.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
