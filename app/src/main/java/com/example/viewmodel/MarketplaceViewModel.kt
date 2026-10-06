package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.FirebaseAuthenticationManager
import com.example.data.firebase.FirebaseBackendStatus
import com.example.data.firebase.FirebaseSyncManager
import com.example.data.local.MarketplaceDatabase
import com.example.data.local.entities.CartItemEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.DeliveryPartnerEntity
import com.example.data.local.entities.EmployeeEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.PricingConfigEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RequirementOrderEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.repository.MarketplaceRepository
import com.example.model.AppScreen
import com.example.model.AppThemeMode
import com.example.model.DeliverySlotOption
import com.example.model.Language
import com.example.model.NetworkMode
import com.example.model.OrderStatus
import com.example.model.SmartPriceBreakdown
import com.example.model.UserRole
import com.example.model.VerificationStatus
import com.example.navigation.AppRoute
import com.example.navigation.NavigationStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MarketplaceDatabase.getInstance(application)
    val repository = MarketplaceRepository(db.marketplaceDao())
    val authManager = FirebaseAuthenticationManager(application, db.marketplaceDao())
    val syncManager = FirebaseSyncManager(application)

    private val _firebaseStatus = MutableStateFlow(syncManager.getStatus())
    val firebaseStatus: StateFlow<FirebaseBackendStatus> = _firebaseStatus.asStateFlow()

    // --- User Session ---
    private val _currentRole = MutableStateFlow(UserRole.BUYER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("Priya Sharma")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("+91 98640 55443")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userAddress = MutableStateFlow("House 42, Green Park Road, Dispur, Guwahati - 781006")
    val userAddress: StateFlow<String> = _userAddress.asStateFlow()

    // --- App Settings ---
    private val _currentLanguage = MutableStateFlow(Language.EN)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _networkMode = MutableStateFlow(NetworkMode.ONLINE)
    val networkMode: StateFlow<NetworkMode> = _networkMode.asStateFlow()

    // --- Navigation ---
    val navManager = NavigationStateManager(AppRoute.BuyerDashboard)
    val currentRoute: StateFlow<AppRoute> = navManager.currentRoute

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.BuyerHome)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<AppScreen>(AppScreen.BuyerHome)

    // --- Search & Filters ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _sortBy = MutableStateFlow("nearest") // "nearest", "lowest_price", "highest_freshness", "best_rating"
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // --- Selection for Detail & Direct Buy ---
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    private val _directBuyProduct = MutableStateFlow<ProductEntity?>(null)
    val directBuyProduct: StateFlow<ProductEntity?> = _directBuyProduct.asStateFlow()

    private val _directBuyQuantity = MutableStateFlow(1.0)
    val directBuyQuantity: StateFlow<Double> = _directBuyQuantity.asStateFlow()

    // --- Status Messages / Snackbars ---
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // --- Data Streams from Repository ---
    val approvedProducts: StateFlow<List<ProductEntity>> = repository.approvedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingProducts: StateFlow<List<ProductEntity>> = repository.pendingVerificationProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val requirementOrders: StateFlow<List<RequirementOrderEntity>> = repository.allRequirementOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deliveryPartners: StateFlow<List<DeliveryPartnerEntity>> = repository.deliveryPartners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<ReviewEntity>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEmployees: StateFlow<List<EmployeeEntity>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pricingConfig: StateFlow<PricingConfigEntity> = repository.pricingConfig
        .combine(MutableStateFlow(PricingConfigEntity())) { config, default ->
            config ?: default
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PricingConfigEntity())

    val notifications: StateFlow<List<NotificationEntity>> = _currentRole
        .combine(repository.allOrders) { role, _ ->
            // trigger refresh
            role
        }
        .combine(MutableStateFlow(0)) { role, _ ->
            role
        }
        .combine(repository.getNotificationsForRole("ALL")) { _, notifs ->
            notifs
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filtered and Sorted Products for Buyer Home ---
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        approvedProducts,
        searchQuery,
        selectedCategoryId,
        sortBy,
        currentLanguage
    ) { products, query, catId, sort, _ ->
        var list = products

        // Category filter
        if (catId != null) {
            list = list.filter { it.categoryId == catId }
        }

        // Search query across all 3 languages
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.nameEn.lowercase().contains(q) ||
                        it.nameHi.lowercase().contains(q) ||
                        it.nameAs.lowercase().contains(q) ||
                        it.subCategory.lowercase().contains(q) ||
                        it.description.lowercase().contains(q)
            }
        }

        // Sorting
        when (sort) {
            "nearest" -> list.sortedBy { it.sellerDistanceKm }
            "lowest_price" -> list.sortedBy { it.originalSellerPrice }
            "highest_freshness" -> list.sortedBy {
                when (it.freshnessStatus) {
                    "Freshly harvested" -> 1
                    "Fresh" -> 2
                    "Premium Grade" -> 3
                    "Good quality" -> 4
                    else -> 5
                }
            }
            "best_rating" -> list.sortedByDescending { it.demandFactor }
            "recently_added" -> list.sortedByDescending { it.createdAt }
            else -> list.sortedBy { it.sellerDistanceKm }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Pricing Calculator Helper ---
    fun getProductPriceBreakdown(product: ProductEntity, quantity: Double = 1.0): SmartPriceBreakdown {
        return repository.calculateSmartPrice(
            sellerBasePrice = product.originalSellerPrice,
            distanceKm = product.sellerDistanceKm,
            quantity = quantity,
            demandFactor = product.demandFactor,
            config = pricingConfig.value
        )
    }

    // --- Navigation Functions ---
    fun navigateTo(route: AppRoute, clearBackStack: Boolean = false) {
        navManager.navigateTo(route, clearBackStack)
        _currentScreen.value = mapRouteToScreen(route)
    }

    fun navigateTo(screen: AppScreen) {
        val route = mapScreenToRoute(screen)
        navManager.navigateTo(route)
        _currentScreen.value = screen
    }

    fun navigateToDashboardForRole(role: UserRole) {
        _currentRole.value = role
        navManager.navigateToDashboardForRole(role)
        authManager.updateUserRole(role)
        _currentScreen.value = mapRouteToScreen(navManager.currentRoute.value)
    }

    fun navigateToLogin() {
        navManager.navigateToLogin()
        _currentScreen.value = AppScreen.RoleAndAuth
    }

    fun handleBack(): Boolean {
        val fallback = when (_currentRole.value) {
            UserRole.BUYER -> AppRoute.BuyerDashboard
            UserRole.SELLER -> AppRoute.SellerDashboard
            UserRole.EMPLOYEE -> AppRoute.EmployeeDashboard
            UserRole.ADMIN -> AppRoute.AdminDashboard
            UserRole.DELIVERY_PARTNER -> AppRoute.DeliveryDashboard
        }
        val handled = navManager.navigateBack(fallback)
        if (handled) {
            _currentScreen.value = mapRouteToScreen(navManager.currentRoute.value)
        }
        return handled
    }

    private fun mapScreenToRoute(screen: AppScreen): AppRoute {
        return when (screen) {
            AppScreen.RoleAndAuth -> AppRoute.Login
            AppScreen.BuyerHome -> AppRoute.BuyerDashboard
            AppScreen.SellerDashboard -> AppRoute.SellerDashboard
            AppScreen.EmployeeDashboard -> AppRoute.EmployeeDashboard
            AppScreen.AdminDashboard -> AppRoute.AdminDashboard
            AppScreen.DeliveryDashboard -> AppRoute.DeliveryDashboard
            AppScreen.ProductDetail -> AppRoute.ProductDetail
            AppScreen.CartAndCheckout -> AppRoute.CartAndCheckout
            AppScreen.DirectBuyCheckout -> AppRoute.DirectBuyCheckout
            AppScreen.OrderTracking -> AppRoute.OrderTracking
            AppScreen.BuyerRequirements -> AppRoute.BuyerRequirements
            AppScreen.SellerProductUpload -> AppRoute.SellerProductUpload
            AppScreen.ProfileAndSettings -> AppRoute.ProfileAndSettings
        }
    }

    private fun mapRouteToScreen(route: AppRoute): AppScreen {
        return when (route) {
            AppRoute.Login -> AppScreen.RoleAndAuth
            AppRoute.BuyerDashboard -> AppScreen.BuyerHome
            AppRoute.SellerDashboard -> AppScreen.SellerDashboard
            AppRoute.EmployeeDashboard -> AppScreen.EmployeeDashboard
            AppRoute.AdminDashboard -> AppScreen.AdminDashboard
            AppRoute.DeliveryDashboard -> AppScreen.DeliveryDashboard
            AppRoute.ProductDetail -> AppScreen.ProductDetail
            AppRoute.CartAndCheckout -> AppScreen.CartAndCheckout
            AppRoute.DirectBuyCheckout -> AppScreen.DirectBuyCheckout
            AppRoute.OrderTracking -> AppScreen.OrderTracking
            AppRoute.BuyerRequirements -> AppScreen.BuyerRequirements
            AppRoute.SellerProductUpload -> AppScreen.SellerProductUpload
            AppRoute.ProfileAndSettings -> AppScreen.ProfileAndSettings
        }
    }

    // --- User & Role Management ---
    fun setRole(role: UserRole) {
        navigateToDashboardForRole(role)
        showMessage("Switched to ${role.name.replace("_", " ")} mode")
    }

    fun loginWithDetails(name: String, phone: String, role: UserRole) {
        _userName.value = name.ifBlank { "User ${phone.takeLast(4)}" }
        _userPhone.value = phone
        _isLoggedIn.value = true
        navigateToDashboardForRole(role)
        showMessage("Logged in successfully as ${_userName.value}")
    }

    fun sendPhoneOtp(
        activity: Activity,
        phone: String,
        onCodeSent: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        authManager.sendPhoneOtp(
            activity = activity,
            phoneNumber = phone,
            onCodeSent = { vid ->
                onCodeSent(vid)
                showMessage("OTP Code dispatched to $phone")
            },
            onError = { err ->
                onError(err)
                showMessage(err)
            }
        )
    }

    fun verifyPhoneOtp(
        verificationId: String,
        otpCode: String,
        name: String,
        phone: String,
        role: UserRole,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        authManager.verifyPhoneOtp(
            verificationId = verificationId,
            otpCode = otpCode,
            userName = name,
            phoneNumber = phone,
            role = role,
            onSuccess = { user ->
                _userName.value = user.name
                _userPhone.value = user.phone
                _isLoggedIn.value = true
                setRole(role)
                showMessage("Welcome back, ${user.name} (${role.name})")
                onSuccess()
            },
            onError = { err ->
                onError(err)
                showMessage(err)
            }
        )
    }

    fun logout() {
        authManager.signOut {
            _isLoggedIn.value = false
            navigateTo(AppScreen.RoleAndAuth)
            showMessage("Logged out successfully")
        }
    }

    // --- Language & Theme ---
    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
        showMessage("Language set to ${lang.displayName} (${lang.nativeName})")
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun setNetworkMode(mode: NetworkMode) {
        _networkMode.value = mode
        showMessage("Network mode changed to ${mode.name}")
    }

    // --- Search & Filter Controls ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun selectProductForDetail(product: ProductEntity) {
        _selectedProduct.value = product
        navigateTo(AppScreen.ProductDetail)
    }

    // --- Cart Actions ---
    fun addToCart(product: ProductEntity, quantity: Double = 1.0) {
        viewModelScope.launch {
            val existing = cartItems.value.firstOrNull { it.productId == product.id }
            val newQty = (existing?.quantity ?: 0.0) + quantity
            repository.addToCart(product.id, newQty)
            showMessage("Added ${product.nameEn} to basket")
        }
    }

    fun updateCartItemQuantity(productId: Long, newQuantity: Double) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, newQuantity)
        }
    }

    fun removeCartItem(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showMessage("Removed from basket")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // --- Direct Buy Flow ---
    fun startDirectBuy(product: ProductEntity, qty: Double = 1.0) {
        _directBuyProduct.value = product
        _directBuyQuantity.value = qty
        navigateTo(AppScreen.DirectBuyCheckout)
    }

    // --- Order Checkout (Prepaid Online Only) ---
    fun checkoutCart(
        deliverySlot: DeliverySlotOption,
        preferredDate: String,
        address: String,
        paymentMethodName: String
    ) {
        viewModelScope.launch {
            val items = cartItems.value
            val productsMap = approvedProducts.value.associateBy { it.id }

            if (items.isEmpty()) {
                showMessage("Basket is empty")
                return@launch
            }

            var totalSellerAmount = 0.0
            var maxDistance = 1.0
            val summaryBuilder = StringBuilder()

            for (item in items) {
                val p = productsMap[item.productId]
                if (p != null) {
                    val itemSellerSubtotal = p.originalSellerPrice * item.quantity
                    totalSellerAmount += itemSellerSubtotal
                    if (p.sellerDistanceKm > maxDistance) {
                        maxDistance = p.sellerDistanceKm
                    }
                    summaryBuilder.append("${p.nameEn} (${item.quantity} ${p.unit}), ")
                }
            }

            val config = pricingConfig.value
            val deliveryCharge = maxDistance * config.perKmDeliveryRate
            val serviceCharge = config.baseServiceFee
            val packagingCharge = config.packagingFee
            val finalTotal = totalSellerAmount + deliveryCharge + serviceCharge + packagingCharge

            val txId = "TXN-" + System.currentTimeMillis().toString().takeLast(8)

            val orderId = repository.placePrepaidOrder(
                buyerId = _userPhone.value,
                buyerName = _userName.value,
                buyerPhone = _userPhone.value,
                buyerAddress = address.ifBlank { _userAddress.value },
                deliverySlot = deliverySlot.timeWindow,
                preferredDeliveryDate = preferredDate,
                totalSellerAmount = totalSellerAmount,
                deliveryCharge = deliveryCharge,
                serviceCharge = serviceCharge,
                packagingCharge = packagingCharge,
                finalTotalAmount = finalTotal,
                paymentMethod = paymentMethodName,
                paymentTransactionId = txId,
                itemsSummary = summaryBuilder.toString().removeSuffix(", ")
            )

            showMessage("Order Placed Successfully! (Txn ID: $txId)")
            navigateTo(AppScreen.OrderTracking)
        }
    }

    fun checkoutDirectBuy(
        product: ProductEntity,
        quantity: Double,
        deliverySlot: DeliverySlotOption,
        preferredDate: String,
        address: String,
        paymentMethodName: String
    ) {
        viewModelScope.launch {
            val breakdown = getProductPriceBreakdown(product, quantity)
            val totalSellerAmount = product.originalSellerPrice * quantity
            val deliveryCharge = breakdown.distanceAdjustment
            val serviceCharge = breakdown.serviceCharge
            val packagingCharge = breakdown.packagingCharge
            val finalTotal = breakdown.finalPricePerUnit * quantity

            val txId = "TXN-" + System.currentTimeMillis().toString().takeLast(8)
            val summary = "${product.nameEn} ($quantity ${product.unit})"

            repository.placePrepaidOrder(
                buyerId = _userPhone.value,
                buyerName = _userName.value,
                buyerPhone = _userPhone.value,
                buyerAddress = address.ifBlank { _userAddress.value },
                deliverySlot = deliverySlot.timeWindow,
                preferredDeliveryDate = preferredDate,
                totalSellerAmount = totalSellerAmount,
                deliveryCharge = deliveryCharge,
                serviceCharge = serviceCharge,
                packagingCharge = packagingCharge,
                finalTotalAmount = finalTotal,
                paymentMethod = paymentMethodName,
                paymentTransactionId = txId,
                itemsSummary = summary
            )

            showMessage("Order Placed Successfully via $paymentMethodName!")
            navigateTo(AppScreen.OrderTracking)
        }
    }

    // --- Order Lifecycle Management ---
    fun updateOrderStatus(orderId: Long, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            showMessage("Order status updated to ${status.name}")
        }
    }

    fun assignDeliveryPartnerToOrder(orderId: Long, partner: DeliveryPartnerEntity) {
        viewModelScope.launch {
            repository.assignDeliveryPartner(orderId, partner.id, partner.name)
            showMessage("Order #$orderId assigned to ${partner.name}")
        }
    }

    // --- Requirement / Bulk Order Management ---
    fun submitRequirementOrder(
        itemsText: String,
        location: String,
        preferredDate: String,
        preferredSlot: String,
        isBulk: Boolean,
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            repository.postRequirement(
                buyerName = _userName.value,
                buyerPhone = _userPhone.value,
                deliveryLocation = location.ifBlank { _userAddress.value },
                preferredDate = preferredDate,
                preferredTimeSlot = preferredSlot,
                isBulk = isBulk,
                itemsText = itemsText,
                photoUri = photoUri
            )
            showMessage("Requirement submitted! Our team will prepare a quotation shortly.")
            navigateTo(AppScreen.BuyerRequirements)
        }
    }

    fun prepareQuotationForRequirement(
        requirementId: Long,
        sellerCost: Double,
        deliveryFee: Double,
        serviceFee: Double,
        grandTotal: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.prepareQuotation(
                requirementId = requirementId,
                sellerCost = sellerCost,
                deliveryFee = deliveryFee,
                serviceFee = serviceFee,
                grandTotal = grandTotal,
                notes = notes
            )
            showMessage("Quotation sent to buyer")
        }
    }

    fun acceptQuotationAndPay(requirementId: Long, paymentMethod: String) {
        viewModelScope.launch {
            repository.acceptQuotationAndPay(requirementId, paymentMethod)
            showMessage("Quotation accepted & paid via $paymentMethod!")
        }
    }

    fun rejectQuotation(requirementId: Long) {
        viewModelScope.launch {
            repository.rejectQuotation(requirementId)
            showMessage("Quotation declined")
        }
    }

    // --- Seller Product Upload & Custom Category ---
    fun uploadSellerProduct(
        nameEn: String,
        nameHi: String,
        nameAs: String,
        categoryId: String,
        subCategory: String,
        description: String,
        price: Double,
        unit: String,
        qty: Double,
        freshness: String,
        quality: String,
        harvestDate: String,
        location: String,
        distanceKm: Double
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                nameEn = nameEn.trim(),
                nameHi = if (nameHi.isNotBlank()) nameHi.trim() else nameEn.trim(),
                nameAs = if (nameAs.isNotBlank()) nameAs.trim() else nameEn.trim(),
                categoryId = categoryId,
                subCategory = subCategory.trim(),
                description = description.trim(),
                originalSellerPrice = price,
                unit = unit,
                availableQty = qty,
                freshnessStatus = freshness,
                quality = quality,
                harvestDate = harvestDate.ifBlank { "Freshly harvested today" },
                availabilityStatus = "In Stock",
                sellerId = "seller_" + _userPhone.value.takeLast(6),
                sellerName = _userName.value,
                sellerPhone = _userPhone.value,
                sellerMandiLocation = location.ifBlank { "Local Farm Gate" },
                sellerDistanceKm = if (distanceKm > 0.0) distanceKm else 2.5,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = VerificationStatus.PENDING.name,
                verificationNotes = "Uploaded by seller. Awaiting employee review call."
            )
            repository.addProduct(product)
            showMessage("Product submitted for verification! It will go live once verified by staff.")
            navigateTo(AppScreen.SellerDashboard)
        }
    }

    fun createCategory(
        nameEn: String,
        nameHi: String,
        nameAs: String,
        parentGroup: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.createCategoryWithDuplicateCheck(nameEn, nameHi, nameAs, parentGroup)
            onResult(result.first, result.second)
            if (result.first) {
                showMessage(result.second)
            }
        }
    }

    // --- Employee / Admin Product Verification ---
    fun verifyProduct(productId: Long, status: VerificationStatus, notes: String) {
        viewModelScope.launch {
            repository.verifyProduct(
                productId = productId,
                status = status,
                employeeId = _userName.value,
                notes = notes
            )
            showMessage("Product marked as ${status.name}")
        }
    }

    // --- Admin Pricing Management ---
    fun updatePricingConfig(config: PricingConfigEntity) {
        viewModelScope.launch {
            repository.updatePricingConfig(config)
            showMessage("Pricing parameters updated successfully")
        }
    }

    fun updatePricingParameters(
        baseServiceFee: Double,
        perKmDeliveryRate: Double,
        packagingFee: Double,
        demandMultiplier: Double,
        bulkDiscountThreshold: Double,
        bulkDiscountPercent: Double
    ) {
        viewModelScope.launch {
            val updated = PricingConfigEntity(
                id = 1,
                baseServiceFee = baseServiceFee,
                perKmDeliveryRate = perKmDeliveryRate,
                packagingFee = packagingFee,
                demandMultiplier = demandMultiplier,
                bulkDiscountThresholdQty = bulkDiscountThreshold,
                bulkDiscountPercent = bulkDiscountPercent
            )
            repository.updatePricingConfig(updated)
            showMessage("Pricing parameters updated successfully")
        }
    }

    // --- Reviews & Moderation ---
    fun submitProductReview(
        productId: Long,
        productName: String,
        rating: Int,
        freshnessRating: Int,
        deliveryRating: Int,
        comment: String
    ) {
        viewModelScope.launch {
            repository.addReview(
                productId = productId,
                productName = productName,
                buyerName = _userName.value,
                rating = rating,
                freshnessRating = freshnessRating,
                deliveryRating = deliveryRating,
                comment = comment
            )
            showMessage("Thank you for your rating!")
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            showMessage("Produce lot removed")
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            showMessage("Review removed by admin")
        }
    }

    // --- Employee Management ---
    fun generateUniqueEmployeeId(): String {
        val existingIds = allEmployees.value.map { it.employeeId }.toSet()
        var candidate: String
        do {
            val randomSuffix = (1000..9999).random()
            candidate = "EMP-NB-2026-$randomSuffix"
        } while (existingIds.contains(candidate))
        return candidate
    }

    fun addEmployee(
        name: String,
        phone: String,
        email: String,
        department: String,
        assignedMandi: String,
        customId: String? = null,
        onSuccess: (EmployeeEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val employeeId = if (!customId.isNullOrBlank()) customId.trim() else generateUniqueEmployeeId()
            val employee = EmployeeEntity(
                employeeId = employeeId,
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                department = department.trim(),
                assignedMandi = assignedMandi.trim(),
                isActive = true
            )
            repository.addEmployee(employee)
            showMessage("Employee ${employee.name} added with ID: $employeeId")
            onSuccess(employee)
        }
    }

    fun toggleEmployeeStatus(employee: EmployeeEntity) {
        viewModelScope.launch {
            val newStatus = !employee.isActive
            repository.updateEmployeeStatus(employee.employeeId, newStatus)
            showMessage("${employee.name} is now ${if (newStatus) "Active" else "Inactive"}")
        }
    }

    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            repository.deleteEmployee(employeeId)
            showMessage("Employee $employeeId removed")
        }
    }

    // --- Firebase Cloud Sync Actions ---
    fun refreshFirebaseStatus() {
        _firebaseStatus.value = syncManager.getStatus()
    }

    fun syncDataWithFirebase(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val products = approvedProducts.value
            syncManager.syncProductsToCloud(products) { success, msg ->
                if (success) {
                    syncManager.syncOrdersToCloud(allOrders.value) { orderSuccess, orderMsg ->
                        val combined = "$msg • $orderMsg"
                        showMessage(combined)
                        refreshFirebaseStatus()
                        onComplete(orderSuccess, combined)
                    }
                } else {
                    showMessage(msg)
                    refreshFirebaseStatus()
                    onComplete(false, msg)
                }
            }
        }
    }

    fun configureCustomFirebase(projectId: String, apiKey: String, appId: String, onResult: (Boolean) -> Unit) {
        val success = syncManager.configureFirebaseProject(projectId, apiKey, appId)
        refreshFirebaseStatus()
        if (success) {
            showMessage("Connected to Firebase: $projectId")
        } else {
            showMessage("Could not initialize Firebase with provided credentials")
        }
        onResult(success)
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    private fun showMessage(msg: String) {
        _userMessage.value = msg
    }
}
