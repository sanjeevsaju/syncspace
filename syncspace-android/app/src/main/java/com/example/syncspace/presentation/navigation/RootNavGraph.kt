package com.example.syncspace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.syncspace.presentation.auth.AuthViewModel
import com.example.syncspace.presentation.notification.NotificationScreen
import com.example.syncspace.presentation.task.CreateTaskScreen
import com.example.syncspace.presentation.task.TaskListsScreen

@Composable
fun RootNavGraph() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoggedIn) {
        startDestination = if (isLoggedIn) "home" else "auth"
    }

    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination,
        ) {
            authNavGraph(
                navController = navController,
                onAuthenticated = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
            )

            composable("home") {
                TaskListsScreen(
                    onNavigateToCreateTask = { navController.navigate("create_task") },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToLogin = {
                        navController.navigate("auth") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                )
            }

            composable("create_task") {
                CreateTaskScreen(
                    onTaskCreated = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable("notifications") {
                NotificationScreen()
            }
        }
    }
}
