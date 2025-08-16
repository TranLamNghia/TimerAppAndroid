package com.example.tlnapp_timemanagement.network

import android.util.Log
import com.example.tlnapp_timemanagement.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException


object AirtableClient {
    private const val BASE_ID = BuildConfig.AIRTABLE_BASE_ID
    private const val TABLE_NAME = "tbliaN3TonmzeV61K"
    private const val API_KEY = BuildConfig.AIRTABLE_PAT

    private val client by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }


    fun sendFeedback(jsonBody: String, callback: (success: Boolean, message: String?) -> Unit) {
        Log.d("AirtableClient", "PAT=${BuildConfig.AIRTABLE_PAT}  BASE=${BuildConfig.AIRTABLE_BASE_ID}")
        val url = "https://api.airtable.com/v0/$BASE_ID/$TABLE_NAME"

        val body = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", API_KEY)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, "HTTP ${it.code}: ${it.message}")
                        Log.d("AirtableClient", "HTTP ${it.code}: ${it.message} : ${it.body}")
                    }
                }
            }
        })
    }
}
