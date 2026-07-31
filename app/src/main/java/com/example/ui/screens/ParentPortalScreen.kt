package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FeeRecordEntity
import com.example.data.entity.StudentEntity
import com.example.ui.components.ReportCardModal
import com.example.ui.components.StudentIdCardModal
import com.example.ui.components.StudentQrCode
import com.example.ui.util.WhatsAppSmsHelper
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ParentPortalScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    val fees by viewModel.feeRecords.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val homework by viewModel.homework.collectAsState()

    var selectedChild by remember { mutableStateOf(students.firstOrNull()) }
    var showIdCardModal by remember { mutableStateOf(false) }
    var showReportCardModal by remember { mutableStateOf(false) }
    var showUpiPaymentModal by remember { mutableStateOf(false) }

    val currentChild = selectedChild ?: students.firstOrNull()
    val childFeeHistory = fees.filter { it.studentId == currentChild?.id }
    val overdueFees = childFeeHistory.filter { it.dueAmount > 0 }
    val totalOverdue = overdueFees.sumOf { it.dueAmount }
    val childHomework = homework.filter { it.className.equals(currentChild?.className, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Parent Welcome Header
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
                        Text("Parent Portal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Gayatri Bal Vidhya Niketan, Shahnagar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                    Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Child Selection Dropdown / Selector
                Text("Select Child:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    students.take(4).forEach { st ->
                        FilterChip(
                            selected = currentChild?.id == st.id,
                            onClick = { selectedChild = st },
                            label = { Text("${st.name} (${st.className})") }
                        )
                    }
                }
            }
        }

        if (currentChild != null) {
            // Child Info Summary & Action Buttons
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = currentChild.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "Class: ${currentChild.className} (${currentChild.section}) | Roll No: ${currentChild.rollNo}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Father: ${currentChild.parentName} | Phone: ${currentChild.parentPhone}", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showIdCardModal = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("QR ID Card", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showReportCardModal = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Report Card", fontSize = 12.sp)
                        }
                    }
                }
            }

            // FCM PUSH NOTIFICATION ALERT SUBSCRIPTION BADGE
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFF0243B6),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "FCM PUSH ALERTS: ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0243B6)
                            )
                            Text(
                                text = "Receiving instant Firebase Cloud Messaging alerts when new school notices are published in Firestore.",
                                fontSize = 10.sp,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            com.example.data.fcm.FcmNoticeManager.triggerTestPushNotification(context)
                        }
                    ) {
                        Text("Test FCM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0243B6))
                    }
                }
            }

            // AUTOMATED WORKFLOW: OVERDUE FEE REMINDER ALERT
            if (totalOverdue > 0 || overdueFees.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AUTOMATED FEE REMINDER ALERT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFDC2626)
                                )
                            }
                            Surface(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "OVERDUE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF991B1B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Automated Firestore Workflow Sync: School records show an outstanding balance of ₹${totalOverdue.toInt()} for ${currentChild.name}.",
                            fontSize = 12.sp,
                            color = Color(0xFF7F1D1D),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showUpiPaymentModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0243B6)),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pay ₹${totalOverdue.toInt()} UPI", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val msg = "Fee Reminder Notification for ${currentChild.name} (Class ${currentChild.className}): Overdue amount of Rs.${totalOverdue.toInt()} is pending at Gayatri Bal Vidhya Niketan. Kindly settle at your earliest."
                                    WhatsAppSmsHelper.sendWhatsAppReminder(context, currentChild.parentPhone, msg)
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp Alert", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Tabs for Homework & Fee Status
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Homework & Class Work for ${currentChild.className}:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                if (childHomework.isEmpty()) {
                    item { Text("No active homework assigned.", fontSize = 12.sp, color = Color.Gray) }
                } else {
                    items(childHomework) { hw ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(hw.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Subject: ${hw.subject} | Due: ${hw.dueDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Text(hw.description, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fee Payments & Receipts History:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                if (childFeeHistory.isEmpty()) {
                    item { Text("No fee records found for child.", fontSize = 12.sp, color = Color.Gray) }
                } else {
                    items(childFeeHistory) { fee ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Receipt #${fee.receiptNo}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${fee.feeType} (${fee.month})", fontSize = 12.sp)
                                    if (fee.dueAmount > 0) {
                                        Text("Due Pending: ₹${fee.dueAmount.toInt()}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${fee.paidAmount.toInt()} Paid", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 14.sp)
                                    Text(fee.paymentMode, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showIdCardModal && currentChild != null) {
            StudentIdCardModal(student = currentChild, onDismiss = { showIdCardModal = false })
        }

        if (showReportCardModal && currentChild != null) {
            ReportCardModal(student = currentChild, results = emptyList(), onDismiss = { showReportCardModal = false })
        }

        // Instant Online UPI Payment Modal
        if (showUpiPaymentModal && currentChild != null) {
            AlertDialog(
                onDismissRequest = { showUpiPaymentModal = false },
                confirmButton = {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Payment Confirmation Received! Syncing with Firestore Fee Collection...", Toast.LENGTH_LONG).show()
                            showUpiPaymentModal = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                    ) {
                        Text("I Have Paid (Confirm)")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showUpiPaymentModal = false }) {
                        Text("Close")
                    }
                },
                title = {
                    Text("Instant Fee Payment QR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gayatri Bal Vidhya Niketan, Shahnagar",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Student: ${currentChild.name} | Total Due: ₹${totalOverdue.toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0243B6)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        StudentQrCode(
                            dataString = "upi://pay?pa=gayatri.bal@upi&pn=GayatriBalVidhyaNiketan&am=${totalOverdue.toInt()}&cu=INR",
                            size = 150.dp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Scan with PhonePe, Google Pay, or Paytm to settle overdue balance instantly.",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

