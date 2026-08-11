package com.example.data

import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.QuickCartDao
import com.example.data.local.SavedItemEntity
import com.example.data.local.UserAddressEntity
import com.example.model.Coupon
import com.example.model.Product
import com.example.model.UserAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class QuickCartRepository(private val dao: QuickCartDao) {

    // --- Cart ---
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()

    suspend fun addToCart(product: Product, quantityDelta: Int = 1) {
        val existing = dao.getCartItem(product.id)
        val newQuantity = (existing?.quantity ?: 0) + quantityDelta
        if (newQuantity <= 0) {
            dao.deleteCartItem(product.id)
        } else {
            val entity = CartItemEntity(
                productId = product.id,
                name = product.name,
                price = product.price,
                mrp = product.mrp,
                unit = product.unit,
                categoryName = product.categoryName,
                isVeg = product.isVeg,
                quantity = newQuantity
            )
            dao.insertOrUpdateCartItem(entity)
        }
    }

    suspend fun updateCartQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItem(productId)
        } else {
            val existing = dao.getCartItem(productId)
            if (existing != null) {
                dao.insertOrUpdateCartItem(existing.copy(quantity = newQuantity))
            }
        }
    }

    suspend fun removeFromCart(productId: String) {
        dao.deleteCartItem(productId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    // --- Addresses ---
    val addresses: Flow<List<UserAddressEntity>> = dao.getAddresses()

    suspend fun addAddress(address: UserAddress): Long {
        val entity = UserAddressEntity(
            title = address.title,
            addressLine = address.addressLine,
            landmark = address.landmark,
            lat = address.lat,
            lng = address.lng,
            isSelected = address.isSelected
        )
        val id = dao.insertAddress(entity)
        if (address.isSelected) {
            dao.setSelectedAddress(id)
        }
        return id
    }

    suspend fun selectAddress(id: Long) {
        dao.setSelectedAddress(id)
    }

    suspend fun ensureDefaultAddress() {
        val list = dao.getAddresses().first()
        if (list.isEmpty()) {
            addAddress(
                UserAddress(
                    title = "Home",
                    addressLine = "Flat 402, Green Park Apartments, Sector 18",
                    landmark = "Near Metro Station Gate 2",
                    isSelected = true
                )
            )
            addAddress(
                UserAddress(
                    title = "Office",
                    addressLine = "Building 5, Cyber City Tech Park, 8th Floor",
                    landmark = "Opposite Main Cafeteria",
                    isSelected = false
                )
            )
        }
    }

    // --- Orders ---
    val orders: Flow<List<OrderEntity>> = dao.getOrders()

    fun getOrderFlow(orderId: String): Flow<OrderEntity?> = dao.getOrderFlow(orderId)

    suspend fun placeOrder(
        cartList: List<CartItemEntity>,
        address: String,
        paymentMethod: String,
        discount: Double
    ): String {
        val orderId = "QC-" + (100000..999999).random()
        val subtotal = cartList.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal > 149) 0.0 else 15.0
        val handlingFee = 5.0
        val totalAmount = (subtotal + deliveryFee + handlingFee - discount).coerceAtLeast(0.0)

        val itemsSummary = cartList.joinToString(", ") { "${it.name} (${it.quantity})" }

        val order = OrderEntity(
            orderId = orderId,
            timestamp = System.currentTimeMillis(),
            totalAmount = totalAmount,
            itemsSummary = itemsSummary,
            deliveryAddress = address,
            paymentMethod = paymentMethod,
            status = "Placed",
            etaMinutes = 11
        )

        dao.insertOrder(order)
        dao.clearCart()
        return orderId
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        dao.updateOrderStatus(orderId, status)
    }

    // --- Favorites ---
    val favoriteIds: Flow<List<String>> = dao.getFavoriteProductIds()

    suspend fun toggleFavorite(productId: String, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            dao.removeFavorite(productId)
        } else {
            dao.addFavorite(SavedItemEntity(productId))
        }
    }

    // --- Catalog Lookup ---
    fun searchProducts(query: String, categoryId: String? = null): List<Product> {
        var result = MockCatalog.products
        if (!categoryId.isNullOrBlank()) {
            result = result.filter { it.categoryId == categoryId }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) ||
                it.categoryName.lowercase().contains(q) ||
                (it.tag?.lowercase()?.contains(q) == true)
            }
        }
        return result
    }
}
