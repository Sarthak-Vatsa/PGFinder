package com.example.pgfinderapp.Api

import com.example.pgfinderapp.dataclasses.LoginResponse
import com.example.pgfinderapp.dataclasses.LogoutResponse
import com.example.pgfinderapp.dataclasses.PG
import com.example.pgfinderapp.dataclasses.User

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    fun registerUser(@Body user: User): Call<Void>

    @POST("auth/login")
    fun loginUser(@Body user: User): Call<LoginResponse>

    @GET("auth/logout")
    fun logoutUser(): Call<LogoutResponse>

    @GET("user/displayPGs")
    fun getBookedPGs(): Call<List<PG>>
}