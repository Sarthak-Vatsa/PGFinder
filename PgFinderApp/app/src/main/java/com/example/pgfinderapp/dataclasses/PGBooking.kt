package com.example.pgfinderapp.dataclasses

import com.google.gson.annotations.SerializedName

data class PGBooking(
    @SerializedName("pgId")
    val pgId: PG,

    @SerializedName("status")
    val status: String,

    @SerializedName("_id")
    val id: String
)
