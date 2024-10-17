package com.example.quotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quotes.R
import com.example.quotes.roomDB.Quote
import com.example.quotes.viewmodel.QuoteViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var quoteViewModel: QuoteViewModel
    private lateinit var currentQuote: Quote

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val goToFavScreen = toolbar.findViewById<ImageButton>(R.id.favscreen)
        //Navigate to fav screen
        goToFavScreen.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        quoteViewModel = ViewModelProvider(this).get(QuoteViewModel::class.java)

        quoteViewModel.quotes.observe(this, Observer { quotes ->
            if (quotes.isNotEmpty()) {
                currentQuote = quotes.random() //For random quotes each time
                displayQuote(currentQuote)
            }
        })

        findViewById<ImageButton>(R.id.share_button).setOnClickListener {
            if (::currentQuote.isInitialized) {
                shareQuote(currentQuote)
            } else {
                Toast.makeText(this, "No quote to share", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.favorite_button).setOnClickListener {
            if (::currentQuote.isInitialized) {
                quoteViewModel.toggleFavorite(currentQuote)
                val message = if (currentQuote.isFavorite) {
                    "Added to favorites"
                } else {
                    "Added to favorites"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No quote to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayQuote(quote: Quote) {
        findViewById<TextView>(R.id.quote_text).text = quote.text
    }

    private fun shareQuote(quote: Quote) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, quote.text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share Quote"))
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.deep_blue)
        }
    }
}
