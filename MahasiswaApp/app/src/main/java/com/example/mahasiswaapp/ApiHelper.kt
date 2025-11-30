package com.example.mahasiswaapp

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class ApiHelper(private val apiService: ApiService) {

    interface Callback<T> {
        fun onSuccess(result: T)
        fun onError(e: Exception)
    }

    fun getAllMahasiswa(callback: Callback<JsonArray>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = apiService.getAllMahasiswa()
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    fun getMahasiswaById(id: String, callback: Callback<JsonObject>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = apiService.getMahasiswaById(id)
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    fun createMahasiswa(data: JsonObject, callback: Callback<JsonObject>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = apiService.createMahasiswa(data)
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    fun updateMahasiswa(id: String, data: JsonObject, callback: Callback<JsonObject>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = apiService.updateMahasiswa(id, data)
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    fun deleteMahasiswa(id: String, callback: Callback<JsonObject>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = apiService.deleteMahasiswa(id)
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }
}
