package com.example.syncspace.presentation.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncspace.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListsScreen(
    onNavigateToCreateTask: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val state by  viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SyncSpace") },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }

                    IconButton(onClick = {
                        viewModel.onEvent(TaskListEvent.Logout)
                        onNavigateToLogin()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateTask) {
                Text(
                    text = "+",
                    fontSize = 30.sp
                )
            }
        }
    ){ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.error}")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.onEvent(TaskListEvent.Refresh) }) {
                                Text("Refresh")
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.onEvent(TaskListEvent.DismissError)
                            }) {
                                Text("Dismiss Error")
                            }
                        }
                    }
                }

                state.tasks.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tasks yet. Tap + to add one")
                    }
                }

                else -> {
                    LazyColumn {
                        items(state.tasks) { task ->
                            TaskItem(
                                task = task,
                                deleteTask = { viewModel.onEvent(TaskListEvent.DeleteTask(task)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    deleteTask: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${task.status}", style = MaterialTheme.typography.bodySmall)

            if(task.assignees.isNotEmpty()) {
                Text(
                    text = "Assignees: ${task.assignees.joinToString { it.displayName }}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        IconButton(
            onClick = deleteTask,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete item"
            )
        }
    }
}