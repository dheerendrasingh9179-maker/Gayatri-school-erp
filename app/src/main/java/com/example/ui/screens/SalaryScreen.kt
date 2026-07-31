package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonetizationOn
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
fun SalaryScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val teachers by viewModel.teachers.collectAsState()
    val staff by viewModel.staff.collectAsState()
    val salaryRecords by viewModel.salaries.collectAsState()

    var showDisburseDialog by remember { mutableStateOf(false) }

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
                text = "Faculty & Staff Salary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = { showDisburseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Disburse Salary")
            }
        }

        Text("Disbursement History (${salaryRecords.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(salaryRecords) { sal ->
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
                                Text(text = sal.personName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Role: ${sal.personRole} | Month: ${sal.month} ${sal.year}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "₹${sal.netPaid.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B5E20),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Basic: ₹${sal.basicSalary.toInt()} | Bonus: ₹${sal.bonus.toInt()}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "Paid On: ${sal.paymentDate}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showDisburseDialog) {
        val allPersonnel = teachers.map { Pair(it.id, "${it.name} (Teacher)") } + staff.map { Pair(it.id, "${it.name} (Staff)") }
        var selectedPair by remember { mutableStateOf(allPersonnel.firstOrNull()) }
        var basicSalary by remember { mutableStateOf("16000") }
        var bonus by remember { mutableStateOf("0") }
        var deductions by remember { mutableStateOf("0") }
        var paymentMode by remember { mutableStateOf("Bank Transfer") }

        AlertDialog(
            onDismissRequest = { showDisburseDialog = false },
            title = { Text("Disburse Monthly Salary") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Employee:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    allPersonnel.take(5).forEach { pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedPair == pair,
                                onClick = { selectedPair = pair }
                            )
                            Text(pair.second, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(value = basicSalary, onValueChange = { basicSalary = it }, label = { Text("Basic Salary (₹)") })
                    OutlinedTextField(value = bonus, onValueChange = { bonus = it }, label = { Text("Bonus (₹)") })
                    OutlinedTextField(value = deductions, onValueChange = { deductions = it }, label = { Text("Deductions (₹)") })
                    OutlinedTextField(value = paymentMode, onValueChange = { paymentMode = it }, label = { Text("Payment Mode") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = selectedPair
                    if (p != null) {
                        viewModel.addSalaryRecord(
                            personId = p.first,
                            personName = p.second,
                            personRole = if (p.second.contains("Teacher")) "TEACHER" else "STAFF",
                            basicSalary = basicSalary.toDoubleOrNull() ?: 15000.0,
                            bonus = bonus.toDoubleOrNull() ?: 0.0,
                            deductions = deductions.toDoubleOrNull() ?: 0.0,
                            paymentMode = paymentMode
                        )
                        Toast.makeText(context, "Salary disbursed for ${p.second}!", Toast.LENGTH_SHORT).show()
                        showDisburseDialog = false
                    }
                }) {
                    Text("Record Payout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisburseDialog = false }) { Text("Cancel") }
            }
        )
    }
}
