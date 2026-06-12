package com.example.nextstep.ui.screens.teacher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.TeacherRequestsRepository
import com.example.nextstep.data.repository.TeacherStudentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherHomeUiState(
    val isLoading: Boolean = false,
    val pendingRequestsCount: Int = 0,
    val studentsFollowedCount: Int = 0,
    val pendingEvaluationsCount: Int = 0,
    val errorMessage: String? = null
)

class TeacherHomeViewModel(
    private val requestsRepository: TeacherRequestsRepository = TeacherRequestsRepository(),
    private val studentsRepository: TeacherStudentsRepository = TeacherStudentsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherHomeUiState())
    val uiState: StateFlow<TeacherHomeUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Load requests to count pending
                val requestsResult = requestsRepository.getRequests()
                val pendingCount = requestsResult.getOrDefault(emptyList())
                    .count { it.status.lowercase() == "pending" }

                // Load students
                val studentsResult = studentsRepository.getStudents()
                val students = studentsResult.getOrDefault(emptyList())
                val studentsCount = students.size
                
                // Count pending evaluations
                val pendingEvalCount = students.count { 
                    val status = it.status?.lowercase() ?: ""
                    status == "to_evaluate" || status == "pendente" 
                }

                Log.d("TeacherHomeVM", "pending requests = $pendingCount")
                Log.d("TeacherHomeVM", "students count = $studentsCount")

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        pendingRequestsCount = pendingCount,
                        studentsFollowedCount = studentsCount,
                        pendingEvaluationsCount = pendingEvalCount
                    )
                }
            } catch (e: Exception) {
                Log.e("TeacherHomeVM", "Error loading dashboard", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar dados do dashboard.") }
            }
        }
    }
}
