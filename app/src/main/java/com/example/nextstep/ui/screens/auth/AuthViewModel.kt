package com.example.nextstep.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.nextstep.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun onRoleChange(role: UserRole) {
        _registerState.value = RegisterUiState(selectedRole = role)
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
            generalError = null
        )
    }

    fun onLastNameChange(value: String) {
        val filteredValue = value.filter { it.isLetter() || it.isWhitespace() }

        _registerState.value = _registerState.value.copy(
            lastName = filteredValue,
            lastNameError = validatePersonName(filteredValue),
            generalError = null
        )
    }

    fun onEmailChange(value: String) {
        _registerState.value = _registerState.value.copy(
            email = value.trim(),
            emailError = validateEmail(value.trim()),
            generalError = null
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
            generalError = null
        )
    }

    fun onConfirmPasswordChange(value: String) {
        _registerState.value = _registerState.value.copy(
            confirmPassword = value,
            confirmPasswordError = null,
            generalError = null
        )
    }

    fun onStudentNumberChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }

        _registerState.value = _registerState.value.copy(
            studentNumber = filteredValue,
            studentNumberError = validateStudentNumber(filteredValue),
            generalError = null
        )
    }

    fun onYearChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }

        _registerState.value = _registerState.value.copy(
            year = filteredValue,
            yearError = validateYear(filteredValue),
            generalError = null
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
            generalError = null
        )
    }

    fun onNifChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }.take(9)

        _registerState.value = _registerState.value.copy(
            nif = filteredValue,
            nifError = validateNif(filteredValue),
            generalError = null
        )
    }

    fun onCourseChange(value: String) {
        _registerState.value = _registerState.value.copy(
            course = value,
            courseError = validateRequiredText(value),
            generalError = null
        )
    }

    fun onAreaChange(value: String) {
        _registerState.value = _registerState.value.copy(
            area = value,
            areaError = validateRequiredText(value),
            generalError = null
        )
    }

    fun onLocationChange(value: String) {
        _registerState.value = _registerState.value.copy(
            location = value,
            locationError = validateRequiredText(value),
            generalError = null
        )
    }

    fun validateRegister(): Boolean {
        val state = _registerState.value

        val nameError = validatePersonName(state.name)
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(
            password = state.password,
            confirmPassword = state.confirmPassword
        )

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
            generalError = if (hasErrors) R.string.form_has_errors else null
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
}