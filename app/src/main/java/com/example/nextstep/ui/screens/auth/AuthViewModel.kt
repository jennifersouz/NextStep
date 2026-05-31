package com.example.nextstep.ui.screens.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val authRepository = AuthRepository()

    private fun mapAuthErrorToMessage(exception: Throwable?): Int {
        val message = exception?.message.orEmpty().lowercase()

        return when {
            "user_already_exists" in message || "already registered" in message ->
                R.string.error_user_already_exists

            "invalid login credentials" in message ->
                R.string.error_invalid_credentials

            "email" in message && "invalid" in message ->
                R.string.error_invalid_email

            "password" in message ->
                R.string.error_password_invalid_supabase

            "network" in message || "unable to resolve host" in message || "timeout" in message ->
                R.string.error_network

            else ->
                R.string.error_unknown_auth
        }
    }

    fun onRoleChange(role: UserRole) {
        _registerState.value = _registerState.value.copy(
            selectedRole = role,
            nameError = null,
            lastNameError = null,
            studentNumberError = null,
            courseError = null,
            yearError = null,
            companyNameError = null,
            nifError = null,
            areaError = null,
            locationError = null,
            generalError = null,
            generalErrorText = null
        )
    }

    fun onLoginEmailChange(value: String) {
        val cleanedValue = value.trim()

        _loginState.value = _loginState.value.copy(
            email = cleanedValue,
            emailError = validateEmail(cleanedValue),
            generalError = null
        )
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.value = _loginState.value.copy(
            password = value,
            passwordError = validateLoginPassword(value),
            generalError = null
        )
    }

    fun validateLogin(): Boolean {
        val state = _loginState.value

        val emailError = validateEmail(state.email)
        val passwordError = validateLoginPassword(state.password)

        val hasErrors = emailError != null || passwordError != null

        _loginState.value = state.copy(
            emailError = emailError,
            passwordError = passwordError,
            generalError = if (hasErrors) R.string.form_has_errors else null
        )

        return !hasErrors
    }

    fun onNameChange(value: String) {
        val filteredValue = value.filter { it.isLetter() || it.isWhitespace() }

        _registerState.value = _registerState.value.copy(
            name = filteredValue,
            nameError = validatePersonName(filteredValue),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onLastNameChange(value: String) {
        val filteredValue = value.filter { it.isLetter() || it.isWhitespace() }

        _registerState.value = _registerState.value.copy(
            lastName = filteredValue,
            lastNameError = validatePersonName(filteredValue),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onEmailChange(value: String) {
        _registerState.value = _registerState.value.copy(
            email = value.trim(),
            emailError = validateEmail(value.trim()),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onPasswordChange(value: String) {
        val state = _registerState.value

        _registerState.value = state.copy(
            password = value,
            passwordError = validatePassword(value),
            confirmPasswordError = if (state.confirmPassword.isNotBlank()) {
                validateConfirmPassword(value, state.confirmPassword)
            } else {
                null
            },
            generalError = null,
            generalErrorText = null
        )
    }

    fun onConfirmPasswordChange(value: String) {
        _registerState.value = _registerState.value.copy(
            confirmPassword = value,
            confirmPasswordError = null,
            generalError = null,
            generalErrorText = null
        )
    }

    fun onStudentNumberChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }

        _registerState.value = _registerState.value.copy(
            studentNumber = filteredValue,
            studentNumberError = validateStudentNumber(filteredValue),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onYearChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }

        _registerState.value = _registerState.value.copy(
            year = filteredValue,
            yearError = validateYear(filteredValue),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onCompanyNameChange(value: String) {
        val filteredValue = value.filter {
            it.isLetter() ||
                    it.isWhitespace() ||
                    it == '&' ||
                    it == '-' ||
                    it == '.'
        }

        _registerState.value = _registerState.value.copy(
            companyName = filteredValue,
            companyNameError = validateCompanyName(filteredValue),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onNifChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }.take(9)

        _registerState.value = _registerState.value.copy(
            nif = filteredValue,
            nifError = validateNif(filteredValue),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onCourseChange(value: String) {
        _registerState.value = _registerState.value.copy(
            course = value,
            courseError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onAreaChange(value: String) {
        _registerState.value = _registerState.value.copy(
            area = value,
            areaError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null
        )
    }

    fun onLocationChange(value: String) {
        _registerState.value = _registerState.value.copy(
            location = value,
            locationError = validateRequiredText(value),
            generalError = null,
            generalErrorText = null
        )
    }

    fun validateRegister(): Boolean {
        val state = _registerState.value

        // Validação comum
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(
            password = state.password,
            confirmPassword = state.confirmPassword
        )

        // Validação condicional por Role
        val nameError =
            if (state.selectedRole == UserRole.STUDENT) validatePersonName(state.name) else null

        val lastNameError =
            if (state.selectedRole == UserRole.STUDENT) validatePersonName(state.lastName) else null

        val studentNumberError =
            if (state.selectedRole == UserRole.STUDENT) validateStudentNumber(state.studentNumber) else null

        val courseError =
            if (state.selectedRole == UserRole.STUDENT) validateRequiredText(state.course) else null

        val yearError =
            if (state.selectedRole == UserRole.STUDENT) validateYear(state.year) else null

        val companyNameError =
            if (state.selectedRole == UserRole.COMPANY) validateCompanyName(state.companyName) else null

        val nifError =
            if (state.selectedRole == UserRole.COMPANY) validateNif(state.nif) else null

        val areaError =
            if (state.selectedRole == UserRole.COMPANY) validateRequiredText(state.area) else null

        val locationError =
            if (state.selectedRole == UserRole.COMPANY) validateRequiredText(state.location) else null

        // Logs de Debug
        Log.d("AuthViewModel", "selectedRole=${state.selectedRole}")
        Log.d("AuthViewModel", "nameError=$nameError, lastNameError=$lastNameError, studentNumberError=$studentNumberError, courseError=$courseError, yearError=$yearError")
        Log.d("AuthViewModel", "companyNameError=$companyNameError, nifError=$nifError, areaError=$areaError, locationError=$locationError")
        Log.d("AuthViewModel", "emailError=$emailError, passwordError=$passwordError, confirmPasswordError=$confirmPasswordError")

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
            locationError
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
            generalError = if (hasErrors) R.string.form_has_errors else null,
            generalErrorText = null
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
        return if (value.isBlank()) R.string.error_required_field else null
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

    private fun validateConfirmPassword(password: String, confirmPassword: String): Int? {
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

    fun login(onSuccess: (UserRole) -> Unit) {
        if (!validateLogin()) return

        val state = _loginState.value

        viewModelScope.launch {
            _loginState.value = state.copy(
                isLoading = true,
                generalError = null
            )

            val result = authRepository.login(
                email = state.email,
                password = state.password
            )

            _loginState.value = _loginState.value.copy(
                isLoading = false
            )

            if (result.isSuccess) {
                val role = when (result.getOrNull()) {
                    "student" -> UserRole.STUDENT
                    "company" -> UserRole.COMPANY
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

        viewModelScope.launch {
            _registerState.value = state.copy(
                generalError = null
            )

            val result = when (state.selectedRole) {
                UserRole.STUDENT -> {
                    authRepository.registerStudent(
                        email = state.email,
                        password = state.password,
                        firstName = state.name,
                        lastName = state.lastName,
                        studentNumber = state.studentNumber,
                        course = state.course,
                        academicYear = state.year.toInt()
                    )
                }

                UserRole.COMPANY -> {
                    authRepository.registerCompany(
                        email = state.email,
                        password = state.password,
                        companyName = state.companyName,
                        nif = state.nif,
                        businessArea = state.area,
                        location = state.location
                    )
                }
            }

            if (result.isSuccess) {
                _registerState.value = _registerState.value.copy(
                    isRegisterSuccess = true
                )
            } else {
                _registerState.value = _registerState.value.copy(
                    generalError = mapAuthErrorToMessage(result.exceptionOrNull())
                )
            }
        }
    }
}
