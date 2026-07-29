package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        QuizEntity::class,
        QuizQuestionEntity::class,
        QuizResultEntity::class,
        EssayExamEntity::class,
        EssaySubmissionEntity::class,
        DynamicAdEntity::class,
        LiveClassEntity::class,
        AcademicCalendarEventEntity::class,
        SupportMessageEntity::class,
        DailyTaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun quizDao(): QuizDao
    abstract fun essayExamDao(): EssayExamDao
    abstract fun dynamicAdDao(): DynamicAdDao
    abstract fun liveClassDao(): LiveClassDao
    abstract fun academicCalendarDao(): AcademicCalendarDao
    abstract fun supportMessageDao(): SupportMessageDao
    abstract fun dailyTaskDao(): DailyTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "asesmen_profesional_lms.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
