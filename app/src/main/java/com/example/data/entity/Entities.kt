package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rollNo: String,
    val name: String,
    val className: String,
    val section: String = "A",
    val parentName: String,
    val parentPhone: String,
    val address: String,
    val dateOfBirth: String,
    val gender: String,
    val admissionDate: String,
    val busRouteId: Int? = null,
    val photoUri: String? = null
)

@Entity(tableName = "parents")
data class ParentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val name: String,
    val phone: String,
    val email: String = "",
    val occupation: String = "",
    val address: String
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val qualification: String,
    val primarySubject: String,
    val assignedClass: String,
    val monthlySalary: Double
)

@Entity(tableName = "staff")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val role: String, // e.g., Accountant, Peon, Driver
    val monthlySalary: Double,
    val joinDate: String
)

@Entity(tableName = "classes")
data class SchoolClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val section: String = "A",
    val classTeacherName: String = ""
)

@Entity(tableName = "fees")
data class FeeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNo: String,
    val studentId: Int,
    val studentName: String,
    val className: String,
    val feeType: String, // e.g. Tuition Fee, Bus Fee, Annual Fee
    val month: String,
    val year: Int,
    val totalAmount: Double,
    val discount: Double = 0.0,
    val paidAmount: Double,
    val dueAmount: Double,
    val paymentMode: String, // Cash, UPI, Bank Transfer
    val paymentDate: String,
    val remarks: String = ""
)

@Entity(tableName = "bus_routes")
data class BusRouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeName: String,
    val vehicleNo: String,
    val driverName: String,
    val driverPhone: String,
    val monthlyFee: Double
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personId: Int,
    val personType: String, // "STUDENT", "TEACHER", "STAFF"
    val personName: String,
    val className: String? = null,
    val date: String, // YYYY-MM-DD
    val status: String // "PRESENT", "ABSENT", "LATE", "LEAVE"
)

@Entity(tableName = "salary_records")
data class SalaryRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personId: Int,
    val personName: String,
    val personRole: String, // "TEACHER", "STAFF"
    val month: String,
    val year: Int,
    val basicSalary: Double,
    val bonus: Double = 0.0,
    val deductions: Double = 0.0,
    val netPaid: Double,
    val paymentDate: String,
    val paymentMode: String
)

@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Grants, Donations, Prospectus, Other
    val amount: Double,
    val date: String,
    val description: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Electricity, Maintenance, Stationeries, Events, etc.
    val amount: Double,
    val date: String,
    val description: String = ""
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examName: String, // e.g., Quarterly, Half-Yearly, Annual
    val className: String,
    val subject: String,
    val maxMarks: Int,
    val examDate: String
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: Int,
    val examName: String,
    val studentId: Int,
    val studentName: String,
    val className: String,
    val subject: String,
    val marksObtained: Int,
    val maxMarks: Int,
    val grade: String,
    val remarks: String = ""
)

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val dayOfWeek: String, // Monday, Tuesday, etc.
    val periodNumber: Int,
    val subjectName: String,
    val teacherName: String
)

@Entity(tableName = "homework")
data class HomeworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val subject: String,
    val title: String,
    val description: String,
    val assignedDate: String,
    val dueDate: String,
    val teacherName: String
)

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val targetAudience: String, // "ALL", "PARENTS", "TEACHERS"
    val date: String,
    val isUrgent: Boolean = false
)
