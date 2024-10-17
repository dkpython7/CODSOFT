package com.example.quotes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.R
import com.example.quotes.roomDB.Quote

class FavoriteAdapter(
    private val onFavoriteClick: (Quote) -> Unit
) : ListAdapter<Quote, FavoriteAdapter.FavoriteViewHolder>(QuoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_quote, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val quote = getItem(position)
        holder.bind(quote)
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quoteText: TextView = itemView.findViewById(R.id.quote_text)
        private val unfavoriteButton: ImageButton = itemView.findViewById(R.id.unfavorite_button)
        private val shareButton: ImageButton = itemView.findViewById(R.id.share_button)

        fun bind(quote: Quote) {
            quoteText.text = quote.text
            unfavoriteButton.setOnClickListener { onFavoriteClick(quote) }
            shareButton.setOnClickListener { shareQuote(quote) }
        }

        private fun shareQuote(quote: Quote) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, quote.text)
                type = "text/plain"
            }
            itemView.context.startActivity(Intent.createChooser(sendIntent, "Share Quote"))
        }
    }
}

class QuoteDiffCallback : DiffUtil.ItemCallback<Quote>() {
    override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Quote, newItem: Quote): Boolean {
        return oldItem == newItem
    }
}
