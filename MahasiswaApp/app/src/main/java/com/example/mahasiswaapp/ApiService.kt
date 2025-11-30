package com.example.mahasiswaapp

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ApiService(val context: Context) {
    private val BASE_URL = "https://68ff8dfbe02b16d1753e765d.mockapi.io/Mahasiswa"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getAllMahasiswa(): JsonArray {
        val response: HttpResponse = client.get(BASE_URL)
        val body = response.bodyAsText()
        return JsonParser.parseString(body).asJsonArray
    }

    suspend fun getMahasiswaById(id: String): JsonObject {
        val response: HttpResponse = client.get("$BASE_URL/$id")
        val body = response.bodyAsText()
        return JsonParser.parseString(body).asJsonObject
    }

    suspend fun createMahasiswa(data: JsonObject): JsonObject {
        val response: HttpResponse = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            setBody(data.toString())
        }
        val body = response.bodyAsText()
        return JsonParser.parseString(body).asJsonObject
    }

    suspend fun updateMahasiswa(id: String, data: JsonObject): JsonObject {
        val response: HttpResponse = client.put("$BASE_URL/$id") {
            contentType(ContentType.Application.Json)
            setBody(data.toString())
        }
        val body = response.bodyAsText()
        return JsonParser.parseString(body).asJsonObject
    }

    suspend fun deleteMahasiswa(id: String): JsonObject {
        val response: HttpResponse = client.delete("$BASE_URL/$id")
        val body = response.bodyAsText()
        return JsonParser.parseString(body).asJsonObject
    }

    fun close() {
        client.close()
    }
}
