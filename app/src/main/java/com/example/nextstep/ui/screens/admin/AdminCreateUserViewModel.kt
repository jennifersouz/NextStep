package com.example.nextstep.ui.screens.admin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyOptionDto
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
    val selectedStudentInstitutionId: String = "",
    val selectedStudentInstitutionName: String = "",

    // Teacher specific
    val department: String = "",
    val institutionProfileId: String = "",

    // Company specific
    val companyName: String = "",
    val nif: String = "",
    val businessArea: String = "",
    val location: String = "",
    val description: String = "",

    // Institution specific
    val institutionName: String = "",

    // Advisor specific (company assignment required)
    val availableCompanies: List<AdminCompanyOptionDto> = emptyList(),
    val isLoadingCompanies: Boolean = false,
    val selectedCompanyProfileId: String? = null,
    val selectedCompanyName: String? = null,
    val companyErrorRes: Int? = null,

    // Field-level errors (resource IDs)
    val roleError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val studentNumberError: Int? = null,
    val courseError: Int? = null,
    val academicYearError: Int? = null,
    val studentInstitutionError: Int? = null,
    val companyNameError: Int? = null,
    val nifError: Int? = null,
    val businessAreaError: Int? = null,
    val locationError: Int? = null,
    val institutionError: Int? = null,
    val institutionNameError: Int? = null,
    val generalErrorMessage: String? = null,
    val generalErrorMessageRes: Int? = null,

    // Institutions loaded from Supabase
    val availableInstitutions: List<InstitutionOptionDto> = emptyList(),
    val isLoadingInstitutions: Boolean = false,
    val institutionsLoaded: Boolean = false,

    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val successMessageRes: Int? = null,
    val isCreated: Boolean = false
)

class AdminCreateUserViewModel : ViewModel() {

    private val repository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(AdminCreateUserUiState())
    val uiState: StateFlow<AdminCreateUserUiState> = _uiState.asStateFlow()

    init {
        loadInstitutions()
        // Se o role inicial for advisor, carregar empresas
        if (_uiState.value.selectedRole == "advisor") {
            loadCompanies()
        }
    }

    // ── Allowed roles (defense-in-depth) ─────────────────────────────────────

    private val allowedRoles = listOf("student", "teacher", "company", "advisor", "institution", "admin")

    // ── Load institutions (for teacher) ─────────────────────────────────────────

    fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingInstitutions = true)
            val result = repository.getInstitutions()
            result.fold(
                onSuccess = { institutions ->
                    Log.d("AdminCreateUserVM", "Institutions loaded: ${institutions.size}")
                    val uniqueSorted = institutions
                        .distinctBy { it.id }
                        .sortedBy { it.displayName.lowercase() }
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = uniqueSorted,
                        isLoadingInstitutions = false,
                        institutionsLoaded = true
                    )
                },
                onFailure = { e ->
                    Log.e("AdminCreateUserVM", "Error loading institutions", e)
                    _uiState.value = _uiState.value.copy(
                        availableInstitutions = emptyList(),
                        isLoadingInstitutions = false,
                        institutionsLoaded = true
                    )
                }
            )
        }
    }

    // ── Load active companies (for advisor) ────────────────────────────────────

    fun loadCompanies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCompanies = true)

            val result = repository.getActiveCompanies()

            result.fold(
                onSuccess = { companies ->
                    Log.d("AdminCreateUserVM", "Companies loaded: ${companies.size}")
                    val processed = companies
                        .distinctBy { it.effectiveId }
                        .sortedBy { it.companyName.lowercase() }
                    _uiState.value = _uiState.value.copy(
                        availableCompanies = processed,
                        isLoadingCompanies = false
                    )
                },
                onFailure = { e ->
                    Log.e("AdminCreateUserVM", "Error loading companies", e)
                    _uiState.value = _uiState.value.copy(
                        availableCompanies = emptyList(),
                        isLoadingCompanies = false,
                        companyErrorRes = R.string.error_failed_to_load_companies
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
            courseError = null,
            academicYearError = null,
            studentInstitutionError = null,
            companyNameError = null,
            nifError = null,
            businessAreaError = null,
            locationError = null,
            institutionError = null,
            institutionNameError = null,
            generalErrorMessage = null,
            generalErrorMessageRes = null,
            companyErrorRes = null,
            selectedCompanyProfileId = null,
            selectedCompanyName = null,
            selectedStudentInstitutionId = "",
            selectedStudentInstitutionName = ""
        )
        if (role == "student" || role == "teacher") {
            loadInstitutions()
        } else {
            _uiState.value = _uiState.value.copy(
                availableInstitutions = emptyList(),
                selectedStudentInstitutionId = "",
                selectedStudentInstitutionName = ""
            )
        }
        if (role == "advisor") {
            loadCompanies()
        } else {
            _uiState.value = _uiState.value.copy(
                availableCompanies = emptyList(),
                selectedCompanyProfileId = null,
                selectedCompanyName = null,
                isLoadingCompanies = false
            )
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

    fun onStudentInstitutionSelected(id: String, name: String) {
        _uiState.value = _uiState.value.copy(
            selectedStudentInstitutionId = id,
            selectedStudentInstitutionName = name,
            studentInstitutionError = null,
            generalErrorMessage = null
        )
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

    fun onCompanyChange(companyProfileId: String, companyName: String) {
        _uiState.value = _uiState.value.copy(
            selectedCompanyProfileId = companyProfileId,
            selectedCompanyName = companyName,
            companyErrorRes = null,
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

    fun onInstitutionNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            institutionName = value,
            institutionNameError = null,
            generalErrorMessage = null
        )
    }

    // ── Create ───────────────────────────────────────────────────────────────

    fun createUser() {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalErrorMessage = null)

            val state = _uiState.value
            val cleanedEmail = state.email.trim().lowercase()

            // Validate role is in allowed list (defense-in-depth)
            if (state.selectedRole !in allowedRoles) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalErrorMessageRes = R.string.error_invalid_role_type
                )
                return@launch
            }

            // Verificar email duplicado antes de chamar Edge Function
            val emailAlreadyExists = repository.emailExists(cleanedEmail)
            if (emailAlreadyExists) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    emailError = R.string.error_email_already_exists
                )
                return@launch
            }

            val request = AdminCreateUserRequest(
                email = cleanedEmail,
                password = state.password,
                role = state.selectedRole,
                firstName = state.firstName.takeIf { it.isNotBlank() }?.trim(),
                lastName = state.lastName.takeIf { it.isNotBlank() }?.trim(),
                phone = state.phone.takeIf { it.isNotBlank() }?.trim(),
                isActive = state.isActive,
                studentNumber = state.studentNumber.takeIf { it.isNotBlank() }?.trim(),
                course = state.course.takeIf { it.isNotBlank() }?.trim(),
                academicYear = state.academicYear.toIntOrNull(),
                educationInstitution = state.selectedStudentInstitutionName.takeIf { it.isNotBlank() }?.trim(),
                department = state.department.takeIf { it.isNotBlank() }?.trim(),
                institutionProfileId = if (state.selectedRole == "student") {
                    state.selectedStudentInstitutionId.takeIf { it.isNotBlank() }
                } else {
                    state.institutionProfileId.takeIf { it.isNotBlank() }
                },
                companyName = state.companyName.takeIf { it.isNotBlank() }?.trim(),
                institutionName = state.institutionName.takeIf { it.isNotBlank() }?.trim(),
                nif = state.nif.takeIf { it.isNotBlank() }?.trim(),
                businessArea = state.businessArea.takeIf { it.isNotBlank() }?.trim(),
                location = state.location.takeIf { it.isNotBlank() }?.trim(),
                description = state.description.takeIf { it.isNotBlank() }?.trim(),
                companyProfileId = state.selectedCompanyProfileId?.takeIf { it.isNotBlank() }
            )

            Log.d(
                "AdminCreateUserVM",
                "createUser: role=${request.role}, email=${request.email}, " +
                    "companyProfileId=${request.companyProfileId}"
            )

            val result = repository.createUser(request)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessageRes = R.string.user_created_success,
                    isCreated = true
                )
            } else {
                val rawError = result.exceptionOrNull()?.message ?: ""
                Log.e("AdminCreateUserVM", "createUser failed: $rawError")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalErrorMessageRes = mapCreateUserError(rawError)
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
            _uiState.value = _uiState.value.copy(emailError = R.string.error_email_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.value = _uiState.value.copy(emailError = R.string.error_invalid_email)
            isValid = false
        }

        // Password obrigatória
        if (state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = R.string.error_password_required)
            isValid = false
        } else if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = R.string.error_password_too_short)
            isValid = false
        }

        // Validação por role
        when (state.selectedRole) {
            "student" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = R.string.error_name_required)
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = R.string.error_last_name_required)
                    isValid = false
                }
                if (state.studentNumber.isBlank()) {
                    _uiState.value = _uiState.value.copy(studentNumberError = R.string.error_student_number_required)
                    isValid = false
                }
                if (state.course.isBlank()) {
                    _uiState.value = _uiState.value.copy(courseError = R.string.error_course_required)
                    isValid = false
                }
                if (state.academicYear.isBlank()) {
                    _uiState.value = _uiState.value.copy(academicYearError = R.string.error_academic_year_required)
                    isValid = false
                } else {
                    val year = state.academicYear.toIntOrNull()
                    if (year == null || year < 1 || year > 6) {
                        _uiState.value = _uiState.value.copy(academicYearError = R.string.error_academic_year_invalid)
                        isValid = false
                    }
                }
                if (state.selectedStudentInstitutionId.isBlank()) {
                    _uiState.value = _uiState.value.copy(studentInstitutionError = R.string.error_institution_required)
                    isValid = false
                }
            }
            "teacher" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = R.string.error_name_required)
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = R.string.error_last_name_required)
                    isValid = false
                }
                if (state.institutionProfileId.isBlank()) {
                    _uiState.value = _uiState.value.copy(institutionError = R.string.error_institution_required)
                    isValid = false
                }
            }
            "company" -> {
                if (state.companyName.isBlank()) {
                    _uiState.value = _uiState.value.copy(companyNameError = R.string.error_company_name_required)
                    isValid = false
                }
                if (state.nif.isBlank()) {
                    _uiState.value = _uiState.value.copy(nifError = R.string.error_nif_required)
                    isValid = false
                } else if (state.nif.length != 9) {
                    _uiState.value = _uiState.value.copy(nifError = R.string.error_nif_length)
                    isValid = false
                }
                if (state.businessArea.isBlank()) {
                    _uiState.value = _uiState.value.copy(businessAreaError = R.string.error_business_area_required)
                    isValid = false
                }
                if (state.location.isBlank()) {
                    _uiState.value = _uiState.value.copy(locationError = R.string.error_location_required)
                    isValid = false
                }
            }
            "advisor" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = R.string.error_name_required)
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = R.string.error_last_name_required)
                    isValid = false
                }
                if (state.selectedCompanyProfileId.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(companyErrorRes = R.string.error_company_select_required)
                    isValid = false
                }
            }
            "institution" -> {
                if (state.institutionName.isBlank()) {
                    _uiState.value = _uiState.value.copy(institutionNameError = R.string.error_institution_name_required)
                    isValid = false
                }
            }
            "admin" -> {
                if (state.firstName.isBlank()) {
                    _uiState.value = _uiState.value.copy(firstNameError = R.string.error_name_required)
                    isValid = false
                }
                if (state.lastName.isBlank()) {
                    _uiState.value = _uiState.value.copy(lastNameError = R.string.error_last_name_required)
                    isValid = false
                }
            }
        }

        return isValid
    }

    // ── Error mapping ────────────────────────────────────────────────────────

    /**
     * Converte erros técnicos da Edge Function em resource IDs amigáveis.
     * O erro técnico completo vai apenas para o Logcat.
     */
    private fun mapCreateUserError(raw: String): Int {
        val lower = raw.lowercase()
        return when {
            "empresa obrigatória" in lower -> R.string.error_company_select_required
            "institution_profile_id" in lower -> R.string.error_institution_required
            "nif" in lower && "companies_nif_check" in lower -> R.string.error_nif_length
            "nif" in lower -> R.string.error_nif_required
            "business_area" in lower -> R.string.error_business_area_required
            "location" in lower -> R.string.error_location_required
            "companies_nif_check" in lower -> R.string.error_nif_length
            "course" in lower -> R.string.error_course_required
            "academic_year" in lower -> R.string.error_academic_year_required
            "student_number" in lower -> R.string.error_student_number_required
            "last_name" in lower -> R.string.error_last_name_required
            "first_name" in lower -> R.string.error_name_required
            "email" in lower -> R.string.error_verify_email
            "company_name" in lower -> R.string.error_company_name_required
            "tipo de utilizador inválido" in lower -> R.string.error_invalid_role_type
            "already exists" in lower ||
            "duplicate" in lower ||
            "já existe" in lower ||
            "conta com este email" in lower -> R.string.error_email_already_exists
            else -> R.string.error_create_user_failed
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            generalErrorMessage = null,
            generalErrorMessageRes = null,
            successMessage = null,
            successMessageRes = null
        )
    }

    fun clearCreationState() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            successMessageRes = null,
            isCreated = false
        )
    }
}