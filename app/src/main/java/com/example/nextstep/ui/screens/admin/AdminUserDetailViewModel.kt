package com.example.nextstep.ui.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminProfileDto
import com.example.nextstep.data.repository.AdminUsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminUserDetailViewModel : ViewModel() {

    private val repository = AdminUsersRepository()

    private val _uiState = MutableStateFlow(AdminUserDetailUiState())
    val uiState: StateFlow<AdminUserDetailUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.getUserById(userId)
            if (result.isSuccess) {
                val user = result.getOrNull()
                Log.d(
                    "AdminUserDetailVM",
                    "Loaded user detail id=${user?.id}, email=${user?.email}, " +
                    "isActive=${user?.isActive}, archivedAt=${user?.archivedAt}"
                )
                _uiState.value = _uiState.value.copy(isLoading = false, profile = user)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_load_profile
                )
            }
        }
    }

    fun setProfile(profile: AdminProfileDto) {
        Log.d(
            "AdminUserDetailVM",
            "setProfile id=${profile.id}, email=${profile.email}, " +
            "isActive=${profile.isActive}, archivedAt=${profile.archivedAt}"
        )
        _uiState.value = _uiState.value.copy(profile = profile)
    }

    // Dialogs
    fun showDeactivateDialog() {
        _uiState.value = _uiState.value.copy(showDeactivateDialog = true)
    }

    fun showReactivateDialog() {
        _uiState.value = _uiState.value.copy(showReactivateDialog = true)
    }

    fun showArchiveDialog() {
        _uiState.value = _uiState.value.copy(showArchiveDialog = true)
    }

    fun dismissDialogs() {
        _uiState.value = _uiState.value.copy(
            showDeactivateDialog = false,
            showReactivateDialog = false,
            showArchiveDialog = false
        )
    }

    // Actions

    fun deactivate(adminId: String) {
        val profile = _uiState.value.profile ?: return
        Log.d("AdminUserDetailVM", "deactivate called for id=${profile.id}, email=${profile.email}")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null)
            dismissDialogs()

            val result = repository.deactivateUser(profile.id)
            if (result.isSuccess) {
                val updatedUser = result.getOrThrow()
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    profile = updatedUser,
                    successMessageRes = R.string.user_access_deactivated_success
                )
            } else {
                Log.e(
                    "AdminUserDetailVM",
                    "deactivate failed for id=${profile.id}",
                    result.exceptionOrNull()
                )
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.error_could_not_deactivate_access
                )
            }
        }
    }

    fun reactivate(adminId: String) {
        val profile = _uiState.value.profile ?: return
        Log.d("AdminUserDetailVM", "reactivate called for id=${profile.id}, email=${profile.email}")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null)
            dismissDialogs()

            val result = repository.reactivateUser(profile.id)
            if (result.isSuccess) {
                val updatedUser = result.getOrThrow()
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    profile = updatedUser,
                    successMessageRes = R.string.user_access_reactivated_success
                )
            } else {
                Log.e(
                    "AdminUserDetailVM",
                    "reactivate failed for id=${profile.id}",
                    result.exceptionOrNull()
                )
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.error_could_not_reactivate_user
                )
            }
        }
    }

    fun archive(reason: String? = null) {
        val profile = _uiState.value.profile ?: return
        Log.d("AdminUserDetailVM", "archive called for id=${profile.id}, email=${profile.email}")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null)
            dismissDialogs()

            // adminId é obtido internamente no Repository — não passar "" aqui
            val result = repository.archiveUser(profile.id, reason)
            if (result.isSuccess) {
                val updatedUser = result.getOrThrow()
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    profile = updatedUser,
                    successMessageRes = R.string.user_archived_success
                )
            } else {
                Log.e(
                    "AdminUserDetailVM",
                    "archive failed for id=${profile.id}",
                    result.exceptionOrNull()
                )
                _uiState.value = _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.error_could_not_archive_user
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            successMessageRes = null,
            errorMessage = null,
            errorMessageRes = null
        )
    }
}
