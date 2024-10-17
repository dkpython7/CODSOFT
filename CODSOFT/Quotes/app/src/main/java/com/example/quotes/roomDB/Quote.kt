package com.example.quotes.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val isFavorite: Boolean = false
) : Serializable {

    override fun toString(): String {
        return "Quote(id=$id, text='$text', isFavorite=$isFavorite)"
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) true
        else (other is Quote) && id == other.id && text == other.text && isFavorite == other.isFavorite
    }

    override fun hashCode(): Int {
        return 31 * id + text.hashCode() + isFavorite.hashCode()
    }
}
