package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Double,
    val mrp: Double,
    val unit: String,
    val categoryName: String,
    val isVeg: Boolean,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_addresses")
data class UserAddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val addressLine: String,
    val landmark: String,
    val lat: Double,
    val lng: Double,
    val isSelected: Boolean
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val itemsSummary: String, // e.g. "Milk 1L (2), Amul Butter 100g (1)"
    val deliveryAddress: String,
    val paymentMethod: String,
    val status: String, // "Placed", "Packed", "Out for Delivery", "Delivered"
    val etaMinutes: Int = 12
)

@Entity(tableName = "saved_favorites")
data class SavedItemEntity(
    @PrimaryKey val productId: String,
    val savedAt: Long = System.currentTimeMillis()
)
