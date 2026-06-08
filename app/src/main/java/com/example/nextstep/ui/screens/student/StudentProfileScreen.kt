package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.StudentProfile
import com.example.nextstep.ui.components.ProfileField
import com.example.nextstep.ui.components.ProfileScreenLayout

@Composable
fun StudentProfileScreen(
    refreshKey: Int = 0,
    viewModel: StudentProfileViewModel = viewModel(),
    onSentRequestsClick: () -> Unit = {},
    onSavedInternshipsClick: () -> Unit = {},
    onSubmittedApplicationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    var showLogoutDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(refreshKey) {
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
            StudentProfileLoadingState()
        }

        state.errorMessageRes != null -> {
            val errorRes = state.errorMessageRes

            StudentProfileErrorState(
                message = if (errorRes != null) {
                    stringResource(errorRes)
                } else {
                    stringResource(R.string.student_profile_load_error)
                },
                onRetryClick = viewModel::loadProfile
            )
        }

        state.profile != null -> {
            val profile = state.profile

            if (profile != null) {
                StudentProfileContent(
                    profile = profile,
                    onSentRequestsClick = onSentRequestsClick,
                    onSavedInternshipsClick = onSavedInternshipsClick,
                    onSubmittedApplicationsClick = onSubmittedApplicationsClick,
                    onSettingsClick = onSettingsClick,
                    onLogoutRequest = {
                        showLogoutDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun StudentProfileContent(
    profile: StudentProfile,
    onSentRequestsClick: () -> Unit,
    onSavedInternshipsClick: () -> Unit,
    onSubmittedApplicationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    val fields = buildList {
        add(ProfileField(stringResource(R.string.email), profile.email))
        add(ProfileField(stringResource(R.string.contact), ""))
        add(ProfileField(stringResource(R.string.city), ""))
        add(ProfileField(stringResource(R.string.country), ""))
        add(ProfileField(stringResource(R.string.course), profile.course))
        if (profile.academicYear > 0) {
            add(
                ProfileField(
                    label = stringResource(R.string.academic_year),
                    value = profile.academicYear.toString()
                )
            )
        }
    }

    ProfileScreenLayout(
        title = stringResource(R.string.profile),
        name = profile.fullName,
        fields = fields,
        onMenuClick = onLogoutRequest,
        extraContent = {
            StudentProfileMenuList(
                onSentRequestsClick = onSentRequestsClick,
                onSavedInternshipsClick = onSavedInternshipsClick,
                onSubmittedApplicationsClick = onSubmittedApplicationsClick,
                onSettingsClick = onSettingsClick
            )
        }
    )
}

@Composable
fun StudentProfileMenuList(
    onSentRequestsClick: () -> Unit,
    onSavedInternshipsClick: () -> Unit,
    onSubmittedApplicationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    StudentProfileMenuItem(
        icon = Icons.AutoMirrored.Outlined.Send,
        title = stringResource(R.string.sent_requests),
        onClick = onSentRequestsClick
    )

    StudentProfileMenuDivider()

    StudentProfileMenuItem(
        icon = Icons.Outlined.BookmarkBorder,
        title = stringResource(R.string.saved_internships),
        onClick = onSavedInternshipsClick
    )

    StudentProfileMenuDivider()

    StudentProfileMenuItem(
        icon = Icons.Outlined.Description,
        title = stringResource(R.string.submitted_applications),
        onClick = onSubmittedApplicationsClick
    )

    StudentProfileMenuDivider()

    StudentProfileMenuItem(
        icon = Icons.Outlined.Settings,
        title = stringResource(R.string.settings),
        onClick = onSettingsClick
    )
}

@Composable
fun StudentProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.size(22.dp))

        Text(
            text = title,
            fontSize = 21.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun StudentProfileMenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 4.dp),
        color = Color(0xFFE0E0E0),
        thickness = 1.dp
    )
}

@Composable
fun StudentProfileLoadingState() {
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
fun StudentProfileErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color(0xFFB00020),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.try_again),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onRetryClick()
            }
        )
    }
}
