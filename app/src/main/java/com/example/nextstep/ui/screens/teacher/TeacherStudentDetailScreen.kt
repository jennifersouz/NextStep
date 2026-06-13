package com.example.nextstep.ui.screens.teacher

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorDocumentDto
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.TeacherStudentDetailNonSerializable

@Composable
fun TeacherStudentDetailScreen(
    applicationId: String,
    onBackClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    viewModel: TeacherStudentsViewModel = viewModel()
) {
    val state by viewModel.detailUiState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.loadStudentDetail(applicationId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
            Text(
                text = stringResource(R.string.student),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.errorMessage ?: "", color = Color(0xFFB00020), fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.try_again),
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { viewModel.loadStudentDetail(applicationId) }
                        )
                    }
                }
            }
            state.detail != null -> {
                TeacherDetailContent(
                    detail = state.detail!!,
                    onMessageClick = onMessageClick,
                    applicationId = applicationId,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun TeacherDetailContent(
    detail: TeacherStudentDetailNonSerializable,
    onMessageClick: () -> Unit,
    applicationId: String,
    viewModel: TeacherStudentsViewModel
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.summary),
        stringResource(R.string.tasks),
        stringResource(R.string.documents),
        stringResource(R.string.evaluate_student)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TeacherDetailHeaderCard(detail = detail)

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black,
            divider = {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEDEDED)))
            },
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(3.dp)
                            .background(Color(0xFF2B2B2B))
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color.Black else Color(0xFF777777)
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> TeacherSummaryTab(detail = detail, onMessageClick = onMessageClick)
            1 -> TeacherTasksTab(tasks = detail.tasks)
            2 -> TeacherDocumentsTab(documents = detail.documents)
            3 -> TeacherEvaluationTab(applicationId = applicationId, viewModel = viewModel)
        }
    }
}

