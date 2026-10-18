package com.example

import com.example.data.model.MenuItemEntity
import com.example.ui.viewmodel.MenuViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testRupiahFormatting() {
    val formatted = MenuViewModel.formatRupiah(15000L)
    assertTrue("Formatted string should contain 15", formatted.contains("15"))
    assertTrue("Formatted string should contain Rp", formatted.contains("Rp"))
  }

  @Test
  fun testMenuItemAvailability() {
    val item1 = MenuItemEntity(
      id = 1,
      nama = "Mie Ayam Original",
      kategori = "Mie Ayam",
      harga = 13000L,
      status = "Tersedia"
    )
    assertTrue(item1.isAvailable)

    val item2 = MenuItemEntity(
      id = 2,
      nama = "Mie Ayam Bakso",
      kategori = "Mie Ayam",
      harga = 16000L,
      status = "Habis"
    )
    assertTrue(!item2.isAvailable)
  }
}
