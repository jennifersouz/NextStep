package com.example.nextstep.ui.screens.student

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.nextstep.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.StudentSubmittedApplicationDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInternshipDetailScreen(
    internshipId: String,
    onBackClick: () -> Unit,
    onChatClick: (String, String, String) -> Unit,
    onSearchAdvisorClick: () -> Unit,
    onSearchTeacherClick: () -> Unit,
    onAdvisorProfileClick: (String) -> Unit = {},
    onTeacherProfileClick: (String) -> Unit = {},
    viewModel: StudentInternshipDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val reportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
            }
            val fileName = getFileName(context, it)
            viewModel.uploadReport(context, it, fileName)
        }
    }

    LaunchedEffect(internshipId) {
        viewModel.loadDetail(internshipId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_internships), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                }
                uiState.errorMessageRes != null -> {
                    Text(
                        text = stringResource(uiState.errorMessageRes!!),
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = Color.Red
                    )
                }
                uiState.internship != null -> {
                    InternshipDetailContent(
                        internship = uiState.internship!!,
                        tasks = uiState.tasks,
                        onChatClick = onChatClick,
                        onSearchAdvisorClick = onSearchAdvisorClick,
                        onSearchTeacherClick = onSearchTeacherClick,
                        onAddTaskClick = viewModel::showAddTaskDialog,
                        onTaskStatusChange = viewModel::updateTaskStatus,
                        onAdvisorProfileClick = onAdvisorProfileClick,
                        onTeacherProfileClick = onTeacherProfileClick,
                        reportFileName = uiState.reportFileName,
                        isUploadingReport = uiState.isUploadingReport,
                        reportErrorMessage = uiState.reportErrorMessage,
                        reportSuccessMessage = uiState.reportSuccessMessage,
                        onAttachReportClick = {
                            reportLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "application/msword",
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                )
                            )
                        },
                        onClearReportMessages = viewModel::clearReportMessages
                    )
                }
            }
        }
    }

    if (uiState.showAddTaskDialog) {
        AddTaskDialog(
            title = uiState.taskTitle,
            isSaving = uiState.isSavingTask,
            error = uiState.taskError,
            onTitleChange = viewModel::updateTaskTitle,
            onSave = viewModel::createTask,
            onDismiss = viewModel::hideAddTaskDialog
        )
    }
}

