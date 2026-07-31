package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FeeRecordEntity
import com.example.ui.util.WhatsAppSmsHelper

import android.widget.Toast
import com.example.ui.util.PdfGenerator

@Composable
fun FeeReceiptModal(
    fee: FeeRecordEntity,
    parentPhone: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val pdfFile = PdfGenerator.generateFeeReceiptPdf(context, fee)
                            if (pdfFile != null) {
                                Toast.makeText(context, "Fee Receipt PDF Generated!", Toast.LENGTH_SHORT).show()
                                PdfGenerator.openOrSharePdf(context, pdfFile, "Open Fee Receipt PDF")
                            } else {
                                Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0243B6)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print / PDF", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            val msg = "Dear Parent, Fee Receipt #${fee.receiptNo} of Rs.${fee.paidAmount} for ${fee.studentName} (Class ${fee.className}) received with thanks at Gayatri Bal Vidhya Niketan, Shahnagar."
                            WhatsAppSmsHelper.sendWhatsAppReminder(context, parentPhone, msg)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        },
        title = null,
        text = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // School Header
                    Text(
                        text = "GAYATRI BAL VIDHYA NIKETAN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0D47A1),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Shahnagar, District Panna (M.P.)",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "FEE PAYMENT RECEIPT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF8F00),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Receipt No: ${fee.receiptNo}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Date: ${fee.paymentDate}", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Student: ${fee.studentName}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Class: ${fee.className}", fontSize = 13.sp)
                    Text(text = "Fee Type: ${fee.feeType} (${fee.month} ${fee.year})", fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Amount Breakups Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF4F6F9), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Fee Amount:", fontSize = 13.sp)
                                Text("₹${fee.totalAmount}", fontSize = 13.sp)
                            }
                            if (fee.discount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount/Concession:", fontSize = 13.sp, color = Color(0xFF2E7D32))
                                    Text("- ₹${fee.discount}", fontSize = 13.sp, color = Color(0xFF2E7D32))
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount Paid:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                                Text("₹${fee.paidAmount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                            }
                            if (fee.dueAmount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Balance Due:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                    Text("₹${fee.dueAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Payment Mode: ${fee.paymentMode}", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Divider(modifier = Modifier.width(100.dp))
                            Text("Authorized Seal", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    )
}
