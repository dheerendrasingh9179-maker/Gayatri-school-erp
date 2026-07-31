package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: MainViewModel
) {
    var selectedClass by remember { mutableStateOf("Class 8") }
    val classes = listOf("KG-1", "KG-2", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var selectedDay by remember { mutableStateOf("Monday") }

    val subjects = listOf("Mathematics", "English", "Science", "Social Science", "Hindi", "Sanskrit")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Class Timetable Schedule",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Class Selector
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(classes) { c ->
                FilterChip(
                    selected = selectedClass == c,
                    onClick = { selectedClass = c },
                    label = { Text(c, fontSize = 12.sp) }
                )
            }
        }

        // Day Selector
        TabRow(selectedTabIndex = days.indexOf(selectedDay)) {
            days.forEach { day ->
                Tab(
                    selected = selectedDay == day,
                    onClick = { selectedDay = day }
                ) {
                    Text(day.take(3), modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Text("Periods for $selectedClass on $selectedDay:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(subjects.mapIndexed { idx, s -> Pair(idx + 1, s) }) { pair ->
                val pNum = pair.first
                val sub = pair.second

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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "P$pNum",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = sub, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "Time: ${8 + pNum}:00 AM - ${9 + pNum}:00 AM", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Text(text = "Teacher $sub", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
