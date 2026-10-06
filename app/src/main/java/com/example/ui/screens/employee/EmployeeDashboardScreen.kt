package com.example.ui.screens.employee

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RequirementOrderEntity
import com.example.model.TranslationHelper
import com.example.model.VerificationStatus
import com.example.ui.components.CallVerificationDialog
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestAmber
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun EmployeeDashboardScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val pendingProducts by viewModel.pendingProducts.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val requirementOrders by viewModel.requirementOrders.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val employeeName by viewModel.userName.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Verification Queue, 1: Buyer Quotations, 2: History

    var selectedProductForCall by remember { mutableStateOf<ProductEntity?>(null) }
    var selectedProductForDecision by remember { mutableStateOf<Pair<ProductEntity, VerificationStatus>?>(null) }
    var selectedReqForQuotation by remember { mutableStateOf<RequirementOrderEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Staff Operations Dashboard",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Staff: $employeeName • Verification & Quotations",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Verify (${pendingProducts.size})") }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Quotations (${requirementOrders.filter { it.status == "SUBMITTED" }.size})") }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("All Products") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (activeTab) {
            0 -> {
                // Verification Queue
                if (pendingProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Verification Queue Clean", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "All seller uploaded produce is verified and live.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingProducts) { product ->
                            EmployeeVerificationCard(
                                product = product,
                                currentLang = currentLang,
                                onCallSeller = { selectedProductForCall = product },
                                onApprove = { selectedProductForDecision = Pair(product, VerificationStatus.APPROVED) },
                                onReject = { selectedProductForDecision = Pair(product, VerificationStatus.REJECTED) },
                                onInfoRequired = { selectedProductForDecision = Pair(product, VerificationStatus.INFO_REQUIRED) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Buyer Requirements & Quotation Maker
                val unquoted = requirementOrders.filter { it.status == "SUBMITTED" }
                if (unquoted.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No pending buyer requirements needing quotations.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(unquoted) { req ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth().testTag("unquoted_card_${req.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = req.requirementCode, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = if (req.isBulk) "BULK COMMERCIAL" else "Standard List", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = HarvestAmber)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Buyer: ${req.buyerName} (${req.buyerPhone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Deliver to: ${req.deliveryLocation}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Items:\n${req.itemsText}", fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))

                                    Button(
                                        onClick = { selectedReqForQuotation = req },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                        modifier = Modifier.fillMaxWidth().testTag("prepare_quotation_${req.id}")
                                    ) {
                                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Prepare & Send Price Quotation", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // All products view
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allProducts) { product ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = product.nameEn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Seller: ${product.sellerName} • ₹${"%.0f".format(product.originalSellerPrice)}/${product.unit}", fontSize = 11.sp)
                                    Text(text = "Status: ${product.verificationStatus}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Call Verification Dialog
    if (selectedProductForCall != null) {
        val prod = selectedProductForCall!!
        CallVerificationDialog(
            sellerName = prod.sellerName,
            sellerPhone = prod.sellerPhone,
            productName = prod.nameEn,
            onDismiss = { selectedProductForCall = null },
            onMarkVerifiedCall = { selectedProductForCall = null }
        )
    }

    // Decision & Notes Dialog
    if (selectedProductForDecision != null) {
        val (prod, status) = selectedProductForDecision!!
        var notes by remember { mutableStateOf(if (status == VerificationStatus.APPROVED) "Verified stock & harvest quality via phone call. Ready for buyers." else "Price or quantity clarification needed.") }

        AlertDialog(
            onDismissRequest = { selectedProductForDecision = null },
            title = {
                Text(
                    text = "Confirm ${status.name}: ${prod.nameEn}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Add mandatory verification notes recorded into the audit trail:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Verification Notes") },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("verification_notes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyProduct(prod.id, status, notes)
                        selectedProductForDecision = null
                    },
                    modifier = Modifier.testTag("confirm_verification_decision_button")
                ) {
                    Text("Submit Decision")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProductForDecision = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Quotation Builder Modal
    if (selectedReqForQuotation != null) {
        val req = selectedReqForQuotation!!
        var sellerCostText by remember { mutableStateOf("2800") }
        var deliveryFeeText by remember { mutableStateOf("150") }
        var serviceFeeText by remember { mutableStateOf("100") }
        var notesText by remember { mutableStateOf("Wholesale rates secured from Bonda Mandi Yard. Delivery scheduled tomorrow morning.") }

        val sCost = sellerCostText.toDoubleOrNull() ?: 0.0
        val dFee = deliveryFeeText.toDoubleOrNull() ?: 0.0
        val svFee = serviceFeeText.toDoubleOrNull() ?: 0.0
        val grand = sCost + dFee + svFee

        AlertDialog(
            onDismissRequest = { selectedReqForQuotation = null },
            title = { Text("Prepare Formal Quotation for ${req.requirementCode}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(text = "Requested by: ${req.buyerName}", fontSize = 12.sp)
                    Text(text = req.itemsText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = sellerCostText,
                        onValueChange = { sellerCostText = it },
                        label = { Text("Produce Sourcing Wholesale Cost (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("quote_seller_cost")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = deliveryFeeText,
                        onValueChange = { deliveryFeeText = it },
                        label = { Text("Distance Delivery Charge (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = serviceFeeText,
                        onValueChange = { serviceFeeText = it },
                        label = { Text("Mandi Service & Packaging (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notes for Buyer") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Grand Total Quotation: ₹${"%.2f".format(grand)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.prepareQuotationForRequirement(
                            requirementId = req.id,
                            sellerCost = sCost,
                            deliveryFee = dFee,
                            serviceFee = svFee,
                            grandTotal = grand,
                            notes = notesText
                        )
                        selectedReqForQuotation = null
                    },
                    modifier = Modifier.testTag("send_quotation_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send Quotation")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedReqForQuotation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmployeeVerificationCard(
    product: ProductEntity,
    currentLang: com.example.model.Language,
    onCallSeller: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onInfoRequired: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().testTag("verify_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fresh_market_banner),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = product.nameEn, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "Seller: ${product.sellerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Origin: ${product.sellerMandiLocation} • ${product.sellerDistanceKm} km", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Call Seller Action Button
                IconButton(
                    onClick = onCallSeller,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("call_seller_${product.id}")
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call Seller", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verification Details
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Price: ₹${"%.0f".format(product.originalSellerPrice)} / ${product.unit}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Qty: ${"%.0f".format(product.availableQty)} ${product.unit}", fontSize = 11.sp)
                    Text(text = product.freshnessStatus, fontSize = 11.sp, color = FarmGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Decision Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).testTag("reject_btn_${product.id}")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Reject", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onInfoRequired,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Clarify", fontSize = 11.sp)
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1.3f).testTag("approve_btn_${product.id}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
