package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.auth.UserAccount
import com.example.data.database.AppDatabase
import com.example.data.entity.*
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository
    init {
        val dao = AppDatabase.getDatabase(application).schoolDao()
        repository = SchoolRepository(dao)

        // Seed demo data if database is empty on first launch
        viewModelScope.launch {
            repository.allStudents.firstOrNull().let { list ->
                if (list.isNullOrEmpty()) {
                    repository.seedDemoData()
                }
            }
        }
    }

    // Role & View State
    val currentRole = MutableStateFlow("ADMIN") // "ADMIN", "TEACHER", "PARENT"
    val currentUserAccount = MutableStateFlow<UserAccount?>(null)
    val isLoggedIn = MutableStateFlow(false)
    val isWebAdminView = MutableStateFlow(false) // Desktop / Web Admin Panel layout mode

    fun setLoggedInUser(user: UserAccount) {
        currentUserAccount.value = user
        currentRole.value = user.role
        isLoggedIn.value = true
    }

    fun logoutUser() {
        AuthManager.signOut(getApplication())
        currentUserAccount.value = null
        isLoggedIn.value = false
    }
    val searchQuery = MutableStateFlow("")
    val selectedClassFilter = MutableStateFlow("ALL")
    val selectedStudentForDetail = MutableStateFlow<StudentEntity?>(null)
    val selectedFeeForReceipt = MutableStateFlow<FeeRecordEntity?>(null)
    val selectedStudentForReportCard = MutableStateFlow<StudentEntity?>(null)

    // Data Streams
    val students: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teachers: StateFlow<List<TeacherEntity>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staff: StateFlow<List<StaffEntity>> = repository.allStaff
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feeRecords: StateFlow<List<FeeRecordEntity>> = repository.allFees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val busRoutes: StateFlow<List<BusRouteEntity>> = repository.allBusRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val income: StateFlow<List<IncomeEntity>> = repository.allIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<NoticeEntity>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val homework: StateFlow<List<HomeworkEntity>> = repository.allHomework
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salaries: StateFlow<List<SalaryRecordEntity>> = repository.allSalaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayAttendance: StateFlow<List<AttendanceEntity>> = repository.getAttendanceByDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setRole(role: String) {
        currentRole.value = role
    }

    fun toggleWebAdminView() {
        isWebAdminView.value = !isWebAdminView.value
    }

    fun addStudent(
        name: String,
        rollNo: String,
        className: String,
        section: String,
        parentName: String,
        parentPhone: String,
        address: String,
        dob: String,
        gender: String,
        busRouteId: Int?
    ) {
        viewModelScope.launch {
            val student = StudentEntity(
                rollNo = rollNo,
                name = name,
                className = className,
                section = section,
                parentName = parentName,
                parentPhone = parentPhone,
                address = address,
                dateOfBirth = dob,
                gender = gender,
                admissionDate = todayDate,
                busRouteId = busRouteId
            )
            val id = repository.insertStudent(student).toInt()
            repository.insertParent(ParentEntity(studentId = id, name = parentName, phone = parentPhone, address = address))
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(id: Int) {
        viewModelScope.launch {
            repository.deleteStudent(id)
        }
    }

    fun addTeacher(name: String, phone: String, email: String, qualification: String, subject: String, assignedClass: String, salary: Double) {
        viewModelScope.launch {
            repository.insertTeacher(TeacherEntity(
                name = name, phone = phone, email = email, qualification = qualification, primarySubject = subject, assignedClass = assignedClass, monthlySalary = salary
            ))
        }
    }

    fun deleteTeacher(id: Int) {
        viewModelScope.launch { repository.deleteTeacher(id) }
    }

    fun addStaff(name: String, phone: String, role: String, salary: Double) {
        viewModelScope.launch {
            repository.insertStaff(StaffEntity(name = name, phone = phone, role = role, monthlySalary = salary, joinDate = todayDate))
        }
    }

    fun deleteStaff(id: Int) {
        viewModelScope.launch { repository.deleteStaff(id) }
    }

    fun addFeeRecord(studentId: Int, studentName: String, className: String, feeType: String, month: String, totalAmt: Double, discount: Double, paidAmt: Double, paymentMode: String) {
        viewModelScope.launch {
            val receiptNo = "GBVN-${System.currentTimeMillis() % 100000}"
            val due = (totalAmt - discount - paidAmt).coerceAtLeast(0.0)
            repository.insertFeeRecord(FeeRecordEntity(
                receiptNo = receiptNo,
                studentId = studentId,
                studentName = studentName,
                className = className,
                feeType = feeType,
                month = month,
                year = 2026,
                totalAmount = totalAmt,
                discount = discount,
                paidAmount = paidAmt,
                dueAmount = due,
                paymentMode = paymentMode,
                paymentDate = todayDate
            ))
        }
    }

    fun addBusRoute(routeName: String, vehicleNo: String, driverName: String, driverPhone: String, monthlyFee: Double) {
        viewModelScope.launch {
            repository.insertBusRoute(BusRouteEntity(routeName = routeName, vehicleNo = vehicleNo, driverName = driverName, driverPhone = driverPhone, monthlyFee = monthlyFee))
        }
    }

    fun markAttendance(personId: Int, personType: String, personName: String, className: String?, status: String) {
        viewModelScope.launch {
            repository.insertAttendance(AttendanceEntity(
                personId = personId,
                personType = personType,
                personName = personName,
                className = className,
                date = todayDate,
                status = status
            ))
        }
    }

    fun addSalaryRecord(personId: Int, personName: String, personRole: String, basicSalary: Double, bonus: Double, deductions: Double, paymentMode: String) {
        viewModelScope.launch {
            val net = basicSalary + bonus - deductions
            repository.insertSalaryRecord(SalaryRecordEntity(
                personId = personId,
                personName = personName,
                personRole = personRole,
                month = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date()),
                year = 2026,
                basicSalary = basicSalary,
                bonus = bonus,
                deductions = deductions,
                netPaid = net,
                paymentDate = todayDate,
                paymentMode = paymentMode
            ))
        }
    }

    fun addIncome(title: String, category: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertIncome(IncomeEntity(title = title, category = category, amount = amount, date = todayDate, description = description))
        }
    }

    fun addExpense(title: String, category: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertExpense(ExpenseEntity(title = title, category = category, amount = amount, date = todayDate, description = description))
        }
    }

    fun addHomework(className: String, subject: String, title: String, description: String, dueDate: String, teacherName: String) {
        viewModelScope.launch {
            repository.insertHomework(HomeworkEntity(
                className = className, subject = subject, title = title, description = description, assignedDate = todayDate, dueDate = dueDate, teacherName = teacherName
            ))
        }
    }

    fun addNotice(title: String, content: String, targetAudience: String, isUrgent: Boolean) {
        viewModelScope.launch {
            repository.insertNotice(NoticeEntity(title = title, content = content, targetAudience = targetAudience, date = todayDate, isUrgent = isUrgent))
        }
    }

    fun deleteNotice(id: Int) {
        viewModelScope.launch { repository.deleteNotice(id) }
    }

    fun addExam(examName: String, className: String, subject: String, maxMarks: Int, examDate: String) {
        viewModelScope.launch {
            repository.insertExam(ExamEntity(examName = examName, className = className, subject = subject, maxMarks = maxMarks, examDate = examDate))
        }
    }

    fun enterExamResult(examId: Int, examName: String, studentId: Int, studentName: String, className: String, subject: String, marksObtained: Int, maxMarks: Int, remarks: String) {
        viewModelScope.launch {
            val pct = (marksObtained.toDouble() / maxMarks) * 100
            val grade = when {
                pct >= 90 -> "A+"
                pct >= 75 -> "A"
                pct >= 60 -> "B"
                pct >= 50 -> "C"
                pct >= 33 -> "D"
                else -> "F"
            }
            repository.insertExamResult(ExamResultEntity(
                examId = examId, examName = examName, studentId = studentId, studentName = studentName, className = className, subject = subject, marksObtained = marksObtained, maxMarks = maxMarks, grade = grade, remarks = remarks
            ))
        }
    }

    fun resetAndSeedDemo() {
        viewModelScope.launch {
            repository.seedDemoData()
        }
    }

    // Export database backup JSON string
    fun exportBackupJson(): String {
        val root = JSONObject()
        val stArray = JSONArray()
        students.value.forEach { st ->
            val obj = JSONObject()
            obj.put("id", st.id)
            obj.put("name", st.name)
            obj.put("rollNo", st.rollNo)
            obj.put("className", st.className)
            obj.put("parentName", st.parentName)
            obj.put("parentPhone", st.parentPhone)
            stArray.put(obj)
        }
        root.put("school", "Gayatri Bal Vidhya Niketan")
        root.put("students", stArray)
        root.put("exportDate", todayDate)
        return root.toString(2)
    }
}
