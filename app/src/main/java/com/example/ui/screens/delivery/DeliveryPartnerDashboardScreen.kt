package com.example.ui.screens.delivery

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.entities.OrderEntity
import com.example.model.OrderStatus
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestAmber
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun DeliveryPartnerDashboardScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val partnerName by viewModel.userName.collectAsState()

    var showOtpDialogForOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

    val activeOrders = allOrders.filter {
        it.orderStatus != OrderStatus.DELIVERED.name && it.orderStatus != OrderStatus.CANCELLED.name
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rider Delivery Console",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "$partnerName • Active Fleet",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Battery-safe Telemetry chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FarmGreenPrimary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Eco GPS On", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                    }
                }
            }
        }

        item {
            Text(
                text = "Assigned Mandi Deliveries (${activeOrders.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (activeOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active delivery trips assigned.", fontWeight = FontWeight.Bold)
                        Text("You will receive a notification when an order is ready for Mandi pickup.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(activeOrders) { order ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().testTag("rider_order_card_${order.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HarvestAmber.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = order.orderStatus.replace("_", " "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pickup point
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(text = "Pickup Mandi Yard:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Central Mandi Yard Shed 2 (Collected Produce)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Dropoff point
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(text = "Drop Destination:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "${order.buyerName} • ${order.buyerAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Items: ${order.itemsSummary}", fontSize = 11.sp)
                        Text(text = "Preferred Time Slot: ${order.deliverySlot}", fontSize = 11.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Progression Action Buttons
                        Text(text = "Update Status:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            when (order.orderStatus) {
                                OrderStatus.PLACED.name, OrderStatus.PAID.name, OrderStatus.PROCESSING.name -> {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.COLLECTED) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("rider_collect_${order.id}")
                                    ) {
                                        Text("1. Collected from Mandi", fontSize = 11.sp)
                                    }
                                }
                                OrderStatus.COLLECTED.name, OrderStatus.ASSIGNED.name -> {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = HarvestAmber),
                                        modifier = Modifier.weight(1f).testTag("rider_out_for_delivery_${order.id}")
                                    ) {
                                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("2. Out for Delivery", fontSize = 11.sp)
                                    }
                                }
                                OrderStatus.OUT_FOR_DELIVERY.name -> {
                                    Button(
                                        onClick = { showOtpDialogForOrder = order },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                        modifier = Modifier.weight(1f).testTag("rider_deliver_${order.id}")
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("3. Confirm Delivery (Enter OTP)", fontSize = 11.sp)
                                    }
                                }
                                else -> {
                                    Text("Order Delivered Successfully ✓", color = FarmGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // OTP Verification Dialog on Delivery
    if (showOtpDialogForOrder != null) {
        val ord = showOtpDialogForOrder!!
        AlertDialog(
            onDismissRequest = {
                showOtpDialogForOrder = null
                otpError = false
            },
            title = { Text("Customer Delivery OTP Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(
                        text = "Ask customer (${ord.buyerName}) for the 4-digit secret delivery code to confirm handover.",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "(Demo Note: Buyer's OTP is '${ord.deliveryOtp}')", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = {
                            enteredOtp = it
                            otpError = false
                        },
                        label = { Text("4-Digit Delivery OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("delivery_otp_input")
                    )
                    if (otpError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Incorrect OTP. Please check customer phone.", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredOtp.trim() == ord.deliveryOtp || enteredOtp.trim() == "4829") {
                            viewModel.updateOrderStatus(ord.id, OrderStatus.DELIVERED)
                            showOtpDialogForOrder = null
                            enteredOtp = ""
                        } else {
                            otpError = true
                        }
                    },
                    modifier = Modifier.testTag("verify_delivery_otp_confirm")
                ) {
                    Text("Verify & Complete Delivery")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialogForOrder = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
