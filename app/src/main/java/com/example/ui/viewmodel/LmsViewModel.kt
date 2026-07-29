package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.LmsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LmsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LmsRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LmsRepository(db)
        viewModelScope.launch {
            repository.seedInitialData()
            // Auto login as default student Budi Santoso for smooth initial preview, or user can switch/login/register
            val defaultStudent = repository.userDao.getUserByEmail("siswa@asesmen.id")
            if (defaultStudent != null) {
                _currentUser.value = defaultStudent
            }
        }
    }

    // User State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("28 Jul 2026, 23:00 WIB")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    // Navigation State
    private val _currentRoute = MutableStateFlow("HOME")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _selectedCourseId = MutableStateFlow<Int?>(1)
    val selectedCourseId: StateFlow<Int?> = _selectedCourseId.asStateFlow()

    private val _selectedQuizId = MutableStateFlow<Int?>(null)
    val selectedQuizId: StateFlow<Int?> = _selectedQuizId.asStateFlow()

    private val _selectedEssayExamId = MutableStateFlow<Int?>(null)
    val selectedEssayExamId: StateFlow<Int?> = _selectedEssayExamId.asStateFlow()

    // Filter & Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("SEMUA")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Auth OTP & 2FA State
    private val _otpState = MutableStateFlow<OtpFlowState>(OtpFlowState.Idle)
    val otpState: StateFlow<OtpFlowState> = _otpState.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccess = MutableStateFlow<String?>(null)
    val authSuccess: StateFlow<String?> = _authSuccess.asStateFlow()

    private val _requires2FA = MutableStateFlow(false)
    val requires2FA: StateFlow<Boolean> = _requires2FA.asStateFlow()

    private var pendingUserFor2FA: UserEntity? = null

    // Room Flows
    val courses: StateFlow<List<CourseEntity>> = repository.courseDao.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dynamicAds: StateFlow<List<DynamicAdEntity>> = repository.dynamicAdDao.getActiveAds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAds: StateFlow<List<DynamicAdEntity>> = repository.dynamicAdDao.getAllAds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveClasses: StateFlow<List<LiveClassEntity>> = repository.liveClassDao.getAllLiveClasses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val academicEvents: StateFlow<List<AcademicCalendarEventEntity>> = repository.academicCalendarDao.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubmissions: StateFlow<List<EssaySubmissionEntity>> = repository.essayExamDao.getAllSubmissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportMessages: StateFlow<List<SupportMessageEntity>> = repository.supportMessageDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyTasks: StateFlow<List<DailyTaskEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.dailyTaskDao.getTasksForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userQuizResults: StateFlow<List<QuizResultEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.quizDao.getQuizResultsForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions & Methods
    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun selectCourse(courseId: Int) {
        _selectedCourseId.value = courseId
        _currentRoute.value = "COURSE_DETAIL"
    }

    fun selectQuiz(quizId: Int) {
        _selectedQuizId.value = quizId
        _currentRoute.value = "QUIZ"
    }

    fun selectEssayExam(essayExamId: Int) {
        _selectedEssayExamId.value = essayExamId
        _currentRoute.value = "ESSAY_EXAM"
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun clearAuthMessages() {
        _authError.value = null
        _authSuccess.value = null
    }

    // OTP Verification Flow for Register
    fun sendOtpEmail(email: String, onSent: (String) -> Unit) {
        viewModelScope.launch {
            clearAuthMessages()
            if (email.isBlank() || !email.contains("@")) {
                _authError.value = "Format email tidak valid!"
                return@launch
            }
            val existing = repository.userDao.getUserByEmail(email)
            if (existing != null) {
                _authError.value = "Email $email sudah terdaftar! Gunakan email lain."
                return@launch
            }
            val generatedCode = (100000..999999).random().toString()
            _otpState.value = OtpFlowState.Sent(email, generatedCode)
            _authSuccess.value = "Kode OTP $generatedCode telah dikirimkan ke email $email"
            onSent(generatedCode)
        }
    }

    fun verifyAndRegisterUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String,
        program: String,
        specialReq: String,
        inputOtp: String,
        expectedOtp: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            clearAuthMessages()
            if (inputOtp != expectedOtp) {
                _authError.value = "Kode OTP yang dimasukkan salah!"
                return@launch
            }

            // Verify Special Requirement based on Role
            if (role == "ADMIN" && specialReq != "ADMIN-API-2026") {
                _authError.value = "Kode Kunci Akses Admin tidak valid! (Gunakan: ADMIN-API-2026)"
                return@launch
            }
            if (role == "INSTRUKTUR" && specialReq.isBlank()) {
                _authError.value = "Nomor Sertifikat/Spesialisasi Instruktur wajib diisi!"
                return@launch
            }

            val newUser = UserEntity(
                name = name,
                email = email,
                phone = phone,
                passwordHash = password,
                role = role,
                program = if (program.isNotBlank()) program else "Asesmen Profesional Indonesia",
                isOtpVerified = true,
                is2FaEnabled = false,
                specialReq = specialReq
            )

            val id = repository.userDao.insertUser(newUser)
            val insertedUser = newUser.copy(id = id.toInt())
            _currentUser.value = insertedUser
            _otpState.value = OtpFlowState.Idle
            _authSuccess.value = "Registrasi Berhasil! Selamat datang, ${insertedUser.name}."
            _currentRoute.value = "HOME"
            onSuccess()
        }
    }

    // Login Flow
    fun login(identifier: String, password: String, on2FARequired: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            clearAuthMessages()
            if (identifier.isBlank() || password.isBlank()) {
                _authError.value = "Email/No HP dan Password tidak boleh kosong!"
                return@launch
            }

            val user = repository.userDao.getUserByIdentifier(identifier)
            if (user == null || user.passwordHash != password) {
                _authError.value = "Email/No HP atau Password salah!"
                return@launch
            }

            if (user.is2FaEnabled) {
                pendingUserFor2FA = user
                _requires2FA.value = true
                val code = (100000..999999).random().toString()
                _otpState.value = OtpFlowState.Sent(user.email, code)
                _authSuccess.value = "Kode 2FA $code telah dikirim ke ${user.email}"
                on2FARequired()
            } else {
                _currentUser.value = user
                _authSuccess.value = "Login berhasil sebagai ${user.role} (${user.name})"
                _currentRoute.value = "HOME"
                onSuccess()
            }
        }
    }

    fun verify2FA(inputCode: String, expectedCode: String, onSuccess: () -> Unit) {
        if (inputCode == expectedCode && pendingUserFor2FA != null) {
            _currentUser.value = pendingUserFor2FA
            pendingUserFor2FA = null
            _requires2FA.value = false
            _otpState.value = OtpFlowState.Idle
            _authSuccess.value = "Autentikasi 2FA Berhasil!"
            _currentRoute.value = "HOME"
            onSuccess()
        } else {
            _authError.value = "Kode 2FA salah!"
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRoute.value = "HOME"
    }

    fun toggle2FA(enabled: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(is2FaEnabled = enabled)
            repository.userDao.updateUser(updated)
            _currentUser.value = updated
            _authSuccess.value = if (enabled) "Autentikasi Dua Faktor (2FA) Ditingkatkan!" else "2FA Dinonaktifkan."
        }
    }

    // Cloud Sync Simulation
    fun triggerCloudSync() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            kotlinx.coroutines.delay(1800)
            _isCloudSyncing.value = false
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
            _lastSyncTime.value = sdf.format(Date())
            _authSuccess.value = "Sinkronisasi Awan Berhasil! Seluruh data tercadangkan otomatis."
        }
    }

    // Admin Ad Management
    fun addDynamicAd(title: String, description: String, mediaType: String, mediaUrl: String) {
        viewModelScope.launch {
            repository.dynamicAdDao.insertAd(
                DynamicAdEntity(
                    title = title,
                    description = description,
                    mediaType = mediaType,
                    mediaUrl = mediaUrl,
                    bannerImageRes = R.drawable.img_hero_banner_1785305486488,
                    isActive = true,
                    createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
            )
            _authSuccess.value = "Iklan Dinamis berhasil ditambahkan ke Halaman Depan!"
        }
    }

    fun deleteAd(adId: Int) {
        viewModelScope.launch {
            repository.dynamicAdDao.deleteAd(adId)
            _authSuccess.value = "Iklan berhasil dihapus."
        }
    }

    // Instruktur Course Management
    fun addCourse(title: String, category: String, description: String, videoUrl: String, pdfUrl: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.courseDao.insertCourse(
                CourseEntity(
                    title = title,
                    category = category,
                    description = description,
                    instructorName = user.name,
                    bannerImageRes = R.drawable.img_hero_banner_1785305486488,
                    videoUrl = if (videoUrl.isNotBlank()) videoUrl else "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    pdfUrl = if (pdfUrl.isNotBlank()) pdfUrl else "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3",
                    progressPercent = 0
                )
            )
            _authSuccess.value = "Materi Pembelajaran berhasil diunggah!"
        }
    }

    // Instructor Essay Grading
    fun gradeEssaySubmission(submissionId: Int, grade: String, feedback: String) {
        viewModelScope.launch {
            repository.essayExamDao.gradeSubmission(submissionId, grade, feedback)
            // Real-time academic notification
            _authSuccess.value = "Nilai Ujian Esai ($grade) dan Masukan Instruktur Berhasil Disimpan & Disinkronkan!"
        }
    }

    // Student Quiz Submission
    fun submitQuizResult(quizId: Int, score: Int, total: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            repository.quizDao.insertQuizResult(
                QuizResultEntity(
                    quizId = quizId,
                    userId = user.id,
                    score = score,
                    totalQuestions = total,
                    completedAt = sdf.format(Date())
                )
            )
            // Also update course progress
            _authSuccess.value = "Kuis Selesai! Nilai Anda: $score / $total (${(score.toDouble()/total*100).toInt()}%)"
        }
    }

    // Student Essay Submission
    fun submitEssayAnswer(essayExamId: Int, fileName: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            repository.essayExamDao.insertSubmission(
                EssaySubmissionEntity(
                    essayExamId = essayExamId,
                    userId = user.id,
                    userName = user.name,
                    fileName = fileName,
                    fileType = if (fileName.endsWith(".pdf", ignoreCase = true)) "PDF" else "DOC",
                    submittedAt = sdf.format(Date()),
                    status = "TERKIRIM",
                    grade = "Menunggu Penilaian",
                    instructorFeedback = "-"
                )
            )
            _authSuccess.value = "Jawaban Ujian Esai $fileName Berhasil Diunggah!"
        }
    }

    // Support Messages
    fun sendSupportMessage(subject: String, message: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            repository.supportMessageDao.insertMessage(
                SupportMessageEntity(
                    userId = user.id,
                    senderName = user.name,
                    senderRole = user.role,
                    subject = subject,
                    message = message,
                    timestamp = sdf.format(Date())
                )
            )
            _authSuccess.value = "Pesan Bantuan Berhasil Dikirim ke Tim Support & Admin!"
        }
    }

    fun replySupportMessage(messageId: Int, replyText: String) {
        viewModelScope.launch {
            repository.supportMessageDao.replyMessage(messageId, replyText)
            _authSuccess.value = "Tanggapan Bantuan Berhasil Diberikan!"
        }
    }

    // Task completion
    fun toggleTaskCompletion(taskId: Int, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.dailyTaskDao.updateTaskCompletion(taskId, !currentCompleted)
        }
    }

    suspend fun getQuestionsForQuiz(quizId: Int): List<QuizQuestionEntity> {
        return repository.quizDao.getQuestionsForQuiz(quizId)
    }

    suspend fun getCourseById(courseId: Int): CourseEntity? {
        return repository.courseDao.getCourseById(courseId)
    }
}

sealed class OtpFlowState {
    object Idle : OtpFlowState()
    data class Sent(val email: String, val otpCode: String) : OtpFlowState()
}
