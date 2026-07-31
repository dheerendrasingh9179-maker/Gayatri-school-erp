package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAdmissionScreen(
    viewModel: MainViewModel,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val busRoutes by viewModel.busRoutes.collectAsState()

    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("Class 1") }
    var section by remember { mutableStateOf("A") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Shahnagar, Panna") }
    var dob by remember { mutableStateOf("2016-01-01") }
    var gender by remember { mutableStateOf("Male") }
    var selectedBusRouteId by remember { mutableStateOf<Int?>(null) }

    val classes = listOf("KG-1", "KG-2", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")
    var classExpanded by remember { mutableStateOf(false) }
    var busExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Student Admission",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Gayatri Bal Vidhya Niketan • Shahnagar",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Divider()

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Student Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = rollNo,
                    onValueChange = { rollNo = it },
                    label = { Text("Roll No *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                // Class Dropdown
                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = !classExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedClass,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Class *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = classExpanded,
                        onDismissRequest = { classExpanded = false }
                    ) {
                        classes.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    selectedClass = c
                                    classExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = parentName,
                onValueChange = { parentName = it },
                label = { Text("Father/Mother Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = parentPhone,
                onValueChange = { parentPhone = it },
                label = { Text("Parent Mobile Number *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Residential Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = gender == "Male",
                        onClick = { gender = "Male" },
                        label = { Text("Male") },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    FilterChip(
                        selected = gender == "Female",
                        onClick = { gender = "Female" },
                        label = { Text("Female") }
                    )
                }
            }

            // Bus Route Dropdown
            Text("Bus Facility (Optional):", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(
                expanded = busExpanded,
                onExpandedChange = { busExpanded = !busExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val currentBusText = busRoutes.find { it.id == selectedBusRouteId }?.routeName ?: "No Bus (Self/Walk)"
                OutlinedTextField(
                    value = currentBusText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Bus Route") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = busExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = busExpanded,
                    onDismissRequest = { busExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("No Bus (Self / Walk)") },
                        onClick = {
                            selectedBusRouteId = null
                            busExpanded = false
                        }
                    )
                    busRoutes.forEach { route ->
                        DropdownMenuItem(
                            text = { Text("${route.routeName} (₹${route.monthlyFee.toInt()}/mo)") },
                            onClick = {
                                selectedBusRouteId = route.id
                                busExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || rollNo.isBlank() || parentName.isBlank() || parentPhone.isBlank()) {
                        Toast.makeText(context, "Please fill all mandatory fields (*)", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addStudent(
                            name = name,
                            rollNo = rollNo,
                            className = selectedClass,
                            section = section,
                            parentName = parentName,
                            parentPhone = parentPhone,
                            address = address,
                            dob = dob,
                            gender = gender,
                            busRouteId = selectedBusRouteId
                        )
                        Toast.makeText(context, "Student $name admitted successfully!", Toast.LENGTH_LONG).show()
                        onSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Admission & Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
