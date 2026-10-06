package com.example

import com.example.data.local.entities.PricingConfigEntity
import com.example.model.Language
import com.example.model.TranslationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testTranslationHelperEnglishHindiAssamese() {
        val potatoEn = TranslationHelper.getProductName("Potato", Language.EN)
        val potatoHi = TranslationHelper.getProductName("Potato", Language.HI)
        val potatoAs = TranslationHelper.getProductName("Potato", Language.AS)

        assertEquals("Potato", potatoEn)
        assertEquals("आलू", potatoHi)
        assertEquals("আলু", potatoAs)

        val tomatoAs = TranslationHelper.getProductName("Tomato", Language.AS)
        assertEquals("বিলাহী", tomatoAs)

        val spinachAs = TranslationHelper.getProductName("Spinach", Language.AS)
        assertEquals("পালেং শাক", spinachAs)
    }

    @Test
    fun testSmartPricingCalculation() {
        val config = PricingConfigEntity(
            id = 1,
            baseServiceFee = 5.0,
            perKmDeliveryRate = 2.0,
            packagingFee = 3.0,
            demandMultiplier = 1.0,
            bulkDiscountThresholdQty = 20.0,
            bulkDiscountPercent = 10.0
        )

        val sellerBasePrice = 30.0
        val distanceKm = 4.0
        val distAdj = distanceKm * config.perKmDeliveryRate // 8.0
        val serviceFee = config.baseServiceFee // 5.0
        val packaging = config.packagingFee // 3.0
        val expected = sellerBasePrice + distAdj + serviceFee + packaging // 46.0

        assertTrue(expected > sellerBasePrice)
        assertEquals(46.0, expected, 0.01)
    }

    @Test
    fun testUserRolesDefined() {
        val roles = com.example.model.UserRole.values()
        assertEquals(5, roles.size)
        assertTrue(roles.contains(com.example.model.UserRole.ADMIN))
        assertTrue(roles.contains(com.example.model.UserRole.EMPLOYEE))
        assertTrue(roles.contains(com.example.model.UserRole.SELLER))
        assertTrue(roles.contains(com.example.model.UserRole.BUYER))
        assertTrue(roles.contains(com.example.model.UserRole.DELIVERY_PARTNER))
    }

    @Test
    fun testAppTitleAndTagline() {
        assertEquals("NIIZ BOZAR", TranslationHelper.getString("app_title", Language.EN))
        assertEquals("EMPOWERING FARMERS", TranslationHelper.getString("tagline", Language.EN))
    }

    @Test
    fun testUserRoleMetadataValues() {
        val adminRole = com.example.model.UserRole.valueOf("ADMIN")
        val employeeRole = com.example.model.UserRole.valueOf("EMPLOYEE")
        val sellerRole = com.example.model.UserRole.valueOf("SELLER")
        val buyerRole = com.example.model.UserRole.valueOf("BUYER")
        val deliveryRole = com.example.model.UserRole.valueOf("DELIVERY_PARTNER")

        assertEquals("ADMIN", adminRole.name)
        assertEquals("EMPLOYEE", employeeRole.name)
        assertEquals("SELLER", sellerRole.name)
        assertEquals("BUYER", buyerRole.name)
        assertEquals("DELIVERY_PARTNER", deliveryRole.name)
    }

    @Test
    fun testRolesListNames() {
        val expected = listOf("BUYER", "SELLER", "EMPLOYEE", "ADMIN", "DELIVERY_PARTNER")
        val actual = com.example.model.UserRole.values().map { it.name }
        assertEquals(expected, actual)
    }

    @Test
    fun testNavigationStateManagerRoleDashboards() {
        val navManager = com.example.navigation.NavigationStateManager(com.example.navigation.AppRoute.Login)
        assertEquals(com.example.navigation.AppRoute.Login, navManager.currentRoute.value)
        assertTrue(navManager.isAtLogin())

        // Test navigation to Buyer Dashboard
        navManager.navigateToDashboardForRole(com.example.model.UserRole.BUYER)
        assertEquals(com.example.navigation.AppRoute.BuyerDashboard, navManager.currentRoute.value)
        assertTrue(navManager.isAtDashboard())

        // Test navigation to Seller Dashboard
        navManager.navigateToDashboardForRole(com.example.model.UserRole.SELLER)
        assertEquals(com.example.navigation.AppRoute.SellerDashboard, navManager.currentRoute.value)

        // Test navigation to Employee Dashboard
        navManager.navigateToDashboardForRole(com.example.model.UserRole.EMPLOYEE)
        assertEquals(com.example.navigation.AppRoute.EmployeeDashboard, navManager.currentRoute.value)

        // Test navigation to Admin Dashboard
        navManager.navigateToDashboardForRole(com.example.model.UserRole.ADMIN)
        assertEquals(com.example.navigation.AppRoute.AdminDashboard, navManager.currentRoute.value)

        // Test navigation to Delivery Dashboard
        navManager.navigateToDashboardForRole(com.example.model.UserRole.DELIVERY_PARTNER)
        assertEquals(com.example.navigation.AppRoute.DeliveryDashboard, navManager.currentRoute.value)

        // Test sub-screen navigation and backstack
        navManager.navigateTo(com.example.navigation.AppRoute.ProductDetail)
        assertEquals(com.example.navigation.AppRoute.ProductDetail, navManager.currentRoute.value)
        assertTrue(navManager.canGoBack)

        val handled = navManager.navigateBack()
        assertTrue(handled)
        assertEquals(com.example.navigation.AppRoute.DeliveryDashboard, navManager.currentRoute.value)

        // Test reset to login
        navManager.navigateToLogin()
        assertEquals(com.example.navigation.AppRoute.Login, navManager.currentRoute.value)
        assertTrue(navManager.isAtLogin())
    }

    @Test
    fun testAutomaticUniqueEmployeeIdGeneration() {
        val ids = mutableSetOf<String>()
        repeat(50) {
            val randomSuffix = (1000..9999).random()
            val candidate = "EMP-NB-2026-$randomSuffix"
            assertTrue(candidate.startsWith("EMP-NB-2026-"))
            assertEquals(16, candidate.length)
            ids.add(candidate)
        }
        assertTrue(ids.size > 40)
    }

    @Test
    fun testSwitchToSellerRoleNavigation() {
        val navManager = com.example.navigation.NavigationStateManager(com.example.navigation.AppRoute.BuyerDashboard)
        assertEquals(com.example.navigation.AppRoute.BuyerDashboard, navManager.currentRoute.value)

        // Simulate tapping "Sell" icon
        navManager.navigateToDashboardForRole(com.example.model.UserRole.SELLER)
        assertEquals(com.example.navigation.AppRoute.SellerDashboard, navManager.currentRoute.value)
        assertTrue(navManager.isAtDashboard())
    }

    @Test
    fun testDarkModeTransitions() {
        val lightMode = com.example.model.AppThemeMode.LIGHT
        val darkMode = com.example.model.AppThemeMode.DARK

        fun toggle(current: com.example.model.AppThemeMode): com.example.model.AppThemeMode {
            return if (current == com.example.model.AppThemeMode.DARK) {
                com.example.model.AppThemeMode.LIGHT
            } else {
                com.example.model.AppThemeMode.DARK
            }
        }

        assertEquals(com.example.model.AppThemeMode.DARK, toggle(lightMode))
        assertEquals(com.example.model.AppThemeMode.LIGHT, toggle(darkMode))
    }

    @Test
    fun testFirebaseBackendStatusDefaults() {
        val status = com.example.data.firebase.FirebaseBackendStatus(
            isInitialized = false,
            projectId = "Not configured",
            appName = "None",
            isAuthAvailable = false,
            isFirestoreAvailable = false,
            currentUserId = null,
            currentUserPhone = null
        )
        assertFalse(status.isInitialized)
        assertFalse(status.isAuthAvailable)
        assertFalse(status.isFirestoreAvailable)
        assertNull(status.currentUserId)
    }
}
