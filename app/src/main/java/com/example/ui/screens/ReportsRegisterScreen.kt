package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Summarize
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
fun ReportsRegisterScreen(
    viewModel: MainViewModel
) {
    var selectedRegisterIndex by remember { mutableStateOf(0) }
    val registerTypes = listOf(
        "Admission Register",
        "Fee Register",
        "Attendance Register",
        "Salary Register",
        "Expense Register",
        "Income Register"
    )

    val students by viewModel.students.collectAsState()
    val fees by viewModel.feeRecords.collectAsState()
    val salaries by viewModel.salaries.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val income by viewModel.income.collectAsState()
    val attendance by viewModel.todayAttendance.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Summarize, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Official School Registers & Reports",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Register Tabs
        ScrollableTabRow(selectedTabIndex = selectedRegisterIndex) {
            registerTypes.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedRegisterIndex == idx,
                    onClick = { selectedRegisterIndex = idx }
                ) {
                    Text(title, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search records in register...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "${registerTypes[selectedRegisterIndex]} (Gayatri Bal Vidhya Niketan)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (selectedRegisterIndex) {
                        0 -> { // Admission Register
                            val filtered = students.filter { it.name.contains(searchQuery, true) || it.rollNo.contains(searchQuery, true) }
                            items(filtered) { st ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Roll ${st.rollNo}. ${st.name} (${st.className})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Admitted: ${st.admissionDate}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                        1 -> { // Fee Register
                            val filtered = fees.filter { it.studentName.contains(searchQuery, true) || it.receiptNo.contains(searchQuery, true) }
                            items(filtered) { f ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${f.receiptNo} - ${f.studentName} (${f.className})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("₹${f.paidAmount.toInt()} (${f.paymentMode})", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 13.sp)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                        2 -> { // Attendance Register
                            val filtered = attendance.filter { it.personName.contains(searchQuery, true) }
                            items(filtered) { att ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${att.personName} (${att.className ?: "Staff"})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(att.status, fontWeight = FontWeight.Bold, color = if (att.status == "PRESENT") Color(0xFF2E7D32) else Color.Red, fontSize = 12.sp)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                        3 -> { // Salary Register
                            val filtered = salaries.filter { it.personName.contains(searchQuery, true) }
                            items(filtered) { s ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${s.personName} (${s.month})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("₹${s.netPaid.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 13.sp)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                        4 -> { // Expense Register
                            val filtered = expenses.filter { it.title.contains(searchQuery, true) }
                            items(filtered) { exp ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${exp.title} (${exp.category})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("₹${exp.amount.toInt()}", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 13.sp)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                        5 -> { // Income Register
                            val filtered = income.filter { it.title.contains(searchQuery, true) }
                            items(filtered) { inc ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${inc.title} (${inc.category})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("₹${inc.amount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 13.sp)
                                }
                                Divider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }
        }
    }
}
