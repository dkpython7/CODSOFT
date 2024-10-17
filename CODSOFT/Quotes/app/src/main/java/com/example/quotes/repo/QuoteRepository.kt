package com.example.quotes.roomDB

import androidx.lifecycle.LiveData

class QuoteRepository(private val quoteDao: QuoteDao) {

    // Insert a list of quotes
    suspend fun insertQuotes(quotes: List<Quote>) {
        quoteDao.insert(quotes)
    }

    // Get all quotes
    fun getAllQuotes(): LiveData<List<Quote>> {
        return quoteDao.getAllQuotes()
    }

    // Get all favorite quotes
    suspend fun getAllFavorites(): List<Quote> {
        return quoteDao.getAllFavorites()
    }

    // Update a quote
    suspend fun updateQuote(quote: Quote) {
        quoteDao.update(quote)
    }
}
