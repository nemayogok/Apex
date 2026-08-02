package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ArticleEntity
import com.example.ui.NewsViewModel
import com.example.ui.theme.ApexAmber
import com.example.ui.theme.ApexCard
import com.example.ui.theme.ApexCardBorder
import com.example.ui.theme.ApexCyan
import com.example.ui.theme.ApexDark
import com.example.ui.theme.ApexGrayMuted
import com.example.ui.theme.ApexRed
import com.example.ui.theme.ApexTextPrimary
import com.example.ui.theme.ApexTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApexFeedScreen(
    viewModel: NewsViewModel,
    articles: List<ArticleEntity>,
    selectedCategory: String,
    searchQuery: String,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val categories = listOf("Todos", "MotoGP", "Reviews", "Industry News")

    val heroArticle = articles.firstOrNull()
    val trendingArticles = articles.drop(1).take(3)
    val remainingArticles = if (heroArticle != null) articles.drop(4) else articles

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshFeed() },
        modifier = modifier.fillMaxSize().background(ApexDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("apex_feed_screen"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Top Header
            item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ApexRed,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "MotoGrid Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MOTOGRID",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = ApexTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = ApexRed,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "APEX",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "El Hub Definitivo del Motociclismo",
                                style = MaterialTheme.typography.bodySmall,
                                color = ApexTextSecondary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isSearchExpanded = !isSearchExpanded },
                            modifier = Modifier.testTag("toggle_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = ApexTextPrimary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refreshFeed() },
                            modifier = Modifier.testTag("refresh_feed_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar",
                                tint = ApexTextPrimary
                            )
                        }
                    }
                }

                // Expandable Search Bar
                AnimatedVisibility(visible = isSearchExpanded) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Buscar noticias, pilotos, marcas...", color = ApexTextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .testTag("search_input_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ApexRed,
                            unfocusedBorderColor = ApexCardBorder,
                            focusedContainerColor = ApexCard,
                            unfocusedContainerColor = ApexCard,
                            focusedTextColor = ApexTextPrimary,
                            unfocusedTextColor = ApexTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Category Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ApexRed,
                                selectedLabelColor = Color.White,
                                containerColor = ApexCard,
                                labelColor = ApexTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = ApexCardBorder,
                                selectedBorderColor = ApexRed
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }
            }
        }

        // Hero Feature Banner Article
        if (heroArticle != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "NOTICIA DESTACADA APEX",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ApexAmber,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onArticleClick(heroArticle.id) }
                            .testTag("hero_article_card"),
                        colors = CardDefaults.cardColors(containerColor = ApexCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = heroArticle.imageUrl,
                                contentDescription = heroArticle.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Gradient Scrim
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                ApexDark.copy(alpha = 0.5f),
                                                ApexDark.copy(alpha = 0.95f)
                                            )
                                        )
                                    )
                            )

                            // Content Overlay
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = ApexRed,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = heroArticle.category.uppercase(),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(heroArticle) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(ApexDark.copy(alpha = 0.6f), CircleShape)
                                            .testTag("hero_bookmark_button")
                                    ) {
                                        Icon(
                                            imageVector = if (heroArticle.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Guardar",
                                            tint = if (heroArticle.isBookmarked) ApexAmber else Color.White
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = heroArticle.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 22.sp
                                        ),
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = heroArticle.publishDate,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ApexTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trending Horizontal Carousel Section
        if (trendingArticles.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = ApexRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TENDENCIAS EN EL PADDOCK",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = ApexTextPrimary
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(trendingArticles) { article ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { onArticleClick(article.id) }
                                    .testTag("trending_article_${article.id}"),
                                colors = CardDefaults.cardColors(containerColor = ApexCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                    ) {
                                        AsyncImage(
                                            model = article.imageUrl,
                                            contentDescription = article.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Surface(
                                            color = ApexDark.copy(alpha = 0.7f),
                                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(top = 8.dp)
                                        ) {
                                            Text(
                                                text = article.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ApexCyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = article.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ApexTextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.ThumbUp,
                                                    contentDescription = null,
                                                    tint = ApexAmber,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${article.likesCount}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = ApexTextSecondary
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title for All Feeds
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ÚLTIMAS PUBLICACIONES",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = ApexTextPrimary
                )
                Text(
                    text = "${remainingArticles.size} artículos",
                    style = MaterialTheme.typography.bodySmall,
                    color = ApexTextSecondary
                )
            }
        }

        // Remaining Feed Articles List
        items(remainingArticles) { article ->
            ArticleListCard(
                article = article,
                onArticleClick = { onArticleClick(article.id) },
                onBookmarkToggle = { viewModel.toggleBookmark(article) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("feed_article_item_${article.id}")
            )
        }
    }
    }
}

@Composable
fun ArticleListCard(
    article: ArticleEntity,
    onArticleClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick() },
        colors = CardDefaults.cardColors(containerColor = ApexCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (article.isRead) {
                    Surface(
                        color = ApexDark.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Leído",
                                tint = ApexCyan,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Leído",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = ApexCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ApexRed
                    )
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = if (article.isBookmarked) ApexAmber else ApexTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ApexTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.publishDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = ApexTextSecondary
                    )

                }
            }
        }
    }
}
