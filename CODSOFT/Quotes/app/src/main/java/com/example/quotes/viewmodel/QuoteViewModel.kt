package com.example.quotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quotes.roomDB.Quote
import com.example.quotes.roomDB.QuoteDao
import com.example.quotes.roomDB.QuoteDatabase
import com.example.quotes.roomDB.QuoteRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val quoteDao: QuoteDao = QuoteDatabase.getDatabase(application).quoteDao()
    private val repository: QuoteRepository = QuoteRepository(quoteDao)

    // LiveData for all quotes
    val quotes: LiveData<List<Quote>> = repository.getAllQuotes()

    // LiveData for favorite quotes
    private val _favoriteQuotes = MutableLiveData<List<Quote>>()
    val favoriteQuotes: LiveData<List<Quote>> = _favoriteQuotes

    init {
        loadQuotesFromJson()  // Load quotes on initialization
    }

    private fun loadQuotesFromJson() {
        viewModelScope.launch(Dispatchers.IO) {
            val jsonString = getJsonDataFromAsset("quotes.json")
            if (jsonString != null) {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter = moshi.adapter(Array<Quote>::class.java)
                val quotesArray = jsonAdapter.fromJson(jsonString)

                quotesArray?.let {
                    repository.insertQuotes(it.toList())
                }
            }
        }
    }

    // Read JSON data from the assets folder
    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            val inputStream = getApplication<Application>().assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedQuote = quote.copy(isFavorite = !quote.isFavorite)
            repository.updateQuote(updatedQuote)
            loadFavoriteQuotes() // Call to refresh favorite quotes
        }
    }

    fun loadFavoriteQuotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = repository.getAllFavorites()
            _favoriteQuotes.postValue(favorites)
        }
    }
}


