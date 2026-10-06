package com.example.ui.screens.buyer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ProductEntity
import com.example.model.AppScreen
import com.example.model.SmartPriceBreakdown
import com.example.model.TranslationHelper
import com.example.ui.theme.FarmGreenContainer
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.HarvestAmber
import com.example.viewmodel.MarketplaceViewModel

/**
 * Buyer Marketplace Home Screen transformed with a Flipkart-style shopping view.
 * Features a 2-column product grid, Flipkart Assured trust badge, green rating pill,
 * strikethrough MRP discounts, and quick-add actions.
 */
@Composable
fun BuyerHomeScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCatId by viewModel.selectedCategoryId.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val userAddress by viewModel.userAddress.collectAsState()

    // View mode: default true for Flipkart 2-column grid view
    var isGridView by remember { mutableStateOf(true) }
    var wishlistIds by remember { mutableStateOf(setOf<Long>()) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = if (isGridView) GridCells.Fixed(2) else GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 88.dp),
            horizontalArrangement = Arrangement.spacedBy(if (isGridView) 8.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(if (isGridView) 8.dp else 8.dp)
        ) {
            // 1. Flipkart-style Top Bar & Delivery Location
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Location strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Deliver to: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userAddress,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = "Mandi Express",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Flipkart Style Search Bar + Prominent SELL Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = {
                                Text(
                                    "Search vegetables, fruits...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("buyer_search_bar")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Prominently Visible "SELL" Button (Automatically converts to Seller page)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF047857), // Signature Farmer Green
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .height(50.dp)
                                .clickable {
                                    viewModel.setRole(com.example.model.UserRole.SELLER)
                                }
                                .testTag("buyer_prominent_sell_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sell,
                                    contentDescription = "Sell Produce",
                                    tint = Color(0xFF86EFAC),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = "SELL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Produce",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Flipkart Category Story Circles / Bubbles (With Pinned SELL Bubble)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 10.dp)
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Pinned "Sell" Bubble for Farmers/Growers
                        item {
                            CategoryCircleItem(
                                label = "Sell / বিক্ৰী",
                                isSelected = false,
                                isSpecialSell = true,
                                onClick = { viewModel.setRole(com.example.model.UserRole.SELLER) },
                                tag = "buyer_category_sell_bubble"
                            )
                        }

                        item {
                            CategoryCircleItem(
                                label = "All Items",
                                isSelected = selectedCatId == null,
                                onClick = { viewModel.selectCategory(null) }
                            )
                        }
                        items(categories) { cat ->
                            val isSelected = selectedCatId == cat.id
                            val catName = when (currentLang) {
                                com.example.model.Language.EN -> cat.nameEn
                                com.example.model.Language.HI -> cat.nameHi
                                com.example.model.Language.AS -> cat.nameAs
                            }
                            CategoryCircleItem(
                                label = catName,
                                isSelected = isSelected,
                                onClick = { viewModel.selectCategory(cat.id) },
                                tag = "category_chip_${cat.id}"
                            )
                        }
                    }
                }
            }

            // 3. Flipkart Deals Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.height(115.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.fresh_market_banner),
                            contentDescription = "Flipkart Mandi Deals",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF1E3A8A).copy(alpha = 0.88f),
                                            Color(0xFF047857).copy(alpha = 0.70f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFFC200)
                                ) {
                                    Text(
                                        text = "⚡ DEALS OF THE DAY",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = Color(0xFF1E293B),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Direct Mandi Prices",
                                    color = Color(0xFFFDE68A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Up to 35% Off Wholesale Vegetables",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "100% Farm Fresh • Quality Checked by Staff",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 4. Flipkart Sort & Layout Controls Bar
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sorting Pills
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        item { FlipkartSortPill("Nearest", sortBy == "nearest") { viewModel.setSortBy("nearest") } }
                        item { FlipkartSortPill("Price: Low", sortBy == "lowest_price") { viewModel.setSortBy("lowest_price") } }
                        item { FlipkartSortPill("Freshness", sortBy == "highest_freshness") { viewModel.setSortBy("highest_freshness") } }
                        item { FlipkartSortPill("Top Rated", sortBy == "best_rating") { viewModel.setSortBy("best_rating") } }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // View Toggle (Grid / List)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable { isGridView = !isGridView }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.GridView else Icons.Default.ViewAgenda,
                                contentDescription = "Toggle Grid",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isGridView) "Grid" else "List",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Section Subheader: Product Count
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FRESH PRODUCE (${filteredProducts.size} Items)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = Color(0xFF2874F0)
                        ) {
                            Text(
                                text = "NB Assured ✓",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // 5. Products Content (Flipkart 2-Column Grid or List View)
            if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No fresh produce found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "Try clearing search filters or pick another category.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    val breakdown = viewModel.getProductPriceBreakdown(product)
                    val isWishlisted = wishlistIds.contains(product.id)

                    if (isGridView) {
                        // Flipkart 2-Column Grid Card
                        FlipkartGridProductCard(
                            product = product,
                            breakdown = breakdown,
                            currentLang = currentLang,
                            isWishlisted = isWishlisted,
                            onWishlistToggle = {
                                wishlistIds = if (isWishlisted) wishlistIds - product.id else wishlistIds + product.id
                            },
                            onProductClick = { viewModel.selectProductForDetail(product) },
                            onAddToCart = { viewModel.addToCart(product, 1.0) },
                            onDirectBuy = { viewModel.startDirectBuy(product, 1.0) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        )
                    } else {
                        // Horizontal List View Alternative
                        FlipkartListProductCard(
                            product = product,
                            breakdown = breakdown,
                            currentLang = currentLang,
                            isWishlisted = isWishlisted,
                            onWishlistToggle = {
                                wishlistIds = if (isWishlisted) wishlistIds - product.id else wishlistIds + product.id
                            },
                            onProductClick = { viewModel.selectProductForDetail(product) },
                            onAddToCart = { viewModel.addToCart(product, 1.0) },
                            onDirectBuy = { viewModel.startDirectBuy(product, 1.0) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }

        // Action FABs: Prominent Sell FAB (auto-converting to Seller) & Post Requirement
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prominent "Sell Produce" FAB
            ExtendedFloatingActionButton(
                onClick = { viewModel.setRole(com.example.model.UserRole.SELLER) },
                icon = { Icon(Icons.Default.Sell, contentDescription = "Sell", tint = Color(0xFF86EFAC)) },
                text = { Text("Sell Produce", fontWeight = FontWeight.Black, fontSize = 12.sp) },
                containerColor = Color(0xFF047857), // Signature Farmer Green
                contentColor = Color.White,
                modifier = Modifier.testTag("buyer_sell_fab")
            )

            // Post Requirement FAB
            ExtendedFloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.BuyerRequirements) },
                icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                text = { Text("Post Req", fontSize = 11.sp) },
                containerColor = Color(0xFFFB641B), // Flipkart Orange Action Color
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_post_requirement")
            )
        }
    }
}

