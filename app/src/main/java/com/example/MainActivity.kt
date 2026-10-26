package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.data.local.ArsyDatabase
import com.example.data.repository.MenuRepository
import com.example.ui.ArsyApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MenuViewModel
import com.example.ui.viewmodel.MenuViewModelFactory
import com.example.ui.viewmodel.UpdateViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = ArsyDatabase.getDatabase(applicationContext, lifecycleScope)
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val repository = MenuRepository(
      firestore,
      contentResolver,
      database.menuItemDao(),
      database.shopConfigDao()
    )
    val viewModelFactory = MenuViewModelFactory(repository, auth)
    val viewModel by viewModels<MenuViewModel> { viewModelFactory }
    val updateViewModel by viewModels<UpdateViewModel>()

    setContent {
      // Desain referensi memakai tema terang (cream/cokelat/gold);
      // tema gelap sistem membuat kontras buruk, jadi selalu pakai skema terang.
      MyApplicationTheme(darkTheme = false) {
        ArsyApp(viewModel = viewModel, updateViewModel = updateViewModel)
      }
    }
  }
}