@Composable
fun InternshipDetailContent(
    internship: StudentSubmittedApplicationDto,
    tasks: List<AdvisorTaskListItemDto>,
    onChatClick: (String, String, String) -> Unit,
    onSearchAdvisorClick: () -> Unit,
    onSearchTeacherClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskStatusChange: (String, String) -> Unit,
    onAdvisorProfileClick: (String) -> Unit = {},
    onTeacherProfileClick: (String) -> Unit = {},
    reportFileName: String? = null,
    isUploadingReport: Boolean = false,
    reportErrorMessage: String? = null,
    reportSuccessMessage: String? = null,
    onAttachReportClick: () -> Unit = {},
    onClearReportMessages: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F4F4))
                    .border(1.dp, Color(0xFFE5E5E5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = internship.companyName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(internship.companyName, color = Color.Gray, fontSize = 14.sp)
                Text(internship.offerTitle, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(stringResource(R.string.supervision_section_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val isAdvisorAssigned = internship.advisorProfileId != null
        val isTeacherAssigned = internship.teacherProfileId != null && internship.teacherStatus == "accepted"

        FollowupItem(
            label = stringResource(R.string.supervisor_label),
            name = internship.formattedAdvisorName,
            email = internship.advisorEmail,
            profileId = internship.advisorProfileId,
            chatType = "advisor",
            onChatClick = { name, type -> onChatClick(internship.id, name, type) },
            onProfileClick = onAdvisorProfileClick,
            isAssigned = isAdvisorAssigned,
            notAssignedText = stringResource(R.string.supervisor_not_assigned)
        )

        if (!isAdvisorAssigned) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSearchAdvisorClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52), contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(stringResource(R.string.search_advisor), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FollowupItem(
            label = stringResource(R.string.teacher_label),
            name = internship.formattedTeacherName,
            email = internship.teacherEmail,
            profileId = internship.teacherProfileId,
            chatType = "teacher",
            onChatClick = { name, type -> onChatClick(internship.id, name, type) },
            onProfileClick = onTeacherProfileClick,
            isAssigned = isTeacherAssigned,
            notAssignedText = stringResource(R.string.teacher_not_assigned_message)
        )

        if (internship.teacherProfileId == null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSearchTeacherClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52), contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(stringResource(R.string.request_teacher), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (internship.status == "completed") {
            EvaluationsSection(internship)
        } else {
            TasksSection(tasks, onAddTaskClick, onTaskStatusChange)
        }

        Spacer(modifier = Modifier.height(32.dp))

        ReportSection(
            fileName = reportFileName,
            isUploading = isUploadingReport,
            errorMessage = reportErrorMessage,
            successMessage = reportSuccessMessage,
            onAttachClick = onAttachReportClick,
            onClearMessages = onClearReportMessages
        )
    }
}

@Composable
fun ReportSection(
    fileName: String?,
    isUploading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onAttachClick: () -> Unit,
    onClearMessages: () -> Unit
) {
    Text(stringResource(R.string.final_report), fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = Color(0xFFB00020),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
    if (successMessage != null) {
        Text(
            text = successMessage,
            color = Color(0xFF2E7D32),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (fileName != null) "\uD83D\uDCC4 $fileName" else stringResource(R.string.no_report_attached),
            color = if (fileName != null) Color.Black else Color(0xFF8A8A8A),
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                onClearMessages()
                onAttachClick()
            },
            enabled = !isUploading,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDFA52),
                contentColor = Color.Black
            )
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
            } else {
                Text(
                    text = if (fileName != null) stringResource(R.string.replace_report) else stringResource(R.string.attach_report),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FollowupItem(
    label: String,
    name: String?,
    email: String?,
    profileId: String?,
    chatType: String,
    onChatClick: (String, String) -> Unit,
    onProfileClick: (String) -> Unit,
    isAssigned: Boolean = true,
    notAssignedText: String
) {
    Column {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        if (isAssigned && !name.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = profileId != null) {
                        profileId?.let { onProfileClick(it) }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9D9D9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name!!, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    if (!email.isNullOrBlank()) {
                        Text(email, color = Color.Gray, fontSize = 13.sp)
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = stringResource(R.string.chat),
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp).clickable { onChatClick(name, chatType) }
                )
            }
        } else {
            Text(
                notAssignedText,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
fun TasksSection(
    tasks: List<AdvisorTaskListItemDto>,
    onAddTaskClick: () -> Unit,
    onTaskStatusChange: (String, String) -> Unit
) {
    Text(stringResource(R.string.tasks_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))

    val grouped = tasks.groupBy { task ->
        if (!task.dueDate.isNullOrBlank()) task.dueDate
        else task.createdAt?.take(10) ?: "sem data"
    }

    if (grouped.isEmpty()) {
        Text(stringResource(R.string.no_tasks_assigned), color = Color.Gray)
    } else {
        grouped.forEach { (dateKey, dateTasks) ->
            if (dateKey != "sem data") {
                val formattedDate = try {
                    val date = if (dateKey.length == 10) LocalDate.parse(dateKey)
                    else LocalDate.parse(dateKey.take(10))
                    date.format(DateTimeFormatter.ofPattern("EEE, d 'de' MMMM", Locale("pt", "PT")))
                } catch (_: Exception) {
                    dateKey
                }

                Text(formattedDate, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(modifier = Modifier.height(8.dp))
            }

            dateTasks.forEach { task ->
                TaskCard(task, onTaskStatusChange)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Button(
        onClick = onAddTaskClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52), contentColor = Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.add_task), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TaskCard(task: AdvisorTaskListItemDto, onStatusChange: (String, String) -> Unit) {
    val isCompleted = task.status == "completed"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { checked ->
                    val newStatus = if (checked) "completed" else "pending"
                    onStatusChange(task.id, newStatus)
                }
            )
            Text(
                task.title,
                fontWeight = FontWeight.Medium,
                color = if (isCompleted) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EvaluationsSection(internship: StudentSubmittedApplicationDto) {
    Text(stringResource(R.string.advisor_evaluation), fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))

    val companyGrade = internship.companyAdvisorGrade
    val teacherGrade = internship.academicAdvisorGrade

    EvaluationRow(name = internship.formattedAdvisorName, grade = companyGrade ?: stringResource(R.string.default_not_available))
    Spacer(modifier = Modifier.height(16.dp))
    EvaluationRow(name = internship.formattedTeacherName, grade = teacherGrade ?: stringResource(R.string.default_not_available))

    Spacer(modifier = Modifier.height(32.dp))
    Text(stringResource(R.string.final_evaluation), fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(16.dp))

    val finalGradeValue = computeFinalGrade(companyGrade, teacherGrade)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Text(text = finalGradeValue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Text(text = stringResource(R.string.grade_max), color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
    }
}

/**
 * Computes the final grade string to display.
 * - If both grades exist, returns the average.
 * - If only one grade exists, returns that grade.
 * - If no grades exist, returns user-friendly message.
 */
private fun computeFinalGrade(companyGrade: String?, teacherGrade: String?): String {
    val grades = listOfNotNull(
        companyGrade?.toDoubleOrNull(),
        teacherGrade?.toDoubleOrNull()
    )
    return if (grades.isNotEmpty()) {
        val average = grades.average()
        if (average == average.toLong().toDouble()) {
            average.toLong().toString()
        } else {
            String.format("%.1f", average)
        }
    } else {
        "Avaliação ainda não disponível."
    }
}

@Composable
fun EvaluationRow(name: String, grade: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFD9D9D9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, modifier = Modifier.weight(1f))

        Surface(
            modifier = Modifier.border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Text(text = grade, fontWeight = FontWeight.Medium)
        }
        Text(text = stringResource(R.string.grade_max), color = Color.Gray, modifier = Modifier.padding(start = 8.dp), fontSize = 14.sp)
    }
}

@Composable
fun AddTaskDialog(
    title: String,
    isSaving: Boolean,
    error: String?,
    onTitleChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_task), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(R.string.task_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52), contentColor = Color.Black)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
                } else {
                    Text(stringResource(R.string.save_task), fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
