package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameEn: String,
    val nameHi: String,
    val nameAs: String,
    val categoryId: String,
    val subCategory: String,
    val description: String,
    val originalSellerPrice: Double,
    val unit: String,
    val availableQty: Double,
    val freshnessStatus: String,
    val quality: String,
    val harvestDate: String,
    val availabilityStatus: String,
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String,
    val sellerMandiLocation: String,
    val sellerDistanceKm: Double,
    val imageUrl: String,
    val verificationStatus: String, // PENDING, APPROVED, REJECTED, INFO_REQUIRED
    val verificationNotes: String = "",
    val verifiedByEmployeeId: String = "",
    val demandFactor: Double = 1.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameAs: String,
    val parentGroup: String, // "Vegetables" or "Daily Cooking Products"
    val isCustomBySeller: Boolean = false,
    val isApproved: Boolean = true,
    val iconIdentifier: String = "eco"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Double,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val buyerAddress: String,
    val deliverySlot: String,
    val preferredDeliveryDate: String,
    val totalSellerAmount: Double,
    val deliveryCharge: Double,
    val serviceCharge: Double,
    val packagingCharge: Double,
    val finalTotalAmount: Double,
    val paymentMethod: String,
    val paymentTransactionId: String,
    val paymentStatus: String, // "PAID"
    val orderStatus: String, // PLACED, PAID, PROCESSING, COLLECTED, ASSIGNED, OUT_FOR_DELIVERY, DELIVERED
    val deliveryPartnerId: String = "",
    val deliveryPartnerName: String = "",
    val deliveryOtp: String = "4829",
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryTime: String = "Today, within selected slot",
    val itemsSummary: String
)

@Entity(tableName = "requirement_orders")
data class RequirementOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requirementCode: String,
    val buyerName: String,
    val buyerPhone: String,
    val deliveryLocation: String,
    val preferredDate: String,
    val preferredTimeSlot: String,
    val isBulk: Boolean,
    val itemsText: String,
    val photoUri: String? = null,
    val status: String, // "SUBMITTED", "QUOTATION_PREPARED", "ACCEPTED_PAID", "REJECTED", "FULFILLED"
    val quotationItemsJson: String = "",
    val quotationTotalSellerCost: Double = 0.0,
    val quotationDeliveryFee: Double = 0.0,
    val quotationServiceFee: Double = 0.0,
    val quotationGrandTotal: Double = 0.0,
    val employeeNotes: String = "",
    val paymentMethod: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pricing_config")
data class PricingConfigEntity(
    @PrimaryKey val id: Int = 1,
    val baseServiceFee: Double = 5.0,
    val perKmDeliveryRate: Double = 2.5,
    val packagingFee: Double = 3.0,
    val demandMultiplier: Double = 1.0,
    val bulkDiscountThresholdQty: Double = 20.0,
    val bulkDiscountPercent: Double = 10.0
)

@Entity(tableName = "delivery_partners")
data class DeliveryPartnerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val vehicleType: String,
    val activeAssignedOrderId: Long? = null,
    val currentLat: Double = 26.1445,
    val currentLng: Double = 91.7362,
    val isAvailable: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val buyerName: String,
    val rating: Int,
    val freshnessRating: Int,
    val deliveryRating: Int,
    val comment: String,
    val isModerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetRole: String, // "ALL", "BUYER", "SELLER", "EMPLOYEE", "ADMIN", "DELIVERY_PARTNER"
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String = "",
    val role: String, // BUYER, SELLER, EMPLOYEE, ADMIN, DELIVERY_PARTNER
    val isCurrent: Boolean = true,
    val token: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey val employeeId: String, // Automatically generated e.g. "EMP-NB-2026-4821"
    val name: String,
    val phone: String,
    val email: String = "",
    val department: String = "Quality & Verification",
    val assignedMandi: String = "Pamohi Mandi, Guwahati",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

