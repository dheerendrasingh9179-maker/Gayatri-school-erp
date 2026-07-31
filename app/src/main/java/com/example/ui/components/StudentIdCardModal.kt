package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.StudentEntity
import com.example.ui.util.PdfGenerator
import com.example.ui.util.WhatsAppSmsHelper

@Composable
fun StudentIdCardModal(
    student: StudentEntity,
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
                            val pdfFile = PdfGenerator.generateStudentIdCardPdf(context, student)
                            if (pdfFile != null) {
                                Toast.makeText(context, "Student ID Card PDF Created!", Toast.LENGTH_SHORT).show()
                                PdfGenerator.openOrSharePdf(context, pdfFile, "Open Student ID Card PDF")
                            } else {
                                Toast.makeText(context, "Failed to generate ID Card PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0243B6)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print / PDF ID Card", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            val msg = "Digital Student ID Card for ${student.name} (Roll No: ${student.rollNo}, Class: ${student.className}) - Gayatri Bal Vidhya Niketan, Shahnagar."
                            WhatsAppSmsHelper.sendWhatsAppReminder(context, student.parentPhone, msg)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share ID Details", fontSize = 12.sp)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official ID Card Graphic Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFF0243B6), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        // School Header Strip
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0243B6))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "GAYATRI BAL VIDHYA NIKETAN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Shahnagar, Panna (M.P.) | Session 2026-27",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        // ID Card Body
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0))
                                        .border(2.dp, Color(0xFF0243B6), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(38.dp),
                                        tint = Color(0xFF0243B6)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // QR Code
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    StudentQrCode(
                                        dataString = "GBVN-ID:${student.id}:${student.rollNo}:${student.name}",
                                        size = 60.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name & Badge
                            Text(
                                text = student.name.ifBlank { "Student Name" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEFF6FF)
                            ) {
                                Text(
                                    text = "STUDENT IDENTITY CARD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0243B6),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Info Key-Value Table
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                IdRow(label = "Roll No:", value = student.rollNo)
                                IdRow(label = "Class & Sec:", value = "${student.className} (${student.section})")
                                IdRow(label = "Father's Name:", value = student.parentName)
                                IdRow(label = "Emergency Contact:", value = student.parentPhone)
                                IdRow(label = "Transport Route:", value = if (student.busRouteId != null) "School Bus Assigned" else "Self Transport")
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bottom Seal Note
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Blood Group: O+",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Principal Sign: _____",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0243B6)
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun IdRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}
