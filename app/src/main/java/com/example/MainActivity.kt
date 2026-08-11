package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.QuickCartViewModel
import com.example.ui.components.AiRecipeDialog
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.DeliveryPartnerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveTrackingScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.theme.QuickCartTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuickCartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuickCartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickCartApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun QuickCartApp(viewModel: QuickCartViewModel) {
    val navController = rememberNavController()
    var showAiDialog by remember { mutableStateOf(false) }
    val aiRecipeState by viewModel.aiRecipeState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Home Screen
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onOpenCart = { navController.navigate("cart") },
                onOpenOrdersHistory = { navController.navigate("history") },
                onOpenPartnerPortal = { navController.navigate("partner") },
                onOpenAiRecipe = { showAiDialog = true },
                onProductClick = { /* Detail */ }
            )
        }

        // Cart & Checkout Screen
        composable("cart") {
            CartCheckoutScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onOrderPlaced = { orderId ->
                    navController.navigate("tracking/$orderId") {
                        popUpTo("home")
                    }
                }
            )
        }

        // Live Order Tracking Screen
        composable(
            route = "tracking/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "QC-1001"
            LiveTrackingScreen(
                orderId = orderId,
                viewModel = viewModel,
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Order History Screen
        composable("history") {
            OrderHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onTrackOrder = { orderId ->
                    navController.navigate("tracking/$orderId")
                }
            )
        }

        // Delivery Partner Portal Screen
        composable("partner") {
            DeliveryPartnerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }

    // AI Meal Builder Dialog Popup
    if (showAiDialog) {
        AiRecipeDialog(
            state = aiRecipeState,
            onDismiss = {
                showAiDialog = false
                viewModel.resetAiRecipe()
            },
            onRequestRecipe = { prompt ->
                viewModel.requestAiRecipe(prompt)
            },
            onAddAllToCart = { products ->
                viewModel.addAllRecipeProductsToCart(products)
            }
        )
    }
}
