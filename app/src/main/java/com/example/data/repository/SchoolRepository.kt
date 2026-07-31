package com.example.data.repository

import com.example.data.dao.SchoolDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val dao: SchoolDao) {

    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>> = dao.getStudentsByClass(className)
    suspend fun getStudentById(id: Int): StudentEntity? = dao.getStudentById(id)
    fun searchStudents(query: String): Flow<List<StudentEntity>> = dao.searchStudents(query)
    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)
    suspend fun deleteStudent(id: Int) = dao.deleteStudent(id)

    // Parents
    val allParents: Flow<List<ParentEntity>> = dao.getAllParents()
    suspend fun getParentByStudentId(studentId: Int): ParentEntity? = dao.getParentByStudentId(studentId)
    suspend fun insertParent(parent: ParentEntity): Long = dao.insertParent(parent)

    // Teachers
    val allTeachers: Flow<List<TeacherEntity>> = dao.getAllTeachers()
    suspend fun insertTeacher(teacher: TeacherEntity): Long = dao.insertTeacher(teacher)
    suspend fun deleteTeacher(id: Int) = dao.deleteTeacher(id)

    // Staff
    val allStaff: Flow<List<StaffEntity>> = dao.getAllStaff()
    suspend fun insertStaff(staff: StaffEntity): Long = dao.insertStaff(staff)
    suspend fun deleteStaff(id: Int) = dao.deleteStaff(id)

    // Classes
    val allClasses: Flow<List<SchoolClassEntity>> = dao.getAllClasses()
    suspend fun insertClass(schoolClass: SchoolClassEntity): Long = dao.insertClass(schoolClass)

    // Fees
    val allFees: Flow<List<FeeRecordEntity>> = dao.getAllFeeRecords()
    fun getFeesForStudent(studentId: Int): Flow<List<FeeRecordEntity>> = dao.getFeesForStudent(studentId)
    suspend fun insertFeeRecord(fee: FeeRecordEntity): Long = dao.insertFeeRecord(fee)

    // Bus Routes
    val allBusRoutes: Flow<List<BusRouteEntity>> = dao.getAllBusRoutes()
    suspend fun insertBusRoute(busRoute: BusRouteEntity): Long = dao.insertBusRoute(busRoute)

    // Attendance
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>> = dao.getAttendanceByDate(date)
    fun getAttendanceForPerson(personId: Int, personType: String): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForPerson(personId, personType)
    suspend fun insertAttendance(attendance: AttendanceEntity) = dao.insertAttendance(attendance)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>) = dao.insertAttendanceList(list)

    // Salary
    val allSalaries: Flow<List<SalaryRecordEntity>> = dao.getAllSalaryRecords()
    suspend fun insertSalaryRecord(salary: SalaryRecordEntity): Long = dao.insertSalaryRecord(salary)

    // Income
    val allIncome: Flow<List<IncomeEntity>> = dao.getAllIncome()
    suspend fun insertIncome(income: IncomeEntity): Long = dao.insertIncome(income)
    suspend fun deleteIncome(id: Int) = dao.deleteIncome(id)

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity): Long = dao.insertExpense(expense)
    suspend fun deleteExpense(id: Int) = dao.deleteExpense(id)

    // Exams & Results
    val allExams: Flow<List<ExamEntity>> = dao.getAllExams()
    suspend fun insertExam(exam: ExamEntity): Long = dao.insertExam(exam)
    fun getResultsForExam(examId: Int): Flow<List<ExamResultEntity>> = dao.getResultsForExam(examId)
    fun getResultsForStudent(studentId: Int): Flow<List<ExamResultEntity>> = dao.getResultsForStudent(studentId)
    suspend fun insertExamResult(result: ExamResultEntity): Long = dao.insertExamResult(result)

    // Timetable
    fun getTimetableForClass(className: String): Flow<List<TimetableEntity>> = dao.getTimetableForClass(className)
    suspend fun insertTimetableEntry(entry: TimetableEntity): Long = dao.insertTimetableEntry(entry)

    // Homework
    val allHomework: Flow<List<HomeworkEntity>> = dao.getAllHomework()
    fun getHomeworkForClass(className: String): Flow<List<HomeworkEntity>> = dao.getHomeworkForClass(className)
    suspend fun insertHomework(homework: HomeworkEntity): Long = dao.insertHomework(homework)

    // Notices
    val allNotices: Flow<List<NoticeEntity>> = dao.getAllNotices()
    suspend fun insertNotice(notice: NoticeEntity): Long = dao.insertNotice(notice)
    suspend fun deleteNotice(id: Int) = dao.deleteNotice(id)

    // Seed Demo Data for Gayatri Bal Vidhya Niketan
    suspend fun seedDemoData() {
        dao.clearStudents()
        dao.clearTeachers()
        dao.clearStaff()
        dao.clearFees()
        dao.clearExpenses()
        dao.clearIncome()
        dao.clearNotices()
        dao.clearBusRoutes()
        dao.clearExams()
        dao.clearResults()
        dao.clearTimetable()
        dao.clearHomework()

        // 1. Classes
        val classList = listOf("KG-1", "KG-2", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")
        classList.forEach { className ->
            dao.insertClass(SchoolClassEntity(className = className, section = "A", classTeacherName = "Teacher $className"))
        }

        // 2. Bus Routes
        val r1 = dao.insertBusRoute(BusRouteEntity(routeName = "Route 1: Shahnagar - Mohindra Road", vehicleNo = "MP-35-P-1008", driverName = "Ramlal Sahu", driverPhone = "9826123456", monthlyFee = 600.0))
        val r2 = dao.insertBusRoute(BusRouteEntity(routeName = "Route 2: Shahnagar - Amanganj Highway", vehicleNo = "MP-35-P-1009", driverName = "Santosh Yadav", driverPhone = "9826987654", monthlyFee = 750.0))
        val r3 = dao.insertBusRoute(BusRouteEntity(routeName = "Route 3: Kakarhati - Town Circle", vehicleNo = "MP-35-P-1010", driverName = "Dinesh Soni", driverPhone = "9826554433", monthlyFee = 800.0))

        // 3. Teachers
        val t1 = dao.insertTeacher(TeacherEntity(name = "Ramesh Sharma", phone = "9425112233", email = "ramesh.sharma@gbvn.edu.in", qualification = "M.Sc Mathematics, B.Ed", primarySubject = "Mathematics", assignedClass = "Class 8", monthlySalary = 18000.0))
        val t2 = dao.insertTeacher(TeacherEntity(name = "Sunita Verma", phone = "9425223344", email = "sunita.v@gbvn.edu.in", qualification = "M.A. English, B.Ed", primarySubject = "English", assignedClass = "Class 5", monthlySalary = 16000.0))
        val t3 = dao.insertTeacher(TeacherEntity(name = "Manoj Kumar Patel", phone = "9425334455", email = "manoj.p@gbvn.edu.in", qualification = "B.Sc Physics, B.Ed", primarySubject = "Science", assignedClass = "Class 7", monthlySalary = 17000.0))
        val t4 = dao.insertTeacher(TeacherEntity(name = "Anita Singh", phone = "9425445566", email = "anita.s@gbvn.edu.in", qualification = "M.A. History, B.Ed", primarySubject = "Social Science", assignedClass = "Class 6", monthlySalary = 15500.0))

        // 4. Staff
        val s1 = dao.insertStaff(StaffEntity(name = "Shivam Dubey", phone = "9893110022", role = "Accountant", monthlySalary = 14000.0, joinDate = "2022-04-01"))
        val s2 = dao.insertStaff(StaffEntity(name = "Ramlal Sahu", phone = "9826123456", role = "Bus Driver", monthlySalary = 11000.0, joinDate = "2021-06-15"))
        val s3 = dao.insertStaff(StaffEntity(name = "Geeta Bai", phone = "9893445566", role = "Support Staff", monthlySalary = 8000.0, joinDate = "2020-07-01"))

        // 5. Students & Parents
        val sampleStudents = listOf(
            StudentEntity(rollNo = "101", name = "Aarav Sharma", className = "Class 8", section = "A", parentName = "Rajesh Sharma", parentPhone = "9827011223", address = "Main Market, Shahnagar, Panna", dateOfBirth = "2012-05-14", gender = "Male", admissionDate = "2018-04-10", busRouteId = r1.toInt()),
            StudentEntity(rollNo = "102", name = "Ananya Patel", className = "Class 8", section = "A", parentName = "Mahesh Patel", parentPhone = "9827022334", address = "Mohindra Road, Shahnagar", dateOfBirth = "2012-08-20", gender = "Female", admissionDate = "2018-04-12", busRouteId = r1.toInt()),
            StudentEntity(rollNo = "103", name = "Devansh Verma", className = "Class 7", section = "A", parentName = "Prakash Verma", parentPhone = "9827033445", address = "Amanganj Road, Shahnagar", dateOfBirth = "2013-02-11", gender = "Male", admissionDate = "2019-04-05", busRouteId = r2.toInt()),
            StudentEntity(rollNo = "104", name = "Isha Singh", className = "Class 6", section = "A", parentName = "Vikram Singh", parentPhone = "9827044556", address = "Near Bus Stand, Shahnagar", dateOfBirth = "2014-11-03", gender = "Female", admissionDate = "2020-04-08", busRouteId = null),
            StudentEntity(rollNo = "105", name = "Kavya Soni", className = "Class 5", section = "A", parentName = "Rakesh Soni", parentPhone = "9827055667", address = "Town Circle, Kakarhati", dateOfBirth = "2015-07-25", gender = "Female", admissionDate = "2021-04-15", busRouteId = r3.toInt()),
            StudentEntity(rollNo = "106", name = "Rohan Gupta", className = "Class 4", section = "A", parentName = "Sanjay Gupta", parentPhone = "9827066778", address = "Station Road, Shahnagar", dateOfBirth = "2016-01-19", gender = "Male", admissionDate = "2022-04-01", busRouteId = r1.toInt()),
            StudentEntity(rollNo = "107", name = "Sneha Chourasia", className = "Class 3", section = "A", parentName = "Pankaj Chourasia", parentPhone = "9827077889", address = "Panna Road, Shahnagar", dateOfBirth = "2017-09-09", gender = "Female", admissionDate = "2023-04-10", busRouteId = null),
            StudentEntity(rollNo = "108", name = "Vivaan Jain", className = "KG-2", section = "A", parentName = "Amit Jain", parentPhone = "9827088990", address = "Shahnagar Market", dateOfBirth = "2019-12-01", gender = "Male", admissionDate = "2024-04-02", busRouteId = null)
        )

        sampleStudents.forEach { st ->
            val stId = dao.insertStudent(st).toInt()
            dao.insertParent(ParentEntity(studentId = stId, name = st.parentName, phone = st.parentPhone, address = st.address, occupation = "Business/Agriculture"))

            // Sample Fee Collections
            dao.insertFeeRecord(FeeRecordEntity(
                receiptNo = "GBVN-2026-${1000 + stId}",
                studentId = stId,
                studentName = st.name,
                className = st.className,
                feeType = "Tuition & Bus Fee",
                month = "July",
                year = 2026,
                totalAmount = 1800.0,
                discount = 100.0,
                paidAmount = 1700.0,
                dueAmount = 0.0,
                paymentMode = "UPI / Cash",
                paymentDate = "2026-07-10",
                remarks = "Paid in full"
            ))
        }

        // 6. Expenses
        dao.insertExpense(ExpenseEntity(title = "Electricity Bill July", category = "Electricity", amount = 4250.0, date = "2026-07-15", description = "M.P. Electricity Board payment"))
        dao.insertExpense(ExpenseEntity(title = "Bus Diesel Refill", category = "Transport", amount = 8500.0, date = "2026-07-18", description = "Fuel for MP-35-P-1008"))
        dao.insertExpense(ExpenseEntity(title = "Printing Exam Sheets & Prospectus", category = "Stationery", amount = 3100.0, date = "2026-07-22", description = "Stationery store Shahnagar"))

        // 7. Income
        dao.insertIncome(IncomeEntity(title = "Prospectus & Form Sales", category = "Prospectus", amount = 6500.0, date = "2026-07-05", description = "New admissions form fee"))
        dao.insertIncome(IncomeEntity(title = "School Uniform & Tie Sales", category = "Other", amount = 12400.0, date = "2026-07-12", description = "Counter sale"))

        // 8. Notices
        dao.insertNotice(NoticeEntity(title = "Independence Day Celebration 2026", content = "Gayatri Bal Vidhya Niketan will celebrate Independence Day on 15th August at 8:00 AM. Flag hoisting followed by cultural performances by students. Attendance mandatory.", targetAudience = "ALL", date = "2026-07-28", isUrgent = true))
        dao.insertNotice(NoticeEntity(title = "Parent Teacher Meeting (PTM)", content = "PTM for Class KG-1 to Class 8 will be held on Saturday, 5th August 2026 from 9:00 AM to 1:00 PM. Parents are requested to collect quarterly report cards.", targetAudience = "PARENTS", date = "2026-07-25", isUrgent = false))

        // 9. Homework
        dao.insertHomework(HomeworkEntity(className = "Class 8", subject = "Mathematics", title = "Linear Equations Ex 3.2", description = "Complete question 1 to 10 in fair notebook.", assignedDate = "2026-07-30", dueDate = "2026-08-02", teacherName = "Ramesh Sharma"))
        dao.insertHomework(HomeworkEntity(className = "Class 5", subject = "English", title = "Chapter 4 Poem Recitation", description = "Learn poem 'The Arrow and the Song' and write summary.", assignedDate = "2026-07-30", dueDate = "2026-08-01", teacherName = "Sunita Verma"))

        // 10. Timetable
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val subjectsClass8 = listOf("Mathematics", "English", "Science", "Social Science", "Hindi", "Sanskrit / Computer")
        days.forEach { day ->
            subjectsClass8.forEachIndexed { idx, sub ->
                dao.insertTimetableEntry(TimetableEntity(className = "Class 8", dayOfWeek = day, periodNumber = idx + 1, subjectName = sub, teacherName = "Teacher $sub"))
            }
        }

        // 11. Exam & Results
        val exId = dao.insertExam(ExamEntity(examName = "Quarterly Examination 2026", className = "Class 8", subject = "Mathematics", maxMarks = 100, examDate = "2026-07-15")).toInt()
        dao.insertExamResult(ExamResultEntity(examId = exId, examName = "Quarterly Examination 2026", studentId = 1, studentName = "Aarav Sharma", className = "Class 8", subject = "Mathematics", marksObtained = 92, maxMarks = 100, grade = "A+", remarks = "Excellent Performance"))
        dao.insertExamResult(ExamResultEntity(examId = exId, examName = "Quarterly Examination 2026", studentId = 2, studentName = "Ananya Patel", className = "Class 8", subject = "Mathematics", marksObtained = 88, maxMarks = 100, grade = "A", remarks = "Very Good"))
    }
}
