package com.example.nextstep.ui.screens.student

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

@Composable
fun StudentSubmittedApplicationDetailScreen(
    applicationId: String,
    onBackClick: () -> Unit,
    viewModel: StudentSubmittedApplicationDetailViewModel = viewModel(),
    onMessagesClick: (String, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(applicationId) {
        viewModel.loadApplication(applicationId)
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
            StudentApplicationDetailLoadingState()
        }

        state.errorMessageRes != null -> {
            val errorRes = state.errorMessageRes

            StudentApplicationDetailErrorState(
                message = if (errorRes != null) {
                    stringResource(errorRes)
                } else {
                    stringResource(R.string.student_application_detail_load_error)
                },
                onBackClick = onBackClick
            )
        }

        state.application != null -> {
            StudentApplicationDetailContent(
                applicationId = applicationId,
                application = state.application!!,
                isConfirmingPresence = state.isConfirmingPresence,
                confirmPresenceErrorRes = state.confirmPresenceErrorRes,
                isOpeningDocument = state.isOpeningDocument,
                documentErrorRes = state.documentErrorRes,
                onBackClick = onBackClick,
                onOpenMotivationLetter = viewModel::openMotivationLetter,
                onOpenCv = viewModel::openCv,
                onOpenFinalReport = viewModel::openFinalReport,
                onConfirmPresence = viewModel::confirmPresence,
                onMessagesClick = onMessagesClick
            )
        }
    }
}

