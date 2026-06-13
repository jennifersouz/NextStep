package com.example.nextstep.ui.screens.company

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.nextstep.data.model.CompanyApplicationDto

@Composable
fun CompanyApplicationDetailScreen(
    applicationId: String,
    advisorAssigned: Boolean = false,
    onAdvisorAssignedConsumed: () -> Unit = {},
    onBackClick: () -> Unit,
    onStudentProfileClick: (String) -> Unit = {},
    onAssignAdvisorClick: (String) -> Unit = {},
    viewModel: CompanyApplicationDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(applicationId) {
        viewModel.loadApplication(applicationId)
    }

    LaunchedEffect(advisorAssigned) {
        if (advisorAssigned) {
            viewModel.loadApplication(applicationId, showLoading = false)
            onAdvisorAssignedConsumed()
        }
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
            CompanyApplicationDetailLoadingState()
        }

        state.errorMessageRes != null -> {
            val errorRes = state.errorMessageRes

            CompanyApplicationDetailErrorState(
                message = if (errorRes != null) {
                    stringResource(errorRes)
                } else {
                    stringResource(R.string.company_application_detail_load_error)
                },
                onBackClick = onBackClick
            )
        }

        state.application != null -> {
            CompanyApplicationDetailContent(
                applicationId = applicationId,
                application = state.application!!,
                isUpdatingStatus = state.isUpdatingStatus,
                isOpeningDocument = state.isOpeningDocument,
                statusErrorRes = state.statusErrorRes,
                documentErrorRes = state.documentErrorRes,
                onBackClick = onBackClick,
                onStatusSelected = viewModel::updateStatus,
                onOpenMotivationLetter = viewModel::openMotivationLetter,
                onOpenCv = viewModel::openCv,
                onStudentProfileClick = onStudentProfileClick,
                onAssignAdvisorClick = onAssignAdvisorClick
            )
        }
    }
}

