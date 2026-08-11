package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickCartDao {

    // --- Cart operations ---
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getCartItem(productId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- Address operations ---
    @Query("SELECT * FROM user_addresses ORDER BY isSelected DESC, id DESC")
    fun getAddresses(): Flow<List<UserAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: UserAddressEntity): Long

    @Query("UPDATE user_addresses SET isSelected = (id = :selectedId)")
    suspend fun setSelectedAddress(selectedId: Long)

    @Query("DELETE FROM user_addresses WHERE id = :id")
    suspend fun deleteAddress(id: Long)

    // --- Order operations ---
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderFlow(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    // --- Favorites operations ---
    @Query("SELECT productId FROM saved_favorites")
    fun getFavoriteProductIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(saved: SavedItemEntity)

    @Query("DELETE FROM saved_favorites WHERE productId = :productId")
    suspend fun removeFavorite(productId: String)
}
