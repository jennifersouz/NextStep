package com.example.nextstep.ui.screens.company

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyInternStudentProfileDto

@Composable
fun CompanyInternStudentProfileScreen(
    applicationId: String,
    onBackClick: () -> Unit,
    viewModel: CompanyInternStudentProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(applicationId) {
        viewModel.loadProfile(applicationId)
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
            CompanyInternStudentProfileContent(
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
private fun CompanyInternStudentProfileContent(
    profile: CompanyInternStudentProfileDto,
    isOpeningDocument: Boolean,
    documentErrorRes: Int?,
    onBackClick: () -> Unit,
    onOpenCv: () -> Unit,
    onOpenMotivationLetter: () -> Unit
) {
    val fullName = profile.studentName.orEmpty().ifBlank {
        stringResource(R.string.student)
    }

    val initials = fullName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    val statusText = translateInternshipStatus(profile.internshipStatus)
    val statusColor = when (profile.internshipStatus?.trim()?.lowercase()) {
        "accepted" -> Color(0xFF138A36)
        "active" -> Color(0xFF1565C0)
        "in_progress" -> Color(0xFFE65100)
        "completed" -> Color(0xFF2E7D32)
        else -> Color(0xFF777777)
    }
    val statusBg = when (profile.internshipStatus?.trim()?.lowercase()) {
        "accepted" -> Color(0xFFE8F5E9)
        "active" -> Color(0xFFE3F2FD)
        "in_progress" -> Color(0xFFFFF3E0)
        "completed" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF3F3F3)
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

            // Internship status badge
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

            Spacer(modifier = Modifier.height(30.dp))

            // Personal data card
            InternProfileSectionCard(
                title = stringResource(R.string.company_intern_profile_personal_data)
            ) {
                InternProfileInfoRow(
                    label = stringResource(R.string.first_name),
                    value = profile.studentName
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.email),
                    value = profile.studentEmail
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.phone),
                    value = profile.studentPhone
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Academic data card
            InternProfileSectionCard(
                title = stringResource(R.string.company_intern_profile_academic_data)
            ) {
                InternProfileInfoRow(
                    label = stringResource(R.string.student_number),
                    value = profile.studentNumber
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.course),
                    value = profile.course
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.year),
                    value = profile.academicYear?.toString()
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.education_institution),
                    value = profile.educationInstitution
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Internship card
            InternProfileSectionCard(
                title = stringResource(R.string.company_intern_profile_internship)
            ) {
                InternProfileInfoRow(
                    label = stringResource(R.string.offer),
                    value = profile.offerTitle
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.offer_area),
                    value = profile.offerArea
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.location),
                    value = profile.offerLocation
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.offer_work_mode),
                    value = profile.offerWorkMode
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.status),
                    value = statusText
                )

                InternProfileInfoRow(
                    label = stringResource(R.string.company_intern_profile_application_date),
                    value = profile.applicationCreatedAt
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Documents card
            InternProfileSectionCard(
                title = stringResource(R.string.application_documents_title)
            ) {
                // CV
                Text(
                    text = stringResource(R.string.cv),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (profile.cvPath.isNullOrBlank()) {
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

                if (profile.motivationLetterPath.isNullOrBlank()) {
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

@Composable
private fun InternProfileSectionCard(
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
private fun InternProfileInfoRow(
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

private fun translateInternshipStatus(status: String?): String {
    return when (status?.trim()?.lowercase()) {
        "accepted" -> "Aceite"
        "active" -> "Ativo"
        "in_progress" -> "Em progresso"
        "completed" -> "Concluído"
        "pending" -> "Pendente"
        "rejected" -> "Recusada"
        else -> status.orEmpty()
    }
}