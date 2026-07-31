package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
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

@Composable
fun HomeworkScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val homeworkList by viewModel.homework.collectAsState()
    var showAssignDialog by remember { mutableStateOf(false) }

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
                text = "Homework Assignments (${homeworkList.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = { showAssignDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Assign Homework")
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(homeworkList) { hw ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = hw.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = hw.className,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Subject: ${hw.subject} | Teacher: ${hw.teacherName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = hw.description, fontSize = 13.sp)

                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Assigned: ${hw.assignedDate}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "Due Date: ${hw.dueDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        }
                    }
                }
            }
        }
    }

    if (showAssignDialog) {
        var className by remember { mutableStateOf("Class 8") }
        var subject by remember { mutableStateOf("Mathematics") }
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("2026-08-03") }

        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign New Homework") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class") })
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Homework Title") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Detailed Description / Exercises") })
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Submission Due Date") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addHomework(className, subject, title, desc, dueDate, "Faculty Teacher")
                        Toast.makeText(context, "Homework assigned to $className!", Toast.LENGTH_SHORT).show()
                        showAssignDialog = false
                    }
                }) {
                    Text("Publish Homework")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) { Text("Cancel") }
            }
        )
    }
}
