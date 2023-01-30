package com.example.kitchenhelperkotlin.recipe

import androidx.room.*
import com.example.kitchenhelperkotlin.SortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("select * from recipe_table where title like '%' || :searchQuery || '%' order by favorite desc, id desc")
    fun getRecipesSortedByDefault(searchQuery: String): Flow<List<Recipe>>

    @Query("select * from recipe_table where title like '%' || :searchQuery || '%' order by favorite desc, title")
    fun getRecipesSortedByName(searchQuery: String): Flow<List<Recipe>>

    fun getRecipes(searchQuery: String, sortOrder: SortOrder): Flow<List<Recipe>> =
        when (sortOrder) {
            SortOrder.DEFAULT -> getRecipesSortedByDefault(searchQuery)
            SortOrder.BY_NAME -> getRecipesSortedByName(searchQuery)
            SortOrder.BY_DATE -> getRecipesSortedByName(searchQuery)//заглушка, не должна работать
        }

    @Query("delete from recipe_table")
    suspend fun deleteAllRecipe()
}