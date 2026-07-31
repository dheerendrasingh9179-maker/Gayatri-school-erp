package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.MainViewModel

data class WebAdminNavItem(
    val title: String,
    val icon: ImageVector,
    val routeIndex: Int,
    val hasSubMenu: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebAdminDashboard(
    viewModel: MainViewModel,
    currentSelectedModuleIndex: Int,
    onSelectModuleIndex: (Int) -> Unit,
    onToggleMobileView: () -> Unit,
    content: @Composable () -> Unit
) {
    val navItems = listOf(
        WebAdminNavItem("Dashboard", Icons.Default.Home, 0, hasSubMenu = false),
        WebAdminNavItem("Student Management", Icons.Default.Group, 2),
        WebAdminNavItem("Fee Management", Icons.Default.Payments, 5),
        WebAdminNavItem("Bus Management", Icons.Default.DirectionsBus, 6),
        WebAdminNavItem("Teacher Management", Icons.Default.Person, 3),
        WebAdminNavItem("Attendance", Icons.Default.HowToReg, 7),
        WebAdminNavItem("Examination", Icons.Default.Assignment, 13),
        WebAdminNavItem("Salary Management", Icons.Default.MonetizationOn, 8),
        WebAdminNavItem("Income & Expenses", Icons.Default.TrendingUp, 9),
        WebAdminNavItem("Notice / SMS", Icons.Default.Notifications, 15),
        WebAdminNavItem("Reports", Icons.Default.BarChart, 18),
        WebAdminNavItem("Settings", Icons.Default.Settings, 19),
        WebAdminNavItem("Backup & Restore", Icons.Default.Backup, 19),
        WebAdminNavItem("User Management", Icons.Default.AdminPanelSettings, 4)
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Web Navigation Sidebar (Matching Exact Screenshot Styling)
        Surface(
            modifier = Modifier
                .width(250.dp)
                .fillMaxHeight(),
            color = Color(0xFF0243B6), // Exact Royal Blue Background
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // White Header School Crest Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_school_logo),
                                contentDescription = "GBVN Crest",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "GAYATRI BAL",
                            color = Color(0xFF0243B6),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "VIDHYA NIKETAN",
                            color = Color(0xFF0243B6),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Shahnagar, District Panna (M.P.)",
                            color = Color(0xFF64748B),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0243B6)
                        ) {
                            Text(
                                text = "GBVN ERP",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Vertical Nav Items
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentSelectedModuleIndex == item.routeIndex

                        Surface(
                            onClick = { onSelectModuleIndex(item.routeIndex) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF1976D2) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = item.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }

                                if (item.hasSubMenu) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onToggleMobileView,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Switch to Mobile View", fontSize = 12.sp)
                }
            }
        }

        // Right Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFF8FAFC))
        ) {
            content()
        }
    }
}
