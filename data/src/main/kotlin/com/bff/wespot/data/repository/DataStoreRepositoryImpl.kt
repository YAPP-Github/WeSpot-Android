package com.bff.wespot.data.repository

import com.bff.wespot.data.local.source.WeSpotDataStore
import com.bff.wespot.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: WeSpotDataStore
) : DataStoreRepository {
    override suspend fun saveString(key: String, value: String) =
        dataStore.saveString(key, value)

    override fun getString(key: String): Flow<String> =
        dataStore.getString(key)

    override suspend fun saveBoolean(key: String, value: Boolean) =
        dataStore.saveBoolean(key, value)

    override fun getBoolean(key: String): Flow<Boolean> =
        dataStore.getBoolean(key)

    override suspend fun clear() {
        dataStore.clear()
    }
}