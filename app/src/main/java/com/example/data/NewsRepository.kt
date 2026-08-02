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

    suspend fun refreshNews() {
        val feeds = listOf(
            "https://es.motorsport.com/rss/motogp/news/",
            "https://news.google.com/rss/search?q=motociclismo&hl=es-419&gl=US&ceid=US:es-419",
            "https://news.google.com/rss/search?q=MotoGP&hl=es-419&gl=US&ceid=US:es-419"
        )
        val fallbackImages = listOf(
            "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1000&q=80",
            "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1000&q=80",
            "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1000&q=80",
            "https://images.unsplash.com/photo-1558980664-3a031cf67ea8?auto=format&fit=crop&w=1000&q=80"
        )

        val newArticles = mutableListOf<ArticleEntity>()
        for (feed in feeds) {
            try {
                val response = apiService.getRssFeed(feed)
                if (response.status == "ok") {
                    response.items.forEachIndexed { index, item ->
                        val rawContent = item.content.ifBlank { item.description }
                        val plainContent = Html.fromHtml(rawContent, Html.FROM_HTML_MODE_COMPACT).toString()
                            .replace(Regex("(?i)Sigue leyendo.*"), "")
                            .trim()

                        val snippet = if (plainContent.length > 140) plainContent.substring(0, 140) + "..." else plainContent
                        val fallbackImg = fallbackImages[Math.abs(item.title.hashCode()) % fallbackImages.size]
                        val img = item.enclosure?.link?.takeIf { it.isNotBlank() }
                            ?: item.thumbnail?.takeIf { it.isNotBlank() }
                            ?: fallbackImg

                        val cat = when {
                            item.title.contains("MotoGP", ignoreCase = true) || feed.contains("motogp") -> "MotoGP"
                            item.title.contains("Review", ignoreCase = true) || item.title.contains("Prueba", ignoreCase = true) -> "Reviews"
                            else -> "Industry News"
                        }

                        val articleId = if (item.guid.isNotBlank()) item.guid.hashCode().toString() else item.link.hashCode().toString()

                        newArticles.add(
                            ArticleEntity(
                                id = articleId,
                                title = item.title,
                                subtitle = snippet,
                                content = if (plainContent.isBlank()) item.title else plainContent,
                                category = cat,
                                imageUrl = img,
                                authorName = item.author?.takeIf { it.isNotBlank() } ?: "Redacción Motociclismo",
                                authorRole = "Reportero Especializado",
                                authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                                publishDate = item.pubDate,
                                readTimeMinutes = (3..7).random(),
                                isBookmarked = false,
                                isRead = false,
                                likesCount = (15..350).random(),
                                commentsCount = 0,
                                keyHighlights = "",
                                bikeSpecs = null,
                                savedTimestamp = System.currentTimeMillis(),
                                articleUrl = item.link
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (newArticles.isNotEmpty()) {
            articleDao.insertArticles(newArticles)
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        if (articleDao.getArticleCount() == 0) {
            refreshNews()
        }
    }
}
