package com.example.kitchenhelperkotlin.tobuy

import androidx.room.*
import com.example.kitchenhelperkotlin.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ToBuyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(toBuy: ToBuy)

    @Update
    suspend fun update(toBuy: ToBuy)

    @Delete
    suspend fun delete(toBuy: ToBuy)

    @Query("select * from toBuy_table where (completed != :hideCompleted or completed = 0) and title like '%' || :searchQuery || '%' order by important desc, title")
    fun getToBuySortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<ToBuy>>

    @Query("select * from toBuy_table where (completed != :hideCompleted or completed = 0) and title like '%' || :searchQuery || '%' order by important desc, timestamp")
    fun getToBuySortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<ToBuy>>

    @Query("delete from toBuy_table where completed = 1")
    suspend fun deleteCompletedToBuy()

    fun getToBuy(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<ToBuy>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getToBuySortedByDate(query, hideCompleted)
            SortOrder.BY_NAME -> getToBuySortedByName(query, hideCompleted)
            SortOrder.DEFAULT -> getToBuySortedByName(
                query,
                hideCompleted
            ) //заглушка, не должна работать
        }

    @Query("delete from toBuy_table")
    suspend fun deleteAllToBuy()
}