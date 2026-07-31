package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val articleId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val commentText: String,
    val timestamp: String,
    val likesCount: Int = 0
)
