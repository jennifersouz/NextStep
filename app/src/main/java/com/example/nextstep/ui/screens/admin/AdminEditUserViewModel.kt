package com.example.nextstep.ui.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdminCompanyDto
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
    val companyName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "student",
    val isActive: Boolean = true,
    val firstNameError: String? = null,
    val companyNameError: String? = null,
    val roleError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // Flag que indica se o role atual é "company" (usado na UI para renderização condicional)
    val isCompany: Boolean = false
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
        if (_uiState.value.userId == userId && !_uiState.value.isLoading && (_uiState.value.firstName.isNotBlank() || _uiState.value.companyName.isNotBlank())) {
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
                    val normalizedRole = user.role?.trim()?.lowercase() ?: "student"
                    val isCompany = normalizedRole == "company" || normalizedRole == "empresa"

                    var companyName = ""

                    // Se for empresa, carregar company_name da tabela companies
                    if (isCompany) {
                        val companyResult = repository.getCompanyByProfileId(userId)
                        if (companyResult.isSuccess) {
                            companyName = companyResult.getOrNull()?.companyName ?: ""
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        firstName = user.firstName ?: "",
                        lastName = user.lastName ?: "",
                        companyName = companyName,
                        email = user.email ?: "",
                        phone = user.phone ?: "",
                        role = user.role ?: "student",
                        isActive = user.isActive ?: true,
                        isCompany = isCompany
                    )
                    Log.d("AdminEditUserVM", "User loaded: id=${user.id}, email=${user.email}, role=${user.role}, isCompany=$isCompany")
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

    fun onCompanyNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            companyName = value,
            companyNameError = null,
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
        val normalizedRole = value.trim().lowercase()
        val isCompany = normalizedRole == "company" || normalizedRole == "empresa"
        _uiState.value = _uiState.value.copy(
            role = value,
            roleError = null,
            isCompany = isCompany,
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

        if (state.isCompany) {
            // Empresa: validar nome da empresa
            if (state.companyName.isBlank()) {
                _uiState.value = _uiState.value.copy(companyNameError = "Insere o nome da empresa.")
                hasError = true
            }
        } else {
            // Pessoa: validar nome
            if (state.firstName.isBlank()) {
                _uiState.value = _uiState.value.copy(firstNameError = "Insere o nome.")
                hasError = true
            }
        }

        if (state.role.isBlank()) {
            _uiState.value = _uiState.value.copy(roleError = "Seleciona a função.")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)

            val now = Instant.now().toString()
            val normalizedRole = state.role.trim().lowercase()
            val isCompany = normalizedRole == "company" || normalizedRole == "empresa"

            // 1) Atualizar profiles (campos comuns)
            val request = if (isCompany) {
                // Para empresa: usar firstName como fallback do company_name (consistência)
                // e lastName vazio/null
                AdminUserEditRequest(
                    firstName = state.companyName.trim(),
                    lastName = null,
                    phone = state.phone.trim().takeIf { it.isNotBlank() },
                    role = state.role,
                    isActive = state.isActive,
                    updatedAt = now
                )
            } else {
                AdminUserEditRequest(
                    firstName = state.firstName.trim(),
                    lastName = state.lastName.trim().takeIf { it.isNotBlank() },
                    phone = state.phone.trim().takeIf { it.isNotBlank() },
                    role = state.role,
                    isActive = state.isActive,
                    updatedAt = now
                )
            }

            Log.d("AdminEditUserVM", "Saving user id=${state.userId} role=${state.role} isActive=${state.isActive} isCompany=$isCompany")

            val result = repository.updateUser(state.userId, request)

            if (result.isSuccess) {
                // 2) Se for empresa, atualizar também companies.company_name
                if (isCompany) {
                    val companyResult = repository.updateCompanyName(
                        profileId = state.userId,
                        companyName = state.companyName.trim()
                    )
                    if (companyResult.isFailure) {
                        Log.e("AdminEditUserVM", "Failed to update company name", companyResult.exceptionOrNull())
                        // Não bloquear o save — o profile foi atualizado
                    }
                }

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