package com.example.nextstep.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.SessionRepository
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {

    private val repository = SessionRepository()

    fun logout(
        onSuccess: () -> Unit,
        onError: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.logout()

            if (result.isSuccess) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
}