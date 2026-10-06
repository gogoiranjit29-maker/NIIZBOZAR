package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.FirebaseAuthenticationManager
import com.example.model.TranslationHelper
import com.example.model.UserRole
import com.example.viewmodel.MarketplaceViewModel

/**
 * Compose Login Screen using FirebaseAuthenticationManager.
 *
 * Supports phone number OTP authentication with real-time verification and stores
 * the user's selected role (Admin, Employee, Seller, Buyer, Delivery Partner)
 * in user metadata.
 * Features both a Radio Button selection group and a Dropdown menu group.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MarketplaceViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    authManager: FirebaseAuthenticationManager = viewModel.authManager
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current
    val currentLang = viewModel.currentLanguage.value

    // User Form State
    var userName by remember { mutableStateOf("Priya Sharma") }
    var phoneNumber by remember { mutableStateOf("+91 98640 55443") }
    var selectedRole by remember { mutableStateOf(UserRole.BUYER) }
    var roleSelectionMode by remember { mutableIntStateOf(0) } // 0: Radio Group, 1: Dropdown Menu

    // OTP Verification State
    var verificationId by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("123456") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    // Dropdown expanded state
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val rolesList = listOf(
        RoleItem(
            role = UserRole.BUYER,
            title = "Buyer",
            subtitle = "Browse fresh produce, transparent pricing & doorstep delivery",
            icon = Icons.Default.ShoppingBasket,
            defaultName = "Priya Sharma",
            defaultPhone = "+91 98640 55443"
        ),
        RoleItem(
            role = UserRole.SELLER,
            title = "Seller / Farmer",
            subtitle = "List crops directly, manage mandi prices & bulk orders",
            icon = Icons.Default.Store,
            defaultName = "Biren Das (Farmer)",
            defaultPhone = "+91 98540 12345"
        ),
        RoleItem(
            role = UserRole.EMPLOYEE,
            title = "Employee / Staff",
            subtitle = "Quality checks, seller call verification & order dispatch",
            icon = Icons.Default.Badge,
            defaultName = "Rahul Baruah",
            defaultPhone = "+91 94350 11223"
        ),
        RoleItem(
            role = UserRole.ADMIN,
            title = "Admin",
            subtitle = "Marketplace metrics, fee config & user management",
            icon = Icons.Default.Security,
            defaultName = "Operations Admin",
            defaultPhone = "+91 99999 88888"
        ),
        RoleItem(
            role = UserRole.DELIVERY_PARTNER,
            title = "Delivery Partner",
            subtitle = "Assigned pickup routes, GPS live tracking & cash collection",
            icon = Icons.Default.DeliveryDining,
            defaultName = "Arun Kalita",
            defaultPhone = "+91 98640 11223"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Branding Header
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = "NIIZ BOZAR Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "NIIZ BOZAR",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("app_brand_title")
        )

        Text(
            text = "EMPOWERING FARMERS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.testTag("app_brand_tagline")
        )

        Text(
            text = "Vegetable & Essentials Marketplace Login",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card Container for Authentication Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {

                // Step 1: User Details
                Text(
                    text = "1. Personal Information",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Full Name / पूरा नाम") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step 2: Role Selection Section (Radio Button or Dropdown)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Select Role / भूमिका चुनें",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Role Selector Toggle (Radio vs Dropdown)
                    Row {
                        FilterChip(
                            selected = roleSelectionMode == 0,
                            onClick = { roleSelectionMode = 0 },
                            label = { Text("Radio", fontSize = 11.sp) },
                            modifier = Modifier.testTag("mode_radio_chip")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = roleSelectionMode == 1,
                            onClick = { roleSelectionMode = 1 },
                            label = { Text("Dropdown", fontSize = 11.sp) },
                            modifier = Modifier.testTag("mode_dropdown_chip")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (roleSelectionMode == 0) {
                    // --- Radio Button Group ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_radio_group"),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rolesList.forEach { roleItem ->
                            val isSelected = selectedRole == roleItem.role
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = isSelected,
                                        onClick = {
                                            selectedRole = roleItem.role
                                            userName = roleItem.defaultName
                                            phoneNumber = roleItem.defaultPhone
                                        },
                                        role = Role.RadioButton
                                    )
                                    .testTag("role_radio_${roleItem.role.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null, // null recommended for selectable parent
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = roleItem.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = roleItem.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = roleItem.subtitle,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // --- Dropdown Menu Group ---
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_dropdown_group")
                    ) {
                        OutlinedTextField(
                            value = rolesList.firstOrNull { it.role == selectedRole }?.title ?: selectedRole.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assigned User Role") },
                            leadingIcon = {
                                val currentIcon = rolesList.firstOrNull { it.role == selectedRole }?.icon ?: Icons.Default.Person
                                Icon(currentIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("role_dropdown_field")
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            rolesList.forEach { roleItem ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = roleItem.icon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(roleItem.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(roleItem.subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedRole = roleItem.role
                                        userName = roleItem.defaultName
                                        phoneNumber = roleItem.defaultPhone
                                        isDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("dropdown_item_${roleItem.role.name.lowercase()}")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Step 3: Mobile Number & OTP Verification
                Text(
                    text = "3. Mobile & OTP (Firebase Authentication)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        statusMessage = null
                    },
                    label = { Text("Phone Number (+91 Mobile)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true,
                    enabled = !isSendingOtp && !isVerifyingOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!isOtpSent) {
                    // Send OTP Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (activity != null) {
                                isSendingOtp = true
                                isError = false
                                statusMessage = "Requesting OTP from Firebase..."

                                authManager.sendPhoneOtp(
                                    activity = activity,
                                    phoneNumber = phoneNumber,
                                    onCodeSent = { vid ->
                                        isSendingOtp = false
                                        verificationId = vid
                                        isOtpSent = true
                                        statusMessage = "OTP sent to $phoneNumber"
                                    },
                                    onError = { err ->
                                        isSendingOtp = false
                                        isError = true
                                        statusMessage = err
                                    }
                                )
                            } else {
                                isOtpSent = true
                                statusMessage = "Ready for OTP entry (Demo: 123456)"
                            }
                        },
                        enabled = phoneNumber.isNotBlank() && !isSendingOtp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("send_otp_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isSendingOtp) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sending OTP via Firebase...")
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send 6-Digit OTP")
                        }
                    }
                } else {
                    // Enter OTP Field
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = {
                            otpCode = it
                            statusMessage = null
                        },
                        label = { Text("Enter 6-Digit OTP (Demo: 123456)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_otp_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Verify & Complete Registration / Sign In Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            isVerifyingOtp = true
                            isError = false
                            statusMessage = "Verifying OTP & recording user metadata..."

                            authManager.verifyPhoneOtp(
                                verificationId = verificationId,
                                otpCode = otpCode,
                                userName = userName,
                                phoneNumber = phoneNumber,
                                role = selectedRole,
                                onSuccess = { userEntity ->
                                    isVerifyingOtp = false
                                    viewModel.loginWithDetails(userEntity.name, userEntity.phone, selectedRole)
                                    statusMessage = "Signed in as ${selectedRole.name}!"
                                    onAuthSuccess()
                                },
                                onError = { err ->
                                    isVerifyingOtp = false
                                    isError = true
                                    statusMessage = err
                                }
                            )
                        },
                        enabled = otpCode.isNotBlank() && !isVerifyingOtp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_otp_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isVerifyingOtp) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying & Registering Role...")
                        } else {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify OTP & Login as ${selectedRole.name.replace("_", " ")}")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            isOtpSent = false
                            statusMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("resend_otp_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Number / Resend OTP")
                    }
                }

                // Status & Feedback message
                AnimatedVisibility(
                    visible = statusMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    statusMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo Quick Personas for Instant Evaluation
        Text(
            text = "One-Tap Demo Switch for Evaluation:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rolesList.forEach { roleItem ->
                OutlinedButton(
                    onClick = {
                        selectedRole = roleItem.role
                        userName = roleItem.defaultName
                        phoneNumber = roleItem.defaultPhone
                        viewModel.loginWithDetails(roleItem.defaultName, roleItem.defaultPhone, roleItem.role)
                        onAuthSuccess()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_login_${roleItem.role.name.lowercase()}"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    Text(roleItem.title.split(" ").first(), fontSize = 10.sp, maxLines = 1)
                }
            }
        }
    }
}

private data class RoleItem(
    val role: UserRole,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val defaultName: String,
    val defaultPhone: String
)
