package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val content: String,
    val category: String,
    val imageUrl: String,
    val authorName: String,
    val authorRole: String,
    val authorAvatarUrl: String,
    val publishDate: String,
    val readTimeMinutes: Int,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val keyHighlights: String = "", // Delimited by "|"
    val bikeSpecs: String? = null,  // Format "Label: Value|Label: Value"
    val savedTimestamp: Long = 0L
)
