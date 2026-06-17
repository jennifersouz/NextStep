package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.local.LanguageManager
import com.example.nextstep.data.model.InstitutionProfileDto
import com.example.nextstep.ui.components.LanguageOptionsSection
import com.example.nextstep.ui.components.ProfileFieldItem
import com.example.nextstep.ui.components.ProfileScreenLayout

@Composable
fun InstitutionProfileScreen(
    refreshKey: Int = 0,
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: InstitutionProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var showLogoutDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(refreshKey) {
        viewModel.loadProfile()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProfile()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text(
                    text = stringResource(R.string.logout_confirmation_title)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.logout_confirmation_message)
                )
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
                    Text(
                        text = stringResource(R.string.cancel)
                    )
                }
            }
        )
    }

    when {
        state.isLoading -> {
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

        state.errorMessageRes != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        state.profile != null -> {
            InstitutionProfileContent(
                profile = state.profile!!,
                onEditProfileClick = onEditProfileClick,
                onLogoutRequest = {
                    showLogoutDialog = true
                }
            )
        }
    }
}

@Composable
private fun InstitutionProfileContent(
    profile: InstitutionProfileDto,
    onEditProfileClick: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    ProfileScreenLayout(
        title = stringResource(R.string.profile),
        name = profile.name.orEmpty(),
        subtitle = stringResource(R.string.institution_role),
        onEditProfileClick = onEditProfileClick,
        onLogoutClick = onLogoutRequest,
        extraContent = {
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileFieldItem(
                    label = stringResource(R.string.email),
                    value = profile.email.orEmpty()
                )

                if (!profile.phone.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileFieldItem(
                        label = stringResource(R.string.phone),
                        value = profile.phone
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                ProfileFieldItem(
                    label = stringResource(R.string.status),
                    value = if (profile.isActive != false) {
                        stringResource(R.string.active_status)
                    } else {
                        stringResource(R.string.inactive_status)
                    }
                )
            }
        },
        accountOptions = {
            LanguageOptionsSection(
                selectedLanguage = "pt",
                onLanguageSelected = { languageCode ->
                    LanguageManager.changeLanguage(languageCode)
                }
            )
        }
    )
}