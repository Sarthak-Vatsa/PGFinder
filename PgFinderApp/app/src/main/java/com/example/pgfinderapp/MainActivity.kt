package com.example.pgfinderapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pgfinderapp.Activities.RegisterActivity
import com.example.pgfinderapp.Activities.SignInActivity
import com.example.pgfinderapp.ui.theme.PgFinderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PgFinderAppTheme {
                MainScreen(
                    onSignUpClick = { navigateToRegister() },
                    onSignInClick = { navigateToSignIn() }
                )
            }
        }
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
    }
}

@Composable
fun MainScreen(onSignUpClick: () -> Unit, onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onSignUpClick) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick =  onSignInClick ) {
            Text("Sign In")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PgFinderAppTheme {
        MainScreen(
            onSignUpClick = {},
            onSignInClick = {}
        )
    }
}