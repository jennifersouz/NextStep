package com.example.nextstep.ui.screens.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.remote.SupabaseClientProvider
import com.example.nextstep.data.repository.AdvisorRegistrationRepository
import com.example.nextstep.data.repository.AuthRepository
import com.example.nextstep.ui.utils.SanitizationUtils
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthViewModel : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val authRepository = AuthRepository()
    private val advisorRegistrationRepository = AdvisorRegistrationRepository()
    private val supabase = SupabaseClientProvider.client

    private fun mapAuthErrorToMessage(exception: Throwable?): Int {
        val message = exception?.message.orEmpty().lowercase()

        return when {
            "no_pending_advisor_invite" in message ->
                R.string.advisor_register_no_invite

            "invite_not_found" in message ->
                R.string.invite_not_found

            "auth_required" in message ->
                R.string.error_unknown_auth

            "user_already_exists" in message || "already registered" in message ->
                R.string.error_user_already_exists

            "invalid login credentials" in message ->
                R.string.error_invalid_credentials

            "email" in message && "invalid" in message ->
                R.string.error_invalid_email
                
            "incomplete_account" in message ->
                R.string.incomplete_account

            "account_disabled" in message ->
                R.string.account_disabled

            else -> R.string.error_unknown_auth
        }
    }

    fun onRoleChange(role: UserRole) {
        _registerState.value = _registerState.value.copy(
            selectedRole = role,
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onEmailChange(value: String) {
        val sanitized = value
            .filter { c -> c != '\n' && c != '\r' && c != '\t' && c != ' ' && !SanitizationUtils.isInvisibleChar(c) }
            .lowercase()
        _registerState.value = _registerState.value.copy(
            email = sanitized,
            emailError = validateEmail(sanitized),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onPasswordChange(value: String) {
        val sanitized = value
            .filter { c -> c != '\n' && c != '\r' && c != '\t' && !SanitizationUtils.isInvisibleChar(c) }
        _registerState.value = _registerState.value.copy(
            password = sanitized,
            passwordError = validatePassword(sanitized),
            confirmPasswordError = validateConfirmPassword(sanitized, _registerState.value.confirmPassword),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onConfirmPasswordChange(value: String) {
        val sanitized = value
            .filter { c -> c != '\n' && c != '\r' && c != '\t' && !SanitizationUtils.isInvisibleChar(c) }
        _registerState.value = _registerState.value.copy(
            confirmPassword = sanitized,
            confirmPasswordError = validateConfirmPassword(_registerState.value.password, sanitized),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onLoginEmailChange(value: String) {
        val sanitized = value
            .filter { c -> c != '\n' && c != '\r' && c != '\t' && c != ' ' && !SanitizationUtils.isInvisibleChar(c) }
            .lowercase()
        _loginState.value = _loginState.value.copy(
            email = sanitized,
            generalError = null
        )
    }

    fun onLoginPasswordChange(value: String) {
        val sanitized = value
            .filter { c -> c != '\n' && c != '\r' && c != '\t' && !SanitizationUtils.isInvisibleChar(c) }
        _loginState.value = _loginState.value.copy(
            password = sanitized,
            generalError = null
        )
    }

    fun validateLogin(): Boolean {
        val state = _loginState.value
        val emailError = validateEmail(state.email)
        val passwordError = validateLoginPassword(state.password)

        _loginState.value = state.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    fun onNameChange(value: String) {
        _registerState.value = _registerState.value.copy(
            name = value,
            nameError = validatePersonName(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onLastNameChange(value: String) {
        _registerState.value = _registerState.value.copy(
            lastName = value,
            lastNameError = validatePersonName(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onStudentNumberChange(value: String) {
        _registerState.value = _registerState.value.copy(
            studentNumber = value,
            studentNumberError = validateStudentNumber(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onCourseChange(value: String) {
        _registerState.value = _registerState.value.copy(
            course = value,
            courseError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onYearChange(value: String) {
        _registerState.value = _registerState.value.copy(
            year = value,
            yearError = validateYear(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onCompanyNameChange(value: String) {
        _registerState.value = _registerState.value.copy(
            companyName = value,
            companyNameError = validateCompanyName(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onNifChange(value: String) {
        _registerState.value = _registerState.value.copy(
            nif = value,
            nifError = validateNif(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onAreaChange(value: String) {
        _registerState.value = _registerState.value.copy(
            area = value,
            areaError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onLocationChange(value: String) {
        _registerState.value = _registerState.value.copy(
            location = value,
            locationError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onInstitutionNameChange(value: String) {
        _registerState.value = _registerState.value.copy(
            institutionName = value,
            institutionNameError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onInstitutionNifChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }.take(9)

        _registerState.value = _registerState.value.copy(
            institutionNif = filteredValue,
            institutionNifError = if (filteredValue.isNotBlank()) {
                validateNif(filteredValue)
            } else {
                null
            },
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onInstitutionLocalityChange(value: String) {
        _registerState.value = _registerState.value.copy(
            institutionLocality = value,
            institutionLocalityError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onInstitutionAddressChange(value: String) {
        _registerState.value = _registerState.value.copy(
            institutionAddress = value,
            institutionAddressError = null,
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onInstitutionPhoneChange(value: String) {
        _registerState.value = _registerState.value.copy(
            institutionPhone = value,
            institutionPhoneError = null,
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onTeacherDepartmentChange(value: String) {
        _registerState.value = _registerState.value.copy(
            teacherDepartment = value,
            teacherDepartmentError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun onTeacherPhoneChange(value: String) {
        _registerState.value = _registerState.value.copy(
            teacherPhone = value,
            teacherPhoneError = null,
            generalError = null,
            generalErrorText = null,
            isRegisterSuccess = false
        )
    }

    fun validateRegister(): Boolean {
        val state = _registerState.value

        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(
            password = state.password,
            confirmPassword = state.confirmPassword
        )

        val nameError =
            if (state.selectedRole == UserRole.STUDENT || state.selectedRole == UserRole.TEACHER) {
                validatePersonName(state.name)
            } else {
                null
            }

        val lastNameError =
            if (state.selectedRole == UserRole.STUDENT || state.selectedRole == UserRole.TEACHER) {
                validatePersonName(state.lastName)
            } else {
                null
            }

        val studentNumberError =
            if (state.selectedRole == UserRole.STUDENT) {
                validateStudentNumber(state.studentNumber)
            } else {
                null
            }

        val courseError =
            if (state.selectedRole == UserRole.STUDENT) {
                validateRequiredText(state.course)
            } else {
                null
            }

        val yearError =
            if (state.selectedRole == UserRole.STUDENT) {
                validateYear(state.year)
            } else {
                null
            }

        val companyNameError =
            if (state.selectedRole == UserRole.COMPANY) {
                validateCompanyName(state.companyName)
            } else {
                null
            }

        val nifError =
            if (state.selectedRole == UserRole.COMPANY) {
                validateNif(state.nif)
            } else {
                null
            }

        val areaError =
            if (state.selectedRole == UserRole.COMPANY) {
                validateRequiredText(state.area)
            } else {
                null
            }

        val locationError =
            if (state.selectedRole == UserRole.COMPANY) {
                validateRequiredText(state.location)
            } else {
                null
            }

        val institutionNameError =
            if (state.selectedRole == UserRole.INSTITUTION) {
                validateRequiredText(state.institutionName)
            } else {
                null
            }

        val institutionNifError =
            if (state.selectedRole == UserRole.INSTITUTION && state.institutionNif.isNotBlank()) {
                validateNif(state.institutionNif)
            } else {
                null
            }

        val institutionLocalityError =
            if (state.selectedRole == UserRole.INSTITUTION) {
                validateRequiredText(state.institutionLocality)
            } else {
                null
            }

        val teacherDepartmentError =
            if (state.selectedRole == UserRole.TEACHER) {
                validateRequiredText(state.teacherDepartment)
            } else {
                null
            }

        val hasErrors = listOf(
            nameError,
            lastNameError,
            emailError,
            passwordError,
            confirmPasswordError,
            studentNumberError,
            courseError,
            yearError,
            companyNameError,
            nifError,
            areaError,
            locationError,
            institutionNameError,
            institutionNifError,
            institutionLocalityError,
            teacherDepartmentError
        ).any { it != null }

        _registerState.value = state.copy(
            nameError = nameError,
            lastNameError = lastNameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            studentNumberError = studentNumberError,
            courseError = courseError,
            yearError = yearError,
            companyNameError = companyNameError,
            nifError = nifError,
            areaError = areaError,
            locationError = locationError,
            institutionNameError = institutionNameError,
            institutionNifError = institutionNifError,
            institutionLocalityError = institutionLocalityError,
            teacherDepartmentError = teacherDepartmentError,
            generalError = if (hasErrors) R.string.form_has_errors else null,
            generalErrorText = null,
            isRegisterSuccess = false
        )

        return !hasErrors
    }

    private fun validateLoginPassword(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            else -> null
        }
    }

    private fun validateRequiredText(value: String): Int? {
        return if (value.isBlank()) {
            R.string.error_required_field
        } else {
            null
        }
    }

    private fun validateEmail(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> R.string.error_invalid_email
            else -> null
        }
    }

    private fun validatePassword(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            value.length < 6 -> R.string.error_password_too_short
            else -> null
        }
    }

    private fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): Int? {
        return when {
            confirmPassword.isBlank() -> R.string.error_required_field
            password != confirmPassword -> R.string.error_passwords_do_not_match
            else -> null
        }
    }

    private fun validateStudentNumber(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            !value.all { it.isDigit() } -> R.string.error_student_number_digits
            else -> null
        }
    }

    private fun validateYear(value: String): Int? {
        val yearNumber = value.toIntOrNull()

        return when {
            value.isBlank() -> R.string.error_required_field
            yearNumber == null -> R.string.error_year_number
            yearNumber !in 1..6 -> R.string.error_year_range
            else -> null
        }
    }

    private fun validateNif(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            !value.all { it.isDigit() } -> R.string.error_nif_digits
            value.length != 9 -> R.string.error_nif_length
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

    private fun validateCompanyName(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            value.length < 2 -> R.string.error_company_name_too_short
            !value.all {
                it.isLetter() ||
                        it.isWhitespace() ||
                        it == '&' ||
                        it == '-' ||
                        it == '.'
            } -> R.string.error_invalid_company_name

            else -> null
        }
    }

    fun login(
        onSuccess: (UserRole) -> Unit
    ) {
        if (!validateLogin()) return

        val state = _loginState.value
        val sanitizedEmail = SanitizationUtils.sanitizeEmail(state.email)
        val sanitizedPassword = SanitizationUtils.sanitizePassword(state.password)

        viewModelScope.launch {
            _loginState.value = state.copy(
                isLoading = true,
                generalError = null
            )

            val result = authRepository.login(
                email = sanitizedEmail,
                password = sanitizedPassword
            )

            _loginState.value = _loginState.value.copy(
                isLoading = false
            )

            if (result.isSuccess) {
                val role = when (result.getOrNull()?.lowercase()) {
                    "student" -> UserRole.STUDENT
                    "company" -> UserRole.COMPANY
                    "advisor" -> UserRole.ADVISOR
                    "institution" -> UserRole.INSTITUTION
                    "teacher" -> UserRole.TEACHER
                    "admin" -> UserRole.ADMIN
                    else -> null
                }

                if (role != null) {
                    onSuccess(role)
                } else {
                    _loginState.value = _loginState.value.copy(
                        generalError = R.string.error_unknown_user_role
                    )
                }
            } else {
                _loginState.value = _loginState.value.copy(
                    generalError = mapAuthErrorToMessage(result.exceptionOrNull())
                )
            }
        }
    }

    fun register() {
        if (!validateRegister()) return

        val state = _registerState.value
        val sanitizedEmail = SanitizationUtils.sanitizeEmail(state.email)
        val sanitizedPassword = SanitizationUtils.sanitizePassword(state.password)

        viewModelScope.launch {
            _registerState.value = state.copy(
                generalError = null,
                generalErrorText = null,
                isRegisterSuccess = false
            )

            val result = when (state.selectedRole) {
                UserRole.STUDENT -> {
                    authRepository.registerInvitedStudent(
                        email = sanitizedEmail,
                        password = sanitizedPassword,
                        firstName = state.name,
                        lastName = state.lastName,
                        phone = state.teacherPhone.ifBlank { null },
                        studentNumber = state.studentNumber,
                        course = state.course,
                        academicYear = state.year.toIntOrNull() ?: 0
                    )
                }

                UserRole.COMPANY -> {
                    authRepository.registerCompany(
                        email = sanitizedEmail,
                        password = sanitizedPassword,
                        companyName = state.companyName,
                        nif = state.nif,
                        businessArea = state.area,
                        location = state.location
                    )
                }

                UserRole.INSTITUTION -> {
                    authRepository.registerInstitution(
                        name = state.institutionName,
                        nif = state.institutionNif.ifBlank { null },
                        locality = state.institutionLocality,
                        address = state.institutionAddress.ifBlank { null },
                        phone = state.institutionPhone.ifBlank { null },
                        email = sanitizedEmail,
                        password = sanitizedPassword
                    )
                }

                UserRole.ADVISOR -> {
                    advisorRegistrationRepository.registerAdvisor(
                        email = sanitizedEmail,
                        password = sanitizedPassword
                    )
                }

                UserRole.TEACHER -> {
                    authRepository.registerInvitedTeacher(
                        email = sanitizedEmail,
                        password = sanitizedPassword,
                        firstName = state.name,
                        lastName = state.lastName,
                        department = state.teacherDepartment.ifBlank { null },
                        phone = state.teacherPhone.ifBlank { null }
                    )
                }

                UserRole.ADMIN -> {
                    Result.failure(IllegalStateException("Admin registration is not allowed"))
                }
            }

            if (result.isSuccess) {
                _registerState.value = _registerState.value.copy(
                    isRegisterSuccess = true,
                    generalError = null,
                    generalErrorText = null
                )
            } else {
                _registerState.value = _registerState.value.copy(
                    generalError = mapAuthErrorToMessage(result.exceptionOrNull()),
                    generalErrorText = null,
                    isRegisterSuccess = false
                )
            }
        }
    }
}