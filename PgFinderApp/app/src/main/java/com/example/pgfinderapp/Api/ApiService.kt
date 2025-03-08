package com.example.pgfinderapp.Api

import com.example.pgfinderapp.dataclasses.User

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    fun registerUser(@Body user: User): Call<Void>
}