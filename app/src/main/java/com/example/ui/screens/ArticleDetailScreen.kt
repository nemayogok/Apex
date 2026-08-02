package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.DisposableEffect
import java.util.Locale
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ArticleEntity
import com.example.data.CommentEntity
import com.example.ui.NewsViewModel
import com.example.ui.theme.ApexAmber
import com.example.ui.theme.ApexCard
import com.example.ui.theme.ApexCardBorder
import com.example.ui.theme.ApexCyan
import com.example.ui.theme.ApexDark
import com.example.ui.theme.ApexRed
import com.example.ui.theme.ApexTextPrimary
import com.example.ui.theme.ApexTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    viewModel: NewsViewModel,
    article: ArticleEntity?,
    comments: List<CommentEntity>,
    readerFontSize: Float,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFontControlsOpen by remember { mutableStateOf(false) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var commentAuthorText by remember { mutableStateOf("") }
    var commentBodyText by remember { mutableStateOf("") }

    DisposableEffect(context, article?.id) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = ttsEngine?.setLanguage(Locale("es", "ES"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsEngine?.setLanguage(Locale.getDefault())
                }
            }
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isAudioPlaying = true
            }
            override fun onDone(utteranceId: String?) {
                isAudioPlaying = false
            }
            override fun onError(utteranceId: String?) {
                isAudioPlaying = false
            }
        })
        ttsEngine = tts

        onDispose {
            tts.stop()
            tts.shutdown()
            isAudioPlaying = false
        }
    }

    if (article == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ApexDark),
            contentAlignment = Alignment.Center
        ) {
            Text("Cargando noticia Apex...", color = ApexTextSecondary)
        }
        return
    }

    val keyHighlightsList = article.keyHighlights.split("|").filter { it.isNotBlank() }
    val bikeSpecsList = article.bikeSpecs?.split("|")?.filter { it.isNotBlank() } ?: emptyList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ApexDark)
            .testTag("article_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Image Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = article.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ApexDark.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        ApexDark
                                    )
                                )
                            )
                    )

                    // Top Action Bar Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                ttsEngine?.stop()
                                isAudioPlaying = false
                                onBackClick()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(ApexDark.copy(alpha = 0.7f), CircleShape)
                                .testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    val tts = ttsEngine
                                    if (tts != null) {
                                        if (isAudioPlaying) {
                                            tts.stop()
                                            isAudioPlaying = false
                                        } else {
                                            val fullSpeechText = "${article.title}. ${article.subtitle}. ${article.content}"
                                            val params = android.os.Bundle().apply {
                                                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ApexUtterance")
                                            }
                                            tts.speak(fullSpeechText, TextToSpeech.QUEUE_FLUSH, params, "ApexUtterance")
                                            isAudioPlaying = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(ApexDark.copy(alpha = 0.7f), CircleShape)
                                    .testTag("audio_player_button")
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Escuchar audio",
                                    tint = if (isAudioPlaying) ApexRed else ApexCyan
                                )
                            }

                            IconButton(
                                onClick = { isFontControlsOpen = !isFontControlsOpen },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(ApexDark.copy(alpha = 0.7f), CircleShape)
                                    .testTag("font_size_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = "Tamaño de letra",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleBookmark(article) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(ApexDark.copy(alpha = 0.7f), CircleShape)
                                    .testTag("detail_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Guardar",
                                    tint = if (article.isBookmarked) ApexAmber else Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "${article.title}\n${article.articleUrl.ifBlank { "https://es.motorsport.com/motogp/news/" }}")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Compartir Noticia Apex")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(ApexDark.copy(alpha = 0.7f), CircleShape)
                                    .testTag("detail_share_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Compartir",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Audio Player Bar (when active)
            item {
                AnimatedVisibility(visible = isAudioPlaying) {
                    Surface(
                        color = ApexRed.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ApexRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = ApexRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reproduciendo Narración Apex Voice IA",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Voz sintética HD • Modo Audio Conducción",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ApexTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Font Controls Bar
            item {
                AnimatedVisibility(visible = isFontControlsOpen) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = ApexCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "A-",
                                style = MaterialTheme.typography.titleMedium,
                                color = ApexTextSecondary,
                                modifier = Modifier.clickable { viewModel.setFontSize(readerFontSize - 2f) }
                            )
                            Slider(
                                value = readerFontSize,
                                onValueChange = { viewModel.setFontSize(it) },
                                valueRange = 12f..24f,
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = ApexRed,
                                    activeTrackColor = ApexRed,
                                    inactiveTrackColor = ApexCardBorder
                                )
                            )
                            Text(
                                text = "A+",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.clickable { viewModel.setFontSize(readerFontSize + 2f) }
                            )
                        }
                    }
                }
            }

            // Category & Metadata
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = ApexRed,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable {
                                viewModel.selectCategory(article.category)
                                onBackClick()
                            }
                        ) {
                            Text(
                                text = article.category.uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "• ${article.publishDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ApexTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            lineHeight = 30.sp
                        ),
                        color = ApexTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = article.subtitle,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            fontStyle = FontStyle.Italic
                        ),
                        color = ApexTextSecondary
                    )


                }
            }

            // Technical Specs Box (if available)
            if (bikeSpecsList.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = ApexCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ApexCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ESPECIFICACIONES TÉCNICAS OFICIALES",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ApexCyan
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            bikeSpecsList.forEach { spec ->
                                val parts = spec.split(":")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = parts.firstOrNull() ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = ApexTextSecondary
                                    )
                                    Text(
                                        text = parts.getOrNull(1) ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = ApexTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Article Main Body Text
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    val paragraphs = article.content.split("\n\n")
                    paragraphs.forEach { para ->
                        Text(
                            text = para,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = readerFontSize.sp,
                                lineHeight = (readerFontSize * 1.55f).sp
                            ),
                            color = ApexTextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))



                    // Like & Reaction Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.likeArticle(article.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = ApexRed),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("like_article_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${article.likesCount} Reacciones",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${comments.size} Comentarios",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ApexTextSecondary
                        )
                    }
                }
            }

            // Comment Section Divider
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    color = ApexCardBorder
                )
            }

            // Comment Box & Feed
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = null,
                            tint = ApexCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEBATE Y COMENTARIOS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ApexTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ApexCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = commentAuthorText,
                                onValueChange = { commentAuthorText = it },
                                placeholder = { Text("Tu Apodo / Nickname (ej. Motero_Apex)", color = ApexTextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("comment_author_input"),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ApexCyan,
                                    unfocusedBorderColor = ApexCardBorder,
                                    focusedTextColor = ApexTextPrimary,
                                    unfocusedTextColor = ApexTextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = commentBodyText,
                                onValueChange = { commentBodyText = it },
                                placeholder = { Text("¿Qué opinas sobre este análisis?", color = ApexTextSecondary) },
                                modifier = Modifier.fillMaxWidth().testTag("comment_body_input"),
                                minLines = 2,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ApexCyan,
                                    unfocusedBorderColor = ApexCardBorder,
                                    focusedTextColor = ApexTextPrimary,
                                    unfocusedTextColor = ApexTextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.postComment(article.id, commentAuthorText, commentBodyText)
                                        commentBodyText = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ApexCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("submit_comment_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        tint = ApexDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Publicar Comentario",
                                        color = ApexDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Existing Comments List
            items(comments) { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = ApexCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ApexCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = comment.authorAvatarUrl,
                                    contentDescription = comment.authorName,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = comment.authorName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ApexTextPrimary
                                )
                            }
                            Text(
                                text = comment.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = ApexTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = comment.commentText,
                            style = MaterialTheme.typography.bodySmall,
                            color = ApexTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.likeComment(comment.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Me gusta",
                                    tint = ApexAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${comment.likesCount}",
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
