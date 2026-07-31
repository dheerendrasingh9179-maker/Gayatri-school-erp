package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.SchoolRepository
import com.example.ui.components.GbvnHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.GbvnErpTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase
        com.example.data.firebase.FirebaseInitializer.init(applicationContext)
        // Initialize Firebase Cloud Messaging & Notice Listener
        com.example.data.fcm.FcmNoticeManager.initFcm(applicationContext)

        setContent {
            GbvnErpTheme {
                val viewModel: MainViewModel = viewModel()
                GbvnMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GbvnMainApp(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUserAccount.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    
    var isWebAdminView by remember { mutableStateOf(true) }
    var currentModuleIndex by remember { mutableStateOf(0) } // 0: Dashboard, 1: Admission, etc.

    if (!isLoggedIn) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { role ->
                if (role == "PARENT") {
                    currentModuleIndex = 16
                } else if (role == "TEACHER") {
                    currentModuleIndex = 17
                } else {
                    currentModuleIndex = 0
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                GbvnHeaderBar(
                    currentRole = currentRole,
                    currentUser = currentUser,
                    isWebAdminView = isWebAdminView,
                    onRoleChange = { role ->
                        viewModel.currentRole.value = role
                        if (role == "PARENT") currentModuleIndex = 16
                        else if (role == "TEACHER") currentModuleIndex = 17
                        else currentModuleIndex = 0
                    },
                    onToggleWebAdmin = { isWebAdminView = !isWebAdminView },
                    onLogout = { viewModel.logoutUser() }
                )
            },
            bottomBar = {
                if (!isWebAdminView) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentModuleIndex == 0,
                            onClick = { currentModuleIndex = 0 },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                            label = { Text("Dashboard", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentModuleIndex == 2,
                            onClick = { currentModuleIndex = 2 },
                            icon = { Icon(Icons.Default.School, contentDescription = null) },
                            label = { Text("Students", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentModuleIndex == 5,
                            onClick = { currentModuleIndex = 5 },
                            icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                            label = { Text("Fees", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentModuleIndex == 7,
                            onClick = { currentModuleIndex = 7 },
                            icon = { Icon(Icons.Default.HowToReg, contentDescription = null) },
                            label = { Text("Attendance", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentModuleIndex == 15,
                            onClick = { currentModuleIndex = 15 },
                            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                            label = { Text("Notices", fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val contentComposable: @Composable () -> Unit = {
                    when (currentModuleIndex) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToModule = { idx -> currentModuleIndex = idx }
                        )
                        1 -> StudentAdmissionScreen(
                            viewModel = viewModel,
                            onSuccess = { currentModuleIndex = 2 }
                        )
                        2 -> StudentManagementScreen(
                            viewModel = viewModel,
                            onNavigateToAdmission = { currentModuleIndex = 1 }
                        )
                        3 -> TeacherManagementScreen(viewModel = viewModel)
                        4 -> StaffManagementScreen(viewModel = viewModel)
                        5 -> FeeManagementScreen(viewModel = viewModel)
                        6 -> BusFeeManagementScreen(viewModel = viewModel)
                        7 -> AttendanceScreen(viewModel = viewModel)
                        8 -> SalaryScreen(viewModel = viewModel)
                        9, 10 -> IncomeExpenseScreen(viewModel = viewModel, initialTab = if (currentModuleIndex == 9) 0 else 1)
                        11 -> TimetableScreen(viewModel = viewModel)
                        12 -> HomeworkScreen(viewModel = viewModel)
                        13, 14 -> ExamReportCardScreen(viewModel = viewModel)
                        15 -> NoticesScreen(viewModel = viewModel)
                        16 -> ParentPortalScreen(viewModel = viewModel)
                        17 -> TeacherPortalScreen(
                            viewModel = viewModel,
                            onNavigateToAttendance = { currentModuleIndex = 7 },
                            onNavigateToHomework = { currentModuleIndex = 12 },
                            onNavigateToExams = { currentModuleIndex = 13 }
                        )
                        18 -> ReportsRegisterScreen(viewModel = viewModel)
                        19 -> BackupRestoreScreen(viewModel = viewModel)
                        else -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToModule = { idx -> currentModuleIndex = idx }
                        )
                    }
                }

                if (isWebAdminView) {
                    WebAdminDashboard(
                        viewModel = viewModel,
                        currentSelectedModuleIndex = currentModuleIndex,
                        onSelectModuleIndex = { idx -> currentModuleIndex = idx },
                        onToggleMobileView = { isWebAdminView = false },
                        content = contentComposable
                    )
                } else {
                    contentComposable()
                }
            }
        }
    }
}
