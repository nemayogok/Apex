package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ArticleEntity
import com.example.data.CommentEntity
import com.example.data.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _libraryFilter = MutableStateFlow("Todos") // "Todos", "Leídos", "Sin Leer"
    val libraryFilter: StateFlow<String> = _libraryFilter.asStateFlow()

    private val _selectedArticleId = MutableStateFlow<String?>("motogp-2026-sepang")
    val selectedArticleId: StateFlow<String?> = _selectedArticleId.asStateFlow()

    private val _readerFontSize = MutableStateFlow(16f) // Sp
    val readerFontSize: StateFlow<Float> = _readerFontSize.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val filteredArticles: StateFlow<List<ArticleEntity>> = combine(
        repository.allArticles,
        _selectedCategory,
        _searchQuery
    ) { articles, category, query ->
        articles.filter { article ->
            val matchesCategory = (category == "Todos") || (article.category.lowercase() == category.lowercase())
            val matchesQuery = query.isBlank() ||
                    article.title.contains(query, ignoreCase = true) ||
                    article.subtitle.contains(query, ignoreCase = true) ||
                    article.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookmarkedArticles: StateFlow<List<ArticleEntity>> = combine(
        repository.bookmarkedArticles,
        _libraryFilter,
        _searchQuery
    ) { bookmarks, filter, query ->
        bookmarks.filter { article ->
            val matchesReadState = when (filter) {
                "Leídos" -> article.isRead
                "Sin Leer" -> !article.isRead
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    article.title.contains(query, ignoreCase = true) ||
                    article.category.contains(query, ignoreCase = true)
            matchesReadState && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedArticle: StateFlow<ArticleEntity?> = _selectedArticleId.flatMapLatest { id ->
        if (id != null) repository.getArticleById(id) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedArticleComments: StateFlow<List<CommentEntity>> = _selectedArticleId.flatMapLatest { id ->
        if (id != null) repository.getCommentsForArticle(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectLibraryFilter(filter: String) {
        _libraryFilter.value = filter
    }

    fun selectArticle(articleId: String) {
        _selectedArticleId.value = articleId
        viewModelScope.launch {
            repository.markAsRead(articleId)
        }
    }

    fun toggleBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            val nextState = !article.isBookmarked
            repository.toggleBookmark(article.id, article.isBookmarked)
            _userMessage.value = if (nextState) "Guardado en Mi Biblioteca" else "Eliminado de Mi Biblioteca"
        }
    }

    fun toggleReadStatus(article: ArticleEntity) {
        viewModelScope.launch {
            repository.markAsRead(article.id)
        }
    }

    fun likeArticle(articleId: String) {
        viewModelScope.launch {
            repository.addLike(articleId)
        }
    }

    fun postComment(articleId: String, authorName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(articleId, authorName.ifBlank { "Piloto MotoGrid" }, text)
            _userMessage.value = "¡Comentario publicado!"
        }
    }

    fun likeComment(commentId: Int) {
        viewModelScope.launch {
            repository.likeComment(commentId)
        }
    }

    fun setFontSize(size: Float) {
        _readerFontSize.value = size.coerceIn(12f, 24f)
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
            _userMessage.value = "Noticias actualizadas"
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}

class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
