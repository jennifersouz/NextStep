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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.nextstep.data.model.StudentProfile
import com.example.nextstep.ui.components.ProfileResponsiveLayout
import com.example.nextstep.ui.components.isLandscape

@Composable
fun StudentProfileScreen(
    refreshKey: Int = 0,
    viewModel: StudentProfileViewModel = viewModel(),
    onSentRequestsClick: () -> Unit = {},
    onSavedInternshipsClick: () -> Unit = {},
    onSubmittedApplicationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(refreshKey) {
        viewModel.loadProfile()
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
                    onSettingsClick = onSettingsClick
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
    onSettingsClick: () -> Unit
) {
    val landscape = isLandscape()

    if (landscape) {
        // Landscape: avatar/nome à esquerda, info + menu à direita
        ProfileResponsiveLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            headerContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StudentProfileAvatar(fullName = profile.fullName)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = profile.fullName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            },
            bodyContent = {
                Text(
                    text = stringResource(R.string.profile),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                StudentProfileInfoBlock(
                    label = stringResource(R.string.email),
                    value = profile.email
                )

                Spacer(modifier = Modifier.height(20.dp))

                StudentProfileInfoBlock(
                    label = stringResource(R.string.education_institution),
                    value = profile.educationInstitution
                )

                Spacer(modifier = Modifier.height(32.dp))

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
        )
    } else {
        // Portrait: layout original em coluna
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.profile),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StudentProfileAvatar(fullName = profile.fullName)

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = profile.fullName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(58.dp))

            StudentProfileInfoBlock(
                label = stringResource(R.string.email),
                value = profile.email
            )

            Spacer(modifier = Modifier.height(24.dp))

            StudentProfileInfoBlock(
                label = stringResource(R.string.education_institution),
                value = profile.educationInstitution
            )

            Spacer(modifier = Modifier.height(54.dp))

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
    }
}

@Composable
fun StudentProfileAvatar(
    fullName: String
) {
    val initials = fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank {
            "?"
        }

    Box(
        modifier = Modifier
            .size(136.dp)
            .clip(CircleShape)
            .background(Color(0xFFEAEAEA)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun StudentProfileInfoBlock(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color(0xFF8A8A8A)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 17.sp,
            color = Color.Black,
            lineHeight = 23.sp
        )
    }
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
            modifier = Modifier.clickable { onRetryClick() }
        )
    }
}
