package com.example.quotes.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quote: List<Quote>)

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    suspend fun getAllFavorites(): List<Quote>

    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): LiveData<List<Quote>>

    @Update
    suspend fun update(quote: Quote)

    @Delete
    suspend fun delete(quote: Quote)
}