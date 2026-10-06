package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.EmployeeEntity
import com.example.ui.screens.seller.MetricCard
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestAmber
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val pricingConfig by viewModel.pricingConfig.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val deliveryPartners by viewModel.deliveryPartners.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val allEmployees by viewModel.allEmployees.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Smart Pricing, 1: Orders & Fleet, 2: Employees, 3: Moderation
    var showAddEmployeeDialog by remember { mutableStateOf(false) }

    // Pricing parameters state
    var serviceFeeText by remember(pricingConfig) { mutableStateOf(pricingConfig.baseServiceFee.toString()) }
    var perKmRateText by remember(pricingConfig) { mutableStateOf(pricingConfig.perKmDeliveryRate.toString()) }
    var packagingFeeText by remember(pricingConfig) { mutableStateOf(pricingConfig.packagingFee.toString()) }
    var bulkThresholdText by remember(pricingConfig) { mutableStateOf(pricingConfig.bulkDiscountThresholdQty.toString()) }
    var bulkPercentText by remember(pricingConfig) { mutableStateOf(pricingConfig.bulkDiscountPercent.toString()) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Marketplace Admin Control Center",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text(
            text = "Global Pricing Engine, Field Employees, Fleet Dispatch & Moderation",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Analytics Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Orders", allOrders.size.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            MetricCard("Fleet", deliveryPartners.size.toString(), FarmGreenPrimary, Modifier.weight(1f))
            MetricCard("Staff", allEmployees.size.toString(), MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
            MetricCard("Reviews", allReviews.size.toString(), HarvestAmber, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Smart Pricing") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Orders & Fleet") })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Employees (${allEmployees.size})") })
            Tab(selected = activeTab == 3, onClick = { activeTab = 3 }, text = { Text("Moderation") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeTab) {
            0 -> {
                // Smart Pricing Parameter Controls
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Admin Dynamic Pricing Parameters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Text(
                                    text = "Buyer final price is calculated automatically: Seller Price + (Distance × Rate) + Service Fee + Packaging Fee - Bulk Discount.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = serviceFeeText,
                                    onValueChange = { serviceFeeText = it },
                                    label = { Text("Base Marketplace Service Fee (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_service_fee_input")
                                )

                                OutlinedTextField(
                                    value = perKmRateText,
                                    onValueChange = { perKmRateText = it },
                                    label = { Text("Per Km Delivery Rate (₹/km)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_per_km_rate_input")
                                )

                                OutlinedTextField(
                                    value = packagingFeeText,
                                    onValueChange = { packagingFeeText = it },
                                    label = { Text("Fresh Packaging & Handling Fee (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_packaging_fee_input")
                                )

                                OutlinedTextField(
                                    value = bulkThresholdText,
                                    onValueChange = { bulkThresholdText = it },
                                    label = { Text("Bulk Discount Threshold Qty (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_bulk_threshold_input")
                                )

                                OutlinedTextField(
                                    value = bulkPercentText,
                                    onValueChange = { bulkPercentText = it },
                                    label = { Text("Bulk Discount Percentage (%)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_bulk_percent_input")
                                )

                                Button(
                                    onClick = {
                                        val sFee = serviceFeeText.toDoubleOrNull() ?: pricingConfig.baseServiceFee
                                        val kmRate = perKmRateText.toDoubleOrNull() ?: pricingConfig.perKmDeliveryRate
                                        val pkgFee = packagingFeeText.toDoubleOrNull() ?: pricingConfig.packagingFee
                                        val bThresh = bulkThresholdText.toDoubleOrNull() ?: pricingConfig.bulkDiscountThresholdQty
                                        val bPerc = bulkPercentText.toDoubleOrNull() ?: pricingConfig.bulkDiscountPercent

                                        viewModel.updatePricingConfig(
                                            pricingConfig.copy(
                                                baseServiceFee = sFee,
                                                perKmDeliveryRate = kmRate,
                                                packagingFee = pkgFee,
                                                bulkDiscountThresholdQty = bThresh,
                                                bulkDiscountPercent = bPerc
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("admin_save_pricing_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Apply & Save Parameters")
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Orders & Fleet Management
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(text = "Active Delivery Fleet (${deliveryPartners.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    items(deliveryPartners) { partner ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${partner.phone} • ${partner.vehicleType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = if (partner.isAvailable) "Available for Assignment" else "On Active Delivery (Order #${partner.activeAssignedOrderId})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (partner.isAvailable) FarmGreenPrimary else HarvestAmber
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "All Marketplace Orders (${allOrders.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    items(allOrders) { order ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("₹${"%.2f".format(order.finalTotalAmount)}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                                Text("Buyer: ${order.buyerName} (${order.buyerPhone})", fontSize = 11.sp)
                                Text("Items: ${order.itemsSummary}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Status: ${order.orderStatus} • Partner: ${if (order.deliveryPartnerName.isNotBlank()) order.deliveryPartnerName else "Unassigned"}", fontSize = 11.sp)

                                if (order.deliveryPartnerId.isBlank() && deliveryPartners.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        deliveryPartners.take(2).forEach { p ->
                                            OutlinedButton(
                                                onClick = { viewModel.assignDeliveryPartnerToOrder(order.id, p) },
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Assign ${p.name.split(" ").first()}", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Employees Management (NEW)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Employees & Field Staff (${allEmployees.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Automatically generated unique Employee IDs",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { showAddEmployeeDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("admin_add_employee_button")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Employee", fontSize = 12.sp)
                            }
                        }
                    }

                    if (allEmployees.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No employees registered yet.", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Tap '+ Add Employee' to recruit field officers with automatic unique IDs.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(allEmployees) { employee ->
                            EmployeeCard(
                                employee = employee,
                                onToggleStatus = { viewModel.toggleEmployeeStatus(employee) },
                                onDelete = { viewModel.deleteEmployee(employee.employeeId) }
                            )
                        }
                    }
                }
            }

            3 -> {
                // Review Moderation
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(text = "Customer Ratings & Comments Moderation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    if (allReviews.isEmpty()) {
                        item {
                            Text("No reviews logged yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(allReviews) { review ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth().testTag("review_card_${review.id}")
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "${review.productName} by ${review.buyerName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Row {
                                            repeat(review.rating) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = HarvestAmber, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                        if (review.comment.isNotBlank()) {
                                            Text(text = "\"${review.comment}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteReview(review.id) },
                                        modifier = Modifier.testTag("delete_review_${review.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Moderate review", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Employee Dialog with Automatic Unique Employee ID Generation
    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            viewModel = viewModel,
            onDismiss = { showAddEmployeeDialog = false }
        )
    }
}

/**
 * Dialog for Admin to register a new employee with an automatically created unique Employee ID.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEmployeeDialog(
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    // Generate unique ID automatically
    var generatedId by remember { mutableStateOf(viewModel.generateUniqueEmployeeId()) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 ") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Quality & Verification") }
    var assignedMandi by remember { mutableStateOf("Pamohi Wholesale Mandi, Guwahati") }

    val departments = listOf(
        "Quality & Verification",
        "Mandi Field Operations",
        "Logistics & Dispatch",
        "Farmer Onboarding"
    )

    val mandis = listOf(
        "Pamohi Wholesale Mandi, Guwahati",
        "Beltola Daily Market, Guwahati",
        "Silchar Wholesale Mandi",
        "Jorhat Agricultural Hub",
        "Tezpur Central Mandi"
    )

    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Employee", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Unique Employee ID Display & Regeneration Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AUTOMATIC UNIQUE EMPLOYEE ID",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = generatedId,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.testTag("generated_employee_id_display")
                            )
                        }

                        IconButton(
                            onClick = { generatedId = viewModel.generateUniqueEmployeeId() },
                            modifier = Modifier.testTag("regenerate_employee_id_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate Unique ID", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        hasError = false
                        if (email.isBlank() && it.isNotBlank()) {
                            val clean = it.lowercase().replace(" ", ".")
                            email = "$clean@niizbozar.in"
                        }
                    },
                    label = { Text("Employee Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("employee_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        hasError = false
                    },
                    label = { Text("Mobile Phone Number *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("employee_phone_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Work Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("employee_email_input")
                )

                Text("Select Department:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    departments.forEach { dept ->
                        FilterChip(
                            selected = department == dept,
                            onClick = { department = dept },
                            label = { Text(dept, fontSize = 10.sp) }
                        )
                    }
                }

                Text("Assigned Mandi Location:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    mandis.forEach { m ->
                        FilterChip(
                            selected = assignedMandi == m,
                            onClick = { assignedMandi = m },
                            label = { Text(m.split(",").first(), fontSize = 10.sp) }
                        )
                    }
                }

                if (hasError) {
                    Text(
                        text = "Please enter valid Employee Name and Mobile Phone Number.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || phone.length < 5) {
                        hasError = true
                    } else {
                        viewModel.addEmployee(
                            name = name,
                            phone = phone,
                            email = email,
                            department = department,
                            assignedMandi = assignedMandi,
                            customId = generatedId,
                            onSuccess = { onDismiss() }
                        )
                    }
                },
                modifier = Modifier.testTag("submit_new_employee_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Register Employee")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Card displaying an Employee with their unique Employee ID badge and management controls.
 */
@Composable
fun EmployeeCard(
    employee: EmployeeEntity,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("employee_card_${employee.employeeId}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Unique ID Badge and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Badge,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = employee.employeeId,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("employee_id_badge_${employee.employeeId}")
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (employee.isActive) FarmGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { onToggleStatus() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (employee.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (employee.isActive) FarmGreenPrimary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (employee.isActive) "Active Staff" else "Inactive",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (employee.isActive) FarmGreenPrimary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = employee.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = employee.department,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(employee.phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (employee.email.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(employee.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(employee.assignedMandi, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onToggleStatus,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(if (employee.isActive) "Mark Inactive" else "Activate", fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp).testTag("delete_employee_${employee.employeeId}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Employee", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
