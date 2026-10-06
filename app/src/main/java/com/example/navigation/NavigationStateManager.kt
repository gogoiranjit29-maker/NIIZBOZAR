package com.example.navigation

import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sealed class defining all navigation routes across the application.
 */
sealed class AppRoute(val routeName: String, val title: String) {
    // Auth & Onboarding
    object Login : AppRoute("login", "Login / Register")

    // Role-Based Dashboards
    object BuyerDashboard : AppRoute("buyer_dashboard", "Buyer Store")
    object SellerDashboard : AppRoute("seller_dashboard", "Farmer Dashboard")
    object EmployeeDashboard : AppRoute("employee_dashboard", "Verification Staff")
    object AdminDashboard : AppRoute("admin_dashboard", "Operations Admin")
    object DeliveryDashboard : AppRoute("delivery_dashboard", "Delivery Partner")

    // Secondary & Sub-Screens
    object ProductDetail : AppRoute("product_detail", "Product Details")
    object CartAndCheckout : AppRoute("cart_checkout", "Cart & Checkout")
    object DirectBuyCheckout : AppRoute("direct_buy_checkout", "Instant Buy")
    object OrderTracking : AppRoute("order_tracking", "Track Orders")
    object BuyerRequirements : AppRoute("buyer_requirements", "Custom Requirements")
    object SellerProductUpload : AppRoute("seller_upload", "Sell Produce")
    object ProfileAndSettings : AppRoute("profile_settings", "Settings & Profile")
}

/**
 * NavigationStateManager
 *
 * Manages the app navigation state and backstack, providing helper methods to switch
 * between the Login screen and the role-based dashboards upon authentication.
 */
class NavigationStateManager(
    initialRoute: AppRoute = AppRoute.BuyerDashboard
) {
    private val _currentRoute = MutableStateFlow<AppRoute>(initialRoute)
    val currentRoute: StateFlow<AppRoute> = _currentRoute.asStateFlow()

    private val backStack = mutableListOf<AppRoute>(initialRoute)

    val canGoBack: Boolean
        get() = backStack.size > 1

    /**
     * Navigate to a destination route.
     * @param route Target AppRoute
     * @param clearBackStack If true, clears history (e.g., after login or logout)
     */
    fun navigateTo(route: AppRoute, clearBackStack: Boolean = false) {
        if (clearBackStack) {
            backStack.clear()
        }
        backStack.add(route)
        _currentRoute.value = route
    }

    /**
     * Handle switching to the appropriate role-based dashboard after authentication.
     */
    fun navigateToDashboardForRole(role: UserRole) {
        val targetRoute = when (role) {
            UserRole.BUYER -> AppRoute.BuyerDashboard
            UserRole.SELLER -> AppRoute.SellerDashboard
            UserRole.EMPLOYEE -> AppRoute.EmployeeDashboard
            UserRole.ADMIN -> AppRoute.AdminDashboard
            UserRole.DELIVERY_PARTNER -> AppRoute.DeliveryDashboard
        }
        navigateTo(targetRoute, clearBackStack = true)
    }

    /**
     * Reset to Login screen and clear the backstack.
     */
    fun navigateToLogin() {
        navigateTo(AppRoute.Login, clearBackStack = true)
    }

    /**
     * Handle backstack navigation.
     * @return true if popped, false if backstack cannot be popped further
     */
    fun navigateBack(defaultFallback: AppRoute? = null): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
            val previous = backStack.last()
            _currentRoute.value = previous
            return true
        } else if (defaultFallback != null && _currentRoute.value != defaultFallback) {
            navigateTo(defaultFallback, clearBackStack = true)
            return true
        }
        return false
    }

    /**
     * Returns true if the current route is one of the main role dashboards.
     */
    fun isAtDashboard(): Boolean {
        return when (_currentRoute.value) {
            AppRoute.BuyerDashboard,
            AppRoute.SellerDashboard,
            AppRoute.EmployeeDashboard,
            AppRoute.AdminDashboard,
            AppRoute.DeliveryDashboard -> true
            else -> false
        }
    }

    /**
     * Returns true if the current route is the Login screen.
     */
    fun isAtLogin(): Boolean {
        return _currentRoute.value == AppRoute.Login
    }
}
