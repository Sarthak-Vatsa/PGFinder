package com.example.pgfinderapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pgfinderapp.Api.RetrofitClient
import com.example.pgfinderapp.dataclasses.LoginResponse
import com.example.pgfinderapp.dataclasses.User
import com.example.pgfinderapp.ui.theme.PgFinderAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PgFinderAppTheme {
                SignInScreen { email, password -> loginUser(email, password) }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val student = User("", email = email, password = password, "") // Other fields not needed for login
        RetrofitClient.instance.loginUser(student)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        runOnUiThread {
                            Toast.makeText(this@SignInActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            // Create intent for DashboardActivity instead of MainActivity
                            val intent = Intent(this@SignInActivity, DashboardActivity::class.java).apply {
                                // Pass the roll number to dashboard
                                putExtra("Email", loginResponse?.email ?: email)
                            }
                            startActivity(intent)
                            finish() // Close the SignInActivity so user can't go back to it
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SignInActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("SignInActivity", "Network call failed", t)
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity, "Failed!!. Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}

@Composable
fun SignInScreen(onLoginClick: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign In", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    onLoginClick(email, password)
                }
            }
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Don't have an account? Create one")
            Button(
                onClick = {
                    // Navigate to SignInActivity
                    val intent = Intent(context, RegisterActivity::class.java)
                    context.startActivity(intent)
                }
//                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Register", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    PgFinderAppTheme {
        SignInScreen { _, _ ->  }
    }
}
