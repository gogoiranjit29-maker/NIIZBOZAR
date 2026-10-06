package com.example.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.model.UserRole

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val screen: AppScreen,
    val testTag: String,
    val badgeCount: Int = 0
)

@Composable
fun MarketplaceBottomNavBar(
    currentRole: UserRole,
    currentScreen: AppScreen,
    cartCount: Int,
    pendingVerificationsCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = when (currentRole) {
        UserRole.BUYER -> listOf(
            NavItem("Market", Icons.Default.Store, AppScreen.BuyerHome, "nav_market"),
            NavItem("Sell", Icons.Default.Sell, AppScreen.SellerDashboard, "nav_buyer_sell"),
            NavItem("Basket", Icons.Default.ShoppingCart, AppScreen.CartAndCheckout, "nav_basket", cartCount),
            NavItem("Tracking", Icons.Default.LocalShipping, AppScreen.OrderTracking, "nav_tracking"),
            NavItem("Settings", Icons.Default.Settings, AppScreen.ProfileAndSettings, "nav_settings")
        )
        UserRole.SELLER -> listOf(
            NavItem("Dashboard", Icons.Default.Dashboard, AppScreen.SellerDashboard, "nav_seller_dash"),
            NavItem("Sell", Icons.Default.Sell, AppScreen.SellerProductUpload, "nav_seller_sell"),
            NavItem("Requirements", Icons.Default.Description, AppScreen.BuyerRequirements, "nav_requirements"),
            NavItem("Settings", Icons.Default.Settings, AppScreen.ProfileAndSettings, "nav_settings")
        )
        UserRole.EMPLOYEE -> listOf(
            NavItem("Verifications", Icons.Default.CheckCircle, AppScreen.EmployeeDashboard, "nav_emp_verify", pendingVerificationsCount),
            NavItem("Buyer Reqs", Icons.Default.Description, AppScreen.BuyerRequirements, "nav_emp_reqs"),
            NavItem("Market View", Icons.Default.Store, AppScreen.BuyerHome, "nav_market_view"),
            NavItem("Settings", Icons.Default.Settings, AppScreen.ProfileAndSettings, "nav_settings")
        )
        UserRole.ADMIN -> listOf(
            NavItem("Admin Hub", Icons.Default.Dashboard, AppScreen.AdminDashboard, "nav_admin_hub"),
            NavItem("Verifications", Icons.Default.CheckCircle, AppScreen.EmployeeDashboard, "nav_emp_verify", pendingVerificationsCount),
            NavItem("Market", Icons.Default.Store, AppScreen.BuyerHome, "nav_market"),
            NavItem("Settings", Icons.Default.Settings, AppScreen.ProfileAndSettings, "nav_settings")
        )
        UserRole.DELIVERY_PARTNER -> listOf(
            NavItem("Deliveries", Icons.Default.DeliveryDining, AppScreen.DeliveryDashboard, "nav_delivery_dash"),
            NavItem("Tracking", Icons.Default.LocalShipping, AppScreen.OrderTracking, "nav_tracking"),
            NavItem("Market", Icons.Default.Store, AppScreen.BuyerHome, "nav_market"),
            NavItem("Settings", Icons.Default.Settings, AppScreen.ProfileAndSettings, "nav_settings")
        )
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.navigationBarsPadding()
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    if (item.badgeCount > 0) {
                        BadgedBox(badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(item.badgeCount.toString(), fontSize = 9.sp)
                            }
                        }) {
                            Icon(item.icon, contentDescription = item.title)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
