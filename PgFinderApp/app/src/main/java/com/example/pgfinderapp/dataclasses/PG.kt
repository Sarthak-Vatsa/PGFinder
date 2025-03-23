package com.example.pgfinderapp.dataclasses

import android.os.Message

data class PG(
    val name: String,
    val address: String,
    val city: String,
    val price: Number,
    val owner: User,
    val availability: Boolean,
    val message: String
)