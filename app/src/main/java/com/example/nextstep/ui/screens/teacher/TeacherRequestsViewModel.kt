package com.example.nextstep.ui.screens.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.TeacherOrientationRequestDto
import com.example.nextstep.data.repository.TeacherRequestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherRequestsUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val requests: List<TeacherOrientationRequestDto> = emptyList(),
    val filteredRequests: List<TeacherOrientationRequestDto> = emptyList(),
    val selectedFilter: String = "", // Will be set from string resources
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class TeacherRequestsViewModel(
    private val repository: TeacherRequestsRepository = TeacherRequestsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherRequestsUiState())
    val uiState: StateFlow<TeacherRequestsUiState> = _uiState.asStateFlow()

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getRequests()
                .onSuccess { requests ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            requests = requests,
                            filteredRequests = applyFilters(requests, it.searchQuery, it.selectedFilter)
                        ) 
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar pedidos.") }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { 
            val filtered = applyFilters(it.requests, query, it.selectedFilter)
            it.copy(searchQuery = query, filteredRequests = filtered) 
        }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { 
            val filtered = applyFilters(it.requests, it.searchQuery, filter)
            it.copy(selectedFilter = filter, filteredRequests = filtered) 
        }
    }

    private fun applyFilters(
        requests: List<TeacherOrientationRequestDto>,
        query: String,
        filter: String
    ): List<TeacherOrientationRequestDto> {
        return requests.filter { request ->
            val matchesQuery = query.isEmpty() || request.studentName.contains(query, ignoreCase = true) || 
                              request.offerTitle.contains(query, ignoreCase = true)
            
            val filterType = getFilterType(filter)
            val matchesFilter = when (filterType) {
                TeacherRequestFilter.PENDING -> request.status.lowercase() == "pending"
                TeacherRequestFilter.ACCEPTED -> request.status.lowercase() == "accepted"
                TeacherRequestFilter.REJECTED -> request.status.lowercase() == "rejected"
                TeacherRequestFilter.ALL -> true
            }
            
            matchesQuery && matchesFilter
        }
    }

    private fun getFilterType(filterName: String): TeacherRequestFilter {
        return when {
            filterName.contains("pending", ignoreCase = true) -> TeacherRequestFilter.PENDING
            filterName.contains("accepted", ignoreCase = true) -> TeacherRequestFilter.ACCEPTED
            filterName.contains("rejected", ignoreCase = true) -> TeacherRequestFilter.REJECTED
            filterName.contains("pendente", ignoreCase = true) -> TeacherRequestFilter.PENDING
            filterName.contains("aceite", ignoreCase = true) -> TeacherRequestFilter.ACCEPTED
            filterName.contains("rejeitado", ignoreCase = true) -> TeacherRequestFilter.REJECTED
            else -> TeacherRequestFilter.ALL
        }
    }

    fun acceptRequest(applicationId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            repository.acceptRequest(applicationId)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false) }
                    loadRequests()
                    onSuccess()
                }
                .onFailure {
                    _uiState.update { it.copy(isUpdating = false, errorMessage = "Erro ao aceitar pedido.") }
                }
        }
    }

    fun rejectRequest(applicationId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            repository.rejectRequest(applicationId)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false) }
                    loadRequests()
                    onSuccess()
                }
                .onFailure {
                    _uiState.update { it.copy(isUpdating = false, errorMessage = "Erro ao rejeitar pedido.") }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
