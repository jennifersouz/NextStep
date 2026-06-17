package com.example.nextstep.ui.screens.advisor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorProfileRepository
import com.example.nextstep.data.repository.TeacherProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileDetailData(
    val name: String,
    val email: String,
    val phone: String?,
    val department: String?,
    val roleType: String
)

data class ProfileDetailUiState(
    val profile: ProfileDetailData? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ProfileDetailViewModel : ViewModel() {

    private val advisorRepository = AdvisorProfileRepository()
    private val teacherRepository = TeacherProfileRepository()

    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()

    fun loadProfile(profileId: String, type: String) {
        Log.d("ProfileDebug", "ViewModel loading profileId=$profileId type=$type")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = when (type) {
                "advisor" -> advisorRepository.getAdvisorById(profileId)
                "teacher" -> teacherRepository.getTeacherById(profileId)
                else -> Result.failure(IllegalArgumentException("Tipo inválido: $type"))
            }

            _uiState.value = if (result.isSuccess) {
                val dto = result.getOrNull()
                val data = when (type) {
                    "advisor" -> {
                        val advisor = dto as com.example.nextstep.data.model.AdvisorProfileDto
                        Log.d("ProfileDetail", "Nome: ${advisor.name}")
                        ProfileDetailData(
                            name = advisor.name ?: "",
                            email = advisor.email ?: "",
                            phone = advisor.phone,
                            department = advisor.department,
                            roleType = "advisor"
                        )
                    }
                    "teacher" -> {
                        val teacher = dto as com.example.nextstep.data.model.TeacherProfileDto
                        Log.d("ProfileDetail", "Nome: ${teacher.displayName}")
                        ProfileDetailData(
                            name = teacher.displayName,
                            email = teacher.email ?: "",
                            phone = teacher.phone,
                            department = teacher.department,
                            roleType = "teacher"
                        )
                    }
                    else -> ProfileDetailData("", "", null, null, "")
                }
                _uiState.value.copy(
                    profile = data,
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    profile = null,
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Erro ao carregar perfil"
                )
            }
        }
    }
}
