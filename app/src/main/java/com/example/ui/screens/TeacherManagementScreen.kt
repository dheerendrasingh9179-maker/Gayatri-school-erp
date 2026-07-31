package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherManagementScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val teachers by viewModel.teachers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

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
                text = "Faculty Teachers (${teachers.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Teacher")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(teachers) { teacher ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = teacher.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Subject: ${teacher.primarySubject} | Class: ${teacher.assignedClass}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteTeacher(teacher.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Qual: ${teacher.qualification}", fontSize = 12.sp)
                            Text(text = "Phone: ${teacher.phone}", fontSize = 12.sp)
                            Text(text = "Salary: ₹${teacher.monthlySalary.toInt()}/mo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var qual by remember { mutableStateOf("B.Ed") }
        var subject by remember { mutableStateOf("Mathematics") }
        var assignedClass by remember { mutableStateOf("Class 5") }
        var salary by remember { mutableStateOf("16000") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Faculty Teacher") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Teacher Name") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                    OutlinedTextField(value = qual, onValueChange = { qual = it }, label = { Text("Qualification") })
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Primary Subject") })
                    OutlinedTextField(value = assignedClass, onValueChange = { assignedClass = it }, label = { Text("Assigned Class") })
                    OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Monthly Salary (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addTeacher(name, phone, "", qual, subject, assignedClass, salary.toDoubleOrNull() ?: 15000.0)
                        showAddDialog = false
                    }
                }) {
                    Text("Save Teacher")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
