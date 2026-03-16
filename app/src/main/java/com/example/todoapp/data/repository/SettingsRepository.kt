package com.example.todoapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Crea una única instancia de DataStore para toda la app.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


// Esto es necesario para que coincida con lo que pusimos en MainActivity.
class SettingsRepository(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Lectura del valor (Flow)
    // Accedemos a context.dataStore (la variable privada de arriba)
    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    //  Escritura del valor (Suspend function)
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }
}