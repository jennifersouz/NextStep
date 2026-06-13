package com.example.nextstep.ui.screens.institution

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                errorMessage = null
            )

            val result = repository.getInstitutionTeachers(filter)

            result.fold(
                onSuccess = { teachers ->
                    _uiState.value = _uiState.value.copy(
                        teachers = teachers,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionTeachersVM", "Erro ao carregar docentes", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar os docentes."
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
            val currentSuccessMessage = if (preserveSuccess) _uiState.value.successMessage else null
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = currentSuccessMessage
            )

            val result = repository.getInstitutionTeacherDetail(teacherProfileId)

            result.fold(
                onSuccess = { teacher ->
                    _uiState.value = _uiState.value.copy(
                        teacher = teacher,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionTeacherDetailVM", "Erro ao carregar detalhe do docente", exception)
                    if (currentSuccessMessage == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Não foi possível carregar o docente."
                        )
                    } else {
                        // Se estávamos a recarregar após arquivar, não substituir a mensagem de sucesso por erro
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            )
        }
    }

    fun archiveTeacher(teacherProfileId: String, reason: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null, successMessage = null)
            
            val result = repository.archiveTeacher(teacherProfileId, reason)
            
            result.fold(
                onSuccess = { updatedTeacher ->
                    // Só mostrar sucesso se institutionArchivedAt foi realmente preenchido
                    if (updatedTeacher.institutionArchivedAt != null) {
                        _uiState.value = _uiState.value.copy(
                            teacher = updatedTeacher,
                            isActionLoading = false,
                            successMessage = "Docente arquivado com sucesso."
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isActionLoading = false,
                            errorMessage = "Não foi possível arquivar o docente."
                        )
                    }
                },
                onFailure = { exception ->
                    Log.e("InstitutionTeacherDetailVM", "Erro ao arquivar docente", exception)
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        errorMessage = "Não foi possível arquivar o docente."
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}