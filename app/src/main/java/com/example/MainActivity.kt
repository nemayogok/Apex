package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.NewsRepository
import com.example.ui.MotoGridApp
import com.example.ui.NewsViewModel
import com.example.ui.NewsViewModelFactory
import com.example.ui.theme.MotoGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NewsRepository(database.articleDao(), database.commentDao())
        val factory = NewsViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        setContent {
            MotoGridTheme {
                MotoGridApp(viewModel = viewModel)
            }
        }
    }
}