@Composable
private fun TeacherDetailHeaderCard(detail: TeacherStudentDetailNonSerializable) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {
            val initials = detail.studentName
                .split(" ").filter { it.isNotBlank() }.take(2)
                .joinToString("") { it.first().uppercase() }.ifBlank { "?" }
            Text(text = initials, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.studentName,
                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            detail.offerTitle?.takeIf { it.isNotBlank() }?.let { offer ->
                Text(
                    text = offer, fontSize = 13.sp, color = Color(0xFF555555),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        TeacherDetailStatusBadge(status = detail.status)
    }
}

@Composable
private fun TeacherDetailStatusBadge(status: String?) {
    val (labelRes, bgColor, textColor) = when {
        status == "accepted" || status == "active" || status == "ativo" || status == "aceite" -> Triple(R.string.teacher_status_active, Color(0xFFE8F5E9), Color(0xFF2E7D32))
        status == "pending" || status == "pendente" -> Triple(R.string.teacher_status_pending, Color(0xFFFFF9C4), Color(0xFF8D6E00))
        status == "completed" || status == "concluido" || status == "concluído" -> Triple(R.string.teacher_status_completed, Color(0xFFE3F2FD), Color(0xFF1565C0))
        else -> Triple(null, Color(0xFFF5F5F5), Color(0xFF777777))
    }
    val label = if (labelRes != null) stringResource(labelRes) else (status?.replaceFirstChar { it.uppercase() } ?: "")
    if (label.isBlank()) return
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

// ── SUMMARY TAB ──

@Composable
private fun TeacherSummaryTab(detail: TeacherStudentDetailNonSerializable, onMessageClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        TeacherDetailSection(title = stringResource(R.string.section_student)) {
            TeacherDetailRow(label = stringResource(R.string.name_required).replace("*", "").trim(), value = detail.studentName)
            detail.studentEmail?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.email), value = it) }
            detail.course?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.course), value = it) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TeacherDetailSection(title = stringResource(R.string.about_internship)) {
            detail.offerTitle?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.offer_title), value = it) }
            detail.companyName?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.company_name), value = it) }
            detail.location?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.location), value = it) }
            detail.workMode?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.work_mode), value = it) }
            detail.duration?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.duration), value = it) }
            detail.status?.let { TeacherDetailRow(label = stringResource(R.string.status), value = it.replaceFirstChar { c -> c.uppercase() }) }
            detail.companyAdvisorName?.takeIf { it.isNotBlank() }?.let { TeacherDetailRow(label = stringResource(R.string.advisor_name), value = it) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TeacherDetailSection(title = stringResource(R.string.task_progress)) {
            val completed = detail.completedTasks
            val total = detail.totalTasks
            val progress = if (total > 0) completed.toFloat() / total else 0f
            val percentage = if (total > 0) (progress * 100).toInt() else 0
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$completed / $total ${stringResource(R.string.status_completed).lowercase()}", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF2B2B2B), trackColor = Color(0xFFEDEDED)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$percentage%", fontSize = 12.sp, color = Color(0xFF777777))
        }
        Spacer(modifier = Modifier.height(12.dp))
        TeacherDetailSection(title = stringResource(R.string.last_activity)) {
            Text(
                text = detail.lastActivityAt?.takeIf { it.isNotBlank() }?.let { it.substringBefore("T") }
                    ?: stringResource(R.string.no_recent_activities),
                fontSize = 14.sp, color = Color(0xFF777777)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onMessageClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.message), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── TASKS TAB ──

@Composable
private fun TeacherTasksTab(tasks: List<AdvisorTaskListItemDto>) {
    var taskFilter by rememberSaveable { mutableStateOf("all") }
    val filteredTasks = when (taskFilter) {
        "pending" -> tasks.filter { it.status != "completed" && it.status != "concluida" && it.status != "concluída" }
        "completed" -> tasks.filter { it.status == "completed" || it.status == "concluida" || it.status == "concluída" }
        else -> tasks
    }
    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("all", "pending", "completed")) { filter ->
                val label = when (filter) {
                    "all" -> stringResource(R.string.all)
                    "pending" -> stringResource(R.string.to_complete)
                    else -> stringResource(R.string.completed)
                }
                Surface(
                    color = if (taskFilter == filter) Color(0xFF2B2B2B) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { taskFilter = filter }
                ) {
                    Text(
                        text = label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 12.sp, maxLines = 1, softWrap = false,
                        fontWeight = if (taskFilter == filter) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (taskFilter == filter) Color.White else Color(0xFF333333)
                    )
                }
            }
        }
        if (filteredTasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_tasks), fontSize = 15.sp, color = Color(0xFF777777))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTasks) { task -> TeacherTaskItem(task = task) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun TeacherTaskItem(task: AdvisorTaskListItemDto) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color.White).padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = task.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(text = desc, fontSize = 12.sp, color = Color(0xFF555555), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            }
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val isCompleted = task.status == "completed" || task.status == "concluida" || task.status == "concluída"
                val statusLabel = if (isCompleted) stringResource(R.string.status_completed) else stringResource(R.string.status_pending)
                val statusColor = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFF57F17)
                Text(text = statusLabel, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
                task.dueDate?.takeIf { it.isNotBlank() }?.let { date ->
                    Text(text = date.substringBefore("T"), fontSize = 11.sp, color = Color(0xFF777777))
                }
            }
        }
    }
}

// ── DOCUMENTS TAB ──

@Composable
private fun TeacherDocumentsTab(documents: List<AdvisorDocumentDto>) {
    if (documents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.no_documents_submitted), fontSize = 15.sp, color = Color(0xFF777777))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            documents.forEach { doc ->
                TeacherDocumentCard(document = doc)
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TeacherDocumentCard(document: AdvisorDocumentDto) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color.White).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (document.type?.lowercase() == "pdf") Icons.Filled.PictureAsPdf else Icons.Filled.Description,
                contentDescription = null, tint = Color(0xFF333333), modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = document.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                document.type?.takeIf { it.isNotBlank() }?.let { type ->
                    Text(text = type.uppercase(), fontSize = 11.sp, color = Color(0xFF777777), modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { }, shape = RoundedCornerShape(8.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.view_pdf), fontSize = 12.sp)
            }
            OutlinedButton(onClick = { }, shape = RoundedCornerShape(8.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.download), fontSize = 12.sp)
            }
        }
    }
}

// ── EVALUATION TAB ──

