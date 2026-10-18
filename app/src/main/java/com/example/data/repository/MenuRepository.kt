package com.example.data.repository

import com.example.data.local.MenuItemDao
import com.example.data.local.ShopConfigDao
import com.example.data.model.MenuItemEntity
import com.example.data.model.ShopConfigEntity
import kotlinx.coroutines.flow.Flow

class MenuRepository(
  private val menuItemDao: MenuItemDao,
  private val shopConfigDao: ShopConfigDao
) {
  val allMenuItems: Flow<List<MenuItemEntity>> = menuItemDao.getAllMenuItems()
  val shopConfig: Flow<ShopConfigEntity?> = shopConfigDao.getShopConfig()

  fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>> {
    return if (category == "Semua" || category.isBlank()) {
      menuItemDao.getAllMenuItems()
    } else {
      menuItemDao.getMenuItemsByCategory(category)
    }
  }

  suspend fun insertMenuItem(item: MenuItemEntity): Long {
    return menuItemDao.insertMenuItem(item)
  }

  suspend fun updateMenuItem(item: MenuItemEntity) {
    menuItemDao.updateMenuItem(item)
  }

  suspend fun toggleMenuItemStatus(id: Int, currentStatus: String) {
    val newStatus = if (currentStatus.equals("Tersedia", ignoreCase = true)) "Habis" else "Tersedia"
    menuItemDao.updateMenuItemStatus(id, newStatus)
  }

  suspend fun updateMenuItemPrice(id: Int, newPrice: Long) {
    menuItemDao.updateMenuItemPrice(id, newPrice)
  }

  suspend fun deleteMenuItemById(id: Int) {
    menuItemDao.deleteMenuItemById(id)
  }

  suspend fun updateShopConfig(config: ShopConfigEntity) {
    shopConfigDao.insertOrUpdateConfig(config)
  }

  suspend fun getShopConfigDirect(): ShopConfigEntity? {
    return shopConfigDao.getShopConfigDirect()
  }
}
