package com.example.nextstep.ui.screens.student

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    viewModel: StudentSubmittedApplicationDetailViewModel = viewModel()
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
            val application = state.application

            if (application != null) {
                StudentApplicationDetailContent(
                    application = application,
                    isConfirmingPresence = state.isConfirmingPresence,
                    confirmPresenceErrorRes = state.confirmPresenceErrorRes,
                    isOpeningDocument = state.isOpeningDocument,
                    documentErrorRes = state.documentErrorRes,
                    onBackClick = onBackClick,
                    onOpenMotivationLetter = viewModel::openMotivationLetter,
                    onOpenCv = viewModel::openCv,
                    onConfirmPresence = viewModel::confirmPresence
                )
            }
        }
    }
}

@Composable
fun StudentApplicationDetailContent(
    application: StudentSubmittedApplicationDto,
    isConfirmingPresence: Boolean,
    confirmPresenceErrorRes: Int?,
    isOpeningDocument: Boolean,
    documentErrorRes: Int?,
    onBackClick: () -> Unit,
    onOpenMotivationLetter: () -> Unit,
    onOpenCv: () -> Unit,
    onConfirmPresence: () -> Unit
) {
    val canConfirmPresence = application.status == "accepted" &&
            !application.studentPresenceConfirmed &&
            !isConfirmingPresence

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
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

        Spacer(modifier = Modifier.height(48.dp))

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

        Spacer(modifier = Modifier.height(64.dp))

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

        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = stringResource(R.string.application_documents_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(30.dp))

        StudentApplicationDetailDocumentField(
            label = stringResource(R.string.motivation_letter),
            fileName = fileNameFromStudentApplicationPath(
                path = application.motivationLetterPath,
                fallback = stringResource(R.string.motivation_letter_placeholder)
            ),
            enabled = !application.motivationLetterPath.isNullOrBlank() && !isOpeningDocument,
            onOpenClick = onOpenMotivationLetter
        )

        Spacer(modifier = Modifier.height(24.dp))

        StudentApplicationDetailDocumentField(
            label = stringResource(R.string.cv),
            fileName = fileNameFromStudentApplicationPath(
                path = application.cvPath,
                fallback = stringResource(R.string.cv_placeholder)
            ),
            enabled = !application.cvPath.isNullOrBlank() && !isOpeningDocument,
            onOpenClick = onOpenCv
        )

        documentErrorRes?.let { errorRes ->
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(errorRes),
                color = Color(0xFFB00020),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isOpeningDocument) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.student_application_opening_document),
                color = Color(0xFF8A8A8A),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

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
            enabled = canConfirmPresence,
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
                    isConfirmingPresence -> stringResource(R.string.confirming_presence)
                    application.studentPresenceConfirmed -> stringResource(R.string.presence_confirmed)
                    application.status != "accepted" -> stringResource(R.string.presence_only_after_acceptance)
                    else -> stringResource(R.string.confirm_presence)
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
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

fun studentApplicationDetailStatusLabel(status: String): String {
    return when (status) {
        "accepted" -> "Aceite"
        "rejected" -> "Recusada"
        else -> "Pendente"
    }
}

fun studentApplicationDetailStatusColor(status: String): Color {
    return when (status) {
        "accepted" -> Color(0xFF2E7D32)
        "rejected" -> Color(0xFFB00020)
        else -> Color(0xFF666666)
    }
}