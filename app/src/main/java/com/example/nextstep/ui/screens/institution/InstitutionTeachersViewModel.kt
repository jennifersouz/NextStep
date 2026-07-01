package com.example.nextstep.ui.screens.institution

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.InstitutionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InstitutionTeachersViewModel : ViewModel() {

    private val repository = InstitutionRepository()

    private val _uiState = MutableStateFlow(InstitutionTeachersUiState())
    val uiState: StateFlow<InstitutionTeachersUiState> = _uiState.asStateFlow()

    fun loadTeachers(filter: InstitutionRepository.ArchiveFilter = _uiState.value.filter) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                filter = filter,
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getInstitutionTeachers(filter)

            result.fold(
                onSuccess = { teachers ->
                    _uiState.value = _uiState.value.copy(
                        teachers = teachers,
                        isLoading = false,
                        errorMessageRes = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionTeachersVM", "Erro ao carregar docentes", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessageRes = R.string.teachers_load_error
                    )
                }
            )
        }
    }
}

class InstitutionTeacherDetailViewModel : ViewModel() {

    private val repository = InstitutionRepository()

    private val _uiState = MutableStateFlow(InstitutionTeacherDetailUiState())
    val uiState: StateFlow<InstitutionTeacherDetailUiState> = _uiState.asStateFlow()

    fun loadTeacherDetail(teacherProfileId: String, preserveSuccess: Boolean = false) {
        viewModelScope.launch {
            val currentSuccessMessageRes = if (preserveSuccess) _uiState.value.successMessageRes else null
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null,
                successMessageRes = currentSuccessMessageRes
            )

            val result = repository.getInstitutionTeacherDetail(teacherProfileId)

            result.fold(
                onSuccess = { teacher ->
                    _uiState.value = _uiState.value.copy(
                        teacher = teacher,
                        isLoading = false,
                        errorMessageRes = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionTeacherDetailVM", "Erro ao carregar detalhe do docente", exception)
                    if (currentSuccessMessageRes == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessageRes = R.string.teacher_load_error
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            )
        }
    }

    fun archiveTeacher(teacherProfileId: String, reason: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessageRes = null, successMessageRes = null)
            
            val result = repository.archiveTeacher(teacherProfileId, reason)
            
            result.fold(
                onSuccess = { updatedTeacher ->
                    if (updatedTeacher.institutionArchivedAt != null) {
                        _uiState.value = _uiState.value.copy(
                            teacher = updatedTeacher,
                            isActionLoading = false,
                            successMessageRes = R.string.teacher_archive_success
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isActionLoading = false,
                            errorMessageRes = R.string.teacher_archive_error
                        )
                    }
                },
                onFailure = { exception ->
                    Log.e("InstitutionTeacherDetailVM", "Erro ao arquivar docente", exception)
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        errorMessageRes = R.string.teacher_archive_error
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessageRes = null, successMessageRes = null)
    }
}