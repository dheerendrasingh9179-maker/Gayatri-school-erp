package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.StudentIdCardModal
import com.example.ui.util.WhatsAppSmsHelper
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagementScreen(
    viewModel: MainViewModel,
    onNavigateToAdmission: () -> Unit
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedClassFilter by remember { mutableStateOf("ALL") }
    var selectedStudentForIdCard by remember { mutableStateOf<StudentEntity?>(null) }

    val classList = listOf("ALL", "KG-1", "KG-2", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")

    val filteredStudents = students.filter { st ->
        val classMatches = (selectedClassFilter == "ALL" || st.className.equals(selectedClassFilter, ignoreCase = true))
        val queryMatches = searchQuery.isEmpty() || st.name.contains(searchQuery, ignoreCase = true) || st.rollNo.contains(searchQuery, ignoreCase = true) || st.parentName.contains(searchQuery, ignoreCase = true)
        classMatches && queryMatches
    }

    // Auto-sync students to Firestore persistent offline cache
    LaunchedEffect(students) {
        if (students.isNotEmpty()) {
            com.example.data.firestore.FirestoreManager.syncStudentsToFirestore(context, students)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // FIRESTORE OFFLINE PERSISTENCE BADGE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "FIRESTORE OFFLINE PERSISTENCE ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF15803D)
                    )
                    Text(
                        text = "Student directory & profiles stored in local persistent cache. Staff can access data without internet.",
                        fontSize = 10.sp,
                        color = Color(0xFF166534)
                    )
                }
            }
        }

        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Student Directory (${filteredStudents.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = onNavigateToAdmission,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Admit Student", fontSize = 12.sp)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Student Name, Roll No, or Parent...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null) } }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Class Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(classList) { c ->
                FilterChip(
                    selected = selectedClassFilter == c,
                    onClick = { selectedClassFilter = c },
                    label = { Text(c, fontSize = 12.sp) }
                )
            }
        }

        // Student List
        if (filteredStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No students found matching filter", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredStudents) { st ->
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
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = st.rollNo,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = st.name.ifBlank { "Student #${st.rollNo}" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(text = "Class: ${st.className} (${st.section})", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                IconButton(onClick = { selectedStudentForIdCard = st }) {
                                    Icon(Icons.Default.QrCode, contentDescription = "QR Student ID", tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Parent: ${st.parentName}", fontSize = 12.sp)
                                    Text(text = "Phone: ${st.parentPhone}", fontSize = 12.sp, color = Color.Gray)
                                }

                                Row {
                                    IconButton(onClick = {
                                        WhatsAppSmsHelper.sendWhatsAppReminder(context, st.parentPhone, "Hello ${st.parentName}, greetings from Gayatri Bal Vidhya Niketan, Shahnagar.")
                                    }) {
                                        Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = { viewModel.deleteStudent(st.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedStudentForIdCard?.let { st ->
        StudentIdCardModal(student = st, onDismiss = { selectedStudentForIdCard = null })
    }
}
