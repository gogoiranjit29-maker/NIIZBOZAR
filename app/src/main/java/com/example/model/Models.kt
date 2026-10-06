package com.example.model

enum class UserRole(val labelKey: String, val badgeColorHex: Long) {
    BUYER("role_buyer", 0xFF16A34A),
    SELLER("role_seller", 0xFFD97706),
    EMPLOYEE("role_employee", 0xFF0284C7),
    ADMIN("role_admin", 0xFF7C3AED),
    DELIVERY_PARTNER("role_delivery", 0xFFEA580C)
}

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    EN("en", "English", "English"),
    HI("hi", "Hindi", "हिन्दी"),
    AS("as", "Assamese", "অসমীয়া")
}

enum class FreshnessStatus(val key: String) {
    FRESHLY_HARVESTED("freshness_freshly_harvested"),
    FRESH("freshness_fresh"),
    GOOD_QUALITY("freshness_good_quality"),
    AVERAGE_QUALITY("freshness_average_quality"),
    PREMIUM_QUALITY("freshness_premium_quality")
}

enum class ProductQuality(val key: String) {
    GRADE_A("quality_grade_a"),
    GRADE_B("quality_grade_b"),
    ORGANIC_DESI("quality_organic_desi")
}

enum class VerificationStatus(val key: String) {
    PENDING("verification_pending"),
    APPROVED("verification_approved"),
    REJECTED("verification_rejected"),
    INFO_REQUIRED("verification_info_required")
}

enum class OrderStatus(val key: String) {
    PLACED("status_order_placed"),
    PAID("status_paid"),
    PROCESSING("status_processing"),
    COLLECTED("status_collected"),
    ASSIGNED("status_assigned"),
    OUT_FOR_DELIVERY("status_out_for_delivery"),
    DELIVERED("status_delivered"),
    CANCELLED("status_cancelled")
}

enum class DeliverySlotOption(val timeWindow: String, val periodKey: String) {
    MORNING_EARLY("6:00 AM – 9:00 AM", "slot_morning_early"),
    MORNING_LATE("9:00 AM – 12:00 PM", "slot_morning_late"),
    AFTERNOON("12:00 PM – 3:00 PM", "slot_afternoon"),
    EVENING_EARLY("3:00 PM – 6:00 PM", "slot_evening_early"),
    EVENING_LATE("6:00 PM – 9:00 PM", "slot_evening_late")
}

enum class PaymentMethod(val displayName: String, val iconRes: String) {
    UPI_GPAY("Google Pay (UPI)", "gpay"),
    UPI_PHONEPE("PhonePe (UPI)", "phonepe"),
    UPI_PAYTM("Paytm UPI", "paytm"),
    DEBIT_CREDIT_CARD("Debit / Credit Card", "card"),
    NET_BANKING("Net Banking", "bank")
}

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class NetworkMode(val labelKey: String) {
    ONLINE("network_online"),
    LOW_NETWORK("network_low"),
    OFFLINE("network_offline")
}

enum class UnitType(val symbol: String, val key: String) {
    KG("kg", "unit_kg"),
    GRAM("g", "unit_gram"),
    PIECE("pc", "unit_piece"),
    BUNDLE("bundle", "unit_bundle"),
    PACKET("pkt", "unit_packet"),
    SACK("sack", "unit_sack"),
    DOZEN("doz", "unit_dozen"),
    CRATE("crate", "unit_crate")
}

data class SmartPriceBreakdown(
    val sellerBasePrice: Double,
    val distanceKm: Double,
    val distanceAdjustment: Double,
    val serviceCharge: Double,
    val packagingCharge: Double,
    val demandAdjustment: Double,
    val bulkDiscount: Double,
    val finalPricePerUnit: Double
) {
    fun formattedFormula(): String {
        return "₹${"%.1f".format(sellerBasePrice)} (Seller) + ₹${"%.1f".format(distanceAdjustment)} (Delivery ${"%.1f".format(distanceKm)}km) + ₹${"%.1f".format(serviceCharge)} (Service) + ₹${"%.1f".format(packagingCharge)} (Pack) + ₹${"%.1f".format(demandAdjustment)} (Demand) - ₹${"%.1f".format(bulkDiscount)} (Disc) = ₹${"%.1f".format(finalPricePerUnit)}"
    }
}
