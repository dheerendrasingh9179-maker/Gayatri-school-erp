package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthResult
import com.example.data.auth.UserAccount
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: (role: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedRole by remember { mutableStateOf("ADMIN") } // "ADMIN", "TEACHER", "PARENT"
    var isRegisterMode by remember { mutableStateOf(false) }

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("admin@gbvn.edu.in") }
    var passwordInput by remember { mutableStateOf("admin123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }

    // Synchronize default email when role tab changes
    LaunchedEffect(selectedRole) {
        if (!isRegisterMode) {
            when (selectedRole) {
                "ADMIN" -> {
                    emailInput = "admin@gbvn.edu.in"
                    passwordInput = "admin123"
                }
                "TEACHER" -> {
                    emailInput = "teacher@gbvn.edu.in"
                    passwordInput = "teacher123"
                }
                "PARENT" -> {
                    emailInput = "parent@gbvn.edu.in"
                    passwordInput = "parent123"
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF1F5F9)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // School Header Card
                Card(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // School Logo Crest
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF8FAFC))
                                .border(2.dp, Color(0xFF0243B6), CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_school_logo),
                                contentDescription = "School Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "GAYATRI BAL VIDHYA NIKETAN",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0243B6)
                        )

                        Text(
                            text = "Shahnagar, District Panna (M.P.) • GBVN ERP",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Firebase Auth Status Indicator Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = Color(0xFF0243B6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Firebase Auth Integrated",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0243B6)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Role Selection Segmented Control
                        Text(
                            text = "SELECT YOUR ROLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoleChipButton(
                                title = "Admin",
                                icon = Icons.Default.AdminPanelSettings,
                                isSelected = selectedRole == "ADMIN",
                                color = Color(0xFF0243B6),
                                onClick = { selectedRole = "ADMIN" },
                                modifier = Modifier.weight(1f)
                            )
                            RoleChipButton(
                                title = "Teacher",
                                icon = Icons.Default.School,
                                isSelected = selectedRole == "TEACHER",
                                color = Color(0xFF00A86B),
                                onClick = { selectedRole = "TEACHER" },
                                modifier = Modifier.weight(1f)
                            )
                            RoleChipButton(
                                title = "Parent",
                                icon = Icons.Default.FamilyRestroom,
                                isSelected = selectedRole == "PARENT",
                                color = Color(0xFF7367F0),
                                onClick = { selectedRole = "PARENT" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Sign In vs Register Tabs
                        TabRow(
                            selectedTabIndex = if (isRegisterMode) 1 else 0,
                            containerColor = Color(0xFFF8FAFC),
                            contentColor = Color(0xFF0243B6),
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        ) {
                            Tab(
                                selected = !isRegisterMode,
                                onClick = { isRegisterMode = false },
                                text = { Text("Sign In", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = isRegisterMode,
                                onClick = { isRegisterMode = true },
                                text = { Text("Register", fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Error Banner if any
                        errorMessage?.let { err ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF2F2),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = err,
                                        fontSize = 12.sp,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }
                        }

                        // Register Full Name Input
                        AnimatedVisibility(visible = isRegisterMode) {
                            Column {
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it; errorMessage = null },
                                    label = { Text("Full Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("name_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // Email Input
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it; errorMessage = null },
                            label = { Text("Email / Username") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            trailingIcon = {
                                if (emailInput.isNotEmpty()) {
                                    IconButton(onClick = { emailInput = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password Input
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it; errorMessage = null },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    performAuthAction(
                                        context = context,
                                        scope = scope,
                                        isRegisterMode = isRegisterMode,
                                        name = nameInput,
                                        email = emailInput,
                                        password = passwordInput,
                                        selectedRole = selectedRole,
                                        viewModel = viewModel,
                                        onLoading = { isLoading = it },
                                        onError = { errorMessage = it },
                                        onSuccess = onLoginSuccess
                                    )
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Options Row: Remember Me & Forgot Password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it }
                                )
                                Text("Remember Me", fontSize = 12.sp, color = Color(0xFF475569))
                            }

                            if (!isRegisterMode) {
                                TextButton(onClick = {
                                    resetEmailInput = emailInput
                                    showForgotPasswordDialog = true
                                }) {
                                    Text("Forgot Password?", fontSize = 12.sp, color = Color(0xFF0243B6))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Main Submit Button
                        Button(
                            onClick = {
                                performAuthAction(
                                    context = context,
                                    scope = scope,
                                    isRegisterMode = isRegisterMode,
                                    name = nameInput,
                                    email = emailInput,
                                    password = passwordInput,
                                    selectedRole = selectedRole,
                                    viewModel = viewModel,
                                    onLoading = { isLoading = it },
                                    onError = { errorMessage = it },
                                    onSuccess = onLoginSuccess
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0243B6)),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRegisterMode) "Register Account" else "Sign In to ERP",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFFE2E8F0))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Demo Login Accounts Section
                        Text(
                            text = "QUICK DEMO LOGIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickDemoChip(
                                label = "Admin Demo",
                                email = "admin@gbvn.edu.in",
                                role = "ADMIN",
                                color = Color(0xFF0243B6),
                                onClick = {
                                    selectedRole = "ADMIN"
                                    emailInput = "admin@gbvn.edu.in"
                                    passwordInput = "admin123"
                                    performAuthAction(
                                        context = context,
                                        scope = scope,
                                        isRegisterMode = false,
                                        name = "",
                                        email = emailInput,
                                        password = passwordInput,
                                        selectedRole = "ADMIN",
                                        viewModel = viewModel,
                                        onLoading = { isLoading = it },
                                        onError = { errorMessage = it },
                                        onSuccess = onLoginSuccess
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            QuickDemoChip(
                                label = "Teacher Demo",
                                email = "teacher@gbvn.edu.in",
                                role = "TEACHER",
                                color = Color(0xFF00A86B),
                                onClick = {
                                    selectedRole = "TEACHER"
                                    emailInput = "teacher@gbvn.edu.in"
                                    passwordInput = "teacher123"
                                    performAuthAction(
                                        context = context,
                                        scope = scope,
                                        isRegisterMode = false,
                                        name = "",
                                        email = emailInput,
                                        password = passwordInput,
                                        selectedRole = "TEACHER",
                                        viewModel = viewModel,
                                        onLoading = { isLoading = it },
                                        onError = { errorMessage = it },
                                        onSuccess = onLoginSuccess
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            QuickDemoChip(
                                label = "Parent Demo",
                                email = "parent@gbvn.edu.in",
                                role = "PARENT",
                                color = Color(0xFF7367F0),
                                onClick = {
                                    selectedRole = "PARENT"
                                    emailInput = "parent@gbvn.edu.in"
                                    passwordInput = "parent123"
                                    performAuthAction(
                                        context = context,
                                        scope = scope,
                                        isRegisterMode = false,
                                        name = "",
                                        email = emailInput,
                                        password = passwordInput,
                                        selectedRole = "PARENT",
                                        viewModel = viewModel,
                                        onLoading = { isLoading = it },
                                        onError = { errorMessage = it },
                                        onSuccess = onLoginSuccess
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email address to receive a Firebase Auth password reset link.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val msg = AuthManager.sendPasswordReset(context, resetEmailInput)
                            showForgotPasswordDialog = false
                            snackbarHostState.showSnackbar(msg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0243B6))
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RoleChipButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color else Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) color else Color(0xFFCBD5E1)
        ),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF334155)
            )
        }
    }
}

@Composable
private fun QuickDemoChip(
    label: String,
    email: String,
    role: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = role,
                fontSize = 9.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

private fun performAuthAction(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    isRegisterMode: Boolean,
    name: String,
    email: String,
    password: String,
    selectedRole: String,
    viewModel: MainViewModel,
    onLoading: (Boolean) -> Unit,
    onError: (String?) -> Unit,
    onSuccess: (role: String) -> Unit
) {
    scope.launch {
        onLoading(true)
        onError(null)

        val result = if (isRegisterMode) {
            AuthManager.signUp(context, email, password, name, selectedRole)
        } else {
            AuthManager.signIn(context, email, password, selectedRole)
        }

        onLoading(false)

        when (result) {
            is AuthResult.Success -> {
                viewModel.setLoggedInUser(result.user)
                onSuccess(result.user.role)
            }
            is AuthResult.Error -> {
                onError(result.message)
            }
        }
    }
}
