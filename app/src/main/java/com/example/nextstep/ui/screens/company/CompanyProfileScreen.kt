package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.nextstep.data.model.CompanyProfileDto
import com.example.nextstep.ui.components.LanguageOptionsSection
import com.example.nextstep.ui.components.ProfileScreenLayout

@Composable
fun CompanyProfileScreen(
    refreshKey: Int = 0,
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onOfferClick: (String) -> Unit = {},
    viewModel: CompanyProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var showLogoutDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(refreshKey) {
        viewModel.loadCompanyProfile()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadCompanyProfile()
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

        state.company != null -> {
            CompanyProfileContent(
                company = state.company!!,
                onEditProfileClick = onEditProfileClick,
                onLogoutRequest = {
                    showLogoutDialog = true
                }
            )
        }
    }
}

@Composable
private fun CompanyProfileContent(
    company: CompanyProfileDto,
    onEditProfileClick: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    val subtitle = buildString {
        append(stringResource(R.string.company_role))
        if (!company.businessArea.isNullOrBlank()) {
            append(" · ")
            append(company.businessArea)
        }
    }

    ProfileScreenLayout(
        title = stringResource(R.string.profile),
        name = company.companyName,
        subtitle = subtitle,
        onEditProfileClick = onEditProfileClick,
        onLogoutClick = onLogoutRequest,
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