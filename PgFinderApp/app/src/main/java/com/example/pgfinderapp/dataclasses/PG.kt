package com.example.pgfinderapp.dataclasses

import android.os.Message
import com.google.gson.annotations.SerializedName

data class PG(
    @SerializedName("_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("availability")
    val availability: Boolean,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("__v")
    val version: Int,

    @SerializedName("reviews")
    val reviews: List<Any> = emptyList(),

    @SerializedName("id")
    val pgId: String
)