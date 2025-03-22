@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pgfinderapp.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pgfinderapp.ui.theme.PgFinderAppTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.pgfinderapp.R
import com.example.pgfinderapp.dataclasses.PgListing

class ViewPgActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PgFinderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PgListingsScreen(getMockPgListings())
                }
            }
        }
    }
}

@Composable
fun PgListingsScreen(listings: List<PgListing>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available PGs") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listings) { listing ->
                PgListingCard(listing)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PgListingCard(pg: PgListing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = pg.imageResId),
                    contentDescription = "PG Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Status tag
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(16.dp),
                    color = if (pg.status == "Available") Color(0xFF4CAF50) else Color(0xFFFF5722)
                ) {
                    Text(
                        text = pg.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Rating
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF4CAF50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pg.rating.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFEB3B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "/5",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = pg.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${pg.address}, ${pg.city}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Owner
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Owner",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Owner: ${pg.owner}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

//                // Features
//                FlowRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    maxItemsInEachRow = 3
//                ) {
//                    pg.features.forEach { feature ->
//                        Surface(
//                            shape = RoundedCornerShape(4.dp),
//                            color = MaterialTheme.colorScheme.primaryContainer
//                        ) {
//                            Text(
//                                text = feature,
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                                fontSize = 12.sp,
//                                color = MaterialTheme.colorScheme.onPrimaryContainer
//                            )
//                        }
//                    }
//                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price and Book Now button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${pg.price}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "per month",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = { /* TODO: Implement booking logic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Book Now")
                    }
                }
            }
        }
    }
}

// Mock data for preview
fun getMockPgListings(): List<PgListing> {
    return listOf(
        PgListing(
            id = 1,
            name = "Comfort PG Homes",
            address = "123 Main Street",
            city = "Bangalore",
            price = 8500,
            owner = "Rahul Singh",
            status = "Available",
            rating = 4.5f,
//            features = listOf("WiFi", "AC", "Meals"),
            imageResId = R.drawable.pg1 // Make sure to add these images to your drawable folder
        ),
        PgListing(
            id = 2,
            name = "Royal Stay PG",
            address = "456 Park Avenue",
            city = "Mumbai",
            price = 12000,
            owner = "Priya Sharma",
            status = "Available",
            rating = 4.7f,
//            features = listOf("WiFi", "AC", "Gym", "Laundry"),
            imageResId = R.drawable.pg2
        ),
        PgListing(
            id = 3,
            name = "Sunshine PG",
            address = "789 Garden Road",
            city = "Delhi",
            price = 7500,
            owner = "Amit Patel",
            status = "Booked",
            rating = 4.2f,
//            features = listOf("WiFi", "Meals", "Power Backup"),
            imageResId = R.drawable.pg3
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PgListingsScreenPreview() {
    PgFinderAppTheme {
        PgListingsScreen(getMockPgListings())
    }
}