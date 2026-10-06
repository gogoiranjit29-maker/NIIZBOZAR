package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.model.AppScreen
import com.example.model.AppThemeMode
import com.example.model.NetworkMode
import com.example.navigation.AppRoute
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.MarketplaceBottomNavBar
import com.example.ui.components.MarketplaceTopAppBar
import com.example.ui.components.NetworkModeBanner
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RoleAndAuthScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.buyer.BuyerHomeScreen
import com.example.ui.screens.buyer.BuyerRequirementScreen
import com.example.ui.screens.buyer.CartAndCheckoutScreen
import com.example.ui.screens.buyer.DirectBuyCheckoutScreen
import com.example.ui.screens.buyer.OrderTrackingScreen
import com.example.ui.screens.buyer.ProductDetailScreen
import com.example.ui.screens.delivery.DeliveryPartnerDashboardScreen
import com.example.ui.screens.employee.EmployeeDashboardScreen
import com.example.ui.screens.profile.SettingsAndProfileScreen
import com.example.ui.screens.seller.SellerDashboardScreen
import com.example.ui.screens.seller.SellerProductUploadScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            MyApplicationTheme(darkTheme = isDark) {
                MarketplaceApp(
                    viewModel = viewModel,
                    onExitApp = { finish() }
                )
            }
        }
    }
}

@Composable
fun MarketplaceApp(
    viewModel: MarketplaceViewModel,
    onExitApp: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    val networkMode by viewModel.networkMode.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val pendingProducts by viewModel.pendingProducts.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDark = when (themeMode) {
        com.example.model.AppThemeMode.SYSTEM -> isSystemDark
        com.example.model.AppThemeMode.LIGHT -> false
        com.example.model.AppThemeMode.DARK -> true
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Intercept Back Press safely
    BackHandler(enabled = true) {
        val handled = viewModel.handleBack()
        if (!handled) {
            onExitApp()
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val isLoginScreen = currentRoute == AppRoute.Login
    val canGoBack = viewModel.navManager.canGoBack && !isLoginScreen

    Scaffold(
        topBar = {
            if (!isLoginScreen) {
                Column {
                    MarketplaceTopAppBar(
                        currentRole = currentRole,
                        currentLanguage = currentLanguage,
                        networkMode = networkMode,
                        cartCount = cartItems.size,
                        canNavigateBack = canGoBack,
                        onNavigateBack = { viewModel.handleBack() },
                        onRoleSelected = { viewModel.setRole(it) },
                        onLanguageClick = { showLanguageDialog = true },
                        onCartClick = { viewModel.navigateTo(AppRoute.CartAndCheckout) },
                        onProfileClick = { viewModel.navigateTo(AppRoute.ProfileAndSettings) },
                        onSellClick = { viewModel.setRole(com.example.model.UserRole.SELLER) },
                        isDarkMode = isDark,
                        onToggleDarkMode = {
                            viewModel.setThemeMode(
                                if (isDark) com.example.model.AppThemeMode.LIGHT else com.example.model.AppThemeMode.DARK
                            )
                        }
                    )
                    NetworkModeBanner(
                        networkMode = networkMode,
                        currentLanguage = currentLanguage,
                        onToggleClick = {
                            val nextMode = when (networkMode) {
                                NetworkMode.ONLINE -> NetworkMode.LOW_NETWORK
                                NetworkMode.LOW_NETWORK -> NetworkMode.OFFLINE
                                NetworkMode.OFFLINE -> NetworkMode.ONLINE
                            }
                            viewModel.setNetworkMode(nextMode)
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (!isLoginScreen) {
                MarketplaceBottomNavBar(
                    currentRole = currentRole,
                    currentScreen = viewModel.currentScreen.collectAsState().value,
                    cartCount = cartItems.size,
                    pendingVerificationsCount = pendingProducts.size,
                    onNavigate = { screen ->
                        if ((screen == AppScreen.SellerDashboard || screen == AppScreen.SellerProductUpload) && currentRole != com.example.model.UserRole.SELLER) {
                            viewModel.setRole(com.example.model.UserRole.SELLER)
                        } else {
                            viewModel.navigateTo(screen)
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize().testTag("marketplace_main_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                AppRoute.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onAuthSuccess = {
                            viewModel.navigateToDashboardForRole(viewModel.currentRole.value)
                        }
                    )
                }
                AppRoute.BuyerDashboard -> {
                    BuyerHomeScreen(viewModel = viewModel)
                }
                AppRoute.SellerDashboard -> {
                    SellerDashboardScreen(viewModel = viewModel)
                }
                AppRoute.EmployeeDashboard -> {
                    EmployeeDashboardScreen(viewModel = viewModel)
                }
                AppRoute.AdminDashboard -> {
                    AdminDashboardScreen(viewModel = viewModel)
                }
                AppRoute.DeliveryDashboard -> {
                    DeliveryPartnerDashboardScreen(viewModel = viewModel)
                }
                AppRoute.ProductDetail -> {
                    ProductDetailScreen(viewModel = viewModel)
                }
                AppRoute.CartAndCheckout -> {
                    CartAndCheckoutScreen(viewModel = viewModel)
                }
                AppRoute.DirectBuyCheckout -> {
                    DirectBuyCheckoutScreen(viewModel = viewModel)
                }
                AppRoute.OrderTracking -> {
                    OrderTrackingScreen(viewModel = viewModel)
                }
                AppRoute.BuyerRequirements -> {
                    BuyerRequirementScreen(viewModel = viewModel)
                }
                AppRoute.SellerProductUpload -> {
                    SellerProductUploadScreen(viewModel = viewModel)
                }
                AppRoute.ProfileAndSettings -> {
                    SettingsAndProfileScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }
}
