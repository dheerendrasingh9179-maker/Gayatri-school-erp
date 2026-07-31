package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
fun StaffManagementScreen(
    viewModel: MainViewModel
) {
    val staffList by viewModel.staff.collectAsState()
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
                text = "Support Staff (${staffList.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Staff")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(staffList) { st ->
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
                            Text(text = st.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Role: ${st.role}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Phone: ${st.phone} | Joined: ${st.joinDate}", fontSize = 11.sp, color = Color.Gray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "₹${st.monthlySalary.toInt()}/mo", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 13.sp)
                            IconButton(onClick = { viewModel.deleteStaff(st.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("Accountant") }
        var salary by remember { mutableStateOf("12000") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Non-Teaching Staff") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Staff Name") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (e.g. Accountant, Driver, Peon)") })
                    OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Monthly Salary (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addStaff(name, phone, role, salary.toDoubleOrNull() ?: 10000.0)
                        showAddDialog = false
                    }
                }) {
                    Text("Save Staff")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
