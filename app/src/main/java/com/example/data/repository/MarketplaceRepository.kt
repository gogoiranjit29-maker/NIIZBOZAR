package com.example.data.repository

import com.example.data.local.dao.MarketplaceDao
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
import com.example.model.OrderStatus
import com.example.model.SmartPriceBreakdown
import com.example.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MarketplaceRepository(private val dao: MarketplaceDao) {

    // --- Products ---
    val approvedProducts: Flow<List<ProductEntity>> = dao.getAllApprovedProducts()
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val pendingVerificationProducts: Flow<List<ProductEntity>> = dao.getPendingVerificationProducts()

    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> = dao.getProductsBySeller(sellerId)

    fun getProductById(id: Long): Flow<ProductEntity?> = dao.getProductById(id)

    suspend fun getProductByIdDirect(id: Long): ProductEntity? = dao.getProductByIdDirect(id)

    suspend fun addProduct(product: ProductEntity): Long {
        val id = dao.insertProduct(product)
        // Send notification to employees
        dao.insertNotification(
            NotificationEntity(
                targetRole = "EMPLOYEE",
                title = "New Product Submitted: ${product.nameEn}",
                message = "Seller ${product.sellerName} uploaded ${product.nameEn} (${product.availableQty} ${product.unit}). Phone: ${product.sellerPhone}. Requires call verification."
            )
        )
        return id
    }

    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)

    suspend fun deleteProduct(id: Long) = dao.deleteProductById(id)

    suspend fun verifyProduct(
        productId: Long,
        status: VerificationStatus,
        employeeId: String,
        notes: String
    ) {
        val existing = dao.getProductByIdDirect(productId) ?: return
        val updated = existing.copy(
            verificationStatus = status.name,
            verifiedByEmployeeId = employeeId,
            verificationNotes = notes
        )
        dao.updateProduct(updated)

        // Notify Seller
        val statusMessage = when (status) {
            VerificationStatus.APPROVED -> "Your product '${existing.nameEn}' has been verified and is now live for buyers!"
            VerificationStatus.REJECTED -> "Your product '${existing.nameEn}' was not approved. Note: $notes"
            VerificationStatus.INFO_REQUIRED -> "Additional clarification required for '${existing.nameEn}'. Note: $notes"
            VerificationStatus.PENDING -> "Product under review."
        }
        dao.insertNotification(
            NotificationEntity(
                targetRole = "SELLER",
                title = "Product Verification Update: ${existing.nameEn}",
                message = statusMessage
            )
        )
    }

    // --- Smart Pricing Calculation ---
    fun calculateSmartPrice(
        sellerBasePrice: Double,
        distanceKm: Double,
        quantity: Double,
        demandFactor: Double,
        config: PricingConfigEntity
    ): SmartPriceBreakdown {
        val distAdj = distanceKm * config.perKmDeliveryRate
        val servChg = config.baseServiceFee
        val packChg = config.packagingFee
        val demandAdj = ((demandFactor - 1.0) * sellerBasePrice * 0.4).coerceAtLeast(-5.0).coerceAtMost(20.0)
        val bulkDiscount = if (quantity >= config.bulkDiscountThresholdQty) {
            sellerBasePrice * (config.bulkDiscountPercent / 100.0)
        } else {
            0.0
        }
        val finalUnitPrice = (sellerBasePrice + distAdj + servChg + packChg + demandAdj - bulkDiscount)
            .coerceAtLeast(sellerBasePrice)

        return SmartPriceBreakdown(
            sellerBasePrice = sellerBasePrice,
            distanceKm = distanceKm,
            distanceAdjustment = distAdj,
            serviceCharge = servChg,
            packagingCharge = packChg,
            demandAdjustment = demandAdj,
            bulkDiscount = bulkDiscount,
            finalPricePerUnit = finalUnitPrice
        )
    }

    // --- Categories & Duplicate Detection ---
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()

    suspend fun createCategoryWithDuplicateCheck(
        nameEn: String,
        nameHi: String,
        nameAs: String,
        parentGroup: String
    ): Pair<Boolean, String> {
        val existingList = dao.getAllCategories().firstOrNull().orEmpty()
        val normalizedInput = normalizeCategoryName(nameEn)

        val duplicate = existingList.firstOrNull {
            normalizeCategoryName(it.nameEn) == normalizedInput ||
                    it.nameEn.equals(nameEn.trim(), ignoreCase = true)
        }

        if (duplicate != null) {
            return Pair(false, "Category similar to '${duplicate.nameEn}' already exists. Please choose the existing category.")
        }

        val generatedId = "custom_" + nameEn.trim().lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]"), "_")
        val category = CategoryEntity(
            id = generatedId,
            nameEn = nameEn.trim(),
            nameHi = if (nameHi.isNotBlank()) nameHi.trim() else nameEn.trim(),
            nameAs = if (nameAs.isNotBlank()) nameAs.trim() else nameEn.trim(),
            parentGroup = parentGroup,
            isCustomBySeller = true,
            isApproved = true,
            iconIdentifier = "eco"
        )
        dao.insertCategory(category)
        return Pair(true, "Category '${nameEn}' created successfully.")
    }

    private fun normalizeCategoryName(name: String): String {
        var clean = name.trim().lowercase(Locale.ROOT)
        // strip trailing 'es' or 's' for plural detection like Potatoes -> Potato
        if (clean.endsWith("es") && clean.length > 4) {
            clean = clean.substring(0, clean.length - 2)
        } else if (clean.endsWith("s") && clean.length > 3) {
            clean = clean.substring(0, clean.length - 1)
        }
        return clean
    }

    // --- Cart ---
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()

    suspend fun addToCart(productId: Long, quantity: Double) {
        dao.insertCartItem(CartItemEntity(productId = productId, quantity = quantity))
    }

    suspend fun updateCartQuantity(productId: Long, quantity: Double) {
        if (quantity <= 0.0) {
            dao.deleteCartItem(productId)
        } else {
            dao.updateCartItemQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: Long) = dao.deleteCartItem(productId)

    suspend fun clearCart() = dao.clearCart()

    // --- Orders (Prepaid Online Only - No COD) ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    fun getOrdersForBuyer(phone: String): Flow<List<OrderEntity>> = dao.getOrdersForBuyer(phone)

    fun getOrdersForDeliveryPartner(partnerId: String): Flow<List<OrderEntity>> = dao.getOrdersForDeliveryPartner(partnerId)

    fun getOrderById(id: Long): Flow<OrderEntity?> = dao.getOrderById(id)

    suspend fun placePrepaidOrder(
        buyerId: String,
        buyerName: String,
        buyerPhone: String,
        buyerAddress: String,
        deliverySlot: String,
        preferredDeliveryDate: String,
        totalSellerAmount: Double,
        deliveryCharge: Double,
        serviceCharge: Double,
        packagingCharge: Double,
        finalTotalAmount: Double,
        paymentMethod: String,
        paymentTransactionId: String,
        itemsSummary: String
    ): Long {
        val dateFormat = SimpleDateFormat("yyMMdd", Locale.ROOT)
        val orderNo = "SBM-${dateFormat.format(Date())}-${Random.nextInt(1000, 9999)}"
        val otp = String.format(Locale.ROOT, "%04d", Random.nextInt(1000, 9999))

        val order = OrderEntity(
            orderNumber = orderNo,
            buyerId = buyerId,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            buyerAddress = buyerAddress,
            deliverySlot = deliverySlot,
            preferredDeliveryDate = preferredDeliveryDate,
            totalSellerAmount = totalSellerAmount,
            deliveryCharge = deliveryCharge,
            serviceCharge = serviceCharge,
            packagingCharge = packagingCharge,
            finalTotalAmount = finalTotalAmount,
            paymentMethod = paymentMethod,
            paymentTransactionId = paymentTransactionId,
            paymentStatus = "PAID",
            orderStatus = OrderStatus.PLACED.name,
            deliveryPartnerId = "",
            deliveryPartnerName = "",
            deliveryOtp = otp,
            itemsSummary = itemsSummary
        )

        val orderId = dao.insertOrder(order)
        clearCart()

        // Notify Buyer and Admin
        dao.insertNotification(
            NotificationEntity(
                targetRole = "BUYER",
                title = "Order Confirmed ($orderNo)",
                message = "Payment of ₹${"%.2f".format(finalTotalAmount)} verified via $paymentMethod. Scheduled for $deliverySlot."
            )
        )
        dao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "New Marketplace Order: $orderNo",
                message = "₹${"%.2f".format(finalTotalAmount)} paid by $buyerName. Ready for Mandi collection and delivery assignment."
            )
        )
        return orderId
    }

    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus) {
        dao.updateOrderStatus(orderId, status.name)
        val order = dao.getAllOrders().firstOrNull()?.firstOrNull { it.id == orderId }
        if (order != null) {
            dao.insertNotification(
                NotificationEntity(
                    targetRole = "BUYER",
                    title = "Order ${order.orderNumber} Update",
                    message = "Status is now: ${status.name.replace("_", " ")}"
                )
            )
        }
    }

    suspend fun assignDeliveryPartner(orderId: Long, partnerId: String, partnerName: String) {
        dao.assignDeliveryPartner(orderId, partnerId, partnerName)
        dao.updateDeliveryPartnerStatus(partnerId, isAvailable = false, activeOrderId = orderId)
        dao.insertNotification(
            NotificationEntity(
                targetRole = "DELIVERY_PARTNER",
                title = "New Delivery Order Assigned",
                message = "Order #$orderId has been assigned to you. Tap to review pickup and delivery location."
            )
        )
    }

    // --- Requirement Order System ---
    val allRequirementOrders: Flow<List<RequirementOrderEntity>> = dao.getAllRequirementOrders()

    fun getRequirementsForBuyer(buyerPhone: String): Flow<List<RequirementOrderEntity>> =
        dao.getRequirementsForBuyer(buyerPhone)

    suspend fun postRequirement(
        buyerName: String,
        buyerPhone: String,
        deliveryLocation: String,
        preferredDate: String,
        preferredTimeSlot: String,
        isBulk: Boolean,
        itemsText: String,
        photoUri: String?
    ): Long {
        val code = "REQ-" + Random.nextInt(10000, 99999)
        val req = RequirementOrderEntity(
            requirementCode = code,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            deliveryLocation = deliveryLocation,
            preferredDate = preferredDate,
            preferredTimeSlot = preferredTimeSlot,
            isBulk = isBulk,
            itemsText = itemsText,
            photoUri = photoUri,
            status = "SUBMITTED"
        )
        val id = dao.insertRequirement(req)

        dao.insertNotification(
            NotificationEntity(
                targetRole = "EMPLOYEE",
                title = "New Requirement / Bulk Inquiry ($code)",
                message = "Buyer $buyerName requested items (${if (isBulk) "BULK ORDER" else "Standard"}). Prepare supplier quotation."
            )
        )
        return id
    }

    suspend fun prepareQuotation(
        requirementId: Long,
        sellerCost: Double,
        deliveryFee: Double,
        serviceFee: Double,
        grandTotal: Double,
        notes: String
    ) {
        val existing = dao.getAllRequirementOrders().firstOrNull()?.firstOrNull { it.id == requirementId } ?: return
        val updated = existing.copy(
            status = "QUOTATION_PREPARED",
            quotationTotalSellerCost = sellerCost,
            quotationDeliveryFee = deliveryFee,
            quotationServiceFee = serviceFee,
            quotationGrandTotal = grandTotal,
            employeeNotes = notes
        )
        dao.updateRequirement(updated)

        dao.insertNotification(
            NotificationEntity(
                targetRole = "BUYER",
                title = "Quotation Ready for ${existing.requirementCode}",
                message = "Price quotation of ₹${"%.2f".format(grandTotal)} prepared. Review items and pay online to confirm."
            )
        )
    }

    suspend fun acceptQuotationAndPay(requirementId: Long, paymentMethod: String) {
        val existing = dao.getAllRequirementOrders().firstOrNull()?.firstOrNull { it.id == requirementId } ?: return
        val updated = existing.copy(
            status = "ACCEPTED_PAID",
            paymentMethod = paymentMethod
        )
        dao.updateRequirement(updated)

        dao.insertNotification(
            NotificationEntity(
                targetRole = "EMPLOYEE",
                title = "Quotation Accepted: ${existing.requirementCode}",
                message = "Buyer accepted and paid online (₹${"%.2f".format(existing.quotationGrandTotal)}). Dispatch supplies."
            )
        )
    }

    suspend fun rejectQuotation(requirementId: Long) {
        val existing = dao.getAllRequirementOrders().firstOrNull()?.firstOrNull { it.id == requirementId } ?: return
        dao.updateRequirement(existing.copy(status = "REJECTED"))
    }

    // --- Pricing Config ---
    val pricingConfig: Flow<PricingConfigEntity?> = dao.getPricingConfig()

    suspend fun getPricingConfigDirect(): PricingConfigEntity {
        return dao.getPricingConfigDirect() ?: PricingConfigEntity()
    }

    suspend fun updatePricingConfig(config: PricingConfigEntity) {
        dao.insertOrUpdatePricingConfig(config)
        dao.insertNotification(
            NotificationEntity(
                targetRole = "ALL",
                title = "Pricing Matrix Updated",
                message = "Admin adjusted delivery rate (₹${config.perKmDeliveryRate}/km) and service fees (₹${config.baseServiceFee})."
            )
        )
    }

    // --- Delivery Partners ---
    val deliveryPartners: Flow<List<DeliveryPartnerEntity>> = dao.getAllDeliveryPartners()

    suspend fun updateDeliveryPartnerStatus(id: String, isAvailable: Boolean, activeOrderId: Long?) {
        dao.updateDeliveryPartnerStatus(id, isAvailable, activeOrderId)
    }

    // --- Reviews & Moderation ---
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> = dao.getReviewsForProduct(productId)
    val allReviews: Flow<List<ReviewEntity>> = dao.getAllReviews()

    suspend fun addReview(
        productId: Long,
        productName: String,
        buyerName: String,
        rating: Int,
        freshnessRating: Int,
        deliveryRating: Int,
        comment: String
    ) {
        dao.insertReview(
            ReviewEntity(
                productId = productId,
                productName = productName,
                buyerName = buyerName,
                rating = rating,
                freshnessRating = freshnessRating,
                deliveryRating = deliveryRating,
                comment = comment,
                isModerated = false
            )
        )
    }

    suspend fun deleteReview(id: Long) = dao.deleteReview(id)

    // --- Notifications ---
    fun getNotificationsForRole(role: String): Flow<List<NotificationEntity>> = dao.getNotificationsForRole(role)

    suspend fun sendNotification(targetRole: String, title: String, message: String) {
        dao.insertNotification(
            NotificationEntity(
                targetRole = targetRole,
                title = title,
                message = message
            )
        )
    }

    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)

    // --- Employees ---
    val allEmployees: Flow<List<EmployeeEntity>> = dao.getAllEmployees()

    suspend fun addEmployee(employee: EmployeeEntity) {
        dao.insertEmployee(employee)
        // Also register user credentials so this employee can log in immediately
        dao.insertUser(
            com.example.data.local.entities.UserEntity(
                id = employee.employeeId,
                name = employee.name,
                phone = employee.phone,
                email = employee.email,
                role = "EMPLOYEE",
                isCurrent = false,
                token = "token_${employee.employeeId}"
            )
        )
        // Admin audit log notification
        dao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "New Employee Registered: ${employee.name}",
                message = "Unique Employee ID: ${employee.employeeId} created for ${employee.department} at ${employee.assignedMandi}."
            )
        )
        // Staff welcome notification
        dao.insertNotification(
            NotificationEntity(
                targetRole = "EMPLOYEE",
                title = "Employee Assigned: ${employee.employeeId}",
                message = "Welcome ${employee.name}! You are registered in ${employee.department} at ${employee.assignedMandi}."
            )
        )
    }

    suspend fun updateEmployeeStatus(employeeId: String, isActive: Boolean) {
        dao.updateEmployeeStatus(employeeId, isActive)
    }

    suspend fun deleteEmployee(employeeId: String) {
        dao.deleteEmployee(employeeId)
    }
}
