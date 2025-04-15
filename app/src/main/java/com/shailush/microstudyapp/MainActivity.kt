package com.shailush.microstudyapp

import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shailush.microstudyapp.ui.theme.AuthAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthAppTheme {
                AuthApp()
            }
        }
    }
}

@Composable
fun AuthApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Обработка входа
                    navController.popBackStack()
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterClick = { email, password, confirmPassword ->
                    // Обработка регистрации
                    navController.popBackStack()
                },
                onLoginClick = { navController.navigate("login") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthAppPreview() {
    AuthAppTheme {
        AuthApp()
    }
}