package com.example.kitchenhelperkotlin.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//Политика миграции при изменинии таблицы баз данных

//Название и с какой версии на какую переходишь
val MIGRATION_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        //Необходимо написать какая конкретно таблица изменилась и что произошло.
        //Затем поменять версию в KHDatabase и зарегистрировать миграции в AppModule
        database.execSQL("ALTER TABLE product_table ADD COLUMN some_name INTEGER DEFAULT 0 NOT NULL")
    }
}

