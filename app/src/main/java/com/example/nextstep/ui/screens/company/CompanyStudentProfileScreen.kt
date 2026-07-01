package com.example.nextstep.ui.screens.company

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    LaunchedEffect(applicationId) {
        viewModel.loadStudentProfile(applicationId)
    }

    val documentUrl = state.documentUrlToOpen

    LaunchedEffect(documentUrl) {
        if (!documentUrl.isNullOrBlank()) {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(documentUrl)
                )
                context.startActivity(intent)
                viewModel.consumeDocumentUrl()
            } catch (_: Exception) {
                viewModel.onDocumentOpenFailed()
            }
        }
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
            CompanyCandidateProfileContent(
                profile = state.profile!!,
                isOpeningDocument = state.isOpeningDocument,
                documentErrorRes = state.documentErrorRes,
                onBackClick = onBackClick,
                onOpenCv = viewModel::openCv,
                onOpenMotivationLetter = viewModel::openMotivationLetter
            )
        }
    }
}

@Composable
private fun CompanyCandidateProfileContent(
    profile: CompanyStudentProfileDto,
    isOpeningDocument: Boolean,
    documentErrorRes: Int?,
    onBackClick: () -> Unit,
    onOpenCv: () -> Unit,
    onOpenMotivationLetter: () -> Unit
) {
    val fullName = listOfNotNull(
        profile.firstName,
        profile.lastName
    ).joinToString(" ").ifBlank {
        stringResource(R.string.student)
    }

    val initials = fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column: Main Profile Summary
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Avatar with initials
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2B2B2B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full name
                    Text(
                        text = fullName,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Email
                    Text(
                        text = profile.studentEmail.orEmpty().ifBlank {
                            stringResource(R.string.not_available)
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Application status badge
                    CandidateStatusBadge(status = profile.applicationStatus)

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Right Column: Details
                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Academic information card
                    ProfileSectionCard(
                        title = stringResource(R.string.academic_information)
                    ) {
                        ProfileInfoRow(
                            label = stringResource(R.string.student_number),
                            value = profile.studentNumber
                        )

                        ProfileInfoRow(
                            label = stringResource(R.string.course),
                            value = profile.course
                        )

                        ProfileInfoRow(
                            label = stringResource(R.string.academic_year),
                            value = profile.academicYear?.toString()
                        )

                        ProfileInfoRow(
                            label = stringResource(R.string.education_institution),
                            value = profile.educationInstitution
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact card
                    ProfileSectionCard(
                        title = stringResource(R.string.contacts)
                    ) {
                        ProfileInfoRow(
                            label = stringResource(R.string.email),
                            value = profile.studentEmail
                        )

                        ProfileInfoRow(
                            label = stringResource(R.string.phone),
                            value = profile.studentPhone
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Application card
                    ProfileSectionCard(
                        title = stringResource(R.string.application)
                    ) {
                        ProfileInfoRow(
                            label = stringResource(R.string.offer),
                            value = profile.offerTitle
                        )

                        ProfileInfoRow(
                            label = stringResource(R.string.status),
                            value = displayApplicationStatus(profile.applicationStatus)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Documents card
                    ProfileSectionCard(
                        title = stringResource(R.string.application_documents_title)
                    ) {
                        DocumentSection(
                            isOpeningDocument = isOpeningDocument,
                            cvPath = profile.cvPath,
                            motivationLetterPath = profile.motivationLetterPath,
                            onOpenCv = onOpenCv,
                            onOpenMotivationLetter = onOpenMotivationLetter
                        )
                    }

                    // Document error
                    documentErrorRes?.let { errorRes ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(errorRes),
                            color = Color(0xFFB00020),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(42.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Avatar with initials
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B2B2B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full name
                Text(
                    text = fullName,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Email
                Text(
                    text = profile.studentEmail.orEmpty().ifBlank {
                        stringResource(R.string.not_available)
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Application status badge
                CandidateStatusBadge(status = profile.applicationStatus)

                Spacer(modifier = Modifier.height(30.dp))

                // Academic information card
                ProfileSectionCard(
                    title = stringResource(R.string.academic_information)
                ) {
                    ProfileInfoRow(
                        label = stringResource(R.string.student_number),
                        value = profile.studentNumber
                    )

                    ProfileInfoRow(
                        label = stringResource(R.string.course),
                        value = profile.course
                    )

                    ProfileInfoRow(
                        label = stringResource(R.string.academic_year),
                        value = profile.academicYear?.toString()
                    )

                    ProfileInfoRow(
                        label = stringResource(R.string.education_institution),
                        value = profile.educationInstitution
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contact card
                ProfileSectionCard(
                    title = stringResource(R.string.contacts)
                ) {
                    ProfileInfoRow(
                        label = stringResource(R.string.email),
                        value = profile.studentEmail
                    )

                    ProfileInfoRow(
                        label = stringResource(R.string.phone),
                        value = profile.studentPhone
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Application card
                ProfileSectionCard(
                    title = stringResource(R.string.application)
                ) {
                    ProfileInfoRow(
                        label = stringResource(R.string.offer),
                        value = profile.offerTitle
                    )

                    ProfileInfoRow(
                        label = stringResource(R.string.status),
                        value = displayApplicationStatus(profile.applicationStatus)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Documents card
                ProfileSectionCard(
                    title = stringResource(R.string.application_documents_title)
                ) {
                    DocumentSection(
                        isOpeningDocument = isOpeningDocument,
                        cvPath = profile.cvPath,
                        motivationLetterPath = profile.motivationLetterPath,
                        onOpenCv = onOpenCv,
                        onOpenMotivationLetter = onOpenMotivationLetter
                    )
                }

                // Document error
                documentErrorRes?.let { errorRes ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(errorRes),
                        color = Color(0xFFB00020),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(42.dp))
            }
        }
    }
}

@Composable
private fun CandidateStatusBadge(status: String?) {
    val statusText = displayApplicationStatus(status)
    val statusColor = when (status?.lowercase()?.trim()) {
        "pending", "pendente" -> Color(0xFF777777)
        "accepted", "aceite" -> Color(0xFF138A36)
        "rejected", "recusada", "rejeitada" -> Color(0xFFB00020)
        else -> Color(0xFF777777)
    }
    val statusBg = when (status?.lowercase()?.trim()) {
        "pending", "pendente" -> Color(0xFFF3F3F3)
        "accepted", "aceite" -> Color(0xFFE8F5E9)
        "rejected", "recusada", "rejeitada" -> Color(0xFFFBE9E7)
        else -> Color(0xFFF3F3F3)
    }

    Box(
        modifier = Modifier
            .background(statusBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

@Composable
private fun DocumentSection(
    isOpeningDocument: Boolean,
    cvPath: String?,
    motivationLetterPath: String?,
    onOpenCv: () -> Unit,
    onOpenMotivationLetter: () -> Unit
) {
    // CV
    Text(
        text = stringResource(R.string.cv),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (cvPath.isNullOrBlank()) {
        Text(
            text = stringResource(R.string.company_candidate_cv_not_available),
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )
    } else {
        Button(
            onClick = onOpenCv,
            enabled = !isOpeningDocument,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0E0E0),
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFFF0F0F0),
                disabledContentColor = Color(0xFF8A8A8A)
            )
        ) {
            Text(
                text = stringResource(R.string.open_document),
                fontWeight = FontWeight.Medium
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Motivation letter
    Text(
        text = stringResource(R.string.motivation_letter),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (motivationLetterPath.isNullOrBlank()) {
        Text(
            text = stringResource(R.string.company_candidate_letter_not_available),
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )
    } else {
        Button(
            onClick = onOpenMotivationLetter,
            enabled = !isOpeningDocument,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0E0E0),
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFFF0F0F0),
                disabledContentColor = Color(0xFF8A8A8A)
            )
        ) {
            Text(
                text = stringResource(R.string.open_document),
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Document loading indicator
    if (isOpeningDocument) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.company_application_opening_document),
            color = Color(0xFF8A8A8A),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileSectionCard(
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
private fun ProfileInfoRow(
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
    return when (status?.trim()?.lowercase()) {
        "pending", "pendente" -> "Pendente"
        "accepted", "aceite" -> "Aceite"
        "rejected", "recusada", "rejeitada" -> "Recusada"
        "viewed" -> "Vista"
        else -> status.orEmpty()
    }
}