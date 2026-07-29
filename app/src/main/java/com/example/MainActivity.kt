package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.DrawerContent
import com.example.ui.components.LmsTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.AsesmenLmsTheme
import com.example.ui.viewmodel.LmsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: LmsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val currentRoute by viewModel.currentRoute.collectAsState()
            val selectedCourseId by viewModel.selectedCourseId.collectAsState()
            val selectedQuizId by viewModel.selectedQuizId.collectAsState()
            val selectedEssayExamId by viewModel.selectedEssayExamId.collectAsState()
            val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            val authSuccess by viewModel.authSuccess.collectAsState()
            val authError by viewModel.authError.collectAsState()

            LaunchedEffect(authSuccess) {
                authSuccess?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }

            LaunchedEffect(authError) {
                authError?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }

            AsesmenLmsTheme(darkTheme = isDarkMode) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            currentUser = currentUser,
                            currentRoute = currentRoute,
                            onNavigate = { route -> viewModel.navigateTo(route) },
                            onLogout = { viewModel.logout() },
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        topBar = {
                            if (currentRoute != "LOGIN") {
                                LmsTopAppBar(
                                    currentUser = currentUser,
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                                    onOpenDrawer = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onProfileClick = { viewModel.navigateTo("PROFILE") },
                                    onLoginRegisterClick = { viewModel.navigateTo("LOGIN") },
                                    onCloudSyncClick = { viewModel.triggerCloudSync() },
                                    isCloudSyncing = isCloudSyncing
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentRoute,
                                label = "ScreenTransition"
                            ) { route ->
                                when (route) {
                                    "HOME" -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigate = { target -> viewModel.navigateTo(target) }
                                    )
                                    "COURSES" -> CourseListScreen(
                                        viewModel = viewModel,
                                        onSelectCourse = { cId -> viewModel.selectCourse(cId) }
                                    )
                                    "COURSE_DETAIL" -> CourseDetailScreen(
                                        viewModel = viewModel,
                                        courseId = selectedCourseId ?: 1,
                                        onBack = { viewModel.navigateTo("COURSES") }
                                    )
                                    "QUIZ" -> QuizScreen(
                                        viewModel = viewModel,
                                        quizId = selectedQuizId ?: 1,
                                        onBack = { viewModel.navigateTo("COURSE_DETAIL") }
                                    )
                                    "ESSAY_EXAM" -> EssayExamScreen(
                                        viewModel = viewModel,
                                        essayExamId = selectedEssayExamId ?: 1,
                                        onBack = { viewModel.navigateTo("COURSE_DETAIL") }
                                    )
                                    "LIVE_CLASS" -> LiveClassScreen(
                                        viewModel = viewModel
                                    )
                                    "CALENDAR" -> AcademicCalendarScreen(
                                        viewModel = viewModel
                                    )
                                    "ANALYTICS" -> AnalyticsAndTasksScreen(
                                        viewModel = viewModel
                                    )
                                    "MESSAGES" -> MessagesSupportScreen(
                                        viewModel = viewModel
                                    )
                                    "ADMIN_ADS" -> AdminAdManagementScreen(
                                        viewModel = viewModel
                                    )
                                    "PROFILE" -> ProfileScreen(
                                        viewModel = viewModel,
                                        onLogout = { viewModel.logout() }
                                    )
                                    "LOGIN" -> LoginRegisterScreen(
                                        viewModel = viewModel,
                                        onAuthSuccess = { viewModel.navigateTo("HOME") }
                                    )
                                    else -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigate = { target -> viewModel.navigateTo(target) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
