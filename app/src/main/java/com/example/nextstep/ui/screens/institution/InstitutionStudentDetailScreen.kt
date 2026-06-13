package com.example.nextstep.ui.screens.institution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.InstitutionStudentDto

@Composable
fun InstitutionStudentDetailScreen(
    studentProfileId: String,
    onBackClick: () -> Unit,
    viewModel: InstitutionStudentDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showArchiveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(studentProfileId) {
        viewModel.loadStudentDetail(studentProfileId)
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            if (state.student != null) {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessages()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                state.errorMessage != null && state.student == null -> {
                    InstitutionStudentDetailErrorState(
                        message = state.errorMessage!!,
                        onRetryClick = { viewModel.loadStudentDetail(studentProfileId) }
                    )
                }

                state.student != null -> {
                    InstitutionStudentDetailContent(
                        student = state.student!!,
                        isActionLoading = state.isActionLoading,
                        onArchiveClick = { showArchiveDialog = true }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showArchiveDialog) {
        ArchiveStudentDialog(
            onConfirm = {
                viewModel.archiveStudent(studentProfileId, null)
                showArchiveDialog = false
            },
            onDismiss = { showArchiveDialog = false }
        )
    }
}

@Composable
private fun InstitutionStudentDetailContent(
    student: InstitutionStudentDto,
    isActionLoading: Boolean,
    onArchiveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${student.firstName} ${student.lastName}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            if (student.institutionArchivedAt != null) {
                Badge(label = "Arquivado", containerColor = Color(0xFFF3F4F6), textColor = Color(0xFF6B7280))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F8F8)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                StudentDetailRow(
                    label = stringResource(R.string.email),
                    value = student.email
                )

                if (student.studentNumber.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StudentDetailRow(
                        label = stringResource(R.string.student_number),
                        value = student.studentNumber
                    )
                }

                if (student.course.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StudentDetailRow(
                        label = stringResource(R.string.course),
                        value = student.course
                    )
                }

                if (student.academicYear != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StudentDetailRow(
                        label = stringResource(R.string.academic_year),
                        value = "${student.academicYear}º ano"
                    )
                }

                if (!student.educationInstitution.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StudentDetailRow(
                        label = stringResource(R.string.education_institution),
                        value = student.educationInstitution!!
                    )
                }

                if (!student.phone.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StudentDetailRow(
                        label = stringResource(R.string.phone),
                        value = student.phone!!
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                StudentDetailRow(
                    label = stringResource(R.string.account_status),
                    value = if (student.isActive) {
                        stringResource(R.string.active_status)
                    } else {
                        stringResource(R.string.inactive_status)
                    }
                )

                if (!student.createdAt.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StudentDetailRow(
                        label = stringResource(R.string.registration_date),
                        value = student.createdAt!!
                    )
                }
            }
        }

        if (student.institutionArchivedAt == null) {
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(
                onClick = onArchiveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isActionLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFC62828)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isActionLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFFC62828))
                } else {
                    Text("Arquivar aluno", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun Badge(label: String, containerColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun ArchiveStudentDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Arquivar aluno") },
        text = { Text("Esta ação remove o aluno da lista ativa da instituição, mas mantém o histórico na plataforma.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFC62828))
            ) {
                Text("Arquivar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun StudentDetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun InstitutionStudentDetailErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                color = Color(0xFFB00020),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDFA52),
                    contentColor = Color.Black
                )
            ) {
                Text(text = stringResource(R.string.try_again))
            }
        }
    }
}
