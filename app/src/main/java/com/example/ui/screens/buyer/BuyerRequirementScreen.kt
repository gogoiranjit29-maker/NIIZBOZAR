package com.example.ui.screens.buyer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.RequirementOrderEntity
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestAmber
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun BuyerRequirementScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val requirementOrders by viewModel.requirementOrders.collectAsState()
    val userAddress by viewModel.userAddress.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Post New, 1: My Requirements & Quotations

    var itemsText by remember { mutableStateOf("Tomato – 20 kg\nPotato – 50 kg\nOnion – 30 kg\nGreen Chili – 5 kg") }
    var locationInput by remember { mutableStateOf(userAddress) }
    var preferredDate by remember { mutableStateOf("Tomorrow Morning") }
    var preferredSlot by remember { mutableStateOf("6:00 AM – 9:00 AM") }
    var isBulkOrder by remember { mutableStateOf(true) }
    var hasPhotoList by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Custom Requirement & Bulk Orders",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text(
            text = "Post custom shopping lists or wholesale requirements for hotels, restaurants & caterers.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Post List") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Quotations (${requirementOrders.size})") })
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == 0) {
            // Post Requirement Form
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Bulk order toggle
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBulkOrder) HarvestAmber.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isBulkOrder = !isBulkOrder }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = HarvestAmber, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Wholesale / Bulk Commercial Order", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Special Mandi wholesale pricing for restaurants, mess, events, and shops.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Checkbox(checked = isBulkOrder, onCheckedChange = { isBulkOrder = it })
                        }
                    }
                }

                item {
                    Text(text = "Write Product Names & Quantities:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = itemsText,
                        onValueChange = { itemsText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("requirement_items_input"),
                        placeholder = { Text("e.g. Potato 50kg, Tomato 20kg, Coriander 10 bundles...") }
                    )
                }

                item {
                    // Optional photo list upload
                    OutlinedButton(
                        onClick = { hasPhotoList = !hasPhotoList },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("upload_shopping_list_photo")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (hasPhotoList) "✓ Paper Shopping List Attached" else "+ Attach Handwritten Shopping List Photo", fontSize = 12.sp)
                    }
                }

                item {
                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("Delivery Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = preferredDate,
                            onValueChange = { preferredDate = it },
                            label = { Text("Preferred Date") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = preferredSlot,
                            onValueChange = { preferredSlot = it },
                            label = { Text("Time Window") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.submitRequirementOrder(
                                itemsText = itemsText,
                                location = locationInput,
                                preferredDate = preferredDate,
                                preferredSlot = preferredSlot,
                                isBulk = isBulkOrder,
                                photoUri = if (hasPhotoList) "attached_shopping_list.jpg" else null
                            )
                            activeTab = 1
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_requirement_button")
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Requirement to Staff", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            // Requirements & Quotations List
            if (requirementOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No requirements posted yet. Switch to 'Post List' to create one.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requirementOrders) { req ->
                        RequirementQuotationCard(
                            req = req,
                            onAcceptAndPay = { viewModel.acceptQuotationAndPay(req.id, "UPI (Prepaid)") },
                            onReject = { viewModel.rejectQuotation(req.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequirementQuotationCard(
    req: RequirementOrderEntity,
    onAcceptAndPay: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().testTag("requirement_card_${req.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = req.requirementCode, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (req.status) {
                        "ACCEPTED_PAID" -> FarmGreenPrimary.copy(alpha = 0.15f)
                        "QUOTATION_PREPARED" -> HarvestAmber.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = req.status.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (req.status) {
                            "ACCEPTED_PAID" -> FarmGreenPrimary
                            "QUOTATION_PREPARED" -> HarvestAmber
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Requested Items:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text = req.itemsText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Delivery to: ${req.deliveryLocation} (${req.preferredDate} • ${req.preferredTimeSlot})", fontSize = 11.sp)

            if (req.status == "QUOTATION_PREPARED" || req.status == "ACCEPTED_PAID") {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Official Mandi Price Quotation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                if (req.employeeNotes.isNotBlank()) {
                    Text(text = "Staff Note: ${req.employeeNotes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Wholesale Produce Cost:", fontSize = 12.sp)
                    Text("₹${"%.2f".format(req.quotationTotalSellerCost)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Distance Delivery Fee:", fontSize = 12.sp)
                    Text("+ ₹${"%.2f".format(req.quotationDeliveryFee)}", fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Handling & Service:", fontSize = 12.sp)
                    Text("+ ₹${"%.2f".format(req.quotationServiceFee)}", fontSize = 12.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Quotation Total:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("₹${"%.2f".format(req.quotationGrandTotal)}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                }

                if (req.status == "QUOTATION_PREPARED") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Decline", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onAcceptAndPay,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                            modifier = Modifier.weight(1.5f).testTag("accept_quotation_${req.id}")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Accept & Pay Online", fontSize = 12.sp)
                        }
                    }
                } else if (req.status == "ACCEPTED_PAID") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "✓ Paid Online & Confirmed for Mandi Sourcing", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FarmGreenPrimary)
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Awaiting employee review. You will receive an itemized quotation shortly.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
