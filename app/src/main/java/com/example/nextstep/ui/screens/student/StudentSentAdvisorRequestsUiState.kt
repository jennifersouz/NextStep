package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.SentAdvisorRequestDto

data class StudentSentAdvisorRequestsUiState(
    val isLoading: Boolean = false,
    val requests: List<SentAdvisorRequestDto> = emptyList(),
    val searchQuery: String = "",
    val isCancellingId: String? = null,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredRequests: List<SentAdvisorRequestDto>
        get() = if (searchQuery.isBlank()) {
            requests
        } else {
            requests.filter { request ->
                val name = request.teacherName ?: ""
                name.contains(searchQuery, ignoreCase = true)
            }
        }
}
