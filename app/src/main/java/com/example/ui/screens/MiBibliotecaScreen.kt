package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.ApexRed
import com.example.ui.theme.ApexTextPrimary
import com.example.ui.theme.ApexTextSecondary

@Composable
fun MiBibliotecaScreen(
    viewModel: NewsViewModel,
    bookmarkedArticles: List<ArticleEntity>,
    libraryFilter: String,
    searchQuery: String,
    onArticleClick: (String) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("Todos", "Leídos", "Sin Leer")
    val readCount = bookmarkedArticles.count { it.isRead }
    val totalCount = bookmarkedArticles.size
    val readProgress = if (totalCount > 0) readCount.toFloat() / totalCount.toFloat() else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ApexDark)
            .testTag("mi_biblioteca_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Library Header Title & Stats
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = ApexAmber.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Biblioteca",
                                    tint = ApexAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MI BIBLIOTECA",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = ApexTextPrimary
                            )
                            Text(
                                text = "Artículos guardados para lectura offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = ApexTextSecondary
                            )
                        }
                    }

                    Surface(
                        color = ApexAmber,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$totalCount Guardados",
                            color = ApexDark,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Card
                if (totalCount > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ApexCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progreso de Lectura",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ApexTextPrimary
                                )
                                Text(
                                    text = "$readCount de $totalCount leídos (${(readProgress * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ApexCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { readProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ApexCyan,
                                trackColor = ApexCardBorder,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = libraryFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectLibraryFilter(filter) },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ApexAmber,
                                selectedLabelColor = ApexDark,
                                containerColor = ApexCard,
                                labelColor = ApexTextSecondary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("library_filter_$filter")
                        )
                    }
                }
            }
        }

        // Empty Library View
        if (bookmarkedArticles.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = ApexCard,
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                contentDescription = null,
                                tint = ApexTextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu Biblioteca está vacía",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ApexTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Guarda tus reportajes, pruebas de motos y noticias favoritas con el icono de marcador para leerlas sin conexión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ApexTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ApexRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("explore_news_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explorar Noticias Apex",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Bookmarked List
            items(bookmarkedArticles) { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onArticleClick(article.id) }
                        .testTag("library_item_${article.id}"),
                    colors = CardDefaults.cardColors(containerColor = ApexCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = article.imageUrl,
                            contentDescription = article.title,
                            modifier = Modifier
                                .size(85.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

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
                                    color = ApexAmber
                                )

                                IconButton(
                                    onClick = { viewModel.toggleBookmark(article) },
                                    modifier = Modifier.size(28.dp).testTag("delete_bookmark_${article.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkRemove,
                                        contentDescription = "Quitar de Biblioteca",
                                        tint = ApexRed,
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.toggleReadStatus(article) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Estado de lectura",
                                        tint = if (article.isRead) ApexCyan else ApexTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (article.isRead) "Leído" else "Marcar leído",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (article.isRead) ApexCyan else ApexTextSecondary
                                    )
                                }

                                Text(
                                    text = "${article.readTimeMinutes} min lect.",
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
