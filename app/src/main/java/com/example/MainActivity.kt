package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.data.local.ArsyDatabase
import com.example.data.repository.MenuRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MenuViewModel
import com.example.ui.viewmodel.MenuViewModelFactory
import com.example.ui.viewmodel.UpdateViewModel

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = ArsyDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = MenuRepository(database.menuItemDao(), database.shopConfigDao())
    val viewModelFactory = MenuViewModelFactory(repository)
    val viewModel by viewModels<MenuViewModel> { viewModelFactory }
    val updateViewModel by viewModels<UpdateViewModel>()

    setContent {
      MyApplicationTheme {
        HomeScreen(viewModel = viewModel, updateViewModel = updateViewModel)
      }
    }
  }
}
