package com.shailush.microstudyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shailush.microstudyapp.data.service.AuthService
import com.shailush.microstudyapp.ui.screens.LoginScreen
import com.shailush.microstudyapp.MainScreen
import com.shailush.microstudyapp.ui.screens.RegisterScreen
import com.shailush.microstudyapp.ui.screens.TaskScreen
import com.shailush.microstudyapp.ui.theme.AuthAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthApp()
                }
            }
        }
    }
}

@Composable
fun AuthApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authService = remember { AuthService(context) }

    val startDestination = if (authService.isLoggedIn()) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("main") {
            MainScreen(
                onProfileClick = { navController.navigate("profile") },
                onAddTopicClick = { /* TODO: добавление темы */ },
                onLogoutClick = {
                    authService.logout()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                navController = navController
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password, onResult ->
                    authService.login(email, password) { success ->
                        onResult(success)
                        if (success) {
                            navController.navigate("main") {
                                popUpTo(0)
                            }
                        }
                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterClick = { email, password, confirmPassword, onResult ->
                    if (password != confirmPassword) {
                        onResult(false)
                        return@RegisterScreen
                    }
                    authService.register(email, password) { success ->
                        onResult(success)
                        if (success) {
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    }
                },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                authService = authService
            )
        }

        composable("task") {
            TaskScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate("main") }
            )
        }
    }
}