package com.example.nextstep.ui.screens.student

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.local.LanguageManager
import com.example.nextstep.data.model.StudentProfile
import com.example.nextstep.ui.components.LanguageOptionsSection
import com.example.nextstep.ui.components.ProfileScreenLayout

@Composable
fun StudentProfileScreen(
    refreshKey: Int = 0,
    viewModel: StudentProfileViewModel = viewModel(),
    onSentRequestsClick: () -> Unit = {},
    onSavedInternshipsClick: () -> Unit = {},
    onSubmittedApplicationsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
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
                    onEditProfileClick = onEditProfileClick,
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
    onEditProfileClick: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    val subtitle = buildString {
        append(stringResource(R.string.student_role))
        if (!profile.course.isNullOrBlank()) {
            append(" · ")
            append(profile.course)
        }
    }

    ProfileScreenLayout(
        title = stringResource(R.string.profile),
        name = profile.fullName,
        subtitle = subtitle,
        onEditProfileClick = onEditProfileClick,
        onLogoutClick = onLogoutRequest,
        extraContent = {
            StudentProfileActionsSection(
                onSentRequestsClick = onSentRequestsClick,
                onSavedInternshipsClick = onSavedInternshipsClick,
                onSubmittedApplicationsClick = onSubmittedApplicationsClick,
                modifier = Modifier.padding(top = 12.dp)
            )
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

@Composable
fun StudentProfileActionsSection(
    onSentRequestsClick: () -> Unit,
    onSavedInternshipsClick: () -> Unit,
    onSubmittedApplicationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.actions),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ProfileActionCard(
                title = stringResource(R.string.sent_requests),
                subtitle = stringResource(R.string.sent_requests_description),
                icon = Icons.AutoMirrored.Filled.Send,
                onClick = onSentRequestsClick
            )

            ProfileActionCard(
                title = stringResource(R.string.saved_internships),
                subtitle = stringResource(R.string.saved_internships_description),
                icon = Icons.Outlined.BookmarkBorder,
                onClick = onSavedInternshipsClick
            )

            ProfileActionCard(
                title = stringResource(R.string.submitted_applications),
                subtitle = stringResource(R.string.submitted_applications_description),
                icon = Icons.AutoMirrored.Filled.Article,
                onClick = onSubmittedApplicationsClick
            )
        }
    }
}

@Composable
fun ProfileActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFEDEDED)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFDFA52).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF6F7585),
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF6F7585),
                modifier = Modifier.size(26.dp)
            )
        }
    }
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
            modifier = Modifier
                .clickable { onRetryClick() }
                .padding(8.dp)
        )
    }
}