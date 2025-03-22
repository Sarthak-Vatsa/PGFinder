package com.example.pgfinderapp.dataclasses

data class PgListing(
    val id: Int,                     // Unique identifier for the PG
    val name: String,                // Name of the PG
    val address: String,             // Street address
    val city: String,                // City
    val price: Int,                  // Monthly rent amount in INR
    val owner: String,               // Owner/manager name
    val status: String,              // Availability status (e.g., "Available", "Booked")
    val rating: Float,               // Rating out of 5
//    val features: List<String>,      // List of amenities/features (e.g., "WiFi", "AC", "Meals")
    val imageResId: Int              // Resource ID for the PG image
)
