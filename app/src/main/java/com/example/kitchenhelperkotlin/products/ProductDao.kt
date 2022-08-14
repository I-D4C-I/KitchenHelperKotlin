package com.example.kitchenhelperkotlin.products

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("select * from product_table where title like '%' || :searchQuery || '%' order by id desc")
    fun getProducts(searchQuery : String) : Flow<List<Product>>

    //TODO: Реализвовать очистку в настройках
    @Query("DELETE FROM product_table")
    suspend fun deleteAllProducts()

}