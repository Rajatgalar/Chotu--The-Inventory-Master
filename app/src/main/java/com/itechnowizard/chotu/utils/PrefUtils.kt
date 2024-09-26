package com.itechnowizard.chotu.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = "app_pref"
)

class PrefUtil (private val context: Context) {

    companion object{
        val IS_USER_LOGIN = booleanPreferencesKey(name = "isUserLogin")
    }

    suspend fun saveUserLogin(s: Boolean){
        context.dataStore.edit { preferences ->
            preferences[IS_USER_LOGIN] = s
        }
    }

    val isUserLogin : Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_USER_LOGIN] ?: false
        }

}