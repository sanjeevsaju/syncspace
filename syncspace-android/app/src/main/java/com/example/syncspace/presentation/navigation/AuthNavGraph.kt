package com.example.syncspace.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.syncspace.presentation.auth.LoginScreen
import com.example.syncspace.presentation.auth.RegisterScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onAuthenticated: () -> Unit
) {
    navigation(
        startDestination = "login",
        route = "auth"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = onAuthenticated
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = onAuthenticated
            )
        }
    }
}
