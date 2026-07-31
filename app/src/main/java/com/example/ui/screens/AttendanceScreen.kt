package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firestore.FirestoreManager
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    val todayAttendance by viewModel.todayAttendance.collectAsState()

    var selectedClass by remember { mutableStateOf("Class 8") }
    val classes = listOf("KG-1", "KG-2", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")

    val classStudents = students.filter { it.className.equals(selectedClass, ignoreCase = true) }

    // Attendance status map for current screen session
    val attendanceMap = remember { mutableStateMapOf<Int, String>() }

    // Pre-populate with existing attendance if available
    LaunchedEffect(classStudents, todayAttendance) {
        classStudents.forEach { st ->
            val existing = todayAttendance.find { it.personId == st.id && it.personType == "STUDENT" }
            if (existing != null) {
                attendanceMap[st.id] = existing.status
            } else if (!attendanceMap.containsKey(st.id)) {
                attendanceMap[st.id] = "PRESENT"
            }
        }
    }

    // Auto-sync students & attendance to Firestore persistent cache
    LaunchedEffect(students, todayAttendance) {
        if (students.isNotEmpty()) {
            FirestoreManager.syncStudentsToFirestore(context, students)
        }
        if (todayAttendance.isNotEmpty()) {
            FirestoreManager.syncAttendanceToFirestore(context, todayAttendance)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // FIRESTORE OFFLINE PERSISTENCE STATUS BADGE
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
                        text = "Attendance register & student database cached locally. Staff can mark & view data without network connection.",
                        fontSize = 10.sp,
                        color = Color(0xFF166534)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Daily Attendance Register",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Date: ${viewModel.todayDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = {
                    classStudents.forEach { st ->
                        attendanceMap[st.id] = "PRESENT"
                    }
                },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("All Present", fontSize = 11.sp)
            }
        }

        // Class Selector Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(classes) { c ->
                FilterChip(
                    selected = selectedClass == c,
                    onClick = { selectedClass = c },
                    label = { Text(c, fontSize = 12.sp) }
                )
            }
        }

        // Attendance List
        if (classStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No students in $selectedClass", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classStudents) { st ->
                    val currentStatus = attendanceMap[st.id] ?: "PRESENT"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text(text = "${st.rollNo}. ${st.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Parent: ${st.parentName}", fontSize = 12.sp, color = Color.Gray)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("PRESENT", "ABSENT", "LATE", "LEAVE").forEach { stCode ->
                                    val isSel = currentStatus == stCode
                                    val bgCol = when (stCode) {
                                        "PRESENT" -> if (isSel) Color(0xFF2E7D32) else Color.LightGray.copy(alpha = 0.3f)
                                        "ABSENT" -> if (isSel) Color(0xFFC62828) else Color.LightGray.copy(alpha = 0.3f)
                                        "LATE" -> if (isSel) Color(0xFFE65100) else Color.LightGray.copy(alpha = 0.3f)
                                        else -> if (isSel) Color(0xFF0288D1) else Color.LightGray.copy(alpha = 0.3f)
                                    }
                                    val textCol = if (isSel) Color.White else Color.Black

                                    Button(
                                        onClick = { attendanceMap[st.id] = stCode },
                                        colors = ButtonDefaults.buttonColors(containerColor = bgCol),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(stCode.take(1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textCol)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                classStudents.forEach { st ->
                    val stStatus = attendanceMap[st.id] ?: "PRESENT"
                    viewModel.markAttendance(
                        personId = st.id,
                        personType = "STUDENT",
                        personName = st.name,
                        className = st.className,
                        status = stStatus
                    )
                }
                Toast.makeText(context, "Attendance saved for $selectedClass!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Attendance Record", fontWeight = FontWeight.Bold)
        }
    }
}
