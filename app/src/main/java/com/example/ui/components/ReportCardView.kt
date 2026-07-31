package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ExamResultEntity
import com.example.data.entity.StudentEntity

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.ui.platform.LocalContext
import com.example.ui.util.PdfGenerator

@Composable
fun ReportCardModal(
    student: StudentEntity,
    results: List<ExamResultEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val pdfFile = PdfGenerator.generateReportCardPdf(context, student, results)
                        if (pdfFile != null) {
                            Toast.makeText(context, "Report Card PDF Generated!", Toast.LENGTH_SHORT).show()
                            PdfGenerator.openOrSharePdf(context, pdfFile, "Open Report Card PDF")
                        } else {
                            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0243B6))
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download / Print PDF", fontSize = 12.sp)
                }

                OutlinedButton(onClick = onDismiss) {
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
                    Text(
                        text = "GAYATRI BAL VIDHYA NIKETAN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0D47A1),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Shahnagar, Dist. Panna (M.P.)",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "ANNUAL / QUARTERLY ACADEMIC REPORT CARD",
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
                        Column {
                            Text(text = "Name: ${student.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Class: ${student.className} (${student.section})", fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Roll No: ${student.rollNo}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Father: ${student.parentName}", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Marks Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0D47A1), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subject", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                        Text("Obtained", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Max", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Grade", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }

                    var totalObtained = 0
                    var totalMax = 0

                    if (results.isEmpty()) {
                        Text("No exam marks recorded yet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        results.forEach { res ->
                            totalObtained += res.marksObtained
                            totalMax += res.maxMarks
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(res.subject, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
                                Text("${res.marksObtained}", fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("${res.maxMarks}", fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text(res.grade, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }

                        val overallPct = if (totalMax > 0) (totalObtained.toDouble() / totalMax) * 100 else 0.0

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Marks: $totalObtained / $totalMax", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Percentage: ${"%.1f".format(overallPct)}%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0D47A1))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // AI Academic Analysis & Recommendation Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Print, // or AutoAwesome
                                        contentDescription = null,
                                        tint = Color(0xFF0243B6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI ACADEMIC PROGRESS & RECOMMENDATION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0243B6)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))

                                val recommendation = when {
                                    overallPct >= 85.0 -> "Exceptional academic performance! Strong mastery in conceptual topics. Recommended for Science Olympiad & School Leadership awards."
                                    overallPct >= 65.0 -> "Consistent good standing across subjects. Minor focus suggested in daily practice problems and revision quizzes to achieve Grade A+."
                                    overallPct >= 33.0 -> "Passed examination. Additional remedial classes recommended in Mathematics & Science to strengthen foundational concepts."
                                    else -> "Requires focused attention and personal coaching. Remedial study schedule assigned."
                                }

                                Text(
                                    text = recommendation,
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(modifier = Modifier.width(90.dp))
                            Text("Class Teacher", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(modifier = Modifier.width(90.dp))
                            Text("Principal GBVN", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    )
}
