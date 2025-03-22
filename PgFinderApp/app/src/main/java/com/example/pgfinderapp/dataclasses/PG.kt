package com.example.pgfinderapp.dataclasses

data class PG(
    val name: String,
    val address: String,
    val city: String,
    val price: Number,
    val owner: User,
    val status: String
)