package com.example.kitchenhelperkotlin.products

import androidx.room.*
import com.example.kitchenhelperkotlin.SortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)
    @Update
    suspend fun update(product: Product)
    @Delete
    suspend fun delete(product: Product)
    fun getProducts(query: String, sortOrder: SortOrder): Flow<List<Product>> =
        when (sortOrder) {
            SortOrder.DEFAULT -> getProductsSortedByDefault(query)
            SortOrder.BY_NAME -> getProductsSortedByName(query)
            SortOrder.BY_DATE -> getProductsSortedByDate(query)
        }
    @Query("select * from product_table where title like '%' || :searchQuery || '%' order by id desc")
    fun getProductsSortedByDefault(searchQuery: String): Flow<List<Product>>

    @Query("select * from product_table where title like '%' || :searchQuery || '%' order by title")
    fun getProductsSortedByName(searchQuery: String): Flow<List<Product>>

    @Query("select * from product_table where title like '%' || :searchQuery || '%' order by date")
    fun getProductsSortedByDate(searchQuery: String): Flow<List<Product>>

    @Query("DELETE FROM product_table")
    suspend fun deleteAllProducts()

}