/**
 * Flipkart Signature 2-Column Grid Product Card.
 * High-density shopping item with image, Assured badge, rating pill, strikethrough MRP, and Add button.
 */
@Composable
fun FlipkartGridProductCard(
    product: ProductEntity,
    breakdown: SmartPriceBreakdown,
    currentLang: com.example.model.Language,
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onDirectBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val translatedName = TranslationHelper.getProductName(product.nameEn, currentLang)

    // Calculate simulated MRP and discount percent just like Flipkart
    val sellingPrice = breakdown.finalPricePerUnit
    val mrpPrice = (sellingPrice * 1.30 + 10).coerceAtLeast(sellingPrice + 12.0)
    val discountPercent = (((mrpPrice - sellingPrice) / mrpPrice) * 100).toInt()

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product Image Box with Flipkart Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fresh_market_banner),
                    contentDescription = translatedName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Left: "NB Assured" Badge
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    color = Color(0xFF2874F0), // Flipkart Blue
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assured ✓",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Top Right: Wishlist Heart Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .clickable { onWishlistToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Bottom Left overlay: Freshness Tag
                Surface(
                    shape = RoundedCornerShape(topEnd = 6.dp),
                    color = Color(0xFF047857).copy(alpha = 0.90f),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = product.freshnessStatus,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Product Details Section
            Column(modifier = Modifier.padding(8.dp)) {
                // Product Title
                Text(
                    text = translatedName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Sub-category / Mandi
                Text(
                    text = "${product.subCategory} • ${"%.1f".format(product.sellerDistanceKm)} km",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Flipkart Green Star Rating Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF388E3C) // Flipkart Green
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "4.3",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(48)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price Row: Selling Price + Strikethrough MRP + Discount %
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "₹${"%.0f".format(sellingPrice)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/${product.unit}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${"%.0f".format(mrpPrice)}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough
                    )
                    Text(
                        text = "$discountPercent% off",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C) // Flipkart Discount Green
                    )
                }

                // Transparent mandi footnote
                Text(
                    text = "Seller gets: ₹${"%.0f".format(product.originalSellerPrice)}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Flipkart Free Delivery indicator
                Text(
                    text = "Free Mandi Delivery",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2874F0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Flipkart Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFF2874F0)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF2874F0),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Add",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2874F0)
                        )
                    }

                    Button(
                        onClick = onDirectBuy,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB641B)), // Flipkart Orange
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .testTag("direct_buy_${product.id}")
                    ) {
                        Text(
                            text = "Buy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Alternative Flipkart Horizontal List Card when user selects list view.
 */
@Composable
fun FlipkartListProductCard(
    product: ProductEntity,
    breakdown: SmartPriceBreakdown,
    currentLang: com.example.model.Language,
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onDirectBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val translatedName = TranslationHelper.getProductName(product.nameEn, currentLang)
    val sellingPrice = breakdown.finalPricePerUnit
    val mrpPrice = (sellingPrice * 1.30 + 10).coerceAtLeast(sellingPrice + 12.0)
    val discountPercent = (((mrpPrice - sellingPrice) / mrpPrice) * 100).toInt()

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Image with Flipkart Assured & Freshness Badges
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fresh_market_banner),
                    contentDescription = translatedName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 4.dp),
                    color = Color(0xFF2874F0),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Assured ✓",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = translatedName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${product.subCategory} • Mandi: ${product.sellerMandiLocation}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = onWishlistToggle,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF388E3C)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("4.3", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📍 ${"%.1f".format(product.sellerDistanceKm)} km away",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Price Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "₹${"%.0f".format(sellingPrice)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("/${product.unit}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "₹${"%.0f".format(mrpPrice)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough
                    )
                    Text(
                        text = "$discountPercent% off",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFF2874F0)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Text("Add to Basket", fontSize = 11.sp, color = Color(0xFF2874F0))
                    }

                    Button(
                        onClick = onDirectBuy,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB641B)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("direct_buy_${product.id}")
                    ) {
                        Text("Direct Buy", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Circular Category Item mimicking Flipkart's top category icon bar.
 */
@Composable
fun CategoryCircleItem(
    label: String,
    isSelected: Boolean,
    isSpecialSell: Boolean = false,
    onClick: () -> Unit,
    tag: String = "category_chip_default"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSpecialSell -> Color(0xFF047857) // Rich Farmer Green
                        isSelected -> Color(0xFF2874F0)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSpecialSell) Icons.Default.Sell else Icons.Default.Eco,
                contentDescription = label,
                tint = if (isSpecialSell) Color(0xFF86EFAC) else if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSpecialSell || isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSpecialSell) Color(0xFF047857) else if (isSelected) Color(0xFF2874F0) else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun FlipkartSortPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
