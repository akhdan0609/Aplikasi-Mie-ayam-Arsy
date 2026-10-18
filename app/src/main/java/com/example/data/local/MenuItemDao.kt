package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
  @Query("SELECT * FROM menu_items ORDER BY id ASC")
  fun getAllMenuItems(): Flow<List<MenuItemEntity>>

  @Query("SELECT * FROM menu_items WHERE kategori = :kategori ORDER BY id ASC")
  fun getMenuItemsByCategory(kategori: String): Flow<List<MenuItemEntity>>

  @Query("SELECT * FROM menu_items WHERE id = :id")
  suspend fun getMenuItemById(id: Int): MenuItemEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMenuItem(item: MenuItemEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMenuItems(items: List<MenuItemEntity>)

  @Update
  suspend fun updateMenuItem(item: MenuItemEntity)

  @Query("UPDATE menu_items SET status = :status WHERE id = :id")
  suspend fun updateMenuItemStatus(id: Int, status: String)

  @Query("UPDATE menu_items SET harga = :harga WHERE id = :id")
  suspend fun updateMenuItemPrice(id: Int, harga: Long)

  @Delete
  suspend fun deleteMenuItem(item: MenuItemEntity)

  @Query("DELETE FROM menu_items WHERE id = :id")
  suspend fun deleteMenuItemById(id: Int)

  @Query("SELECT COUNT(*) FROM menu_items")
  suspend fun getCount(): Int
}
