package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.StudentEntity
import com.example.ui.components.ReportCardModal
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamReportCardScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val exams by viewModel.exams.collectAsState()
    val students by viewModel.students.collectAsState()

    var showAddExamDialog by remember { mutableStateOf(false) }
    var showEnterMarksDialog by remember { mutableStateOf(false) }

    var selectedStudentForReportCard by remember { mutableStateOf<StudentEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Exams & Report Cards",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = { showAddExamDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("New Exam", fontSize = 12.sp)
                }
                Button(onClick = { showEnterMarksDialog = true }) {
                    Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Enter Marks", fontSize = 12.sp)
                }
            }
        }

        // Exam List Section
        Text("Scheduled / Completed Examinations (${exams.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        LazyColumn(
            modifier = Modifier.height(140.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(exams) { ex ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = ex.examName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Class: ${ex.className} | Subject: ${ex.subject} | Max Marks: ${ex.maxMarks}", fontSize = 11.sp)
                        }
                        Text(text = ex.examDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Divider()

        // Generate Report Card Section
        Text("Select Student to View Official GBVN Report Card:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { st ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedStudentForReportCard = st },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = st.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Roll No: ${st.rollNo} | Class: ${st.className}", fontSize = 12.sp, color = Color.Gray)
                        }

                        Button(
                            onClick = { selectedStudentForReportCard = st },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Report Card", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddExamDialog) {
        var examName by remember { mutableStateOf("Quarterly Exam 2026") }
        var className by remember { mutableStateOf("Class 8") }
        var subject by remember { mutableStateOf("Mathematics") }
        var maxMarks by remember { mutableStateOf("100") }
        var examDate by remember { mutableStateOf("2026-08-10") }

        AlertDialog(
            onDismissRequest = { showAddExamDialog = false },
            title = { Text("Schedule Examination") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = examName, onValueChange = { examName = it }, label = { Text("Exam Name") })
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class") })
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
                    OutlinedTextField(value = maxMarks, onValueChange = { maxMarks = it }, label = { Text("Max Marks") })
                    OutlinedTextField(value = examDate, onValueChange = { examDate = it }, label = { Text("Exam Date") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (examName.isNotBlank()) {
                        viewModel.addExam(examName, className, subject, maxMarks.toIntOrNull() ?: 100, examDate)
                        Toast.makeText(context, "Exam scheduled!", Toast.LENGTH_SHORT).show()
                        showAddExamDialog = false
                    }
                }) {
                    Text("Save Exam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExamDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEnterMarksDialog) {
        var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
        var subject by remember { mutableStateOf("Mathematics") }
        var marksObtained by remember { mutableStateOf("88") }
        var maxMarks by remember { mutableStateOf("100") }
        var remarks by remember { mutableStateOf("Good Performance") }

        AlertDialog(
            onDismissRequest = { showEnterMarksDialog = false },
            title = { Text("Enter Exam Marks") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Student:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    students.take(5).forEach { st ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedStudent == st, onClick = { selectedStudent = st })
                            Text("${st.name} (${st.className})", fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
                    OutlinedTextField(value = marksObtained, onValueChange = { marksObtained = it }, label = { Text("Marks Obtained") })
                    OutlinedTextField(value = maxMarks, onValueChange = { maxMarks = it }, label = { Text("Max Marks") })
                    OutlinedTextField(value = remarks, onValueChange = { remarks = it }, label = { Text("Remarks") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val st = selectedStudent
                    if (st != null) {
                        viewModel.enterExamResult(
                            examId = 1,
                            examName = "Quarterly Examination 2026",
                            studentId = st.id,
                            studentName = st.name,
                            className = st.className,
                            subject = subject,
                            marksObtained = marksObtained.toIntOrNull() ?: 0,
                            maxMarks = maxMarks.toIntOrNull() ?: 100,
                            remarks = remarks
                        )
                        Toast.makeText(context, "Marks saved for ${st.name}!", Toast.LENGTH_SHORT).show()
                        showEnterMarksDialog = false
                    }
                }) {
                    Text("Submit Marks")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnterMarksDialog = false }) { Text("Cancel") }
            }
        )
    }

    selectedStudentForReportCard?.let { st ->
        ReportCardModal(
            student = st,
            results = emptyList(), // DAO reactive results can be displayed
            onDismiss = { selectedStudentForReportCard = null }
        )
    }
}
