package com.example.nextstep.ui.screens.advisor

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextstep.R
import com.example.nextstep.data.model.AdvisorEvaluationDto
import com.example.nextstep.ui.utils.applicationStatusToDisplay
import com.example.nextstep.ui.utils.applicationStatusToColor
import com.example.nextstep.data.model.AdvisorStudentDetailDto
import com.example.nextstep.data.model.AdvisorTaskListItemDto

@Composable
fun AdvisorStudentDetailScreen(
    applicationId: String,
    onBackClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onEvaluateClick: () -> Unit = {},
    onTaskClick: (AdvisorTaskListItemDto) -> Unit = {},
    onCancelInternship: () -> Unit = {},
    onEndInternship: () -> Unit = {},
    viewModel: AdvisorStudentDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(applicationId) {
        viewModel.loadDetail(applicationId)
    }

    LaunchedEffect(state.internshipActionSuccess) {
        state.internshipActionSuccess?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearInternshipActionMessages()
            onBackClick()
        }
    }

    LaunchedEffect(state.internshipActionError) {
        state.internshipActionError?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearInternshipActionMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top bar
        AdvisorDetailTopBar(onBackClick = onBackClick)

        when {
            state.isLoading && state.detail == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessage != null && state.detail == null -> {
                AdvisorDetailErrorContent(
                    message = state.errorMessage ?: "",
                    onRetry = { viewModel.refresh(applicationId) }
                )
            }

            state.detail != null -> {
                AdvisorDetailContent(
                    detail = state.detail!!,
                    state = state,
                    onMessageClick = onMessageClick,
                    onTaskClick = onTaskClick,
                    onStatusChange = { taskId, status ->
                        viewModel.updateTaskStatus(taskId, status, applicationId)
                    },
                    onGradeChange = viewModel::onGradeChange,
                    onQualitativeFeedbackChange = viewModel::onQualitativeFeedbackChange,
                    onStrengthsChange = viewModel::onStrengthsChange,
                    onImprovementsChange = viewModel::onImprovementsChange,
                    onSaveEvaluation = { viewModel.saveEvaluation(applicationId) },
                    onCancelInternship = { viewModel.cancelInternship(applicationId) },
                    onEndInternship = { viewModel.finishInternship(applicationId) }
                )
            }
        }
    }
}

@Composable
private fun AdvisorDetailTopBar(onBackClick: () -> Unit) {
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
}

@Composable
private fun AdvisorDetailContent(
    detail: AdvisorStudentDetailDto,
    state: AdvisorStudentDetailUiState,
    onMessageClick: () -> Unit,
    onTaskClick: (AdvisorTaskListItemDto) -> Unit,
    onStatusChange: (String, String) -> Unit,
    onGradeChange: (String) -> Unit,
    onQualitativeFeedbackChange: (String) -> Unit,
    onStrengthsChange: (String) -> Unit,
    onImprovementsChange: (String) -> Unit,
    onSaveEvaluation: () -> Unit,
    onCancelInternship: () -> Unit,
    onEndInternship: () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.summary),
        stringResource(R.string.tasks),
        stringResource(R.string.evaluations)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Student header card
        AdvisorDetailHeaderCard(detail = detail)

        // Tab row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black,
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AdvisorUiColors.BorderGray)
                )
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
                            color = if (selectedTabIndex == index) Color.Black else AdvisorUiColors.TextGray
                        )
                    }
                )
            }
        }

        // Tab content
        when (selectedTabIndex) {
            0 -> AdvisorSummaryTab(
                detail = detail,
                onMessageClick = onMessageClick,
                onEvaluateClick = { selectedTabIndex = 2 },
                onCancelInternship = onCancelInternship,
                onEndInternship = onEndInternship
            )
            1 -> AdvisorTasksList(
                tasks = detail.tasks,
                onTaskClick = onTaskClick,
                onStatusChange = onStatusChange,
                showStudentInfo = false,
                modifier = Modifier.padding(top = 16.dp)
            )
            2 -> AdvisorEvaluationTab(
                state = state,
                onGradeChange = onGradeChange,
                onQualitativeFeedbackChange = onQualitativeFeedbackChange,
                onStrengthsChange = onStrengthsChange,
                onImprovementsChange = onImprovementsChange,
                onSaveEvaluation = onSaveEvaluation
            )
        }
    }
}

