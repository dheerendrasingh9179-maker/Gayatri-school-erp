package com.example.ui.screens

import com.example.ui.components.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ClassStrengthItem
import com.example.ui.components.ClassWiseStrengthDonutChart
import com.example.ui.components.MonthlyFeeCollectionChart
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToModule: (Int) -> Unit
) {
    val students by viewModel.students.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val fees by viewModel.feeRecords.collectAsState()

    val classStrengthItems = listOf(
        ClassStrengthItem("KG - 1", 58, Color(0xFF0052CC)),
        ClassStrengthItem("KG - 2", 62, Color(0xFF00A86B)),
        ClassStrengthItem("Class 1", 64, Color(0xFF28C76F)),
        ClassStrengthItem("Class 2", 58, Color(0xFFEA5455)),
        ClassStrengthItem("Class 3", 61, Color(0xFFFF9F43)),
        ClassStrengthItem("Class 4", 60, Color(0xFF7367F0)),
        ClassStrengthItem("Class 5", 59, Color(0xFF73879C)),
        ClassStrengthItem("Class 6", 66, Color(0xFF00CFDD)),
        ClassStrengthItem("Class 7", 57, Color(0xFFFF9F43)),
        ClassStrengthItem("Class 8", 77, Color(0xFFFFC107))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Bar
        item {
            DashboardTopHeaderBar()
        }

        // Welcome Hero Banner Card
        item {
            DashboardHeroBanner()
        }

        // 5 Stat Metric Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCardItem(
                    title = "Total Students",
                    value = "582",
                    icon = Icons.Default.People,
                    containerColor = Color(0xFF0243B6),
                    onClick = { onNavigateToModule(2) },
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Total Teachers",
                    value = "32",
                    icon = Icons.Default.School,
                    containerColor = Color(0xFF00A86B),
                    onClick = { onNavigateToModule(3) },
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Today Attendance",
                    value = "526",
                    icon = Icons.Default.CalendarToday,
                    containerColor = Color(0xFF6C5CE7),
                    onClick = { onNavigateToModule(7) },
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Today Fee Collection",
                    value = "₹ 45,860",
                    icon = Icons.Default.CurrencyRupee,
                    containerColor = Color(0xFFFF9F43),
                    onClick = { onNavigateToModule(5) },
                    modifier = Modifier.weight(1f)
                )
                MetricCardItem(
                    title = "Pending Fees",
                    value = "₹ 3,28,650",
                    icon = Icons.Default.Receipt,
                    containerColor = Color(0xFFEA5455),
                    onClick = { onNavigateToModule(5) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Middle Row: Line Chart, Donut Chart, Quick Links
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fee Collection & Attendance Trend Charts
                DashboardTrendChartsCard(
                    modifier = Modifier.weight(1.8f)
                )

                // Class Wise Strength Donut Chart
                ClassWiseStrengthDonutChart(
                    items = classStrengthItems,
                    totalCount = 582,
                    modifier = Modifier.weight(1.3f)
                )

                // Quick Links
                QuickLinksCard(
                    onNavigateToModule = onNavigateToModule,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bottom Row: Recent Students Table & Fee Due Alerts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recent Students Table Card
                RecentStudentsCard(
                    studentsList = students,
                    onViewAll = { onNavigateToModule(2) },
                    modifier = Modifier.weight(1.8f)
                )

                // Fee Due Alerts Card
                FeeDueAlertsCard(
                    onViewAll = { onNavigateToModule(5) },
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // Footer Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "© 2026 Gayatri Bal Vidhya Niketan, Shahnagar, District Panna (M.P.) | GBVN ERP v1.0",
                    fontSize = 12.sp,
                    color = Color(0xFF0243B6),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DashboardTopHeaderBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Date Display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "30 July 2026, Wednesday",
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Notification Bell with Badge 5
                Box {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFEA5455),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA5455))
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "5",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // User Profile Avatar & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_school_logo),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Admin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Super Admin",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHeroBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Welcome back, Admin!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "👋", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Here's what's happening at your school today.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            // School Building Stylized Graphic
            Canvas(
                modifier = Modifier
                    .width(160.dp)
                    .height(70.dp)
            ) {
                val blueColor = Color(0xFF0243B6)
                val lightBlue = Color(0xFFE2E8F0)

                // Trees on left and right
                drawCircle(color = blueColor.copy(alpha = 0.6f), radius = 12.dp.toPx(), center = Offset(20.dp.toPx(), 45.dp.toPx()))
                drawLine(color = blueColor, start = Offset(20.dp.toPx(), 45.dp.toPx()), end = Offset(20.dp.toPx(), 65.dp.toPx()), strokeWidth = 2.dp.toPx())

                drawCircle(color = blueColor.copy(alpha = 0.6f), radius = 12.dp.toPx(), center = Offset(140.dp.toPx(), 45.dp.toPx()))
                drawLine(color = blueColor, start = Offset(140.dp.toPx(), 45.dp.toPx()), end = Offset(140.dp.toPx(), 65.dp.toPx()), strokeWidth = 2.dp.toPx())

                // Main School Building Base
                drawRoundRect(
                    color = blueColor,
                    topLeft = Offset(35.dp.toPx(), 25.dp.toPx()),
                    size = Size(90.dp.toPx(), 40.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Roof/Clock Tower Center
                drawRoundRect(
                    color = blueColor,
                    topLeft = Offset(68.dp.toPx(), 10.dp.toPx()),
                    size = Size(24.dp.toPx(), 20.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Flag Pole & Flag
                drawLine(color = blueColor, start = Offset(80.dp.toPx(), 10.dp.toPx()), end = Offset(80.dp.toPx(), 0f), strokeWidth = 2.dp.toPx())
                drawRect(color = blueColor, topLeft = Offset(80.dp.toPx(), 0f), size = Size(10.dp.toPx(), 6.dp.toPx()))

                // Windows
                drawRect(color = blueColor, topLeft = Offset(45.dp.toPx(), 32.dp.toPx()), size = Size(10.dp.toPx(), 10.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))
                drawRect(color = blueColor, topLeft = Offset(60.dp.toPx(), 32.dp.toPx()), size = Size(10.dp.toPx(), 10.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))
                drawRect(color = blueColor, topLeft = Offset(90.dp.toPx(), 32.dp.toPx()), size = Size(10.dp.toPx(), 10.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))
                drawRect(color = blueColor, topLeft = Offset(105.dp.toPx(), 32.dp.toPx()), size = Size(10.dp.toPx(), 10.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))

                // Door
                drawRect(color = blueColor, topLeft = Offset(74.dp.toPx(), 48.dp.toPx()), size = Size(12.dp.toPx(), 17.dp.toPx()))
            }
        }
    }
}

@Composable
fun MetricCardItem(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "View Details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = containerColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = containerColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun QuickLinksCard(
    onNavigateToModule: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Links",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(14.dp))

            val links = listOf(
                Pair("New Admission", Icons.Default.PersonAdd to 1),
                Pair("Collect Fee", Icons.Default.Payments to 5),
                Pair("Student Attendance", Icons.Default.CalendarToday to 7),
                Pair("Teacher Attendance", Icons.Default.HowToReg to 7),
                Pair("Add Expense", Icons.Default.TrendingDown to 9),
                Pair("Notice Board", Icons.Default.Notifications to 15)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                links.forEach { (label, iconRoute) ->
                    val (icon, route) = iconRoute
                    Surface(
                        onClick = { onNavigateToModule(route) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color(0xFF0243B6),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0243B6)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentStudentsCard(
    studentsList: List<com.example.data.entity.StudentEntity> = emptyList(),
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Students",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "View All →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0243B6),
                    modifier = Modifier.clickable { onViewAll() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Student Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.5f))
                Text("Class", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(0.8f))
                Text("Father's Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.2f))
                Text("Mobile No.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.2f))
                Text("Admission No.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1.2f))
                Text("Action", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(40.dp))
            }

            val displayList = if (studentsList.isNotEmpty()) {
                studentsList.take(5).map { st ->
                    val cleanName = if (st.name.isBlank()) "Student #${st.rollNo}" else st.name
                    val cleanFather = if (st.parentName.isBlank()) "Parent Name" else st.parentName
                    RecentStudentData(cleanName, st.className, cleanFather, st.parentPhone, "GBVN2026/${st.rollNo}")
                }
            } else {
                listOf(
                    RecentStudentData("Aaradhya Singh", "Class 3", "Rajesh Singh", "9755404030", "GBVN2026/101"),
                    RecentStudentData("Kunal Patel", "Class 5", "Sanjay Patel", "9425156789", "GBVN2026/102"),
                    RecentStudentData("Divyanshi Tiwari", "Class 2", "Manoj Tiwari", "9752123344", "GBVN2026/103")
                )
            }

            displayList.forEach { student ->
                Divider(color = Color(0xFFF1F5F9))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1.5f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_school_logo),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(student.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }

                    Text(student.className, fontSize = 12.sp, color = Color(0xFF475569), modifier = Modifier.weight(0.8f))
                    Text(student.fatherName, fontSize = 12.sp, color = Color(0xFF475569), modifier = Modifier.weight(1.2f))
                    Text(student.mobile, fontSize = 12.sp, color = Color(0xFF475569), modifier = Modifier.weight(1.2f))
                    Text(student.admissionNo, fontSize = 12.sp, color = Color(0xFF475569), modifier = Modifier.weight(1.2f))

                    Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class RecentStudentData(
    val name: String,
    val className: String,
    val fatherName: String,
    val mobile: String,
    val admissionNo: String
)

@Composable
fun FeeDueAlertsCard(
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fee Due Alerts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "View All →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0243B6),
                    modifier = Modifier.clickable { onViewAll() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val alerts = listOf(
                FeeDueItem("Rahul Verma", "Class 6", "₹ 8,500", "15 Days"),
                FeeDueItem("Ananya Shukla", "Class 4", "₹ 7,000", "12 Days"),
                FeeDueItem("Mohit Yadav", "Class 7", "₹ 9,000", "10 Days"),
                FeeDueItem("Sakshi Sahu", "Class 3", "₹ 6,500", "7 Days")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                alerts.forEach { alert ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF5F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE2E2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(alert.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text(alert.className, fontSize = 11.sp, color = Color(0xFF64748B))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(alert.amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFE2E2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEA5455).copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = alert.daysLeft,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEA5455),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class FeeDueItem(
    val name: String,
    val className: String,
    val amount: String,
    val daysLeft: String
)
