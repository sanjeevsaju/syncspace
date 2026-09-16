package com.example.syncspace.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncspace.domain.usecase.GetAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AuthViewModel @Inject constructor(getAuthStateUseCase: GetAuthStateUseCase) : ViewModel() {
    val isLoggedIn = getAuthStateUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false,
        )
}
