package com.example.nextstep.ui.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdminUserEditRequest
import com.example.nextstep.data.repository.AdminUsersRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant

data class AdminEditUserUiState(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "student",
    val isActive: Boolean = true,
    val firstNameError: String? = null,
    val roleError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed class AdminEditUserEvent {
    data object UserSaved : AdminEditUserEvent()
}

class AdminEditUserViewModel : ViewModel() {

    private val repository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(AdminEditUserUiState())
    val uiState: StateFlow<AdminEditUserUiState> = _uiState.asStateFlow()

    private val _events = Channel<AdminEditUserEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun loadUser(userId: String) {
        Log.d("AdminEditUserVM", "loadUser called with userId=$userId current=${_uiState.value.userId}")

        // Se for o mesmo userId que já está carregado, não recarregar
        if (_uiState.value.userId == userId && !_uiState.value.isLoading && _uiState.value.firstName.isNotBlank()) {
            Log.d("AdminEditUserVM", "loadUser skipped: same userId=$userId already loaded")
            return
        }

        // Reset completo do estado para evitar que dados/sucesso/erro do utilizador anterior persistam
        _uiState.value = AdminEditUserUiState(
            userId = userId,
            isLoading = true
        )

        viewModelScope.launch {
            val result = repository.getUserById(userId)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        firstName = user.firstName ?: "",
                        lastName = user.lastName ?: "",
                        email = user.email ?: "",
                        phone = user.phone ?: "",
                        role = user.role ?: "student",
                        isActive = user.isActive ?: true
                    )
                    Log.d("AdminEditUserVM", "User loaded: id=${user.id}, email=${user.email}, role=${user.role}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Utilizador não encontrado."
                    )
                }
            } else {
                Log.e("AdminEditUserVM", "Error loading user id=$userId", result.exceptionOrNull())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível carregar os dados do utilizador."
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            firstNameError = null,
            errorMessage = null
        )
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            errorMessage = null
        )
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phone = value,
            errorMessage = null
        )
    }

    fun onRoleChange(value: String) {
        _uiState.value = _uiState.value.copy(
            role = value,
            roleError = null,
            errorMessage = null
        )
    }

    fun onActiveChange(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(
            isActive = isActive,
            errorMessage = null
        )
    }

    fun saveUser() {
        val state = _uiState.value

        // Validações
        var hasError = false
        if (state.firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(firstNameError = "Nome obrigatório.")
            hasError = true
        }
        if (state.role.isBlank()) {
            _uiState.value = _uiState.value.copy(roleError = "Seleciona a função.")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)

            val request = AdminUserEditRequest(
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim().takeIf { it.isNotBlank() },
                phone = state.phone.trim().takeIf { it.isNotBlank() },
                role = state.role,
                isActive = state.isActive,
                updatedAt = Instant.now().toString()
            )

            Log.d("AdminEditUserVM", "Saving user id=${state.userId} role=${state.role} isActive=${state.isActive}")

            val result = repository.updateUser(state.userId, request)

            if (result.isSuccess) {
                val updatedUser = result.getOrThrow()
                Log.d("AdminEditUserVM", "User saved successfully: id=${updatedUser.id}, firstName=${updatedUser.firstName}")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Utilizador atualizado com sucesso."
                )
                // Enviar evento de navegação consumível (one-shot)
                _events.send(AdminEditUserEvent.UserSaved)
            } else {
                Log.e("AdminEditUserVM", "Error saving user", result.exceptionOrNull())
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Não foi possível atualizar o utilizador."
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        _events.close()
    }
}