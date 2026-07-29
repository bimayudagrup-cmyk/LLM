package com.example.data.repository

import com.example.R
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class LmsRepository(private val db: AppDatabase) {

    val userDao = db.userDao()
    val courseDao = db.courseDao()
    val quizDao = db.quizDao()
    val essayExamDao = db.essayExamDao()
    val dynamicAdDao = db.dynamicAdDao()
    val liveClassDao = db.liveClassDao()
    val academicCalendarDao = db.academicCalendarDao()
    val supportMessageDao = db.supportMessageDao()
    val dailyTaskDao = db.dailyTaskDao()

    suspend fun seedInitialData() {
        // Seed default Admin if not present
        if (userDao.getUserByEmail("admin@asesmen.id") == null) {
            userDao.insertUser(
                UserEntity(
                    name = "Administrator API",
                    email = "admin@asesmen.id",
                    phone = "081234567890",
                    passwordHash = "admin123",
                    role = "ADMIN",
                    program = "Manajemen Sistem LMS",
                    isOtpVerified = true,
                    is2FaEnabled = false,
                    specialReq = "ADMIN-API-2026"
                )
            )
        }

        // Seed default Instructor if not present
        if (userDao.getUserByEmail("instruktur@asesmen.id") == null) {
            userDao.insertUser(
                UserEntity(
                    name = "Dr. Ir. Hendra Prasetya, M.T.",
                    email = "instruktur@asesmen.id",
                    phone = "081987654321",
                    passwordHash = "instruktur123",
                    role = "INSTRUKTUR",
                    program = "Spesialis Asesmen Kompetensi K3 & IT",
                    isOtpVerified = true,
                    is2FaEnabled = false,
                    specialReq = "INS-K3-8891"
                )
            )
        }

        // Seed default Student if not present
        if (userDao.getUserByEmail("siswa@asesmen.id") == null) {
            userDao.insertUser(
                UserEntity(
                    name = "Budi Santoso",
                    email = "siswa@asesmen.id",
                    phone = "085678901234",
                    passwordHash = "siswa123",
                    role = "SISWA",
                    program = "Sertifikasi Asesor Kompetensi Profesional",
                    isOtpVerified = true,
                    is2FaEnabled = false,
                    specialReq = ""
                )
            )
        }

        // Seed default Courses if empty
        val existingCourse = courseDao.getCourseById(1)
        if (existingCourse == null) {
            val course1Id = courseDao.insertCourse(
                CourseEntity(
                    title = "Sertifikasi Asesor Kompetensi Nasional BNSP",
                    category = "Sertifikasi K3",
                    description = "Program pelatihan dan uji kompetensi untuk menjadi Asesor Lisensi Sertifikasi Kompetensi Profesi terakreditasi.",
                    instructorName = "Dr. Ir. Hendra Prasetya, M.T.",
                    bannerImageRes = R.drawable.img_hero_banner_1785305486488,
                    videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3",
                    progressPercent = 65
                )
            )

            val course2Id = courseDao.insertCourse(
                CourseEntity(
                    title = "Metodologi Asesmen Digital & Artificial Intelligence",
                    category = "Teknologi",
                    description = "Panduan modern penggunaan platform digital dan alat AI dalam melakukan evaluasi portofolio peserta asesmen.",
                    instructorName = "Dr. Ir. Hendra Prasetya, M.T.",
                    bannerImageRes = R.drawable.img_ad_sertifikasi_1785305501941,
                    videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    imageUrl = "https://images.unsplash.com/photo-1531482615713-2afd69097998",
                    progressPercent = 40
                )
            )

            val course3Id = courseDao.insertCourse(
                CourseEntity(
                    title = "Manajemen Mutu Lembaga Sertifikasi Profesi (LSP)",
                    category = "Manajemen",
                    description = "Pengelolaan tata kelola mutu LSP sesuai pedoman ISO 17024 & regulasi Asesmen Profesional Indonesia.",
                    instructorName = "Dra. Siti Rahmawati, M.M.",
                    bannerImageRes = R.drawable.img_hero_banner_1785305486488,
                    videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    imageUrl = "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40",
                    progressPercent = 90
                )
            )

            // Seed Quizzes for Course 1
            val quizId = quizDao.insertQuiz(
                QuizEntity(
                    courseId = course1Id.toInt(),
                    title = "Kuis 1: Prinsip Dasas Asesmen & Uji Kompetensi",
                    durationMinutes = 15
                )
            )

            quizDao.insertQuestions(
                listOf(
                    QuizQuestionEntity(
                        quizId = quizId.toInt(),
                        questionText = "Apa acuan utama pelaksanaan Asesmen Kompetensi Profesi di Indonesia?",
                        optionA = "Standar Kompetensi Kerja Nasional Indonesia (SKKNI)",
                        optionB = "Kurikulum Pendidikan Sekolah",
                        optionC = "Peraturan Pasar Modal",
                        optionD = "Surat Keputusan Camat",
                        correctOption = 0
                    ),
                    QuizQuestionEntity(
                        quizId = quizId.toInt(),
                        questionText = "Manakah yang merupakan prinsip utama dalam pelaksanaan Asesmen?",
                        optionA = "Valid, Pengalaman, Terbuka, Objektif",
                        optionB = "Valid, Reliabel, Fleksibel, Adil",
                        optionC = "Cepat, Murah, Rahasia, Subjektif",
                        optionD = "Teoretis, Komersial, Bebas, Baku",
                        correctOption = 1
                    ),
                    QuizQuestionEntity(
                        quizId = quizId.toInt(),
                        questionText = "Bukti asesmen dianggap 'Cukup' (Sufficient) jika...",
                        optionA = "Memenuhi seluruh elemen kompetensi dan kriteria unjuk kerja",
                        optionB = "Diketik dengan rapi",
                        optionC = "Dicetak di kertas tebal",
                        optionD = "Hanya berisi tanda tangan peserta",
                        correctOption = 0
                    )
                )
            )

            // Seed Essay Exam for Course 1
            val essayExamId = essayExamDao.insertEssayExam(
                EssayExamEntity(
                    courseId = course1Id.toInt(),
                    title = "Ujian Esai Studi Kasus: Penyusunan Perangkat Asesmen MPA",
                    instructions = "Unduh template soal PDF berikut. Analisis skenario instruksi kerja dan buatlah draft perangkat asesmen observasi praktik. Unggah jawaban dalam format PDF atau DOC.",
                    questionPdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    dueDate = "2026-08-05"
                )
            )

            // Seed sample essay submission
            essayExamDao.insertSubmission(
                EssaySubmissionEntity(
                    essayExamId = essayExamId.toInt(),
                    userId = 3, // Budi Santoso
                    userName = "Budi Santoso",
                    fileName = "Jawaban_Ujian_Esai_Budi_Santoso.pdf",
                    fileType = "PDF",
                    submittedAt = "2026-07-28 14:30",
                    status = "TERKIRIM",
                    grade = "",
                    instructorFeedback = ""
                )
            )
        }

        // Seed Dynamic Ads for Lembaga Asesmen Profesional Indonesia if empty
        val ads = dynamicAdDao.getAllAds()
        // Check if ads seeded
        dynamicAdDao.insertAd(
            DynamicAdEntity(
                title = "Pendaftaran Pelatihan & Sertifikasi Asesor Gelombang III",
                description = "Lembaga Asesmen Profesional Indonesia membuka kuota beasiswa sertifikasi profesi terakreditasi nasional. Dapatkan lisensi resmi!",
                mediaType = "GAMBAR",
                mediaUrl = "https://asesmenprofesional.id/program-sertifikasi",
                bannerImageRes = R.drawable.img_hero_banner_1785305486488,
                isActive = true,
                createdAt = "2026-07-28"
            )
        )
        dynamicAdDao.insertAd(
            DynamicAdEntity(
                title = "Video Profil Lembaga Asesmen Profesional Indonesia",
                description = "Saksikan video selayang pandang fasilitas standar laboratorium uji kompetensi modern dan ribuan alumni tersertifikasi.",
                mediaType = "VIDEO",
                mediaUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                bannerImageRes = R.drawable.img_ad_sertifikasi_1785305501941,
                isActive = true,
                createdAt = "2026-07-28"
            )
        )

        // Seed Live Classes
        liveClassDao.insertLiveClass(
            LiveClassEntity(
                courseTitle = "Sertifikasi Asesor Kompetensi Nasional BNSP",
                instructorName = "Dr. Ir. Hendra Prasetya, M.T.",
                topic = "Sesi Live Class Interactive: Mentoring Pra-Asesmen & Tanya Jawab Portofolio",
                scheduledDate = "Hari Ini",
                scheduledTime = "19:00 - 20:30 WIB",
                meetingUrl = "https://meet.jit.si/AsesmenProfesionalIndonesiaLiveClass2026",
                isLiveNow = true
            )
        )

        // Seed Academic Calendar Events
        academicCalendarDao.insertEvent(
            AcademicCalendarEventEntity(
                date = "2026-07-29",
                title = "Live Class Mentoring Asesmen Digital",
                eventType = "LIVE_CLASS",
                description = "Diskusi interaktif penyusunan portofolio bersama Dr. Hendra Prasetya"
            )
        )
        academicCalendarDao.insertEvent(
            AcademicCalendarEventEntity(
                date = "2026-08-01",
                title = "Batas Akhir Kuis Kriteria Unjuk Kerja",
                eventType = "TUGAS",
                description = "Siswa wajib menyelesaikan Kuis 1 sebelum jam 23:59 WIB"
            )
        )
        academicCalendarDao.insertEvent(
            AcademicCalendarEventEntity(
                date = "2026-08-05",
                title = "Ujian Esai Studi Kasus MPA",
                eventType = "UJIAN",
                description = "Pengumpulan berkas pdf/doc jawaban ujian esai perencanaan asesmen"
            )
        )

        // Seed Support Messages
        supportMessageDao.insertMessage(
            SupportMessageEntity(
                userId = 3,
                senderName = "Budi Santoso",
                senderRole = "SISWA",
                subject = "Kendala Mengunggah File Jawaban Esai",
                message = "Selamat siang Admin, saya mencoba mengunggah file pdf jawaban esai tapi koneksi sempat terputus. Mohon konfirmasi apakah file saya sudah masuk?",
                timestamp = "2026-07-28 15:10",
                reply = "Halo Budi, file Anda sudah berhasil terverifikasi di server kami dengan status TERKIRIM.",
                isResolved = true
            )
        )

        // Seed Daily Tasks
        dailyTaskDao.insertTask(
            DailyTaskEntity(
                userId = 3,
                title = "Materi Video: Modul 2 Metodologi Asesmen",
                category = "Belajar Mandiri",
                dueDate = "Hari Ini",
                isCompleted = false
            )
        )
        dailyTaskDao.insertTask(
            DailyTaskEntity(
                userId = 3,
                title = "Kerjakan Kuis 1 Asesmen Profesi",
                category = "Kuis Otomatis",
                dueDate = "Besok",
                isCompleted = true
            )
        )
    }
}
