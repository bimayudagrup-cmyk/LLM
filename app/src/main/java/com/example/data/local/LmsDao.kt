package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :identifier OR phone = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE category = :category")
    fun getCoursesByCategory(category: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Int): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteCourse(id: Int)
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes WHERE courseId = :courseId")
    fun getQuizzesForCourse(courseId: Int): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuiz(quizId: Int): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestionEntity>)

    @Query("SELECT * FROM quiz_results WHERE userId = :userId")
    fun getQuizResultsForUser(userId: Int): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity)
}

@Dao
interface EssayExamDao {
    @Query("SELECT * FROM essay_exams WHERE courseId = :courseId")
    fun getEssayExamsForCourse(courseId: Int): Flow<List<EssayExamEntity>>

    @Query("SELECT * FROM essay_exams")
    fun getAllEssayExams(): Flow<List<EssayExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssayExam(essayExam: EssayExamEntity): Long

    @Query("SELECT * FROM essay_submissions WHERE essayExamId = :essayExamId")
    fun getSubmissionsForExam(essayExamId: Int): Flow<List<EssaySubmissionEntity>>

    @Query("SELECT * FROM essay_submissions")
    fun getAllSubmissions(): Flow<List<EssaySubmissionEntity>>

    @Query("SELECT * FROM essay_submissions WHERE userId = :userId")
    fun getSubmissionsForUser(userId: Int): Flow<List<EssaySubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: EssaySubmissionEntity)

    @Query("UPDATE essay_submissions SET grade = :grade, instructorFeedback = :feedback, status = 'DINILAI' WHERE id = :submissionId")
    suspend fun gradeSubmission(submissionId: Int, grade: String, feedback: String)
}

@Dao
interface DynamicAdDao {
    @Query("SELECT * FROM dynamic_ads WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveAds(): Flow<List<DynamicAdEntity>>

    @Query("SELECT * FROM dynamic_ads ORDER BY id DESC")
    fun getAllAds(): Flow<List<DynamicAdEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: DynamicAdEntity): Long

    @Update
    suspend fun updateAd(ad: DynamicAdEntity)

    @Query("DELETE FROM dynamic_ads WHERE id = :id")
    suspend fun deleteAd(id: Int)
}

@Dao
interface LiveClassDao {
    @Query("SELECT * FROM live_classes ORDER BY id DESC")
    fun getAllLiveClasses(): Flow<List<LiveClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveClass(liveClass: LiveClassEntity): Long

    @Query("DELETE FROM live_classes WHERE id = :id")
    suspend fun deleteLiveClass(id: Int)
}

@Dao
interface AcademicCalendarDao {
    @Query("SELECT * FROM academic_calendar_events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<AcademicCalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AcademicCalendarEventEntity): Long
}

@Dao
interface SupportMessageDao {
    @Query("SELECT * FROM support_messages ORDER BY id DESC")
    fun getAllMessages(): Flow<List<SupportMessageEntity>>

    @Query("SELECT * FROM support_messages WHERE userId = :userId ORDER BY id DESC")
    fun getMessagesForUser(userId: Int): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SupportMessageEntity): Long

    @Query("UPDATE support_messages SET reply = :reply, isResolved = 1 WHERE id = :id")
    suspend fun replyMessage(id: Int, reply: String)
}

@Dao
interface DailyTaskDao {
    @Query("SELECT * FROM daily_tasks WHERE userId = :userId ORDER BY dueDate ASC")
    fun getTasksForUser(userId: Int): Flow<List<DailyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DailyTaskEntity)

    @Query("UPDATE daily_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Int, isCompleted: Boolean)
}
