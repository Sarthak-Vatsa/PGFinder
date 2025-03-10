package com.example.pgfinderapp.Api

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://localhost:3001/api/v1" // localhost -> refers to the emulator itself

    // Inner class for cookie handling
    private class SessionCookieJar : CookieJar {
        private var cookies: List<Cookie> = emptyList()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            this.cookies = cookies
            Log.d("Cookie", "Saved cookies: $cookies")
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            Log.d("Cookie", "Loading cookies: $cookies")
            return cookies
        }
    }

    val instance: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .cookieJar(SessionCookieJar())
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Your base URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
