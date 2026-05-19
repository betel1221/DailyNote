package com.example.dailynote.data.remote

import com.example.dailynote.data.model.Quote
import retrofit2.Response
import retrofit2.http.GET

interface QuoteApi {
    @GET("random")
    suspend fun getRandomQuote(): Response<Quote>
}
