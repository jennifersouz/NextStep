package com.example.nextstep.ui.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorProfileDto
import com.example.nextstep.ui.components.ProfileField
import com.example.nextstep.ui.components.ProfileScreenLayout

@Composable
fun AdvisorProfileScreen(
    onLogoutClick: () -> Unit = {},
    viewModel: AdvisorProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var showLogoutDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text(text = stringResource(R.string.logout_confirmation_title))
            },
            text = {
                Text(text = stringResource(R.string.logout_confirmation_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.logout_confirm),
                        color = Color(0xFFB00020),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    when {
        state.isLoading -> {
            AdvisorProfileLoadingState()
        }

        state.errorMessageRes != null -> {
            AdvisorProfileErrorState(
                message = stringResource(
                    state.errorMessageRes ?: R.string.advisor_profile_load_error
                )
            )
        }

        state.profile != null -> {
            AdvisorProfileContent(
                profile = state.profile!!,
                onLogoutRequest = {
                    showLogoutDialog = true
                }
            )
        }
    }
}

@Composable
private fun AdvisorProfileContent(
    profile: AdvisorProfileDto,
    onLogoutRequest: () -> Unit
) {
    val displayName = profile.name.orEmpty().ifBlank {
        stringResource(R.string.advisor_name_placeholder)
    }

    ProfileScreenLayout(
        title = stringResource(R.string.profile),
        name = displayName,
        fields = listOf(
            ProfileField(
                label = stringResource(R.string.email),
                value = profile.email.orEmpty()
            ),
            ProfileField(
                label = stringResource(R.string.contact),
                value = profile.phone.orEmpty()
            ),
            ProfileField(
                label = stringResource(R.string.department),
                value = profile.department.orEmpty()
            )
        ),
        onMenuClick = onLogoutRequest
    )
}

@Composable
private fun AdvisorProfileLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Black
        )
    }
}

@Composable
private fun AdvisorProfileErrorState(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color(0xFFB00020),
            fontSize = 16.sp
        )
    }
}
