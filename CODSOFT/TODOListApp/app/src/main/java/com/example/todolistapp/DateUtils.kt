package com.example.todolistapp

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun parseDate(dateString: String): Long? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateString)
            date?.time
        } catch (e: Exception) {
            null // Return null if parsing fails
        }
    }

    fun formatDate(timestamp: Long?): String {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } else {
            "" // Return empty string if timestamp is null
        }
    }
}
