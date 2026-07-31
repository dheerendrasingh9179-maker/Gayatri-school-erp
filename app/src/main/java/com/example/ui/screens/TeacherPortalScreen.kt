package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TeacherPortalScreen(
    viewModel: MainViewModel,
    onNavigateToAttendance: () -> Unit,
    onNavigateToHomework: () -> Unit,
    onNavigateToExams: () -> Unit
) {
    val teachers by viewModel.teachers.collectAsState()
    var selectedTeacher by remember { mutableStateOf(teachers.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Teacher Welcome Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Teacher Faculty Portal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Gayatri Bal Vidhya Niketan, Shahnagar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                    Icon(Icons.Default.CoPresent, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Select Teacher Profile:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    teachers.take(3).forEach { t ->
                        FilterChip(
                            selected = selectedTeacher?.id == t.id,
                            onClick = { selectedTeacher = t },
                            label = { Text(t.name) }
                        )
                    }
                }
            }
        }

        val teacher = selectedTeacher
        if (teacher != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(teacher.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Subject: ${teacher.primarySubject} | Class Teacher: ${teacher.assignedClass}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Qualification: ${teacher.qualification} | Salary: ₹${teacher.monthlySalary.toInt()}/mo", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Text("Teacher Quick Operations:", fontWeight = FontWeight.Bold, fontSize = 15.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateToAttendance,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Mark Attendance", fontSize = 11.sp)
                }
            }

            Button(
                onClick = onNavigateToHomework,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Assign Homework", fontSize = 11.sp)
                }
            }

            Button(
                onClick = onNavigateToExams,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Enter Exam Marks", fontSize = 11.sp)
                }
            }
        }
    }
}
