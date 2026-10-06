package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {

    // Products
    @Query("SELECT * FROM products WHERE verificationStatus = 'APPROVED' ORDER BY sellerDistanceKm ASC")
    fun getAllApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE verificationStatus = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingVerificationProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductByIdDirect(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    // Categories
    @Query("SELECT * FROM categories ORDER BY parentGroup ASC, nameEn ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<CategoryEntity>)

    // Cart
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateCartItemQuantity(productId: Long, quantity: Double)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE buyerPhone = :phone ORDER BY createdAt DESC")
    fun getOrdersForBuyer(phone: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE deliveryPartnerId = :partnerId ORDER BY createdAt DESC")
    fun getOrdersForDeliveryPartner(partnerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Long): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("UPDATE orders SET deliveryPartnerId = :partnerId, deliveryPartnerName = :partnerName, orderStatus = 'ASSIGNED' WHERE id = :orderId")
    suspend fun assignDeliveryPartner(orderId: Long, partnerId: String, partnerName: String)

    // Requirements
    @Query("SELECT * FROM requirement_orders ORDER BY createdAt DESC")
    fun getAllRequirementOrders(): Flow<List<RequirementOrderEntity>>

    @Query("SELECT * FROM requirement_orders WHERE buyerPhone = :buyerPhone ORDER BY createdAt DESC")
    fun getRequirementsForBuyer(buyerPhone: String): Flow<List<RequirementOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(req: RequirementOrderEntity): Long

    @Update
    suspend fun updateRequirement(req: RequirementOrderEntity)

    // Pricing Config
    @Query("SELECT * FROM pricing_config WHERE id = 1 LIMIT 1")
    fun getPricingConfig(): Flow<PricingConfigEntity?>

    @Query("SELECT * FROM pricing_config WHERE id = 1 LIMIT 1")
    suspend fun getPricingConfigDirect(): PricingConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePricingConfig(config: PricingConfigEntity)

    // Delivery Partners
    @Query("SELECT * FROM delivery_partners")
    fun getAllDeliveryPartners(): Flow<List<DeliveryPartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryPartner(partner: DeliveryPartnerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeliveryPartners(partners: List<DeliveryPartnerEntity>)

    @Query("UPDATE delivery_partners SET isAvailable = :isAvailable, activeAssignedOrderId = :activeOrderId WHERE id = :id")
    suspend fun updateDeliveryPartnerStatus(id: String, isAvailable: Boolean, activeOrderId: Long?)

    // Reviews
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: Long)

    // Notifications
    @Query("SELECT * FROM notifications WHERE targetRole = :role OR targetRole = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForRole(role: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    // User Session
    @Query("SELECT * FROM users WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentUserDirect(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET isCurrent = 0")
    suspend fun clearCurrentUserFlag()

    // Employees Management
    @Query("SELECT * FROM employees ORDER BY createdAt DESC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE employeeId = :id LIMIT 1")
    suspend fun getEmployeeById(id: String): EmployeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEmployees(employees: List<EmployeeEntity>)

    @Query("UPDATE employees SET isActive = :isActive WHERE employeeId = :id")
    suspend fun updateEmployeeStatus(id: String, isActive: Boolean)

    @Query("DELETE FROM employees WHERE employeeId = :id")
    suspend fun deleteEmployee(id: String)
}
