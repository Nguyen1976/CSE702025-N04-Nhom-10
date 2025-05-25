package com.example.taskify.data.themeStorage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskify.domain.model.themeModel.ThemeOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

object ThemeDataStore {
    private val THEME_KEY = stringPreferencesKey("theme_key")

    suspend fun saveTheme(context: Context, theme: ThemeOption) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }

    fun getSavedTheme(context: Context): Flow<ThemeOption?> {
        return context.dataStore.data.map { prefs ->
            prefs[THEME_KEY]?.let { themeName ->
                try {
                    ThemeOption.valueOf(themeName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    suspend fun isThemeChosen(context: Context): Boolean {
        return context.dataStore.data.first()[THEME_KEY] != null
    }
}