package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishDate DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY savedTimestamp DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    fun getArticleById(id: String): Flow<ArticleEntity?>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishDate DESC")
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("UPDATE articles SET isBookmarked = :isBookmarked, savedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE articles SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: String, isRead: Boolean)

    @Query("UPDATE articles SET likesCount = likesCount + 1 WHERE id = :id")
    suspend fun incrementLikes(id: String)

    @Query("UPDATE articles SET commentsCount = commentsCount + 1 WHERE id = :id")
    suspend fun incrementCommentsCount(id: String)

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticleCount(): Int
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE articleId = :articleId ORDER BY id DESC")
    fun getCommentsForArticle(articleId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: Int)
}
