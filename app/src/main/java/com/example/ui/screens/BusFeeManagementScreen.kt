package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsBus
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
fun BusFeeManagementScreen(
    viewModel: MainViewModel
) {
    val busRoutes by viewModel.busRoutes.collectAsState()
    val students by viewModel.students.collectAsState()
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
                text = "Bus Transport & GPS Radar (${busRoutes.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Route")
            }
        }

        // Live GPS Radar Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = Color(0xFF00A86B),
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE GPS BUS TRACKER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0243B6)
                        )
                    }
                    Text(
                        text = "Updated 10s ago",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Bus #1 (MP-35-P-0412)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Current Stop: Shahnagar Square -> En Route to GBVN Campus", fontSize = 11.sp, color = Color(0xFF334155))
                        Text(text = "Speed: 38 km/h | Expected Arrival: 08:25 AM", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00A86B))
                    }

                    OutlinedButton(
                        onClick = {
                            val context = viewModel.getApplication<android.app.Application>()
                            com.example.ui.util.WhatsAppSmsHelper.sendWhatsAppReminder(
                                context,
                                "9826000000",
                                "School Bus Live GPS Update: Bus MP-35-P-0412 is currently at Shahnagar Square approaching Gayatri Bal Vidhya Niketan."
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share GPS Link", fontSize = 11.sp)
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(busRoutes) { route ->
                val assignedStudents = students.count { it.busRouteId == route.id }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = route.routeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "₹${route.monthlyFee.toInt()}/mo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Bus No: ${route.vehicleNo}", fontSize = 12.sp)
                            Text(text = "Driver: ${route.driverName} (${route.driverPhone})", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Students Using Route: $assignedStudents",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var routeName by remember { mutableStateOf("Route 4: Shahnagar - Panna Road") }
        var vehicleNo by remember { mutableStateOf("MP-35-P-1011") }
        var driverName by remember { mutableStateOf("Suresh Soni") }
        var driverPhone by remember { mutableStateOf("9826001122") }
        var fee by remember { mutableStateOf("700") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Bus Route") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = routeName, onValueChange = { routeName = it }, label = { Text("Route Name & Stops") })
                    OutlinedTextField(value = vehicleNo, onValueChange = { vehicleNo = it }, label = { Text("Vehicle No") })
                    OutlinedTextField(value = driverName, onValueChange = { driverName = it }, label = { Text("Driver Name") })
                    OutlinedTextField(value = driverPhone, onValueChange = { driverPhone = it }, label = { Text("Driver Phone") })
                    OutlinedTextField(value = fee, onValueChange = { fee = it }, label = { Text("Monthly Bus Fee (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (routeName.isNotBlank()) {
                        viewModel.addBusRoute(routeName, vehicleNo, driverName, driverPhone, fee.toDoubleOrNull() ?: 600.0)
                        showAddDialog = false
                    }
                }) {
                    Text("Save Bus Route")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
