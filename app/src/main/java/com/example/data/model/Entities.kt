package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String, // "ADMIN", "INSTRUKTUR", "SISWA"
    val program: String, // e.g., "Asesmen TI & AI", "Keuangan", "Sertifikasi K3"
    val isOtpVerified: Boolean = true,
    val is2FaEnabled: Boolean = false,
    val specialReq: String = "" // Admin Secret Code or Instructor Certificate ID
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Teknologi", "Sertifikasi K3", "Keuangan", "Manajemen"
    val description: String,
    val instructorName: String,
    val bannerImageRes: Int = 0,
    val videoUrl: String,
    val pdfUrl: String,
    val imageUrl: String,
    val progressPercent: Int = 0
)

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val title: String,
    val durationMinutes: Int = 15
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Int // 0 for A, 1 for B, 2 for C, 3 for D
)

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val userId: Int,
    val score: Int,
    val totalQuestions: Int,
    val completedAt: String
)

@Entity(tableName = "essay_exams")
data class EssayExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val title: String,
    val instructions: String,
    val questionPdfUrl: String,
    val dueDate: String
)

@Entity(tableName = "essay_submissions")
data class EssaySubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val essayExamId: Int,
    val userId: Int,
    val userName: String,
    val fileName: String,
    val fileType: String = "PDF", // "PDF", "DOC"
    val submittedAt: String,
    val status: String = "TERKIRIM", // "TERKIRIM", "DINILAI"
    val grade: String = "", // KOLOM NILAI (e.g. "85/100")
    val instructorFeedback: String = ""
)

@Entity(tableName = "dynamic_ads")
data class DynamicAdEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val mediaType: String, // "VIDEO", "PDF", "GAMBAR"
    val mediaUrl: String,
    val bannerImageRes: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String
)

@Entity(tableName = "live_classes")
data class LiveClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseTitle: String,
    val instructorName: String,
    val topic: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val meetingUrl: String,
    val isLiveNow: Boolean = false
)

@Entity(tableName = "academic_calendar_events")
data class AcademicCalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val title: String,
    val eventType: String, // "UJIAN", "LIVE_CLASS", "TUGAS", "PENGUMUMAN"
    val description: String
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val senderName: String,
    val senderRole: String,
    val subject: String,
    val message: String,
    val timestamp: String,
    val reply: String = "",
    val isResolved: Boolean = false
)

@Entity(tableName = "daily_tasks")
data class DailyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val category: String,
    val dueDate: String,
    val isCompleted: Boolean = false
)
