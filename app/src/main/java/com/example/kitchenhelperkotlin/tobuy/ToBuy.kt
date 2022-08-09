package com.example.kitchenhelperkotlin.tobuy

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "toBuy_table")
@Parcelize
data class ToBuy (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val amount: Int,
    val important: Boolean = false,
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {

}
