package com.example.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v1/api.json")
    suspend fun getRssFeed(
        @Query("rss_url") rssUrl: String = "https://es.motorsport.com/rss/motogp/news/"
    ): Rss2JsonResponse
}
