package com.example.nextstep.ui.screens.institution

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.InstitutionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InstitutionStudentsViewModel : ViewModel() {

    private val repository = InstitutionRepository()

    private val _uiState = MutableStateFlow(InstitutionStudentsUiState())
    val uiState: StateFlow<InstitutionStudentsUiState> = _uiState.asStateFlow()

    fun loadStudents(filter: InstitutionRepository.ArchiveFilter = _uiState.value.filter) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                filter = filter,
                isLoading = true,
                errorMessage = null
            )

            val result = repository.getInstitutionStudents(filter)

            result.fold(
                onSuccess = { students ->
                    _uiState.value = _uiState.value.copy(
                        students = students,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionStudentsVM", "Erro ao carregar alunos", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar os alunos."
                    )
                }
            )
        }
    }
}

class InstitutionStudentDetailViewModel : ViewModel() {

    private val repository = InstitutionRepository()

    private val _uiState = MutableStateFlow(InstitutionStudentDetailUiState())
    val uiState: StateFlow<InstitutionStudentDetailUiState> = _uiState.asStateFlow()

    fun loadStudentDetail(studentProfileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            val result = repository.getInstitutionStudentDetail(studentProfileId)

            result.fold(
                onSuccess = { student ->
                    _uiState.value = _uiState.value.copy(
                        student = student,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionStudentDetailVM", "Erro ao carregar detalhe do aluno", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar o aluno."
                    )
                }
            )
        }
    }

    fun archiveStudent(studentProfileId: String, reason: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null, successMessage = null)
            
            val result = repository.archiveStudent(studentProfileId, reason)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        successMessage = "Aluno arquivado com sucesso."
                    )
                    loadStudentDetail(studentProfileId)
                },
                onFailure = { exception ->
                    Log.e("InstitutionStudentDetailVM", "Erro ao arquivar aluno", exception)
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        errorMessage = "Não foi possível arquivar o aluno."
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
