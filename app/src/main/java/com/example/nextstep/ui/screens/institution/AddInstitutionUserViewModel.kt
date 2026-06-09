package com.example.nextstep.ui.screens.institution

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.InstitutionUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddInstitutionUserViewModel : ViewModel() {

    private val institutionUsersRepository = InstitutionUsersRepository()

    private val _uiState = MutableStateFlow(AddInstitutionUserUiState())
    val uiState: StateFlow<AddInstitutionUserUiState> = _uiState.asStateFlow()

    fun onTypeSelected(type: UserType) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            firstNameError = null,
            lastNameError = null,
            emailError = null,
            phoneError = null,
            studentNumberError = null,
            courseError = null,
            academicYearError = null,
            departmentError = null,
            errorMessage = null
        )
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            firstNameError = validatePersonName(value),
            errorMessage = null
        )
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            lastNameError = validatePersonName(value),
            errorMessage = null
        )
    }

    fun onEmailChange(value: String) {
        val cleanedValue = value.trim()
        _uiState.value = _uiState.value.copy(
            email = cleanedValue,
            emailError = validateEmail(cleanedValue),
            errorMessage = null
        )
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phone = value,
            phoneError = null,
            errorMessage = null
        )
    }

    fun onStudentNumberChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(
            studentNumber = filteredValue,
            studentNumberError = validateStudentNumber(filteredValue),
            errorMessage = null
        )
    }

    fun onCourseChange(value: String) {
        _uiState.value = _uiState.value.copy(
            course = value,
            courseError = validateRequiredText(value),
            errorMessage = null
        )
    }

    fun onAcademicYearChange(value: String) {
        val filteredValue = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(
            academicYear = filteredValue,
            academicYearError = validateYear(filteredValue),
            errorMessage = null
        )
    }

    fun onDepartmentChange(value: String) {
        _uiState.value = _uiState.value.copy(
            department = value,
            departmentError = validateRequiredText(value),
            errorMessage = null
        )
    }

    fun createInvite() {
        val state = _uiState.value

        val firstNameError = validatePersonName(state.firstName)
        val lastNameError = validatePersonName(state.lastName)
        val emailError = validateEmail(state.email)

        val studentNumberError = if (state.selectedType == UserType.STUDENT) {
            validateStudentNumber(state.studentNumber)
        } else {
            null
        }

        val courseError = if (state.selectedType == UserType.STUDENT) {
            validateRequiredText(state.course)
        } else {
            null
        }

        val academicYearError = if (state.selectedType == UserType.STUDENT) {
            validateYear(state.academicYear)
        } else {
            null
        }

        val departmentError = if (state.selectedType == UserType.TEACHER) {
            validateRequiredText(state.department)
        } else {
            null
        }

        val hasErrors = listOf(
            firstNameError,
            lastNameError,
            emailError,
            studentNumberError,
            courseError,
            academicYearError,
            departmentError
        ).any { it != null }

        _uiState.value = state.copy(
            firstNameError = firstNameError,
            lastNameError = lastNameError,
            emailError = emailError,
            studentNumberError = studentNumberError,
            courseError = courseError,
            academicYearError = academicYearError,
            departmentError = departmentError,
            errorMessage = if (hasErrors) "Corrija os campos assinalados antes de continuar." else null
        )

        if (hasErrors) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            val result = institutionUsersRepository.createInvite(
                targetRole = if (state.selectedType == UserType.STUDENT) "student" else "teacher",
                email = state.email,
                firstName = state.firstName,
                lastName = state.lastName,
                studentNumber = state.studentNumber.ifBlank { null },
                course = state.course.ifBlank { null },
                academicYear = state.academicYear.toIntOrNull(),
                department = state.department.ifBlank { null },
                phone = state.phone.ifBlank { null }
            )

            _uiState.value = state.copy(isLoading = false)

            if (result.isSuccess) {
                _uiState.value = state.copy(isSuccess = true, errorMessage = null)
            } else {
                _uiState.value = state.copy(
                    errorMessage = "Não foi possível criar o convite. Tente novamente.",
                    isSuccess = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
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

    private fun validatePersonName(value: String): Int? {
        return when {
            value.isBlank() -> R.string.error_required_field
            value.length < 2 -> R.string.error_name_too_short
            !value.all { it.isLetter() || it.isWhitespace() } -> R.string.error_only_letters
            else -> null
        }
    }
}

data class AddInstitutionUserUiState(
    val selectedType: UserType = UserType.STUDENT,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val studentNumber: String = "",
    val course: String = "",
    val academicYear: String = "",
    val department: String = "",
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val emailError: Int? = null,
    val phoneError: Int? = null,
    val studentNumberError: Int? = null,
    val courseError: Int? = null,
    val academicYearError: Int? = null,
    val departmentError: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
