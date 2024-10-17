package com.example.quotes.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.R
import com.example.quotes.adapter.FavoriteAdapter
import com.example.quotes.roomDB.Quote
import com.example.quotes.viewmodel.QuoteViewModel

class FavoriteActivity : AppCompatActivity() {

    private lateinit var quoteViewModel: QuoteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_favorite)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Favorite Quotes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        favoriteAdapter = FavoriteAdapter { quote -> toggleFavorite(quote) }
        recyclerView.adapter = favoriteAdapter

        quoteViewModel = ViewModelProvider(this).get(QuoteViewModel::class.java)

        // Observe only favorite quotes
        quoteViewModel.favoriteQuotes.observe(this, Observer { favoriteQuotes ->
            if (favoriteQuotes.isEmpty()) {
                Toast.makeText(this, "No favorite quotes yet", Toast.LENGTH_SHORT).show()
            } else {
                favoriteAdapter.submitList(favoriteQuotes)
            }
        })

        // Load favorite quotes from ViewModel
        quoteViewModel.loadFavoriteQuotes()
    }

    private fun toggleFavorite(quote: Quote) {
        quoteViewModel.toggleFavorite(quote)
        val message = if (quote.isFavorite) {
            "Quote removed from favorites"
        } else {
            "Quote added to favorites"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.deep_blue)
        }
    }
}
