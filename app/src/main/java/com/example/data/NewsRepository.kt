package com.example.data

import com.example.data.api.NewsApiService
import kotlinx.coroutines.flow.Flow
import android.text.Html

class NewsRepository(
    private val articleDao: ArticleDao,
    private val commentDao: CommentDao,
    private val apiService: NewsApiService
) {
    val allArticles: Flow<List<ArticleEntity>> = articleDao.getAllArticles()
    val bookmarkedArticles: Flow<List<ArticleEntity>> = articleDao.getBookmarkedArticles()

    fun getArticleById(id: String): Flow<ArticleEntity?> = articleDao.getArticleById(id)
    fun getCommentsForArticle(articleId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForArticle(articleId)

    suspend fun toggleBookmark(articleId: String, currentBookmarked: Boolean) {
        val newBookmarked = !currentBookmarked
        articleDao.updateBookmarkStatus(articleId, newBookmarked, System.currentTimeMillis())
    }

    suspend fun markAsRead(articleId: String) {
        articleDao.updateReadStatus(articleId, true)
    }

    suspend fun addLike(articleId: String) {
        articleDao.incrementLikes(articleId)
    }

    suspend fun addComment(articleId: String, authorName: String, commentText: String) {
        val newComment = CommentEntity(
            articleId = articleId,
            authorName = authorName,
            authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
            commentText = commentText,
            timestamp = "Justo ahora",
            likesCount = 0
        )
        commentDao.insertComment(newComment)
        articleDao.incrementCommentsCount(articleId)
    }

    suspend fun likeComment(commentId: Int) {
        commentDao.likeComment(commentId)
    }

    suspend fun seedInitialDataIfEmpty() {
        if (articleDao.getArticleCount() == 0) {
            try {
                val response = apiService.getRssFeed()
                if (response.status == "ok") {
                    val articles = response.items.map { item ->
                        val plainContent = Html.fromHtml(item.description, Html.FROM_HTML_MODE_COMPACT).toString()
                        val snippet = if (plainContent.length > 150) plainContent.substring(0, 150) + "..." else plainContent
                        
                        ArticleEntity(
                            id = item.guid.hashCode().toString(),
                            title = item.title,
                            subtitle = snippet,
                            content = plainContent,
                            category = "Motociclismo",
                            imageUrl = item.enclosure?.link?.takeIf { it.isNotBlank() } ?: item.thumbnail?.takeIf { it.isNotBlank() } ?: "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1000&q=80",
                            authorName = item.author?.takeIf { it.isNotBlank() } ?: "Google News",
                            authorRole = "Reportero",
                            authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                            publishDate = item.pubDate,
                            readTimeMinutes = 5,
                            isBookmarked = false,
                            isRead = false,
                            likesCount = (10..500).random(),
                            commentsCount = 0,
                            keyHighlights = "",
                            bikeSpecs = "",
                            savedTimestamp = System.currentTimeMillis()
                        )
                    }
                    articleDao.insertArticles(articles)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
