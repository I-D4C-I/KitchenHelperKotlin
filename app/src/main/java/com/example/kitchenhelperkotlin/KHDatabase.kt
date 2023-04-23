package com.example.kitchenhelperkotlin

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kitchenhelperkotlin.dependencyinjection.ApplicationScope
import com.example.kitchenhelperkotlin.products.Product
import com.example.kitchenhelperkotlin.products.ProductDao
import com.example.kitchenhelperkotlin.products.UnitOfMeasure.Measure
import com.example.kitchenhelperkotlin.recipe.Recipe
import com.example.kitchenhelperkotlin.recipe.RecipeDao
import com.example.kitchenhelperkotlin.tobuy.ToBuy
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import com.example.kitchenhelperkotlin.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [ToBuy::class, Product::class, Recipe::class], version = 1)
@TypeConverters(Converters::class)
abstract class KHDatabase : RoomDatabase() {

    abstract fun toBuyDao(): ToBuyDao
    abstract fun productDao(): ProductDao
    abstract fun recipeDao(): RecipeDao

    class Callback @Inject constructor(
        private val database: Provider<KHDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val toBuyDao = database.get().toBuyDao()
            val productDao = database.get().productDao()
            val recipeDao = database.get().recipeDao()
            applicationScope.launch {
                toBuyDao.insert(ToBuy(title = "Bread", amount = 3))
                toBuyDao.insert(ToBuy(title = "Milk", amount = 2, important = true))
                toBuyDao.insert(
                    ToBuy(
                        title = "Apples",
                        amount = 13,
                        important = false,
                        completed = true
                    )
                )
                toBuyDao.insert(
                    ToBuy(
                        title = "Sauce",
                        amount = 23,
                        important = true,
                        completed = true
                    )
                )
                productDao.insert(Product(title = "Bread", amount = 5, measure = Measure.pack))
                productDao.insert(
                    Product(
                        title = "Milk",
                        amount = 15,
                        date = LocalDate.now().plusWeeks(1), measure = Measure.l
                    )
                )
                productDao.insert(
                    Product(
                        title = "Meat",
                        amount = 15,
                        date = LocalDate.now().plusMonths(1),
                        measure = Measure.kg
                    )
                )
                recipeDao.insert(
                    Recipe(
                        "Example recipe 1",
                        false,
                        "Note Example 1",
                        "Description Example 1"
                    )
                )
                recipeDao.insert(
                    Recipe(
                        "Example recipe 2",
                        true,
                        "Note Example 2",
                        "Description Example 2"
                    )
                )
                recipeDao.insert(
                    Recipe(
                        "Example recipe 3",
                        false,
                        description = "Description Example 3"
                    )
                )
            }
        }
    }
}