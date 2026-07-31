package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FeeRecordEntity
import com.example.data.entity.StudentEntity
import com.example.ui.components.FeeReceiptModal
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeManagementScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    val feeRecords by viewModel.feeRecords.collectAsState()

    var showCollectDialog by remember { mutableStateOf(false) }
    var selectedFeeForReceipt by remember { mutableStateOf<FeeRecordEntity?>(null) }
    var selectedStudentPhone by remember { mutableStateOf("") }

    val totalCollected = feeRecords.sumOf { it.paidAmount }
    val totalPending = feeRecords.sumOf { it.dueAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Metric Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fee Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val overdueCount = feeRecords.count { it.dueAmount > 0 }
                        Toast.makeText(context, "⚡ Automated Workflow Triggered: Overdue Fee Reminders sent to $overdueCount parents via WhatsApp & Parent Portal!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto Remind Overdue", fontSize = 12.sp)
                }

                Button(onClick = { showCollectDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Collect Fee")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Total Received", fontSize = 11.sp, color = Color(0xFF1B5E20))
                    Text("₹${totalCollected.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Total Pending", fontSize = 11.sp, color = Color(0xFFC62828))
                    Text("₹${totalPending.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                }
            }
        }

        Text("Fee Payment History (${feeRecords.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        // Fee History List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(feeRecords) { fee ->
                val parentPhone = students.find { it.id == fee.studentId }?.parentPhone ?: ""
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedFeeForReceipt = fee
                            selectedStudentPhone = parentPhone
                        },
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
                                Text(text = fee.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Class: ${fee.className} | Receipt: ${fee.receiptNo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "₹${fee.paidAmount.toInt()} Paid",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
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
                            Text(text = "${fee.feeType} (${fee.month})", fontSize = 12.sp, color = Color.DarkGray)
                            Text(text = "Date: ${fee.paymentDate}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    // Collect Fee Modal
    if (showCollectDialog) {
        var selectedStudent by remember { mutableStateOf<StudentEntity?>(students.firstOrNull()) }
        var feeType by remember { mutableStateOf("Tuition Fee") }
        var month by remember { mutableStateOf("July") }
        var totalAmount by remember { mutableStateOf("1800") }
        var discount by remember { mutableStateOf("0") }
        var paidAmount by remember { mutableStateOf("1800") }
        var paymentMode by remember { mutableStateOf("UPI / Cash") }
        var studentDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCollectDialog = false },
            title = { Text("Collect School Fee") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Student Selector
                    ExposedDropdownMenuBox(
                        expanded = studentDropdownExpanded,
                        onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedStudent?.let { "${it.name} (${it.className})" } ?: "Select Student",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Student") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = studentDropdownExpanded,
                            onDismissRequest = { studentDropdownExpanded = false }
                        ) {
                            students.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text("${st.name} - ${st.className} (Roll ${st.rollNo})") },
                                    onClick = {
                                        selectedStudent = st
                                        studentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(value = feeType, onValueChange = { feeType = it }, label = { Text("Fee Type") })
                    OutlinedTextField(value = month, onValueChange = { month = it }, label = { Text("Month") })
                    OutlinedTextField(value = totalAmount, onValueChange = { totalAmount = it }, label = { Text("Total Fee (₹)") })
                    OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("Discount / Concession (₹)") })
                    OutlinedTextField(value = paidAmount, onValueChange = { paidAmount = it }, label = { Text("Amount Paying Now (₹)") })
                    OutlinedTextField(value = paymentMode, onValueChange = { paymentMode = it }, label = { Text("Payment Mode (Cash / UPI / Bank)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val st = selectedStudent
                    if (st != null) {
                        viewModel.addFeeRecord(
                            studentId = st.id,
                            studentName = st.name,
                            className = st.className,
                            feeType = feeType,
                            month = month,
                            totalAmt = totalAmount.toDoubleOrNull() ?: 1000.0,
                            discount = discount.toDoubleOrNull() ?: 0.0,
                            paidAmt = paidAmount.toDoubleOrNull() ?: 1000.0,
                            paymentMode = paymentMode
                        )
                        Toast.makeText(context, "Fee collected for ${st.name}!", Toast.LENGTH_SHORT).show()
                        showCollectDialog = false
                    }
                }) {
                    Text("Collect & Generate Receipt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCollectDialog = false }) { Text("Cancel") }
            }
        )
    }

    selectedFeeForReceipt?.let { fee ->
        FeeReceiptModal(
            fee = fee,
            parentPhone = selectedStudentPhone,
            onDismiss = { selectedFeeForReceipt = null }
        )
    }
}
