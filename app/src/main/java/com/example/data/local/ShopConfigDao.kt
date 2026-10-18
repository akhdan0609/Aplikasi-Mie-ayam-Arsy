package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ShopConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopConfigDao {
  @Query("SELECT * FROM shop_config WHERE id = 1 LIMIT 1")
  fun getShopConfig(): Flow<ShopConfigEntity?>

  @Query("SELECT * FROM shop_config WHERE id = 1 LIMIT 1")
  suspend fun getShopConfigDirect(): ShopConfigEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateConfig(config: ShopConfigEntity)

  @Update
  suspend fun updateConfig(config: ShopConfigEntity)
}
