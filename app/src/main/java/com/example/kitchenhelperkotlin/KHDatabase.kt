package com.example.kitchenhelperkotlin

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kitchenhelperkotlin.dependencyinjection.ApplicationScope
import com.example.kitchenhelperkotlin.tobuy.ToBuy
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [ToBuy::class], version = 1)
abstract class KHDatabase : RoomDatabase(){

    abstract fun toBuyDao() : ToBuyDao

    class Callback @Inject constructor(
        private val database: Provider<KHDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

           val tobuydao = database.get().toBuyDao()
            applicationScope.launch {
                tobuydao.insert(ToBuy("Title 1", 23))
                tobuydao.insert(ToBuy("Title 2", 23, important = true))
                tobuydao.insert(ToBuy("Title 3", 23, important =false, completed = true))
                tobuydao.insert(ToBuy("Title 4", 23, important =true, completed = true))
            }

        }
    }
}