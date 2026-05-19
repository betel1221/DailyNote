package com.example.dailynote.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailynote.data.model.Quote

@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: Quote)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    fun getFavoriteQuotes(): LiveData<List<Quote>>
}
