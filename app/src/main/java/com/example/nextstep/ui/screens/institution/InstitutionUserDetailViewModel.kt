package com.example.nextstep.ui.screens.institution

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.InstitutionUserDetailDto
import com.example.nextstep.data.repository.InstitutionUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InstitutionUserDetailUiState(
    val userDetail: InstitutionUserDetailDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val isPendingInvite: Boolean = false,
    val isArchiving: Boolean = false,
    @StringRes val archiveSuccessRes: Int? = null,
    @StringRes val archiveErrorRes: Int? = null
)

class InstitutionUserDetailViewModel : ViewModel() {

    private val repository = InstitutionUsersRepository()

    private val _uiState = MutableStateFlow(InstitutionUserDetailUiState())
    val uiState: StateFlow<InstitutionUserDetailUiState> = _uiState.asStateFlow()

    fun loadUserDetail(profileId: String, role: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null,
                archiveSuccessRes = null,
                archiveErrorRes = null
            )

            Log.d("InstitutionUserDetailVM", "loadUserDetail profileId=$profileId role=$role")

            val result = repository.getInstitutionUserDetail(
                profileId = profileId,
                role = role
            )

            result.fold(
                onSuccess = { detail ->
                    val isPending = detail.inviteStatus == "pending" && detail.profileId == null
                    
                    Log.d("InstitutionUserDetailVM", "Loaded detail profileId=${detail.profileId} role=$role status=${detail.inviteStatus} acceptedAt=${detail.acceptedAt}")

                    _uiState.value = _uiState.value.copy(
                        userDetail = detail,
                        isLoading = false,
                        isPendingInvite = isPending,
                        errorMessageRes = null
                    )
                },
                onFailure = { exception ->
                    Log.e("InstitutionUserDetailVM", "Erro ao carregar detalhe", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessageRes = R.string.institution_user_load_error
                    )
                }
            )
        }
    }

    fun archiveProfile() {
        val detail = _uiState.value.userDetail ?: return
        val profileId = detail.profileId ?: return
        val role = detail.targetRole

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isArchiving = true,
                archiveSuccessRes = null,
                archiveErrorRes = null
            )

            val result = repository.archiveProfile(profileId, role)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isArchiving = false,
                        archiveSuccessRes = R.string.user_archived_success
                    )
                    loadUserDetail(profileId, role)
                },
                onFailure = { exception ->
                    Log.e("InstitutionUserDetailVM", "Erro ao arquivar perfil", exception)
                    _uiState.value = _uiState.value.copy(
                        isArchiving = false,
                        archiveErrorRes = R.string.error_could_not_archive_user
                    )
                }
            )
        }
    }
}
