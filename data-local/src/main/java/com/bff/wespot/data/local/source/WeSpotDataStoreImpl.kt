package com.bff.wespot.data.local.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeSpotDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WeSpotDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wespot")
    private val datastore = context.dataStore

    override suspend fun saveString(
        key: String,
        value: String
    ) {
        datastore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun saveBoolean(
        key: String,
        value: Boolean
    ) {
        datastore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override fun getString(key: String): Flow<String> =
        datastore.data.distinctUntilChanged()
            .catch {e ->
                if(e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map {
                it[stringPreferencesKey(key)] ?: ""
            }

    override fun getBoolean(key: String): Flow<Boolean> =
        datastore.data.distinctUntilChanged()
            .catch {e ->
                if(e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map {
                it[booleanPreferencesKey(key)] ?: false
            }

    override suspend fun clear() {
        datastore.edit { preferences ->
            preferences.clear()
        }
    }
}