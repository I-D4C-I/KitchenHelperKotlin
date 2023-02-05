package com.example.kitchenhelperkotlin.products

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kitchenhelperkotlin.products.UnitOfMeasure.Measure
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(tableName = "product_table")
data class Product(
    val title: String,
    val amount: Int,
    val date: LocalDate = LocalDate.now(),
    val measure: Measure = Measure.kg,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable