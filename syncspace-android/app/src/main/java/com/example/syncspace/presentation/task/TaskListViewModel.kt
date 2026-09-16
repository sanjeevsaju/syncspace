package com.example.syncspace.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncspace.domain.usecase.DeleteTaskUseCase
import com.example.syncspace.domain.usecase.GetTasksUseCase
import com.example.syncspace.domain.usecase.LogoutUseCase
import com.example.syncspace.domain.util.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState(isLoading = true))
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun onEvent(event: TaskListEvent) {
        when (event) {
            TaskListEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            TaskListEvent.Refresh -> loadTasks()
            TaskListEvent.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                }
            }

            is TaskListEvent.DeleteTask -> {
                viewModelScope.launch {
                    when (val result = deleteTaskUseCase(event.task.id)) {
                        is DomainResult.Error -> {
                            _uiState.value = _uiState.value.copy(error = result.message)
                        }
                        is DomainResult.Success -> {
                            loadTasks()
                        }
                    }
                }
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getTasksUseCase()
            _uiState.value = when (result) {
                is DomainResult.Success -> {
                    _uiState.value.copy(
                        isLoading = false,
                        tasks = result.data,
                        error = null,
                    )
                }
                is DomainResult.Error -> {
                    _uiState.value.copy(
                        isLoading = false,
                        tasks = emptyList(),
                        error = result.message,
                    )
                }
            }
        }
    }
}