@Composable
fun StudentApplicationDetailContent(
    applicationId: String,
    application: StudentSubmittedApplicationDto,
    isConfirmingPresence: Boolean,
    confirmPresenceErrorRes: Int?,
    isOpeningDocument: Boolean,
    documentErrorRes: Int?,
    onBackClick: () -> Unit,
    onOpenMotivationLetter: () -> Unit,
    onOpenCv: () -> Unit,
    onOpenFinalReport: () -> Unit,
    onConfirmPresence: () -> Unit,
    onMessagesClick: (String, String) -> Unit
) {
    val canAcceptInternship = application.status == "accepted" &&
            !application.studentPresenceConfirmed &&
            !isConfirmingPresence

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(46.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudentApplicationDetailAvatar(
                fullName = application.studentFullName
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = application.studentFullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.application_status_title),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = studentApplicationDetailStatusLabel(application.status),
            fontSize = 14.sp,
            color = studentApplicationDetailStatusColor(application.status)
        )

        Spacer(modifier = Modifier.height(24.dp))

        StudentAssignedAdvisorSection(
            applicationId = applicationId,
            advisorName = application.advisorName,
            advisorEmail = application.advisorEmail,
            advisorPhone = application.advisorPhone,
            advisorDepartment = application.advisorDepartment
        )

        Spacer(modifier = Modifier.height(24.dp))

        TeacherSection(application = application)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.application_documents_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        StudentApplicationDetailDocumentField(
            label = stringResource(R.string.motivation_letter),
            fileName = fileNameFromStudentApplicationPath(
                path = application.motivationLetterPath,
                fallback = stringResource(R.string.motivation_letter_placeholder)
            ),
            enabled = !application.motivationLetterPath.isNullOrBlank() && !isOpeningDocument,
            onOpenClick = onOpenMotivationLetter
        )

        Spacer(modifier = Modifier.height(16.dp))

        StudentApplicationDetailDocumentField(
            label = stringResource(R.string.cv),
            fileName = fileNameFromStudentApplicationPath(
                path = application.cvPath,
                fallback = stringResource(R.string.cv_placeholder)
            ),
            enabled = !application.cvPath.isNullOrBlank() && !isOpeningDocument,
            onOpenClick = onOpenCv
        )

        Spacer(modifier = Modifier.height(16.dp))

        StudentApplicationDetailDocumentField(
            label = stringResource(R.string.final_report),
            fileName = fileNameFromStudentApplicationPath(
                path = application.reportPath,
                fallback = stringResource(R.string.final_report_placeholder)
            ),
            enabled = !application.reportPath.isNullOrBlank() && !isOpeningDocument,
            onOpenClick = onOpenFinalReport
        )

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

        if (isOpeningDocument) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.student_application_opening_document),
                color = Color(0xFF8A8A8A),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        confirmPresenceErrorRes?.let { errorRes ->
            Text(
                text = stringResource(errorRes),
                color = Color(0xFFB00020),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
        }

        Button(
            onClick = onConfirmPresence,
            enabled = canAcceptInternship,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFFE5E5A0),
                disabledContentColor = Color.Black
            )
        ) {
            Text(
                text = when {
                    isConfirmingPresence -> stringResource(R.string.accepting_internship)
                    application.studentPresenceConfirmed -> stringResource(R.string.internship_accepted)
                    application.status != "accepted" -> stringResource(R.string.internship_acceptance_only_after_application_acceptance)
                    else -> stringResource(R.string.accept_internship)
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        val canChatAdvisor =
            application.status.lowercase().trim() in listOf("accepted", "aceite") &&
                application.studentPresenceConfirmed &&
                !application.advisorProfileId.isNullOrBlank()

        val canChatTeacher =
            application.status.lowercase().trim() in listOf("accepted", "aceite") &&
                application.studentPresenceConfirmed &&
                !application.teacherProfileId.isNullOrBlank() &&
                application.teacherStatus == "accepted"

        if (canChatAdvisor) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d("ChatDebug", "Button advisor chat applicationId=$applicationId")
                    Log.d("ChatDebug", "status=${application.status}")
                    Log.d("ChatDebug", "studentPresenceConfirmed=${application.studentPresenceConfirmed}")
                    Log.d("ChatDebug", "advisorProfileId=${application.advisorProfileId}")
                    Log.d("ChatNavigation", "Abrir chat orientador applicationId=$applicationId")
                    onMessagesClick(applicationId, "advisor")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.message_advisor),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (canChatTeacher) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    Log.d("ChatDebug", "Button teacher chat applicationId=$applicationId")
                    Log.d("ChatNavigation", "Abrir chat docente applicationId=$applicationId")
                    onMessagesClick(applicationId, "teacher")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2B2B2B),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.message_teacher),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TeacherSection(
    application: StudentSubmittedApplicationDto
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.assigned_teacher),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        val isTeacherAssigned = !application.teacherProfileId.isNullOrBlank()
                && application.teacherStatus == "accepted"

        if (isTeacherAssigned) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = application.teacherName ?: stringResource(R.string.default_not_available),
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!application.teacherDepartment.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = application.institutionName ?: "",
                            color = Color(0xFF777777),
                            fontSize = 14.sp
                        )
                    }

                    if (!application.teacherEmail.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = application.teacherEmail,
                            color = Color(0xFF777777),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                )
            ) {
                Text(
                    text = stringResource(R.string.teacher_not_assigned),
                    color = Color(0xFF777777),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun StudentApplicationDetailDocumentField(
    label: String,
    fileName: String,
    enabled: Boolean,
    onOpenClick: () -> Unit
) {
    Text(
        text = label,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fileName,
            color = Color(0xFF8A8A8A),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Button(
            onClick = onOpenClick,
            enabled = enabled,
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

@Composable
fun StudentApplicationDetailAvatar(
    fullName: String
) {
    val initials = fullName
        .split(" ")
        .filter { part ->
            part.isNotBlank()
        }
        .take(2)
        .joinToString("") { part ->
            part.first().uppercase()
        }
        .ifBlank {
            "?"
        }

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFF2B2B2B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StudentApplicationDetailLoadingState() {
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
fun StudentApplicationDetailErrorState(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = message,
            color = Color(0xFFB00020),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun fileNameFromStudentApplicationPath(
    path: String?,
    fallback: String
): String {
    return path
        ?.substringAfterLast("/")
        ?.takeIf { fileName ->
            fileName.isNotBlank()
        }
        ?: fallback
}

@Composable
fun studentApplicationDetailStatusLabel(status: String): String {
    return when (status) {
        "accepted" -> stringResource(R.string.student_application_status_accepted)
        "rejected" -> stringResource(R.string.student_application_status_rejected)
        else -> stringResource(R.string.student_application_status_pending)
    }
}

fun studentApplicationDetailStatusColor(status: String): Color {
    return when (status) {
        "accepted" -> Color(0xFF2E7D32)
        "rejected" -> Color(0xFFB00020)
        else -> Color(0xFF666666)
    }
}