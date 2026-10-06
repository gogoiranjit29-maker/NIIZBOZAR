package com.example.model

sealed class AppScreen(val routeName: String) {
    object RoleAndAuth : AppScreen("auth")
    object BuyerHome : AppScreen("buyer_home")
    object ProductDetail : AppScreen("product_detail")
    object CartAndCheckout : AppScreen("cart_checkout")
    object DirectBuyCheckout : AppScreen("direct_buy_checkout")
    object OrderTracking : AppScreen("order_tracking")
    object BuyerRequirements : AppScreen("buyer_requirements")
    object SellerDashboard : AppScreen("seller_dashboard")
    object SellerProductUpload : AppScreen("seller_upload")
    object EmployeeDashboard : AppScreen("employee_dashboard")
    object AdminDashboard : AppScreen("admin_dashboard")
    object DeliveryDashboard : AppScreen("delivery_dashboard")
    object ProfileAndSettings : AppScreen("profile_settings")
}
