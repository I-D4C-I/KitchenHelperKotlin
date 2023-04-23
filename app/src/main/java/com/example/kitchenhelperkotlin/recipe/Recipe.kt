package com.example.kitchenhelperkotlin.recipe

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "recipe_table")
@Parcelize
data class Recipe(
    val title: String,
    val favorite: Boolean = false,
    val note: String = "",
    val description: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable