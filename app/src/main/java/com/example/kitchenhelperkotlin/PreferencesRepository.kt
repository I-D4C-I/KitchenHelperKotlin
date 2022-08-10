package com.example.kitchenhelperkotlin

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesRepository"

//Класс имплементириующий настройки
data class FilterPreferences (val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext context: Context){

    //Иницализация
    private val dataStore = context.createDataStore("toBuy_preferences")

    //Чтение текущих настроек
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                Log.e(TAG, "Ошибка в чтении персональных настройек " , exception)
                emit(emptyPreferences())
            } else{
                throw  exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    //Установка настроек
    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}