package com.example.nextstep.ui.screens.teacher

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.TeacherOrientationRequestDto
import com.example.nextstep.data.repository.TeacherRequestsRepository
import com.example.nextstep.data.repository.TeacherStudentsRepository
import com.example.nextstep.data.repository.TeacherMessagesRepository
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
    val recentRequests: List<TeacherOrientationRequestDto> = emptyList(),
    val recentActivities: List<TeacherActivity> = emptyList(),
    @StringRes val errorMessageRes: Int? = null
)

data class TeacherActivity(
    val id: String,
    val title: String,
    val description: String? = null,
    val timestamp: String,
    val type: TeacherActivityType
)

enum class TeacherActivityType {
    NEW_REQUEST, REQUEST_ACCEPTED, REQUEST_REJECTED, NEW_MESSAGE, EVALUATION_SAVED
}

class TeacherHomeViewModel(
    private val requestsRepository: TeacherRequestsRepository = TeacherRequestsRepository(),
    private val studentsRepository: TeacherStudentsRepository = TeacherStudentsRepository(),
    private val messagesRepository: TeacherMessagesRepository = TeacherMessagesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherHomeUiState())
    val uiState: StateFlow<TeacherHomeUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            try {
                // 1. Load Requests
                val requestsResult = requestsRepository.getRequests()
                val allRequests = requestsResult.getOrDefault(emptyList())
                
                val pendingRequests = allRequests
                    .filter { it.status.lowercase() == "pending" }
                    .sortedByDescending { it.createdAt }

                val pendingCount = pendingRequests.size
                val recentRequests = pendingRequests.take(3)

                // 2. Load Students
                val studentsResult = studentsRepository.getStudents()
                val students = studentsResult.getOrDefault(emptyList())
                val studentsCount = students.size
                
                // Count pending evaluations
                val pendingEvalCount = students.count { 
                    val status = it.status?.lowercase() ?: ""
                    status == "to_evaluate" || status == "pendente" 
                }

                // 3. Load Conversations for activities
                val conversationsResult = messagesRepository.getConversations()
                val conversations = conversationsResult.getOrDefault(emptyList())

                // Combine for activities
                val activities = mutableListOf<TeacherActivity>()
                
                // Add requests/responses as activities
                allRequests.forEach { req ->
                    val type = when(req.status.lowercase()) {
                        "accepted" -> TeacherActivityType.REQUEST_ACCEPTED
                        "rejected" -> TeacherActivityType.REQUEST_REJECTED
                        else -> TeacherActivityType.NEW_REQUEST
                    }
                    val title = when(type) {
                        TeacherActivityType.REQUEST_ACCEPTED -> "Pedido aceite"
                        TeacherActivityType.REQUEST_REJECTED -> "Pedido rejeitado"
                        else -> "Novo pedido recebido"
                    }
                    activities.add(TeacherActivity(
                        id = "req_${req.applicationId}",
                        title = title,
                        description = "${req.studentName} - ${req.offerTitle}",
                        timestamp = req.createdAt,
                        type = type
                    ))
                }
                
                // Add messages as activities
                conversations.filter { !it.lastMessage.isNullOrBlank() }.forEach { conv ->
                    activities.add(TeacherActivity(
                        id = "msg_${conv.applicationId}",
                        title = "Nova mensagem",
                        description = "${conv.studentName}: ${conv.lastMessage}",
                        timestamp = conv.lastMessageAt ?: "",
                        type = TeacherActivityType.NEW_MESSAGE
                    ))
                }
                
                // Sort activities by timestamp descending and take 4
                val sortedActivities = activities.filter { it.timestamp.isNotBlank() }
                    .sortedByDescending { it.timestamp }
                    .take(4)

                Log.d("TeacherHomeVM", "Dashboard loaded. Pending: $pendingCount, Students: $studentsCount, Activities: ${sortedActivities.size}")

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        pendingRequestsCount = pendingCount,
                        studentsFollowedCount = studentsCount,
                        pendingEvaluationsCount = pendingEvalCount,
                        recentRequests = recentRequests,
                        recentActivities = sortedActivities
                    )
                }
            } catch (e: Exception) {
                Log.e("TeacherHomeVM", "Error loading dashboard", e)
                _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.teacher_dashboard_load_error) }
            }
        }
    }
}