@Composable
fun CompanyApplicationDetailContent(
    applicationId: String,
    application: CompanyApplicationDto,
    isUpdatingStatus: Boolean,
    isOpeningDocument: Boolean,
    statusErrorRes: Int?,
    documentErrorRes: Int?,
    onBackClick: () -> Unit,
    onStatusSelected: (ApplicationDecisionStatus) -> Unit,
    onOpenMotivationLetter: () -> Unit,
    onOpenCv: () -> Unit,
    onStudentProfileClick: (String) -> Unit,
    onAssignAdvisorClick: (String) -> Unit
) {
    val studentName = "${application.firstName} ${application.lastName}"
    val currentStatus = ApplicationDecisionStatus.fromDbValue(application.status)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp).size(46.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column: Student info and status
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompanyApplicationDetailAvatar(
                            studentName = studentName
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = studentName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            application.course?.takeIf { it.isNotBlank() }?.let { course ->
                                Text(
                                    text = course,
                                    fontSize = 13.sp,
                                    color = Color(0xFF8A8A8A)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Estado da Candidatura",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8A8A8A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CompanyApplicationStatusDropdown(
                        currentStatus = currentStatus,
                        enabled = !isUpdatingStatus,
                        onStatusSelected = onStatusSelected
                    )

                    statusErrorRes?.let { errorRes ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(errorRes),
                            color = Color(0xFFB00020),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onStudentProfileClick(applicationId) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDFA52),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.view_student_profile),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Right Column: Advisor and Documents
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    val isAcceptedStatus = application.status.lowercase().trim() in listOf("accepted", "aceite")

                    if (isAcceptedStatus) {
                        AdvisorSection(
                            application = application,
                            applicationId = applicationId,
                            onAssignAdvisorClick = onAssignAdvisorClick
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text(
                        text = stringResource(R.string.application_documents_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CompanyApplicationDocumentField(
                        label = stringResource(R.string.motivation_letter),
                        fileName = fileNameFromPath(
                            path = application.motivationLetterPath,
                            fallback = stringResource(R.string.motivation_letter_placeholder)
                        ),
                        enabled = !application.motivationLetterPath.isNullOrBlank() && !isOpeningDocument,
                        onOpenClick = onOpenMotivationLetter
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CompanyApplicationDocumentField(
                        label = stringResource(R.string.cv),
                        fileName = fileNameFromPath(
                            path = application.cvPath,
                            fallback = stringResource(R.string.cv_placeholder)
                        ),
                        enabled = !application.cvPath.isNullOrBlank() && !isOpeningDocument,
                        onOpenClick = onOpenCv
                    )

                    documentErrorRes?.let { errorRes ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(errorRes),
                            color = Color(0xFFB00020),
                            fontSize = 13.sp
                        )
                    }

                    if (isOpeningDocument) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.company_application_opening_document),
                            color = Color(0xFF8A8A8A),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompanyApplicationDetailAvatar(
                        studentName = studentName
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = studentName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    CompanyApplicationStatusDropdown(
                        currentStatus = currentStatus,
                        enabled = !isUpdatingStatus,
                        onStatusSelected = onStatusSelected
                    )
                }

                application.course
                    ?.takeIf { it.isNotBlank() }
                    ?.let { course ->
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = course,
                            fontSize = 14.sp,
                            color = Color(0xFF8A8A8A),
                            modifier = Modifier.padding(start = 76.dp)
                        )
                    }

                statusErrorRes?.let { errorRes ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(errorRes),
                        color = Color(0xFFB00020),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        onStudentProfileClick(applicationId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDFA52),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = stringResource(R.string.view_student_profile),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                val isAcceptedStatus = application.status.lowercase().trim() in listOf("accepted", "aceite")

                if (isAcceptedStatus) {
                    Spacer(modifier = Modifier.height(24.dp))
                    AdvisorSection(
                        application = application,
                        applicationId = applicationId,
                        onAssignAdvisorClick = onAssignAdvisorClick
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = stringResource(R.string.application_documents_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                CompanyApplicationDocumentField(
                    label = stringResource(R.string.motivation_letter),
                    fileName = fileNameFromPath(
                        path = application.motivationLetterPath,
                        fallback = stringResource(R.string.motivation_letter_placeholder)
                    ),
                    enabled = !application.motivationLetterPath.isNullOrBlank() && !isOpeningDocument,
                    onOpenClick = onOpenMotivationLetter
                )

                Spacer(modifier = Modifier.height(16.dp))

                CompanyApplicationDocumentField(
                    label = stringResource(R.string.cv),
                    fileName = fileNameFromPath(
                        path = application.cvPath,
                        fallback = stringResource(R.string.cv_placeholder)
                    ),
                    enabled = !application.cvPath.isNullOrBlank() && !isOpeningDocument,
                    onOpenClick = onOpenCv
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
                        text = stringResource(R.string.company_application_opening_document),
                        color = Color(0xFF8A8A8A),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdvisorSection(
    application: CompanyApplicationDto,
    applicationId: String,
    onAssignAdvisorClick: (String) -> Unit
) {
    val canAssignAdvisor = application.studentPresenceConfirmed

    if (canAssignAdvisor) {
        Button(
            onClick = {
                onAssignAdvisorClick(applicationId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Text(
                text = if (application.advisorProfileId.isNullOrBlank()) {
                    stringResource(R.string.assign_advisor)
                } else {
                    stringResource(R.string.change_advisor)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    } else {
        Text(
            text = stringResource(R.string.waiting_student_internship_acceptance),
            color = Color(0xFF777777),
            fontSize = 14.sp
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.assigned_advisor),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(12.dp))

    if (!application.advisorName.isNullOrBlank()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = application.advisorName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (!application.advisorDepartment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = application.advisorDepartment,
                    fontSize = 14.sp,
                    color = Color(0xFF777777)
                )
            }

            if (!application.advisorEmail.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = application.advisorEmail,
                    fontSize = 14.sp,
                    color = Color(0xFF777777)
                )
            }
        }
    } else {
        Text(
            text = stringResource(R.string.no_assigned_advisor),
            color = Color(0xFF777777),
            fontSize = 14.sp
        )
    }
}

@Composable
fun CompanyApplicationStatusDropdown(
    currentStatus: ApplicationDecisionStatus,
    enabled: Boolean,
    onStatusSelected: (ApplicationDecisionStatus) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        Button(
            onClick = {
                if (enabled) {
                    expanded = true
                }
            },
            enabled = enabled,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = statusTextColor(currentStatus),
                disabledContainerColor = Color.White,
                disabledContentColor = statusTextColor(currentStatus)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFFE0E0E0)
            )
        ) {
            Text(
                text = stringResource(currentStatus.labelRes),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = statusTextColor(currentStatus),
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            ApplicationDecisionStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(status.labelRes),
                            color = statusTextColor(status)
                        )
                    },
                    onClick = {
                        expanded = false
                        onStatusSelected(status)
                    }
                )
            }
        }
    }
}

@Composable
fun CompanyApplicationDocumentField(
    label: String,
    fileName: String,
    enabled: Boolean,
    onOpenClick: () -> Unit
) {
    Text(
        text = label,
        fontSize = 16.sp,
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
            fontSize = 15.sp,
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
fun CompanyApplicationDetailAvatar(
    studentName: String
) {
    val initials = studentName
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CompanyApplicationDetailLoadingState() {
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
fun CompanyApplicationDetailErrorState(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 26.dp, vertical = 22.dp),
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

fun fileNameFromPath(
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

fun statusTextColor(
    status: ApplicationDecisionStatus
): Color {
    return when (status) {
        ApplicationDecisionStatus.PENDING -> Color(0xFF777777)
        ApplicationDecisionStatus.ACCEPTED -> Color(0xFF2E7D32)
        ApplicationDecisionStatus.REJECTED -> Color(0xFFB00020)
    }
}