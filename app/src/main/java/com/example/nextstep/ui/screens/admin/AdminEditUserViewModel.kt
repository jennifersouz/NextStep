package com.example.nextstep.ui.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.InstitutionOptionDto
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
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val companyNameError: Int? = null,
    val phoneError: Int? = null,
    val roleError: Int? = null,
    val studentInstitutionError: Int? = null,
    val teacherInstitutionError: Int? = null,
    val availableInstitutions: List<InstitutionOptionDto> = emptyList(),
    val isLoadingInstitutions: Boolean = false,
    val selectedInstitutionId: String = "",
    val selectedInstitutionName: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null,
    val successMessage: String? = null,
    val successMessageRes: Int? = null,
    // Flag que indica se o role atual é "company" (usado na UI para renderização condicional)
    val isCompany: Boolean = false,
    // Flag que indica se o role atual é "student"
    val isStudent: Boolean = false
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

    fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingInstitutions = true)
            val result = repository.getInstitutions()
            result.fold(
                onSuccess = { institutions ->
                    Log.d("AdminEditUserVM", "Institutions loaded: ${institutions.size}")
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = institutions,
                        isLoadingInstitutions = false
                    )
                },
                onFailure = { e ->
                    Log.e("AdminEditUserVM", "Error loading institutions", e)
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = emptyList(),
                        isLoadingInstitutions = false
                    )
                }
            )
        }
    }

    fun loadUser(userId: String) {
        Log.d("AdminEditUserVM", "loadUser called with userId=$userId")

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
                    var selectedInstitutionId = ""
                    var selectedInstitutionName = ""
                    val isStudent = normalizedRole == "student" || normalizedRole == "aluno"
                    val isTeacher = normalizedRole == "teacher" || normalizedRole == "docente"

                    // Se for empresa, carregar company_name da tabela companies
                    if (isCompany) {
                        val companyResult = repository.getCompanyByProfileId(userId)
                        if (companyResult.isSuccess) {
                            companyName = companyResult.getOrNull()?.companyName ?: ""
                        }
                    }

                    // Se for aluno, carregar education_institution da tabela students
                    if (isStudent) {
                        val studentResult = repository.getStudentByProfileId(userId)
                        if (studentResult.isSuccess) {
                            val student = studentResult.getOrNull()
                            selectedInstitutionName = student?.educationInstitution ?: ""
                            // Try to match with an available institution
                            if (selectedInstitutionName.isNotBlank() && _uiState.value.availableInstitutions.isNotEmpty()) {
                                val match = _uiState.value.availableInstitutions.find {
                                    it.displayName.equals(selectedInstitutionName, ignoreCase = true)
                                }
                                if (match != null) {
                                    selectedInstitutionId = match.id
                                }
                            }
                        }
                    }

                    // Se for teacher, carregar institution_profile_id (não disponível no profiles table at the moment)
                    // O institution_profile_id está na tabela teachers, carregar daqui
                    if (isTeacher) {
                        val teacherResult = repository.getTeacherByProfileId(userId)
                        if (teacherResult.isSuccess) {
                            val teacher = teacherResult.getOrNull()
                            if (teacher != null) {
                                selectedInstitutionId = teacher.institutionProfileId ?: ""
                                if (selectedInstitutionId.isNotBlank() && _uiState.value.availableInstitutions.isNotEmpty()) {
                                    val match = _uiState.value.availableInstitutions.find {
                                        it.id == selectedInstitutionId
                                    }
                                    if (match != null) {
                                        selectedInstitutionName = match.displayName
                                    }
                                }
                            }
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
                        isCompany = isCompany,
                        isStudent = isStudent,
                        selectedInstitutionId = selectedInstitutionId,
                        selectedInstitutionName = selectedInstitutionName
                    )
                    Log.d("AdminEditUserVM", "User loaded: id=${user.id}, email=${user.email}, role=${user.role}, isCompany=$isCompany, isStudent=$isStudent")

                    // Load institutions for student/teacher roles
                    if (isStudent || isTeacher) {
                        loadInstitutions()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessageRes = R.string.error_user_not_found
                    )
                }
            } else {
                Log.e("AdminEditUserVM", "Error loading user id=$userId", result.exceptionOrNull())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_load_user
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        val sanitized = value.filter { it.isLetter() || it.isWhitespace() }
        _uiState.value = _uiState.value.copy(
            firstName = sanitized,
            firstNameError = null,
            errorMessage = null
        )
    }

    fun onLastNameChange(value: String) {
        val sanitized = value.filter { it.isLetter() || it.isWhitespace() }
        _uiState.value = _uiState.value.copy(
            lastName = sanitized,
            lastNameError = validatePersonName(sanitized),
            successMessage = null,
            successMessageRes = null,
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
        val filtered = value
            .filter { it.isDigit() }
            .take(9)

        _uiState.value = _uiState.value.copy(
            phone = filtered,
            phoneError = validatePhone(filtered),
            errorMessage = null
        )
    }

    private fun validatePhone(value: String): Int? {
        return when {
            value.isBlank() -> null
            !value.all { it.isDigit() } -> R.string.error_phone_digits
            value.length != 9 -> R.string.error_phone_length
            else -> null
        }
    }

    private fun validatePersonName(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            value.length < 2 -> R.string.error_name_too_short
            !value.all { it.isLetter() || it.isWhitespace() } -> R.string.error_only_letters
            else -> null
        }
    }

    fun onInstitutionSelected(id: String, name: String) {
        _uiState.value = _uiState.value.copy(
            selectedInstitutionId = id,
            selectedInstitutionName = name,
            studentInstitutionError = null,
            teacherInstitutionError = null,
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
                _uiState.value = _uiState.value.copy(companyNameError = R.string.error_required_field)
                hasError = true
            }
        } else {
            // Pessoa: validar nome e apelido (apenas letras, mínimo 2 caracteres)
            val nameError = validatePersonName(state.firstName)
            if (nameError != null) {
                _uiState.value = _uiState.value.copy(firstNameError = nameError)
                hasError = true
            }

            val lastNameError = validatePersonName(state.lastName)
            if (lastNameError != null) {
                _uiState.value = _uiState.value.copy(lastNameError = lastNameError)
                hasError = true
            }
        }

        if (state.role.isBlank()) {
            _uiState.value = _uiState.value.copy(roleError = R.string.error_required_field)
            hasError = true
        }

        val phoneError = validatePhone(state.phone)
        if (phoneError != null) {
            _uiState.value = _uiState.value.copy(phoneError = phoneError)
            hasError = true
        }

        // Validar instituição para alunos
        if (state.isStudent && state.selectedInstitutionId.isBlank()) {
            _uiState.value = _uiState.value.copy(studentInstitutionError = R.string.error_institution_required)
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null, successMessageRes = null)

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

                // 3) Se for aluno, atualizar education_institution na tabela students
                val isStudentRole = normalizedRole == "student" || normalizedRole == "aluno"
                val isTeacherRole = normalizedRole == "teacher" || normalizedRole == "docente"

                if (isStudentRole && state.selectedInstitutionId.isNotBlank()) {
                    val studentResult = repository.updateStudentInstitution(
                        profileId = state.userId,
                        educationInstitution = state.selectedInstitutionName
                    )
                    if (studentResult.isFailure) {
                        Log.e("AdminEditUserVM", "Failed to update student institution", studentResult.exceptionOrNull())
                    }
                }

                // 4) Se for docente, atualizar institution_profile_id na tabela teachers
                if (isTeacherRole && state.selectedInstitutionId.isNotBlank()) {
                    val teacherResult = repository.updateTeacherInstitution(
                        profileId = state.userId,
                        institutionProfileId = state.selectedInstitutionId
                    )
                    if (teacherResult.isFailure) {
                        Log.e("AdminEditUserVM", "Failed to update teacher institution", teacherResult.exceptionOrNull())
                    }
                }

                val updatedUser = result.getOrThrow()
                Log.d("AdminEditUserVM", "User saved successfully: id=${updatedUser.id}, firstName=${updatedUser.firstName}")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessageRes = R.string.user_updated_success
                )
                // Enviar evento de navegação consumível (one-shot)
                _events.send(AdminEditUserEvent.UserSaved)
            } else {
                Log.e("AdminEditUserVM", "Error saving user", result.exceptionOrNull())
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.error_could_not_update_user
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            successMessageRes = null,
            errorMessage = null,
            errorMessageRes = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        _events.close()
    }
}