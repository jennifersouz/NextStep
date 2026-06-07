package com.example.nextstep.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyStudentProfileDto

@Composable
fun CompanyStudentProfileScreen(
    applicationId: String,
    onBackClick: () -> Unit,
    viewModel: CompanyStudentProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.loadStudentProfile(applicationId)
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        state.errorMessageRes != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(state.errorMessageRes!!),
                    color = Color(0xFFB00020),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        state.profile != null -> {
            CompanyStudentProfileContent(
                profile = state.profile!!,
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
private fun CompanyStudentProfileContent(
    profile: CompanyStudentProfileDto,
    onBackClick: () -> Unit
) {
    val fullName = listOfNotNull(
        profile.firstName,
        profile.lastName
    ).joinToString(" ").ifBlank {
        stringResource(R.string.student)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDFA52)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = fullName,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile.studentEmail.orEmpty().ifBlank {
                    stringResource(R.string.not_available)
                },
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            StudentProfileSectionCard(
                title = stringResource(R.string.academic_information)
            ) {
                StudentProfileInfoRow(
                    label = stringResource(R.string.student_number),
                    value = profile.studentNumber
                )

                StudentProfileInfoRow(
                    label = stringResource(R.string.course),
                    value = profile.course
                )

                StudentProfileInfoRow(
                    label = stringResource(R.string.year),
                    value = profile.academicYear?.toString()
                )

                StudentProfileInfoRow(
                    label = stringResource(R.string.education_institution),
                    value = profile.educationInstitution
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            StudentProfileSectionCard(
                title = stringResource(R.string.contacts)
            ) {
                StudentProfileInfoRow(
                    label = stringResource(R.string.email),
                    value = profile.studentEmail
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            StudentProfileSectionCard(
                title = stringResource(R.string.application)
            ) {
                StudentProfileInfoRow(
                    label = stringResource(R.string.offer),
                    value = profile.offerTitle
                )

                StudentProfileInfoRow(
                    label = stringResource(R.string.status),
                    value = displayApplicationStatus(profile.applicationStatus)
                )
            }

            Spacer(modifier = Modifier.height(42.dp))
        }
    }
}

@Composable
private fun StudentProfileSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun StudentProfileInfoRow(
    label: String,
    value: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF8A8A8A)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value.orEmpty().ifBlank {
                stringResource(R.string.not_available)
            },
            fontSize = 15.sp,
            color = Color.Black,
            lineHeight = 21.sp
        )
    }
}

@Composable
private fun displayApplicationStatus(
    status: String?
): String {
    return when (status?.lowercase()?.trim()) {
        "pending", "pendente" -> stringResource(R.string.student_application_status_pending)
        "accepted", "aceite" -> stringResource(R.string.student_application_status_accepted)
        "rejected", "recusada", "rejeitada" -> stringResource(R.string.student_application_status_rejected)
        else -> status.orEmpty()
    }
}