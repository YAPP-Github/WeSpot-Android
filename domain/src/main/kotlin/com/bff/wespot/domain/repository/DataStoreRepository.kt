package com.bff.wespot.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveString(key: String, value: String)
    fun getString(key: String): Flow<String>
    suspend fun saveBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Flow<Boolean>
    suspend fun clear()
}
