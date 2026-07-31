package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    // --- STUDENTS ---
    @Query("SELECT * FROM students ORDER BY className, rollNo")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY rollNo")
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR rollNo LIKE '%' || :query || '%' OR parentName LIKE '%' || :query || '%' ORDER BY name")
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudent(id: Int)

    // --- PARENTS ---
    @Query("SELECT * FROM parents")
    fun getAllParents(): Flow<List<ParentEntity>>

    @Query("SELECT * FROM parents WHERE studentId = :studentId LIMIT 1")
    suspend fun getParentByStudentId(studentId: Int): ParentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParent(parent: ParentEntity): Long

    // --- TEACHERS ---
    @Query("SELECT * FROM teachers ORDER BY name")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Query("DELETE FROM teachers WHERE id = :id")
    suspend fun deleteTeacher(id: Int)

    // --- STAFF ---
    @Query("SELECT * FROM staff ORDER BY name")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity): Long

    @Query("DELETE FROM staff WHERE id = :id")
    suspend fun deleteStaff(id: Int)

    // --- CLASSES ---
    @Query("SELECT * FROM classes ORDER BY id")
    fun getAllClasses(): Flow<List<SchoolClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(schoolClass: SchoolClassEntity): Long

    // --- FEES ---
    @Query("SELECT * FROM fees ORDER BY id DESC")
    fun getAllFeeRecords(): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fees WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeesForStudent(studentId: Int): Flow<List<FeeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(fee: FeeRecordEntity): Long

    // --- BUS ROUTES ---
    @Query("SELECT * FROM bus_routes ORDER BY routeName")
    fun getAllBusRoutes(): Flow<List<BusRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusRoute(busRoute: BusRouteEntity): Long

    // --- ATTENDANCE ---
    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY personName")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE personId = :personId AND personType = :personType ORDER BY date DESC")
    fun getAttendanceForPerson(personId: Int, personType: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    // --- SALARY ---
    @Query("SELECT * FROM salary_records ORDER BY id DESC")
    fun getAllSalaryRecords(): Flow<List<SalaryRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalaryRecord(salary: SalaryRecordEntity): Long

    // --- INCOME ---
    @Query("SELECT * FROM income ORDER BY date DESC")
    fun getAllIncome(): Flow<List<IncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity): Long

    @Query("DELETE FROM income WHERE id = :id")
    suspend fun deleteIncome(id: Int)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Int)

    // --- EXAMS & RESULTS ---
    @Query("SELECT * FROM exams ORDER BY examDate DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Query("SELECT * FROM exam_results WHERE examId = :examId ORDER BY studentName")
    fun getResultsForExam(examId: Int): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY id DESC")
    fun getResultsForStudent(studentId: Int): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResults(results: List<ExamResultEntity>)

    // --- TIMETABLE ---
    @Query("SELECT * FROM timetable WHERE className = :className ORDER BY periodNumber")
    fun getTimetableForClass(className: String): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableEntry(entry: TimetableEntity): Long

    // --- HOMEWORK ---
    @Query("SELECT * FROM homework WHERE className = :className ORDER BY assignedDate DESC")
    fun getHomeworkForClass(className: String): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework ORDER BY assignedDate DESC")
    fun getAllHomework(): Flow<List<HomeworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkEntity): Long

    // --- NOTICES ---
    @Query("SELECT * FROM notices ORDER BY isUrgent DESC, date DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEntity): Long

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun deleteNotice(id: Int)

    // --- SEED CLEAR ---
    @Query("DELETE FROM students") suspend fun clearStudents()
    @Query("DELETE FROM teachers") suspend fun clearTeachers()
    @Query("DELETE FROM staff") suspend fun clearStaff()
    @Query("DELETE FROM fees") suspend fun clearFees()
    @Query("DELETE FROM expenses") suspend fun clearExpenses()
    @Query("DELETE FROM income") suspend fun clearIncome()
    @Query("DELETE FROM notices") suspend fun clearNotices()
    @Query("DELETE FROM bus_routes") suspend fun clearBusRoutes()
    @Query("DELETE FROM exams") suspend fun clearExams()
    @Query("DELETE FROM exam_results") suspend fun clearResults()
    @Query("DELETE FROM timetable") suspend fun clearTimetable()
    @Query("DELETE FROM homework") suspend fun clearHomework()
}
