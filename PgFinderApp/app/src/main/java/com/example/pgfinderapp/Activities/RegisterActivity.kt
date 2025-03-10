package com.example.pgfinderapp.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pgfinderapp.ui.theme.PgFinderAppTheme
import com.example.pgfinderapp.Api.ApiService

import android.content.Context
import android.widget.Toast
import com.example.pgfinderapp.Api.RetrofitClient
import com.example.pgfinderapp.dataclasses.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PgFinderAppTheme {
                RegistrationScreen()
            }
        }
    }
}

@Composable
fun RegistrationScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val user = User(name, email, password,role)
            registerUser(user, context)
        }) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Already have an account? ")
            Button(
                onClick = {
                    // Navigate to SignInActivity
                    val intent = android.content.Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)
                }
//                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Sign in", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

fun registerUser(user: User, context: Context) {
    RetrofitClient.instance.registerUser(user)
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(context, "Failed: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    PgFinderAppTheme {
        RegistrationScreen()
    }
}

