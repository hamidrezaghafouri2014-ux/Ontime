
package com.androlearn.ot.data.billing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val IS_PRO = booleanPreferencesKey("is_pro")
    }

    val isPro: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_PRO] ?: false
    }

    suspend fun setPro(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PRO] = enabled
        }
    }
}
