package com.example.nextstep.ui.screens.admin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdminCreateUserRequest
import com.example.nextstep.data.model.InstitutionOptionDto
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

    // Field-level errors
    val roleError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val studentNumberError: String? = null,
    val courseError: String? = null,
    val academicYearError: String? = null,
    val companyNameError: String? = null,
    val nifError: String? = null,
    val businessAreaError: String? = null,
    val locationError: String? = null,
    val institutionError: String? = null,
    val generalErrorMessage: String? = null,

    // Institutions loaded from Supabase
    val availableInstitutions: List<InstitutionOptionDto> = emptyList(),
    val isLoadingInstitutions: Boolean = false,

    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val isCreated: Boolean = false
)

class AdminCreateUserViewModel : ViewModel() {

    private val repository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(AdminCreateUserUiState())
    val uiState: StateFlow<AdminCreateUserUiState> = _uiState.asStateFlow()

    // ── Load institutions (for teacher) ─────────────────────────────────────────

    fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingInstitutions = true)
            val result = repository.getInstitutions()
            result.fold(
                onSuccess = { institutions ->
                    Log.d("AdminCreateUserVM", "Institutions loaded: ${institutions.size}")
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = institutions,
                        isLoadingInstitutions = false
                    )
                },
                onFailure = { e ->
                    Log.e("AdminCreateUserVM", "Error loading institutions", e)
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = emptyList(),
                        isLoadingInstitutions = false
                    )
                }
            )
        }
    }

    // ── Field change handlers ────────────────────────────────────────────────

    fun onRoleChange(role: String) {
        _uiState.value = _uiState.value.copy(
            selectedRole = role,
            roleError = null,
            firstNameError = null,
            lastNameError = null,
            studentNumberError = null,
            companyNameError = null,
            nifError = null,
            businessAreaError = null,
            locationError = null,
            institutionError = null,
            generalErrorMessage = null
        )
        if (role == "teacher") {
            loadInstitutions()
        }
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null,
            generalErrorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
            generalErrorMessage = null
        )
    }

    fun onFirstNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            firstName = name,
            firstNameError = null,
            generalErrorMessage = null
        )
    }

    fun onLastNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            lastName = name,
            lastNameError = null,
            generalErrorMessage = null
        )
    }

    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun onIsActiveChange(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(isActive = isActive)
    }

    fun onStudentNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(
            studentNumber = value,
            studentNumberError = null,
            generalErrorMessage = null
        )
    }

    fun onCourseChange(value: String) {
        _uiState.value = _uiState.value.copy(
            course = value,
            courseError = null,
            generalErrorMessage = null
        )
    }

    fun onAcademicYearChange(value: String) {
        _uiState.value = _uiState.value.copy(
            academicYear = value,
            academicYearError = null,
            generalErrorMessage = null
        )
    }

    fun onEducationInstitutionChange(value: String) {
        _uiState.value = _uiState.value.copy(educationInstitution = value)
    }

    fun onDepartmentChange(value: String) {
        _uiState.value = _uiState.value.copy(department = value)
    }

    fun onInstitutionChange(id: String) {
        _uiState.value = _uiState.value.copy(
            institutionProfileId = id,
            institutionError = null,
            generalErrorMessage = null
        )
    }

    fun onCompanyNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            companyName = value,
            companyNameError = null,
            generalErrorMessage = null
        )
    }

    fun onNifChange(value: String) {
        // Only allow digits, remove non-digit characters
        val digitsOnly = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(
            nif = digitsOnly,
            nifError = null,
            generalErrorMessage = null
        )
    }

    fun onBusinessAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(
            businessArea = value,
            businessAreaError = null,
            generalErrorMessage = null
        )
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(
            location = value,
            locationError = null,
            generalErrorMessage = null
        )
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    // ── Create ───────────────────────────────────────────────────────────────

    fun createUser() {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalErrorMessage = null)

            val state = _uiState.value
            val request = AdminCreateUserRequest(
                email = state.email.trim(),
                password = state.password,
                role = state.selectedRole,
                firstName = state.firstName.takeIf { it.isNotBlank() }?.trim(),
                lastName = state.lastName.takeIf { it.isNotBlank() }?.trim(),
                phone = state.phone.takeIf { it.isNotBlank() }?.trim(),
                isActive = state.isActive,
                studentNumber = state.studentNumber.takeIf { it.isNotBlank() }?.trim(),
                course = state.course.takeIf { it.isNotBlank() }?.trim(),
                academicYear = state.academicYear.toIntOrNull(),
                educationInstitution = state.educationInstitution.takeIf { it.isNotBlank() }?.trim(),
                department = state.department.takeIf { it.isNotBlank() }?.trim(),
                institutionProfileId = state.institutionProfileId.takeIf { it.isNotBlank() },
                companyName = state.companyName.takeIf { it.isNotBlank() }?.trim(),
                nif = state.nif.takeIf { it.isNotBlank() }?.trim(),
                businessArea = state.businessArea.takeIf { it.isNotBlank() }?.trim(),
                location = state.location.takeIf { it.isNotBlank() }?.trim(),
                description = state.description.takeIf { it.isNotBlank() }?.trim()
            )

            val result = repository.createUser(request)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Utilizador criado com sucesso.",
                    isCreated = true
                )
            } else {
                val rawError = result.exceptionOrNull()?.message ?: ""
                Log.e("AdminCreateUserVM", "createUser failed: $rawError")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalErrorMessage = mapApiErrorToFriendly(rawError)
                )
            }
        }
    }

    // ── Validation ───────────────────────────────────────────────────────────

    private fun validate(): Boolean {
        val state = _uiState.value
        var isValid = true

        // Email obrigatório
        if (state.email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Insere um email.")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "Insere um email válido.")
            isValid = false
        }

        // Password obrigatória
        if (state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Insere uma password temporária.")
            isValid = false
        } else if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "A password deve ter pelo menos 6 caracteres.")
            isValid = false
        }

        // Validação por role
        when (state.selectedRole) {
            "student" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = "Nome obrigatório.")
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = "Apelido obrigatório.")
                    isValid = false
                }
                if (state.studentNumber.isBlank()) {
                    _uiState.value = _uiState.value.copy(studentNumberError = "Número de aluno obrigatório.")
                    isValid = false
                }
                if (state.course.isBlank()) {
                    _uiState.value = _uiState.value.copy(courseError = "Curso obrigatório.")
                    isValid = false
                }
                if (state.academicYear.isBlank()) {
                    _uiState.value = _uiState.value.copy(academicYearError = "Ano académico obrigatório.")
                    isValid = false
                } else {
                    val year = state.academicYear.toIntOrNull()
                    if (year == null || year < 1 || year > 6) {
                        _uiState.value = _uiState.value.copy(academicYearError = "Insere um ano académico válido (1-6).")
                        isValid = false
                    }
                }
            }
            "teacher" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = "Nome obrigatório.")
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = "Apelido obrigatório.")
                    isValid = false
                }
                if (state.institutionProfileId.isBlank()) {
                    _uiState.value = _uiState.value.copy(institutionError = "Seleciona uma instituição.")
                    isValid = false
                }
            }
            "company" -> {
                if (state.companyName.isBlank()) {
                    _uiState.value = _uiState.value.copy(companyNameError = "Nome da empresa obrigatório.")
                    isValid = false
                }
                if (state.nif.isBlank()) {
                    _uiState.value = _uiState.value.copy(nifError = "NIF obrigatório.")
                    isValid = false
                } else if (state.nif.length != 9) {
                    _uiState.value = _uiState.value.copy(nifError = "O NIF deve conter 9 dígitos.")
                    isValid = false
                }
                if (state.businessArea.isBlank()) {
                    _uiState.value = _uiState.value.copy(businessAreaError = "Área de negócio obrigatória.")
                    isValid = false
                }
                if (state.location.isBlank()) {
                    _uiState.value = _uiState.value.copy(locationError = "Localização obrigatória.")
                    isValid = false
                }
            }
            "admin" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = "Nome obrigatório.")
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = "Apelido obrigatório.")
                    isValid = false
                }
            }
        }

        return isValid
    }

    // ── Error mapping ────────────────────────────────────────────────────────

    /**
     * Converte erros técnicos da Edge Function em mensagens amigáveis.
     * O erro técnico completo vai apenas para o Logcat.
     */
    private fun mapApiErrorToFriendly(raw: String): String {
        val lower = raw.lowercase()
        return when {
            "institution_profile_id" in lower -> "Seleciona uma instituição."
            "nif" in lower && "companies_nif_check" in lower -> "O NIF deve conter 9 dígitos."
            "nif" in lower -> "Verifica o NIF informado."
            "business_area" in lower -> "Área de negócio obrigatória."
            "location" in lower -> "Localização obrigatória."
            "companies_nif_check" in lower -> "O NIF deve conter 9 dígitos."
            "course" in lower -> "Curso obrigatório."
            "academic_year" in lower -> "Ano académico obrigatório."
            "student_number" in lower -> "Número de aluno obrigatório."
            "last_name" in lower -> "Apelido obrigatório."
            "first_name" in lower -> "Nome obrigatório."
            "email" in lower -> "Verifica o email informado."
            "company_name" in lower -> "Nome da empresa obrigatório."
            "already exists" in lower ||
            "duplicate" in lower -> "Já existe um utilizador com este email."
            else -> "Não foi possível criar o utilizador. Verifica os dados e tenta novamente."
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(generalErrorMessage = null, successMessage = null)
    }
}