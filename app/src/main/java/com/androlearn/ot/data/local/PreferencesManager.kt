package com.androlearn.ot.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(
    name = "ontime_preferences"
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val IS_PRO = booleanPreferencesKey("is_pro")

    val isPro: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PRO] ?: false
    }

    suspend fun setProStatus(isPro: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PRO] = isPro
        }
    }
}