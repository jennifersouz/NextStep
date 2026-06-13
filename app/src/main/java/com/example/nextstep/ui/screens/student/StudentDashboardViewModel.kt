package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.NotificationsRepository
import com.example.nextstep.data.repository.OffersRepository
import com.example.nextstep.data.repository.StudentNotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentDashboardViewModel : ViewModel() {

    private val offersRepository = OffersRepository()
    private val notificationsRepository = StudentNotificationsRepository()
    private val tableNotificationsRepository = NotificationsRepository()
    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadOffers()
        loadUnreadNotificationsCount()
    }

    fun onSearchChange(value: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = value
        )
    }

    fun loadOffers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = offersRepository.getActiveOffers()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offers = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    offers = emptyList(),
                    isLoading = false,
                    errorMessage = "Não foi possível carregar as ofertas. Tente novamente."
                )
            }
        }
    }

    fun loadUnreadNotificationsCount() {
        viewModelScope.launch {
            var total = 0

            val viewResult = notificationsRepository.getUnreadNotificationsCount()
            if (viewResult.isSuccess) {
                total += viewResult.getOrDefault(0)
            }

            val tableResult = tableNotificationsRepository.getNotifications()
            if (tableResult.isSuccess) {
                val tableUnread = tableResult.getOrDefault(emptyList())
                    .count { n ->
                        !n.isRead && n.type in listOf("message", "evaluation", "teacher_assigned")
                    }
                total += tableUnread
            }

            _uiState.value = _uiState.value.copy(
                unreadNotificationsCount = total
            )
        }
    }
    fun setUnreadNotificationsCount(count: Int) {
        _uiState.value = _uiState.value.copy(
            unreadNotificationsCount = count
        )
    }

    fun onAreaFilterSelected(area: String?) {
        _uiState.value = _uiState.value.copy(
            selectedArea = area
        )
    }

    fun onWorkModeFilterSelected(workMode: String?) {
        _uiState.value = _uiState.value.copy(
            selectedWorkMode = workMode
        )
    }

    fun onLocationFilterSelected(location: String?) {
        _uiState.value = _uiState.value.copy(
            selectedLocation = location
        )
    }

    fun onOnlyWithVacanciesChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            onlyWithVacancies = value
        )
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            selectedArea = null,
            selectedWorkMode = null,
            selectedLocation = null,
            onlyWithVacancies = false
        )
    }
}