@Composable
private fun AdvisorDetailHeaderCard(detail: AdvisorStudentDetailDto) {
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
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2B2B2B)),
            contentAlignment = Alignment.Center
        ) {
            val initials = detail.studentName
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
                .ifBlank { "?" }
            Text(text = initials, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.studentName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            detail.offerTitle?.takeIf { it.isNotBlank() }?.let { offer ->
                Text(
                    text = offer,
                    fontSize = 13.sp,
                    color = AdvisorUiColors.TextDarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        AdvisorDetailStatusBadge(status = detail.status)
    }
}

@Composable
private fun AdvisorDetailStatusBadge(status: String?) {
    val (bgColor, textColor) = when {
        status == "accepted" || status == "active" || status == "ativo" || status == "aceite" ->
            Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        status == "pending" || status == "pendente" ->
            AdvisorUiColors.YellowLight to Color(0xFF8D6E00)
        status == "rejected" || status == "recusado" || status == "recusada" ->
            Color(0xFFFFEBEE) to Color(0xFFC62828)
        status == "completed" || status == "concluido" || status == "concluído" ->
            Color(0xFFE3F2FD) to Color(0xFF1565C0)
        else -> Color(0xFFF5F5F5) to AdvisorUiColors.TextGray
    }

    val label = applicationStatusToDisplay(status)
    if (label.isBlank() || label == "-") return

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

// ──────────────────────────────────────────────────
// SUMMARY TAB
// ──────────────────────────────────────────────────

@Composable
private fun AdvisorSummaryTab(
    detail: AdvisorStudentDetailDto,
    onMessageClick: () -> Unit,
    onEvaluateClick: () -> Unit,
    onCancelInternship: () -> Unit,
    onEndInternship: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        DetailSectionCard(title = stringResource(R.string.about_internship)) {
            DetailRow(label = stringResource(R.string.start_date), value = detail.startDate ?: "-")
            DetailRow(label = stringResource(R.string.expected_end), value = detail.expectedEndDate ?: "-")
            DetailRow(
                label = stringResource(R.string.status),
                value = applicationStatusToDisplay(detail.status)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        DetailSectionCard(title = stringResource(R.string.task_progress)) {
            val completed = detail.completedTasks
            val total = detail.totalTasks
            val progress = if (total > 0) completed.toFloat() / total else 0f
            val percentage = if (total > 0) (progress * 100).toInt() else 0

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$completed / $total ${stringResource(R.string.status_completed).lowercase()}",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF2B2B2B),
                trackColor = AdvisorUiColors.BorderGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(R.string.grade_percentage_format, percentage), fontSize = 12.sp, color = AdvisorUiColors.TextGray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        DetailSectionCard(title = stringResource(R.string.next_deadlines)) {
            Text(
                text = stringResource(R.string.no_recent_activities),
                fontSize = 14.sp,
                color = AdvisorUiColors.TextGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onMessageClick,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.message), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = onEvaluateClick,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdvisorUiColors.YellowAccent)
            ) {
                Icon(
                    Icons.Filled.Grade,
                    contentDescription = null,
                    tint = Color(0xFF5D4037),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.evaluate_student),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5D4037)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        var showCancelDialog by remember { mutableStateOf(false) }
        var showEndDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showCancelDialog = true },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.cancel_internship), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = { showEndDialog = true },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.end_internship), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text(text = stringResource(R.string.cancel_internship_title)) },
                text = { Text(text = stringResource(R.string.cancel_internship_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showCancelDialog = false
                        onCancelInternship()
                    }) {
                        Text(text = stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (showEndDialog) {
            AlertDialog(
                onDismissRequest = { showEndDialog = false },
                title = { Text(text = stringResource(R.string.end_internship_title)) },
                text = { Text(text = stringResource(R.string.end_internship_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showEndDialog = false
                        onEndInternship()
                    }) {
                        Text(text = stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDialog = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────────
// AVALIAÇÃO TAB (formulário completo)
// ──────────────────────────────────────────────────

@Composable
private fun AdvisorEvaluationTab(
    state: AdvisorStudentDetailUiState,
    onGradeChange: (String) -> Unit,
    onQualitativeFeedbackChange: (String) -> Unit,
    onStrengthsChange: (String) -> Unit,
    onImprovementsChange: (String) -> Unit,
    onSaveEvaluation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.student_evaluation),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (state.evaluation != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.evaluation_saved_short),
                    fontSize = 13.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Loading de avaliação
        if (state.isLoadingEvaluation) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.Black)
            }
            return
        }

        // Campo: Nota final
        OutlinedTextField(
            value = state.grade,
            onValueChange = onGradeChange,
            label = { Text(stringResource(R.string.final_grade_required)) },
            placeholder = { Text(stringResource(R.string.grade_placeholder_example)) },
            isError = state.gradeError != null,
            supportingText = state.gradeError?.let {
                { Text(it, color = Color(0xFFB00020)) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.qualitativeFeedback,
            onValueChange = onQualitativeFeedbackChange,
            label = { Text(stringResource(R.string.qualitative_comment_required)) },
            placeholder = { Text(stringResource(R.string.qualitative_feedback_placeholder)) },
            isError = state.qualitativeFeedbackError != null,
            supportingText = state.qualitativeFeedbackError?.let {
                { Text(it, color = Color(0xFFB00020)) }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 3,
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.strengths,
            onValueChange = onStrengthsChange,
            label = { Text(stringResource(R.string.strengths)) },
            placeholder = { Text(stringResource(R.string.strengths_placeholder_advisor)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.improvements,
            onValueChange = onImprovementsChange,
            label = { Text(stringResource(R.string.improvements)) },
            placeholder = { Text(stringResource(R.string.improvements_placeholder_advisor)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mensagens de feedback
        if (state.evaluationSuccessMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = state.evaluationSuccessMessage,
                    color = Color(0xFF2E7D32),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.evaluationErrorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = state.evaluationErrorMessage,
                    color = Color(0xFFB00020),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Botão guardar
        Button(
            onClick = onSaveEvaluation,
            enabled = !state.isSavingEvaluation,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B2B))
        ) {
            if (state.isSavingEvaluation) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Grade,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.evaluation != null) stringResource(R.string.update_evaluation) else stringResource(R.string.save_evaluation),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ──────────────────────────────────────────────────
// COMMON COMPONENTS
// ──────────────────────────────────────────────────

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        content()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = AdvisorUiColors.TextGray)
        Text(text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 15.sp, color = AdvisorUiColors.TextGray)
    }
}

@Composable
private fun AdvisorDetailErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = Color(0xFFB00020), fontSize = 15.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.try_again),
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onRetry() }
            )
        }
    }
}