package com.bff.wespot.data.local.source

import kotlinx.coroutines.flow.Flow

interface WeSpotDataStore {
    suspend fun saveString(key: String, value: String)
    fun getString(key: String): Flow<String>
    suspend fun saveBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Flow<Boolean>
    suspend fun clear()
}