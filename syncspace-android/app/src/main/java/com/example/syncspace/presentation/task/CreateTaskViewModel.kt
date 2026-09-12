package com.example.syncspace.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncspace.domain.usecase.CreateTaskUseCase
import com.example.syncspace.domain.util.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreateTaskEvent) {
        when(event) {
            is CreateTaskEvent.TitleChanged -> {
                _uiState.value = _uiState.value.copy(
                    title = event.title,
                    error = null
                )
            }
            is CreateTaskEvent.DescriptionChanged -> {
                _uiState.value = _uiState.value.copy(
                    description = event.description,
                    error = null
                )
            }
            CreateTaskEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            }
            CreateTaskEvent.Submit -> createTask()
        }
    }

    private fun createTask() {
        if(!validate()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = createTaskUseCase(
                title = _uiState.value.title.trim(),
                description = _uiState.value.description.trim(),
                assigneeIds = emptyList()   // For now, can be extended later.
            )
            _uiState.value = when(result) {
                is DomainResult.Error -> {
                    _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.message
                    )
                }
                is DomainResult.Success -> {
                    _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }
            }
        }
    }

    private fun validate(): Boolean {
        return when {
            _uiState.value.title.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Title is blank")
                false
            }
            _uiState.value.title.length < 3 -> {
                _uiState.value = _uiState.value.copy(error = "Title is too short")
                false
            }
            _uiState.value.description.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Description cannot be blank")
                false
            }
            else -> true
        }
    }

    // To reset the state after a successful creation, e.g. when navigating back.
    fun resetState() {
        _uiState.value = CreateTaskUiState()
    }
}