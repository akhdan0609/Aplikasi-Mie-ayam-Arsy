package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.MenuItemEntity
import com.example.data.model.ShopConfigEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [MenuItemEntity::class, ShopConfigEntity::class],
  version = 3,
  exportSchema = false
)
abstract class ArsyDatabase : RoomDatabase() {
  abstract fun menuItemDao(): MenuItemDao
  abstract fun shopConfigDao(): ShopConfigDao

  companion object {
    @Volatile
    private var INSTANCE: ArsyDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): ArsyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          ArsyDatabase::class.java,
          "arsy_mie_ayam.db"
        )
        .addCallback(ArsyDatabaseCallback(scope))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    private class ArsyDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.menuItemDao(), database.shopConfigDao())
          }
        }
      }
    }

    suspend fun populateInitialData(menuDao: MenuItemDao, configDao: ShopConfigDao) {
      if (configDao.getShopConfigDirect() == null) {
        configDao.insertOrUpdateConfig(
          ShopConfigEntity(
            id = 1,
            namaWarung = "Mie Ayam Arsy",
            nomorWa = "628123456789",
            infoBank = "BCA: 1234567890 a.n Arsy\nBRI: 0987654321 a.n Arsy\nQRIS: Tersedia di Kasir Warung",
            alamat = "Jl. Raya Warung Mie Ayam Arsy",
            jamBuka = "Buka Setiap Hari: 10:00 - 21:00 WIB",
            adminPin = "1234"
          )
        )
      }

      if (menuDao.getCount() == 0) {
        val initialItems = listOf(
          MenuItemEntity(
            nama = "Mie Ayam Original",
            kategori = "Mie Ayam",
            harga = 13000L,
            status = "Tersedia",
            deskripsi = "Mie kenyal gurih dengan topping ayam kecap manis gurih, sawi segar, dan daun bawang.",
            hasSpiceLevel = false,
            hasDrinkTemp = false,
            isBestSeller = true
          ),
          MenuItemEntity(
            nama = "Mie Ayam Bakso",
            kategori = "Mie Ayam",
            harga = 16000L,
            status = "Tersedia",
            deskripsi = "Mie ayam lezat ditambah 2 butir bakso sapi kenyal dan kuah kaldu hangat.",
            hasSpiceLevel = false,
            hasDrinkTemp = false,
            isBestSeller = true
          ),
          MenuItemEntity(
            nama = "Mie Ayam Pangsit",
            kategori = "Mie Ayam",
            harga = 15000L,
            status = "Tersedia",
            deskripsi = "Mie ayam istimewa dengan taburan pangsit goreng renyah dan gurih.",
            hasSpiceLevel = false,
            hasDrinkTemp = false
          ),
          MenuItemEntity(
            nama = "Mie Ayam Komplit (Bakso + Pangsit)",
            kategori = "Mie Ayam",
            harga = 18000L,
            status = "Tersedia",
            deskripsi = "Porsi komplit dengan bakso daging sapi dan pangsit goreng renyah.",
            hasSpiceLevel = false,
            hasDrinkTemp = false,
            isBestSeller = true
          ),
          MenuItemEntity(
            nama = "Mie Jebew Spesial",
            kategori = "Mie Ayam",
            harga = 15000L,
            status = "Tersedia",
            deskripsi = "Mie pedas khas dengan bumbu cabai melimpah dan minyak cabai aromatic. Wajib pilih level pedas!",
            hasSpiceLevel = true,
            hasDrinkTemp = false,
            isBestSeller = true
          ),
          MenuItemEntity(
            nama = "Mie Jebew Bakso",
            kategori = "Mie Ayam",
            harga = 18000L,
            status = "Tersedia",
            deskripsi = "Mie jebew pedas nendang disajikan dengan bakso sapi kenyal dan gurih.",
            hasSpiceLevel = true,
            hasDrinkTemp = false
          ),
          MenuItemEntity(
            nama = "Pangsit Goreng Crispy",
            kategori = "Topping",
            harga = 5000L,
            status = "Tersedia",
            deskripsi = "Pangsit renyah isi ayam gurih, cocok sebagai pelengkap mie ayam.",
            hasSpiceLevel = false,
            hasDrinkTemp = false
          ),
          MenuItemEntity(
            nama = "Bakso Kuah Porsi (5 Butir)",
            kategori = "Topping",
            harga = 10000L,
            status = "Tersedia",
            deskripsi = "Semangkuk bakso sapi kuah kaldu sapi asli dengan seledri dan bawang goreng.",
            hasSpiceLevel = false,
            hasDrinkTemp = false
          ),
          MenuItemEntity(
            nama = "Ceker Pedas Manis",
            kategori = "Topping",
            harga = 8000L,
            status = "Tersedia",
            deskripsi = "Ceker ayam empuk dimasak bumbu pedas manis meresap sampai ke tulang.",
            hasSpiceLevel = false,
            hasDrinkTemp = false
          ),
          MenuItemEntity(
            nama = "Es Teh Manis",
            kategori = "Minuman",
            harga = 4000L,
            status = "Tersedia",
            deskripsi = "Teh seduh wangi melati khas Jawa, disajikan dingin atau hangat.",
            hasSpiceLevel = false,
            hasDrinkTemp = true
          ),
          MenuItemEntity(
            nama = "Es Jeruk Segar",
            kategori = "Minuman",
            harga = 5000L,
            status = "Tersedia",
            deskripsi = "Jeruk peras murni kaya vitamin C, segar disajikan dingin atau hangat.",
            hasSpiceLevel = false,
            hasDrinkTemp = true
          ),
          MenuItemEntity(
            nama = "Es Cappucino Cincau",
            kategori = "Minuman",
            harga = 7000L,
            status = "Tersedia",
            deskripsi = "Kopi cappucino creamy dipadu potongan cincau hitam kenyal dan segar.",
            hasSpiceLevel = false,
            hasDrinkTemp = true
          ),
          MenuItemEntity(
            nama = "Milo Cokelat",
            kategori = "Minuman",
            harga = 6000L,
            status = "Tersedia",
            deskripsi = "Susu cokelat malt Milo favorit, nikmat dingin maupun hangat.",
            hasSpiceLevel = false,
            hasDrinkTemp = true
          ),
          MenuItemEntity(
            nama = "Air Mineral Botol",
            kategori = "Minuman",
            harga = 3000L,
            status = "Tersedia",
            deskripsi = "Air mineral segar 600ml.",
            hasSpiceLevel = false,
            hasDrinkTemp = false
          )
        )
        menuDao.insertMenuItems(initialItems)
      }
    }
  }
}
