package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentInternshipsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentInternshipsViewModel : ViewModel() {

    private val repository = StudentInternshipsRepository()

    private val _uiState = MutableStateFlow(StudentInternshipsUiState())
    val uiState: StateFlow<StudentInternshipsUiState> = _uiState.asStateFlow()

    init {
        loadInternships()
    }

    fun loadInternships() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getStudentInternships()

            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList()).map { dto ->
                    InternshipCardUi(
                        id = dto.id,
                        title = dto.offerTitle,
                        companyName = dto.companyName,
                        advisorName = dto.advisorName,
                        teacherName = dto.teacherName,
                        teacherStatus = dto.teacherStatus,
                        completed = dto.status == "completed"
                    )
                }
                _uiState.value = _uiState.value.copy(
                    internships = list,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.company_internships_load_error
                )
            }
        }
    }
}
