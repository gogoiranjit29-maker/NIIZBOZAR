package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TranslationHelper
import com.example.model.UserRole
import com.example.ui.theme.FarmGreenPrimary
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun RoleAndAuthScreen(
    viewModel: MarketplaceViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedRole by remember { mutableStateOf(UserRole.BUYER) }
    var authTab by remember { mutableIntStateOf(0) } // 0: Mobile OTP, 1: Email/Password
    var mobileNumber by remember { mutableStateOf("+91 98640 55443") }
    var otpCode by remember { mutableStateOf("123456") }
    var email by remember { mutableStateOf("priya.buyer@sabjimandi.in") }
    var password by remember { mutableStateOf("Password@123") }
    var userName by remember { mutableStateOf("Priya Sharma") }

    // Firebase Phone Auth state
    var verificationId by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    val currentLang = viewModel.currentLanguage.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Branding Header
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = TranslationHelper.getString("app_title", currentLang),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = TranslationHelper.getString("tagline", currentLang),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "Multi-Vendor Vegetable & Essentials Marketplace",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Role Selector Section
        Text(
            text = "Select Your Role / भूमिका चुनें:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        RoleSelectionRow(
            selectedRole = selectedRole,
            onRoleSelect = {
                selectedRole = it
                when (it) {
                    UserRole.BUYER -> {
                        userName = "Priya Sharma"
                        mobileNumber = "+91 98640 55443"
                        email = "buyer.priya@niizbozar.in"
                    }
                    UserRole.SELLER -> {
                        userName = "Biren Das (Farmer)"
                        mobileNumber = "+91 98540 12345"
                        email = "farmer.biren@niizbozar.in"
                    }
                    UserRole.EMPLOYEE -> {
                        userName = "Rahul Baruah (Verification Staff)"
                        mobileNumber = "+91 94350 11223"
                        email = "staff.rahul@niizbozar.in"
                    }
                    UserRole.ADMIN -> {
                        userName = "Operations Admin"
                        mobileNumber = "+91 99999 88888"
                        email = "admin@niizbozar.in"
                    }
                    UserRole.DELIVERY_PARTNER -> {
                        userName = "Arun Kalita (Delivery)"
                        mobileNumber = "+91 98640 11223"
                        email = "delivery.arun@niizbozar.in"
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Auth Tabs
        TabRow(selectedTabIndex = authTab) {
            Tab(
                selected = authTab == 0,
                onClick = { authTab = 0 },
                text = { Text("Mobile & OTP (Firebase)") }
            )
            Tab(
                selected = authTab == 1,
                onClick = { authTab = 1 },
                text = { Text("Email & Password") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name Input
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_name_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (authTab == 0) {
            // Firebase Mobile OTP Flow
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = {
                    mobileNumber = it
                    authErrorMessage = null
                },
                label = { Text("Mobile Number (with country code)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_phone_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!otpSent) {
                Button(
                    onClick = {
                        if (activity != null) {
                            isSendingOtp = true
                            authErrorMessage = null
                            viewModel.sendPhoneOtp(
                                activity = activity,
                                phone = mobileNumber,
                                onCodeSent = { vid ->
                                    isSendingOtp = false
                                    verificationId = vid
                                    otpSent = true
                                },
                                onError = { err ->
                                    isSendingOtp = false
                                    authErrorMessage = err
                                }
                            )
                        } else {
                            otpSent = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_otp_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSendingOtp) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sending OTP via Firebase...")
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send 6-Digit OTP via Firebase")
                    }
                }
            } else {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = {
                        otpCode = it
                        authErrorMessage = null
                    },
                    label = { Text("Enter 6-Digit OTP (Demo: 123456)") },
                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_otp_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isVerifyingOtp = true
                        authErrorMessage = null
                        viewModel.verifyPhoneOtp(
                            verificationId = verificationId,
                            otpCode = otpCode,
                            name = userName,
                            phone = mobileNumber,
                            role = selectedRole,
                            onSuccess = {
                                isVerifyingOtp = false
                                onAuthSuccess()
                            },
                            onError = { err ->
                                isVerifyingOtp = false
                                authErrorMessage = err
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("verify_otp_login_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isVerifyingOtp) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verifying Credentials...")
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify OTP & Login as ${selectedRole.name.replace("_", " ")}")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        otpSent = false
                        authErrorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Change Phone Number / Resend OTP")
                }
            }
        } else {
            // Email and password
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    authErrorMessage = null
                },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    authErrorMessage = null
                },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input")
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.loginWithDetails(userName, mobileNumber, selectedRole)
                    onAuthSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("email_login_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Login as ${selectedRole.name.replace("_", " ")}")
            }
        }

        if (authErrorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = authErrorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo Quick-Access Shortcuts for testing all 5 roles instantly!
        Text(
            text = "One-Tap Demo Switch for Testing Personas:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuickRoleButton("Buyer", UserRole.BUYER, Modifier.weight(1f)) {
                viewModel.loginWithDetails("Priya Sharma", "+91 98640 55443", UserRole.BUYER)
                onAuthSuccess()
            }
            QuickRoleButton("Farmer", UserRole.SELLER, Modifier.weight(1f)) {
                viewModel.loginWithDetails("Biren Das (Farmer)", "+91 98540 12345", UserRole.SELLER)
                onAuthSuccess()
            }
            QuickRoleButton("Staff", UserRole.EMPLOYEE, Modifier.weight(1f)) {
                viewModel.loginWithDetails("Rahul Baruah", "+91 94350 11223", UserRole.EMPLOYEE)
                onAuthSuccess()
            }
            QuickRoleButton("Admin", UserRole.ADMIN, Modifier.weight(1f)) {
                viewModel.loginWithDetails("Mandi Admin", "+91 99999 88888", UserRole.ADMIN)
                onAuthSuccess()
            }
            QuickRoleButton("Rider", UserRole.DELIVERY_PARTNER, Modifier.weight(1f)) {
                viewModel.loginWithDetails("Arun Kalita", "+91 98640 11223", UserRole.DELIVERY_PARTNER)
                onAuthSuccess()
            }
        }
    }
}

@Composable
fun QuickRoleButton(
    label: String,
    role: UserRole,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.testTag("quick_login_${role.name.lowercase()}")
    ) {
        Text(label, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun RoleSelectionRow(
    selectedRole: UserRole,
    onRoleSelect: (UserRole) -> Unit
) {
    val roles = listOf(
        Triple(UserRole.BUYER, "Buyer", Icons.Default.ShoppingBasket),
        Triple(UserRole.SELLER, "Seller/Farmer", Icons.Default.Store),
        Triple(UserRole.EMPLOYEE, "Employee", Icons.Default.Badge),
        Triple(UserRole.ADMIN, "Admin", Icons.Default.Security),
        Triple(UserRole.DELIVERY_PARTNER, "Delivery", Icons.Default.DeliveryDining)
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        roles.forEach { (role, label, icon) ->
            val isSelected = selectedRole == role
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRoleSelect(role) }
                    .testTag("role_select_${role.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(role.badgeColorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = Color(role.badgeColorHex), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
