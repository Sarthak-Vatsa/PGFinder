package com.example.pgfinderapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.pgfinderapp.Api.RetrofitClient
import com.example.pgfinderapp.dataclasses.LogoutResponse
import com.example.pgfinderapp.dataclasses.PG
import com.example.pgfinderapp.dataclasses.PGBooking
import com.example.pgfinderapp.ui.theme.PgFinderAppTheme
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get email from intent if passed
        val email = intent.getStringExtra("Email") ?: ""

        setContent {
            PgFinderAppTheme {
                DashboardScreen(
                    email = email,
                    onLogout = { logout() },
                    onEditProfile = { navigateToEditProfile() }
                )
            }
        }
    }

    private fun logout() {
        RetrofitClient.instance.logoutUser().enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful) {
                    // Get the message from response
                    val message = response.body()?.msg ?: "Logged out successfully"

                    // Display the message from backend
                    Toast.makeText(this@DashboardActivity, message, Toast.LENGTH_SHORT).show()

                    // Clear local storage
                    getSharedPreferences("pg_finder_prefs", MODE_PRIVATE).edit().clear().apply()

                    // Navigate to login
                    val intent = Intent(this@DashboardActivity, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // Try to parse error message if available
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = JSONObject(errorBody ?: "").optString("msg", "Logout failed")
                        Toast.makeText(this@DashboardActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@DashboardActivity, "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                // Handle network failure
                Toast.makeText(this@DashboardActivity, "Network error during logout: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToEditProfile() {
        // Navigate to profile edit screen
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    email: String,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val bookedPGs = remember { mutableStateOf<List<PGBooking>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Fetch booked PGs when the screen is first displayed
    LaunchedEffect(key1 = true) {
        fetchBookedPGs(
            onSuccess = { pgBookings ->
                bookedPGs.value = pgBookings
                isLoading.value = false
            },
            onError = { message ->
                errorMessage.value = message
                isLoading.value = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User info section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Welcome Back!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = onEditProfile,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profile")
                        }
                    }

                    if (email.isNotEmpty()) {
                        Text(
                            text = "Email: $email",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Section title
            Text(
                text = "My PG Bookings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            when {
                isLoading.value -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage.value != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage.value ?: "Error loading PGs",
                            color = Color.Red
                        )
                    }
                }
                bookedPGs.value.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You haven't booked any PGs yet",
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(bookedPGs.value) { pgBooking ->
                            BookedPGItem(pgBooking = pgBooking)
                        }
                    }
                }
            }

            // Add a button to browse more PGs
            Button(
                onClick = { /* Navigate to PG listing */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Browse More PGs")
            }
        }
    }
}

private fun fetchBookedPGs(
    onSuccess: (List<PGBooking>) -> Unit,
    onError: (String) -> Unit
) {
    RetrofitClient.instance.getBookedPGs().enqueue(object : Callback<List<PGBooking>> {
        override fun onResponse(call: Call<List<PGBooking>>, response: Response<List<PGBooking>>) {
            if (response.isSuccessful) {
                val bookings = response.body() ?: emptyList()
                onSuccess(bookings)
            } else {
                try {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody ?: "").optString("msg", "Failed to load PGs")
                    onError(errorMessage)
                } catch (e: Exception) {
                    onError("Failed to load PGs: ${response.code()}")
                }
            }
        }

        override fun onFailure(call: Call<List<PGBooking>>, t: Throwable) {
            onError("Network error: ${t.message}")
        }
    })
}

@Composable
fun BookedPGItem(pgBooking: PGBooking) {
    val pg = pgBooking.pgId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = pg.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${pg.address}, ${pg.city}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "₹${pg.price}/month",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display availability status
                StatusChip(availability = pg.availability)

                // Display booking status
                BookingStatusChip(status = pgBooking.status)
            }

            Button(
                onClick = { /* View details */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("View Details")
            }
        }
    }
}

@Composable
fun StatusChip(availability: Boolean) {
    val backgroundColor = when (availability) {
        true -> Color(0xFF4CAF50)  // Green
        false -> Color(0xFFF44336)  // Red
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = when(availability){
                true -> "Available"
                false -> "Not Available"
            },
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BookingStatusChip(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "approved" -> Color(0xFF4CAF50)  // Green
        "pending" -> Color(0xFFFFC107)   // Amber
        "rejected" -> Color(0xFFF44336)  // Red
        else -> Color.Gray
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.capitalize(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    PgFinderAppTheme {
        DashboardScreen(email = "user@example.com", onLogout = {}, onEditProfile = {})
    }
}