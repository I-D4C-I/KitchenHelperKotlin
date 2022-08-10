package com.example.kitchenhelperkotlin.tobuy

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.selects.select

@Dao
interface ToBuyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(toBuy: ToBuy)

    @Update
    suspend fun update(toBuy: ToBuy)

    @Delete
    suspend fun delete(toBuy: ToBuy)

    @Query("select * from toBuy_table")
    fun getToBuy():Flow<List<ToBuy>>
}