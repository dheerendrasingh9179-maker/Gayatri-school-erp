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
fun IncomeExpenseScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0 // 0: Income, 1: Expenses
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val incomeList by viewModel.income.collectAsState()
    val expenseList by viewModel.expenses.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("School Income (₹${incomeList.sumOf { it.amount }.toInt()})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Expenses (₹${expenseList.sumOf { it.amount }.toInt()})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedTab == 0) "General Income Log" else "Operational Expense Log",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (selectedTab == 0) "Add Income" else "Add Expense")
            }
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(incomeList) { inc ->
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
                                Text(text = inc.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Category: ${inc.category} | Date: ${inc.date}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(text = "+ ₹${inc.amount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 15.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenseList) { exp ->
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
                                Text(text = exp.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Category: ${exp.category} | Date: ${exp.date}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(text = "- ₹${exp.amount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(if (selectedTab == 0) "Prospectus" else "Electricity") }
        var amount by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (selectedTab == 0) "Record New Income" else "Record New Expense") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title / Purpose") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (₹)") })
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Remarks") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        if (selectedTab == 0) {
                            viewModel.addIncome(title, category, amt, description)
                        } else {
                            viewModel.addExpense(title, category, amt, description)
                        }
                        showAddDialog = false
                    }
                }) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
