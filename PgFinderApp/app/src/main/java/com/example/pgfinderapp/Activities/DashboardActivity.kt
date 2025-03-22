package com.example.pgfinderapp.Activities

import com.example.pgfinderapp.dataclasses.User

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
import com.example.pgfinderapp.dataclasses.PG
import com.example.pgfinderapp.ui.theme.PgFinderAppTheme
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.example.pgfinderapp.Api.ApiService
import com.example.pgfinderapp.Api.RetrofitClient
import com.example.pgfinderapp.dataclasses.LoginResponse
import com.example.pgfinderapp.dataclasses.LogoutResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get roll number from intent if passed
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

    // Sample data - replace with actual data from your API
    val bookedPGs = remember {
        mutableStateOf(
            listOf(
                PG(
                    name = "Sunshine PG",
                    address = "123 Main St, City",
                    city = "Kurukshetra",
                    owner = User("Baba", "abc@gmail.com", "", "OWNER"),
                    price = 8000,
                    status = "booked"
                ),
                PG(
                    name = "Sunshine PG",
                    address = "123 Main St, City",
                    city = "Kurukshetra",
                    owner = User("Baba", "abc@gmail.com", "", "OWNER"),
                    price = 8000,
                    status = "pending"
                )
            )
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

            // Booked PGs Section
            Text(
                text = "Your Booked PGs",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            if (bookedPGs.value.isEmpty()) {
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(bookedPGs.value) { pg ->
                        BookedPGItem(pg = pg)
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

@Composable
fun BookedPGItem(pg: PG) {
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
                text = pg.address,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = pg.price.toString(),
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
                StatusChip(status = pg.status)

                Button(
                    onClick = { /* View details */ }
                ) {
                    Text("View Details")
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "booked" -> Color(0xFF4CAF50)  // Green
        "pending" -> Color(0xFFFFC107)   // Amber
        "rejected" -> Color(0xFFF44336)  // Red
        else -> Color.Gray
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.uppercase(),
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
        DashboardScreen(email = "", onLogout = { /*TODO*/ }) {
        }
    }
}