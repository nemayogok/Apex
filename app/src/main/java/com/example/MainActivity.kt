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
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://api.rss2json.com/")
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create(
                com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            ))
            .build()
        val apiService = retrofit.create(com.example.data.api.NewsApiService::class.java)
        val repository = NewsRepository(database.articleDao(), database.commentDao(), apiService)
        val factory = NewsViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        setContent {
            MotoGridTheme {
                MotoGridApp(viewModel = viewModel)
            }
        }
    }
}
