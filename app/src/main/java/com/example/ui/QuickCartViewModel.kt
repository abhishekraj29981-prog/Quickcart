package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiMealAssistant
import com.example.data.MockCatalog
import com.example.data.QuickCartRepository
import com.example.data.local.QuickCartDatabase
import com.example.data.RecipeSuggestionResult
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.UserAddressEntity
import com.example.model.Coupon
import com.example.model.Product
import com.example.model.UserAddress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AiRecipeUiState {
    object Idle : AiRecipeUiState
    object Loading : AiRecipeUiState
    data class Success(val result: RecipeSuggestionResult) : AiRecipeUiState
    data class Error(val message: String) : AiRecipeUiState
}

class QuickCartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuickCartRepository
    private val geminiAssistant = GeminiMealAssistant()

    init {
        val dao = QuickCartDatabase.getDatabase(application).dao()
        repository = QuickCartRepository(dao)

        viewModelScope.launch {
            repository.ensureDefaultAddress()
        }
    }

    // --- Cart ---
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemCount: StateFlow<Int> = cartItems.map { list -> list.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.map { list -> list.sumOf { it.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartMrpTotal: StateFlow<Double> = cartItems.map { list -> list.sumOf { it.mrp * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Applied Coupon
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    fun applyCoupon(coupon: Coupon) {
        _appliedCoupon.value = coupon
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun addToCart(product: Product, delta: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, delta)
        }
    }

    fun updateCartQuantity(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, newQuantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // --- Catalog & Search ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _filteredProducts = MutableStateFlow(MockCatalog.products)
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
        filterProducts()
    }

    private fun filterProducts() {
        _filteredProducts.value = repository.searchProducts(_searchQuery.value, _selectedCategoryId.value)
    }

    // --- Addresses ---
    val addresses: StateFlow<List<UserAddressEntity>> = repository.addresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedAddress: StateFlow<UserAddressEntity?> = addresses.map { list ->
        list.find { it.isSelected } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addAddress(address: UserAddress) {
        viewModelScope.launch {
            repository.addAddress(address)
        }
    }

    fun selectAddress(id: Long) {
        viewModelScope.launch {
            repository.selectAddress(id)
        }
    }

    // --- Favorites ---
    val favoriteIds: StateFlow<List<String>> = repository.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(productId)
            repository.toggleFavorite(productId, isFav)
        }
    }

    // --- AI Recipe Assistant ---
    private val _aiRecipeState = MutableStateFlow<AiRecipeUiState>(AiRecipeUiState.Idle)
    val aiRecipeState: StateFlow<AiRecipeUiState> = _aiRecipeState.asStateFlow()

    fun requestAiRecipe(prompt: String) {
        viewModelScope.launch {
            _aiRecipeState.value = AiRecipeUiState.Loading
            try {
                val result = geminiAssistant.generateRecipeAndProducts(prompt, MockCatalog.products)
                _aiRecipeState.value = AiRecipeUiState.Success(result)
            } catch (e: Exception) {
                _aiRecipeState.value = AiRecipeUiState.Error(e.message ?: "Failed to generate recipe")
            }
        }
    }

    fun addAllRecipeProductsToCart(products: List<Product>) {
        viewModelScope.launch {
            products.forEach { repository.addToCart(it, 1) }
        }
    }

    fun resetAiRecipe() {
        _aiRecipeState.value = AiRecipeUiState.Idle
    }

    // --- Order Placement & Real-time Live Tracking ---
    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId: StateFlow<String?> = _activeOrderId.asStateFlow()

    private val _riderProgress = MutableStateFlow(0.1f) // 0.0 to 1.0 map progress
    val riderProgress: StateFlow<Float> = _riderProgress.asStateFlow()

    private var trackingSimulationJob: Job? = null

    fun placeOrder(paymentMethod: String, discount: Double, onOrderPlaced: (String) -> Unit) {
        val currentCart = cartItems.value
        if (currentCart.isEmpty()) return

        val addressStr = selectedAddress.value?.let { "${it.title}: ${it.addressLine}" } ?: "Home Delivery"

        viewModelScope.launch {
            val orderId = repository.placeOrder(currentCart, addressStr, paymentMethod, discount)
            _appliedCoupon.value = null
            _activeOrderId.value = orderId
            startOrderTrackingSimulation(orderId)
            onOrderPlaced(orderId)
        }
    }

    fun setActiveTrackingOrder(orderId: String) {
        _activeOrderId.value = orderId
        startOrderTrackingSimulation(orderId)
    }

    private fun startOrderTrackingSimulation(orderId: String) {
        trackingSimulationJob?.cancel()
        trackingSimulationJob = viewModelScope.launch {
            _riderProgress.value = 0.05f
            repository.updateOrderStatus(orderId, "Placed")
            delay(3000)

            repository.updateOrderStatus(orderId, "Packed")
            _riderProgress.value = 0.25f
            delay(4000)

            repository.updateOrderStatus(orderId, "Out for Delivery")
            var progress = 0.25f
            while (progress < 0.95f) {
                delay(1500)
                progress += 0.12f
                _riderProgress.value = progress.coerceAtMost(0.95f)
            }

            repository.updateOrderStatus(orderId, "Delivered")
            _riderProgress.value = 1.0f
        }
    }

    // --- Delivery Partner Mode (TRD §6.8) ---
    private val _isPartnerMode = MutableStateFlow(false)
    val isPartnerMode: StateFlow<Boolean> = _isPartnerMode.asStateFlow()

    private val _partnerOnline = MutableStateFlow(true)
    val partnerOnline: StateFlow<Boolean> = _partnerOnline.asStateFlow()

    private val _partnerEarnings = MutableStateFlow(420.0)
    val partnerEarnings: StateFlow<Double> = _partnerEarnings.asStateFlow()

    fun togglePartnerMode() {
        _isPartnerMode.value = !_isPartnerMode.value
    }

    fun togglePartnerOnline() {
        _partnerOnline.value = !_partnerOnline.value
    }

    fun completePartnerDelivery(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "Delivered")
            _partnerEarnings.value += 45.0
        }
    }
}
