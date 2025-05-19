package com.example.taskify.data.themeStorage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskify.domain.model.themeModel.ThemeOption
import kotlinx.coroutines.flow.first

object ThemeDataStore {
    private val Context.dataStore by preferencesDataStore(name = "theme_prefs")
    private val THEME_KEY = stringPreferencesKey("theme_key")

    suspend fun saveTheme(context: Context, theme: ThemeOption) {
        context.dataStore.edit { prefs->
            prefs[THEME_KEY] = theme.name
        }
    }

    suspend fun getSavedTheme(context: Context): ThemeOption? {
        val prefs = context.dataStore.data.first()
        return prefs[THEME_KEY]?.let { ThemeOption.valueOf(it) }
    }

    suspend fun isThemeChosen(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[THEME_KEY] != null
    }
}