@Composable
private fun TeacherEvaluationTab(
    applicationId: String,
    viewModel: TeacherStudentsViewModel
) {
    val evalState by viewModel.evaluationUiState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.loadEvaluation(applicationId)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        // Title
        Text(
            text = stringResource(R.string.student_evaluation_form),
            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Loading
        if (evalState.isLoadingEvaluation) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
            return
        }

        // Error message
        evalState.errorMessage?.let { error ->
            Text(text = error, color = Color(0xFFB00020), fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
        }

        // Success message
        evalState.successMessage?.let { success ->
            Text(text = success, color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 12.dp))
        }

        if (evalState.evaluation != null && !evalState.isEditing) {
            // ── Show completed evaluation in read-only ──
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8F5E9))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = stringResource(R.string.evaluation_completed), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Read-only details
            TeacherEvaluationReadOnly(evalState = evalState)
            Spacer(modifier = Modifier.height(16.dp))

            // Edit button
            Button(
                onClick = { viewModel.startEditing() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52))
            ) {
                Icon(imageVector = Icons.Filled.Grade, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.edit_evaluation), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
        } else {
            // ── Evaluation form (new or editing) ──
            TeacherEvaluationForm(
                evalState = evalState,
                onGradeChange = { viewModel.onGradeChange(it) },
                onFeedbackChange = { viewModel.onQualitativeFeedbackChange(it) },
                onStrengthsChange = { viewModel.onStrengthsChange(it) },
                onImprovementsChange = { viewModel.onImprovementsChange(it) },
                onSave = { viewModel.saveEvaluation(applicationId) },
                onCancel = { viewModel.cancelEditing() }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TeacherEvaluationReadOnly(evalState: TeacherEvaluationUiState) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color.White).padding(16.dp)
    ) {
        Text(text = stringResource(R.string.grade_label), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
        Text(text = evalState.grade.ifBlank { "-" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.qualitative_comment), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
        Text(text = evalState.qualitativeFeedback.ifBlank { "-" }, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        if (evalState.strengths.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.strengths), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
            Text(text = evalState.strengths, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        }
        if (evalState.improvements.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.improvements), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF777777))
            Text(text = evalState.improvements, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun TeacherEvaluationForm(
    evalState: TeacherEvaluationUiState,
    onGradeChange: (String) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onStrengthsChange: (String) -> Unit,
    onImprovementsChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val hasExistingEvaluation = evalState.evaluation != null

    // Grade
    Text(text = stringResource(R.string.grade_label) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = evalState.grade,
        onValueChange = onGradeChange,
        placeholder = { Text(stringResource(R.string.grade_placeholder)) },
        singleLine = true,
        isError = evalState.gradeError != null,
        supportingText = evalState.gradeError?.let { err ->
            { Text(text = err, color = Color(0xFFB00020), fontSize = 12.sp) }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (evalState.gradeError != null) Color(0xFFB00020) else Color(0xFFCCCCCC),
            unfocusedBorderColor = if (evalState.gradeError != null) Color(0xFFB00020) else Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Qualitative feedback
    Text(text = stringResource(R.string.qualitative_comment) + " *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = evalState.qualitativeFeedback,
        onValueChange = onFeedbackChange,
        placeholder = { Text(stringResource(R.string.comment_placeholder)) },
        minLines = 3, maxLines = 5,
        isError = evalState.feedbackError != null,
        supportingText = evalState.feedbackError?.let { err ->
            { Text(text = err, color = Color(0xFFB00020), fontSize = 12.sp) }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (evalState.feedbackError != null) Color(0xFFB00020) else Color(0xFFCCCCCC),
            unfocusedBorderColor = if (evalState.feedbackError != null) Color(0xFFB00020) else Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Strengths
    Text(text = stringResource(R.string.strengths), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = evalState.strengths,
        onValueChange = onStrengthsChange,
        placeholder = { Text(stringResource(R.string.strengths_placeholder)) },
        minLines = 2, maxLines = 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFCCCCCC),
            unfocusedBorderColor = Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Improvements
    Text(text = stringResource(R.string.improvements), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = evalState.improvements,
        onValueChange = onImprovementsChange,
        placeholder = { Text(stringResource(R.string.improvements_placeholder)) },
        minLines = 2, maxLines = 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFCCCCCC),
            unfocusedBorderColor = Color(0xFFEDEDED),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(24.dp))

    // Save button
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDFA52)),
        enabled = !evalState.isSavingEvaluation
    ) {
        if (evalState.isSavingEvaluation) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
        } else {
            Icon(imageVector = Icons.Filled.Grade, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            val buttonLabel = if (hasExistingEvaluation) stringResource(R.string.update_evaluation) else stringResource(R.string.save_evaluation)
            Text(text = buttonLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }

    // Cancel button when editing
    if (hasExistingEvaluation) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = stringResource(R.string.cancel), fontSize = 14.sp, color = Color(0xFF333333))
        }
    }
}

// ── COMMON COMPONENTS ──

@Composable
private fun TeacherDetailSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color.White).padding(16.dp)
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        content()
    }
}

@Composable
private fun TeacherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF777777))
        Text(
            text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}