package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ApexFeedScreen
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.MiBibliotecaScreen
import com.example.ui.theme.ApexAmber
import com.example.ui.theme.ApexCard
import com.example.ui.theme.ApexCardBorder
import com.example.ui.theme.ApexDark
import com.example.ui.theme.ApexRed
import com.example.ui.theme.ApexTextPrimary
import com.example.ui.theme.ApexTextSecondary

enum class ScreenTab {
    FEED, LIBRARY, DETAIL
}

@Composable
fun MotoGridApp(
    viewModel: NewsViewModel
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(ScreenTab.FEED) }

    val filteredArticles by viewModel.filteredArticles.collectAsStateWithLifecycle()
    val bookmarkedArticles by viewModel.bookmarkedArticles.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val libraryFilter by viewModel.libraryFilter.collectAsStateWithLifecycle()
    val selectedArticle by viewModel.selectedArticle.collectAsStateWithLifecycle()
    val selectedArticleComments by viewModel.selectedArticleComments.collectAsStateWithLifecycle()
    val readerFontSize by viewModel.readerFontSize.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Display user messages via Toast / Snackbar
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("motogrid_app_root"),
        containerColor = ApexDark,
        bottomBar = {
            if (currentTab != ScreenTab.DETAIL) {
                NavigationBar(
                    containerColor = ApexCard,
                    contentColor = ApexTextPrimary,
                    tonalElevation = androidx.compose.ui.unit.Dp(8f),
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.FEED,
                        onClick = { currentTab = ScreenTab.FEED },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.FEED) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Apex Feed"
                            )
                        },
                        label = {
                            Text(
                                text = "Apex Feed",
                                fontWeight = if (currentTab == ScreenTab.FEED) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ApexRed,
                            selectedTextColor = ApexRed,
                            indicatorColor = ApexRed.copy(alpha = 0.2f),
                            unselectedIconColor = ApexTextSecondary,
                            unselectedTextColor = ApexTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_feed")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.LIBRARY,
                        onClick = { currentTab = ScreenTab.LIBRARY },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (bookmarkedArticles.isNotEmpty()) {
                                        Badge(containerColor = ApexAmber, contentColor = ApexDark) {
                                            Text("${bookmarkedArticles.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.LIBRARY) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Mi Biblioteca"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Mi Biblioteca",
                                fontWeight = if (currentTab == ScreenTab.LIBRARY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ApexAmber,
                            selectedTextColor = ApexAmber,
                            indicatorColor = ApexAmber.copy(alpha = 0.2f),
                            unselectedIconColor = ApexTextSecondary,
                            unselectedTextColor = ApexTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_library")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetTab ->
                when (targetTab) {
                    ScreenTab.FEED -> {
                        ApexFeedScreen(
                            viewModel = viewModel,
                            articles = filteredArticles,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            onArticleClick = { articleId ->
                                viewModel.selectArticle(articleId)
                                currentTab = ScreenTab.DETAIL
                            }
                        )
                    }
                    ScreenTab.LIBRARY -> {
                        MiBibliotecaScreen(
                            viewModel = viewModel,
                            bookmarkedArticles = bookmarkedArticles,
                            libraryFilter = libraryFilter,
                            searchQuery = searchQuery,
                            onArticleClick = { articleId ->
                                viewModel.selectArticle(articleId)
                                currentTab = ScreenTab.DETAIL
                            },
                            onExploreClick = { currentTab = ScreenTab.FEED }
                        )
                    }
                    ScreenTab.DETAIL -> {
                        ArticleDetailScreen(
                            viewModel = viewModel,
                            article = selectedArticle,
                            comments = selectedArticleComments,
                            readerFontSize = readerFontSize,
                            onBackClick = { currentTab = ScreenTab.FEED }
                        )
                    }
                }
            }
        }
    }
}
