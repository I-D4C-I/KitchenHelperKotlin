package com.example.kitchenhelperkotlin

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kitchenhelperkotlin.dependencyinjection.ApplicationScope
import com.example.kitchenhelperkotlin.products.Product
import com.example.kitchenhelperkotlin.products.ProductDao
import com.example.kitchenhelperkotlin.tobuy.ToBuy
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import com.example.kitchenhelperkotlin.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [ToBuy::class, Product::class], version = 1)
@TypeConverters(Converters::class)
abstract class KHDatabase : RoomDatabase() {

    abstract fun toBuyDao(): ToBuyDao
    abstract fun ProductDao(): ProductDao

    class Callback @Inject constructor(
        private val database: Provider<KHDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val toBuyDao = database.get().toBuyDao()
            val productDao = database.get().ProductDao()
            applicationScope.launch {
                toBuyDao.insert(ToBuy(title = "Title 1", amount = 3))
                toBuyDao.insert(ToBuy(title = "Title 2", amount = 2, important = true))
                toBuyDao.insert(
                    ToBuy(
                        title = "Title 3",
                        amount = 13,
                        important = false,
                        completed = true
                    )
                )
                toBuyDao.insert(
                    ToBuy(
                        title = "Title 4",
                        amount = 23,
                        important = true,
                        completed = true
                    )
                )
                productDao.insert(Product(title = "Title 1", amount = 5))
                productDao.insert(
                    Product(
                        title = "Title 2",
                        amount = 15,
                        LocalDate.now().plusWeeks(1)
                    )
                )
                productDao.insert(
                    Product(
                        title = "Title 2",
                        amount = 15,
                        LocalDate.now().plusMonths(1)
                    )
                )
            }
        }
    }
}