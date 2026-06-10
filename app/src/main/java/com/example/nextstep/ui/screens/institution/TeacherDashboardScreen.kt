package com.example.nextstep.ui.screens.institution

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.ui.screens.auth.SessionViewModel

@Composable
fun TeacherDashboardScreen(
    onLogoutSuccess: () -> Unit = {},
    sessionViewModel: SessionViewModel = viewModel()
) {
    var showTeacherEditProfile by remember {
        mutableStateOf(false)
    }

    if (showTeacherEditProfile) {
        // Teacher edit profile placeholder - will navigate back when implemented
        showTeacherEditProfile = false
    } else {
        TeacherProfileScreen(
            onEditProfileClick = {
                showTeacherEditProfile = true
            },
            onLogoutClick = {
                sessionViewModel.logout(
                    onSuccess = onLogoutSuccess
                )
            }
        )
    }
}