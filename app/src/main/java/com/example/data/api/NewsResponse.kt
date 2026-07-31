package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rss2JsonResponse(
    @Json(name = "status") val status: String,
    @Json(name = "items") val items: List<Rss2JsonItem>
)

@JsonClass(generateAdapter = true)
data class Rss2JsonItem(
    @Json(name = "title") val title: String,
    @Json(name = "pubDate") val pubDate: String,
    @Json(name = "link") val link: String,
    @Json(name = "guid") val guid: String,
    @Json(name = "thumbnail") val thumbnail: String?,
    @Json(name = "description") val description: String,
    @Json(name = "content") val content: String,
    @Json(name = "author") val author: String?,
    @Json(name = "enclosure") val enclosure: Rss2JsonEnclosure?
)

@JsonClass(generateAdapter = true)
data class Rss2JsonEnclosure(
    @Json(name = "link") val link: String?
)
