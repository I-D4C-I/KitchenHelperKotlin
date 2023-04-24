package com.example.kitchenhelperkotlin.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, formatter)
        }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun listToString(list: ArrayList<String>): String {
        return list.joinToString(";")
    }

    @TypeConverter
    fun listFromString(data: String): ArrayList<String> {
        return ArrayList(data.split(";"))
    }

}
