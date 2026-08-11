package com.example.model

import androidx.annotation.Keep

@Keep
data class Product(
    val id: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val price: Double,
    val mrp: Double,
    val unit: String,
    val isVeg: Boolean = true,
    val rating: Float = 4.8f,
    val reviewCount: Int = 128,
    val stockQuantity: Int = 50,
    val tag: String? = null, // e.g. "Bestseller", "10 Min", "Organic", "Deal"
    val description: String = "Freshly sourced high quality item, packed with care for quick 10-minute delivery."
)

@Keep
data class Category(
    val id: String,
    val name: String,
    val subCategories: List<String> = emptyList(),
    val iconName: String
)

@Keep
data class Coupon(
    val code: String,
    val discountPercent: Int = 0,
    val discountFlat: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val title: String,
    val description: String
)

@Keep
data class UserAddress(
    val id: Long = 0,
    val title: String, // "Home", "Work", "Other"
    val addressLine: String,
    val landmark: String = "",
    val lat: Double = 28.6139,
    val lng: Double = 77.2090,
    val isSelected: Boolean = false
)
