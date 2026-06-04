package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.TenseDatabase
import com.example.data.TenseRepository
import com.example.ui.screens.TenseAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TenseAppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Instantiate Room Database and Repository
        val database = TenseDatabase.getDatabase(this)
        val repository = TenseRepository(database.tenseDao())

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize stateful ViewModel with simple clean Factory
                val appViewModel: TenseAppViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(TenseAppViewModel::class.java)) {
                                return TenseAppViewModel(repository) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }
                )
                
                TenseAppScreen(viewModel = appViewModel)
            }
        }
    }
}
