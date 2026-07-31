package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.SchoolDao
import com.example.data.entity.*

@Database(
    entities = [
        StudentEntity::class,
        ParentEntity::class,
        TeacherEntity::class,
        StaffEntity::class,
        SchoolClassEntity::class,
        FeeRecordEntity::class,
        BusRouteEntity::class,
        AttendanceEntity::class,
        SalaryRecordEntity::class,
        IncomeEntity::class,
        ExpenseEntity::class,
        ExamEntity::class,
        ExamResultEntity::class,
        TimetableEntity::class,
        HomeworkEntity::class,
        NoticeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gbvn_erp_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
