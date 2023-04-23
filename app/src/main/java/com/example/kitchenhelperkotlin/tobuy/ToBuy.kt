package com.example.kitchenhelperkotlin.tobuy

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "toBuy_table")
@Parcelize
data class ToBuy (
    val title: String,
    val amount: Int  = 0,
    val important: Boolean = false,
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable
