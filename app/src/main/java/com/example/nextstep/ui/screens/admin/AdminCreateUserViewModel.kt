package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdminCreateUserRequest
import com.example.nextstep.data.repository.AdminUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminCreateUserUiState(
    val selectedRole: String = "student",
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val isActive: Boolean = true,
    
    // Student specific
    val studentNumber: String = "",
    val course: String = "",
    val academicYear: String = "",
    val educationInstitution: String = "",
    
    // Teacher specific
    val department: String = "",
    val institutionProfileId: String = "",
    
    // Company specific
    val companyName: String = "",
    val nif: String = "",
    val businessArea: String = "",
    val location: String = "",
    val description: String = "",
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isCreated: Boolean = false
)

class AdminCreateUserViewModel : ViewModel() {

    private val repository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(AdminCreateUserUiState())
    val uiState: StateFlow<AdminCreateUserUiState> = _uiState.asStateFlow()

    fun onRoleChange(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onFirstNameChange(name: String) {
        _uiState.value = _uiState.value.copy(firstName = name)
    }

    fun onLastNameChange(name: String) {
        _uiState.value = _uiState.value.copy(lastName = name)
    }

    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun onIsActiveChange(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(isActive = isActive)
    }

    fun onStudentNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(studentNumber = value)
    }

    fun onCourseChange(value: String) {
        _uiState.value = _uiState.value.copy(course = value)
    }

    fun onAcademicYearChange(value: String) {
        _uiState.value = _uiState.value.copy(academicYear = value)
    }

    fun onEducationInstitutionChange(value: String) {
        _uiState.value = _uiState.value.copy(educationInstitution = value)
    }

    fun onDepartmentChange(value: String) {
        _uiState.value = _uiState.value.copy(department = value)
    }

    fun onInstitutionProfileIdChange(value: String) {
        _uiState.value = _uiState.value.copy(institutionProfileId = value)
    }

    fun onCompanyNameChange(value: String) {
        _uiState.value = _uiState.value.copy(companyName = value)
    }

    fun onNifChange(value: String) {
        _uiState.value = _uiState.value.copy(nif = value)
    }

    fun onBusinessAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(businessArea = value)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun createUser() {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val state = _uiState.value
            val request = AdminCreateUserRequest(
                email = state.email.trim(),
                password = state.password,
                role = state.selectedRole,
                firstName = state.firstName.takeIf { state.selectedRole != "company" }?.trim(),
                lastName = state.lastName.takeIf { state.selectedRole != "company" }?.trim(),
                phone = state.phone.trim(),
                isActive = state.isActive,
                studentNumber = state.studentNumber.takeIf { state.selectedRole == "student" }?.trim(),
                course = state.course.takeIf { state.selectedRole == "student" }?.trim(),
                academicYear = state.academicYear.toIntOrNull().takeIf { state.selectedRole == "student" },
                educationInstitution = state.educationInstitution.takeIf { state.selectedRole == "student" }?.trim(),
                department = state.department.takeIf { state.selectedRole == "teacher" }?.trim(),
                institutionProfileId = state.institutionProfileId.takeIf { state.selectedRole == "teacher" && it.isNotBlank() },
                companyName = state.companyName.takeIf { state.selectedRole == "company" }?.trim(),
                nif = state.nif.takeIf { state.selectedRole == "company" }?.trim(),
                businessArea = state.businessArea.takeIf { state.selectedRole == "company" }?.trim(),
                location = state.location.takeIf { state.selectedRole == "company" }?.trim(),
                description = state.description.takeIf { state.selectedRole == "company" }?.trim()
            )

            val result = repository.createUser(request)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Utilizador criado com sucesso.",
                    isCreated = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Erro ao criar utilizador."
                )
            }
        }
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email inválido.")
            return false
        }
        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "A password deve ter pelo menos 6 caracteres.")
            return false
        }
        
        when (state.selectedRole) {
            "company" -> {
                if (state.companyName.isBlank()) {
                    _uiState.value = _uiState.value.copy(errorMessage = "Nome da empresa é obrigatório.")
                    return false
                }
                if (state.nif.isBlank()) {
                    _uiState.value = _uiState.value.copy(errorMessage = "NIF é obrigatório.")
                    return false
                }
            }
            else -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(errorMessage = "Nome é obrigatório.")
                    return false
                }
            }
        }
        
        return true
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}