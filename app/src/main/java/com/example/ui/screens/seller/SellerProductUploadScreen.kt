package com.example.ui.screens.seller

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.model.FreshnessStatus
import com.example.model.ProductQuality
import com.example.model.TranslationHelper
import com.example.model.UnitType
import com.example.ui.components.AddCustomCategoryDialog
import com.example.ui.theme.FarmGreenPrimary
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProductUploadScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var nameEn by remember { mutableStateOf("Tomato") }
    var nameHi by remember { mutableStateOf("टमाटर") }
    var nameAs by remember { mutableStateOf("বিলাহী") }
    var selectedCatId by remember { mutableStateOf("fruit_veg") }
    var subCategory by remember { mutableStateOf("Fresh Vine Red") }
    var description by remember { mutableStateOf("Naturally ripened farm-fresh tomatoes, sweet and juicy.") }
    var priceText by remember { mutableStateOf("28") }
    var qtyText by remember { mutableStateOf("100") }
    var selectedUnit by remember { mutableStateOf(UnitType.KG) }
    var freshness by remember { mutableStateOf("Freshly harvested") }
    var quality by remember { mutableStateOf("Grade A (Export / Select)") }
    var harvestDate by remember { mutableStateOf("Today 6:00 AM") }
    var location by remember { mutableStateOf("Bonda Mandi Yard, Gate 2") }
    var distanceKmText by remember { mutableStateOf("2.0") }

    var photoAttached by remember { mutableStateOf(true) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val freshnessOptions = listOf(
        "Freshly harvested",
        "Fresh",
        "Good quality",
        "Average quality",
        "Premium Grade"
    )

    val qualityOptions = listOf(
        "Grade A (Export / Select)",
        "Grade B (Standard)",
        "100% Organic Desi"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Sell Produce - Mandi Listing & Verification",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Text(
                text = "Accurate harvest details and clear produce photos ensure rapid employee verification and live buyer sales.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Camera / Picture Upload Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (photoAttached) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.fresh_market_banner),
                                contentDescription = "Captured Product",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                            ) {
                                Text(
                                    text = "✓ Photo Ready (Compressed for low bandwidth)",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    } else {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = FarmGreenPrimary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Take Photo of Your Vegetable or Product", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { photoAttached = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("seller_camera_click")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Capture Camera", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { photoAttached = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Product Name (with auto multi-language translation hint)
        item {
            OutlinedTextField(
                value = nameEn,
                onValueChange = {
                    nameEn = it
                    // Auto translate popular vegetables
                    nameHi = TranslationHelper.getProductName(it, com.example.model.Language.HI)
                    nameAs = TranslationHelper.getProductName(it, com.example.model.Language.AS)
                },
                label = { Text("Product Name (English)*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("upload_name_en")
            )
        }

        // Hindi and Assamese Name Inputs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nameHi,
                    onValueChange = { nameHi = it },
                    label = { Text("नाम (हिन्दी)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = nameAs,
                    onValueChange = { nameAs = it },
                    label = { Text("নাম (অসমীয়া)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category Selector & "+ Add New Category" (Requirement 17)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Category*", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                TextButton(
                    onClick = { showAddCategoryDialog = true },
                    modifier = Modifier.testTag("btn_propose_category")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("+ New Category", fontSize = 12.sp)
                }
            }

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                val selectedCategoryName = categories.firstOrNull { it.id == selectedCatId }?.nameEn ?: "Select Category"
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("upload_category_dropdown")
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text("${cat.nameEn} (${cat.nameAs})", fontSize = 13.sp) },
                            onClick = {
                                selectedCatId = cat.id
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Subcategory
        item {
            OutlinedTextField(
                value = subCategory,
                onValueChange = { subCategory = it },
                label = { Text("Sub-category / Variety (e.g. Desi Vine, Organic, Cold-pressed)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Price and Available Quantity
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Original Price (₹)*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("upload_price_input")
                )
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it },
                    label = { Text("Quantity Available*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("upload_qty_input")
                )
            }
        }

        // Unit Selection: Kilogram, Gram, Piece, Bundle, Packet, Sack, Dozen, Crate
        item {
            Text(text = "Select Unit / इकाई / একক:*", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(UnitType.values()) { unit ->
                    FilterChip(
                        selected = selectedUnit == unit,
                        onClick = { selectedUnit = unit },
                        label = { Text(unit.symbol, fontSize = 12.sp) },
                        modifier = Modifier.testTag("unit_${unit.name}")
                    )
                }
            }
        }

        // Freshness Status
        item {
            Text(text = "Freshness Status:*", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(freshnessOptions) { f ->
                    FilterChip(
                        selected = freshness == f,
                        onClick = { freshness = f },
                        label = { Text(f, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Quality Grade
        item {
            Text(text = "Quality Grade:*", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                qualityOptions.forEach { q ->
                    FilterChip(
                        selected = quality == q,
                        onClick = { quality = q },
                        label = { Text(q, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Harvest Date and Mandi Location
        item {
            OutlinedTextField(
                value = harvestDate,
                onValueChange = { harvestDate = it },
                label = { Text("Harvest / Picking Date & Time") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Farm / Mandi Location") },
                    singleLine = true,
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = distanceKmText,
                    onValueChange = { distanceKmText = it },
                    label = { Text("Distance (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Description
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Product Description & Farming Practices") },
                modifier = Modifier.fillMaxWidth().height(90.dp)
            )
        }

        // Submit Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 25.0
                    val qty = qtyText.toDoubleOrNull() ?: 50.0
                    val dist = distanceKmText.toDoubleOrNull() ?: 2.5
                    viewModel.uploadSellerProduct(
                        nameEn = nameEn,
                        nameHi = nameHi,
                        nameAs = nameAs,
                        categoryId = selectedCatId,
                        subCategory = subCategory,
                        description = description,
                        price = price,
                        unit = selectedUnit.symbol,
                        qty = qty,
                        freshness = freshness,
                        quality = quality,
                        harvestDate = harvestDate,
                        location = location,
                        distanceKm = dist
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_product_for_verification")
            ) {
                Icon(Icons.Default.Sell, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sell Produce Now (Submit for Verification)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Custom Category Dialog
    if (showAddCategoryDialog) {
        AddCustomCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSubmitCategory = { cEn, cHi, cAs, parent ->
                viewModel.createCategory(cEn, cHi, cAs, parent) { success, msg ->
                    if (success) {
                        showAddCategoryDialog = false
                    }
                }
            }
        )
    }
}
