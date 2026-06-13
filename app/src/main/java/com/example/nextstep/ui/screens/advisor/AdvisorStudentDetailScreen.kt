package com.example.nextstep.ui.screens.advisor

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.nextstep.data.model.AdvisorEvaluationDto
import com.example.nextstep.data.model.AdvisorStudentDetailDto
import com.example.nextstep.data.model.AdvisorTaskListItemDto

@Composable
fun AdvisorStudentDetailScreen(
    applicationId: String,
    onBackClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onEvaluateClick: () -> Unit = {},
    onTaskClick: (AdvisorTaskListItemDto) -> Unit = {},
    viewModel: AdvisorStudentDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.loadDetail(applicationId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top bar
        AdvisorDetailTopBar(onBackClick = onBackClick)

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            state.errorMessage != null -> {
                AdvisorDetailErrorContent(
                    message = state.errorMessage ?: "",
                    onRetry = { viewModel.refresh(applicationId) }
                )
            }

            state.detail != null -> {
                AdvisorDetailContent(
                    detail = state.detail!!,
                    onMessageClick = onMessageClick,
                    onEvaluateClick = onEvaluateClick,
                    onTaskClick = onTaskClick,
                    onStatusChange = { taskId, status -> viewModel.updateTaskStatus(taskId, status, applicationId) }
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
    onMessageClick: () -> Unit,
    onEvaluateClick: () -> Unit,
    onTaskClick: (AdvisorTaskListItemDto) -> Unit,
    onStatusChange: (String, String) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.summary),
        stringResource(R.string.tasks),
        stringResource(R.string.evaluations),
        stringResource(R.string.documents)
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
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AdvisorUiColors.BorderGray))
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
                onEvaluateClick = onEvaluateClick
            )
            1 -> AdvisorTasksList(
                tasks = detail.tasks,
                onTaskClick = onTaskClick,
                onStatusChange = onStatusChange,
                showStudentInfo = false,
                modifier = Modifier.padding(top = 16.dp)
            )
            2 -> AdvisorEvaluationsTab(
                evaluation = detail.evaluation,
                onEvaluateClick = onEvaluateClick
            )
            3 -> AdvisorDocumentsTab(documents = detail.documents)
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
        // Avatar
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

        // Status badge
        AdvisorDetailStatusBadge(status = detail.status)
    }
}

@Composable
private fun AdvisorDetailStatusBadge(status: String?) {
    val (label, bgColor, textColor) = when {
        status == "accepted" || status == "active" || status == "ativo" || status == "aceite" -> Triple(
            "Ativo", Color(0xFFE8F5E9), Color(0xFF2E7D32)
        )
        status == "pending" || status == "pendente" -> Triple(
            "Pendente", AdvisorUiColors.YellowLight, Color(0xFF8D6E00)
        )
        status == "rejected" || status == "recusado" || status == "recusada" -> Triple(
            "Recusado", Color(0xFFFFEBEE), Color(0xFFC62828)
        )
        status == "completed" || status == "concluido" || status == "concluído" -> Triple(
            "Concluído", Color(0xFFE3F2FD), Color(0xFF1565C0)
        )
        else -> Triple(
            status?.replaceFirstChar { it.uppercase() } ?: "",
            Color(0xFFF5F5F5), AdvisorUiColors.TextGray
        )
    }

    if (label.isBlank()) return

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
    onEvaluateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // About the internship
        DetailSectionCard(title = stringResource(R.string.about_internship)) {
            DetailRow(label = stringResource(R.string.start_date), value = detail.startDate ?: "-")
            DetailRow(label = stringResource(R.string.expected_end), value = detail.expectedEndDate ?: "-")
            DetailRow(label = stringResource(R.string.status), value = detail.status?.replaceFirstChar { it.uppercase() } ?: "-")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Task progress
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
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF2B2B2B),
                trackColor = AdvisorUiColors.BorderGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$percentage%",
                fontSize = 12.sp,
                color = AdvisorUiColors.TextGray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Next deadlines
        DetailSectionCard(title = stringResource(R.string.next_deadlines)) {
            Text(
                text = stringResource(R.string.no_recent_activities),
                fontSize = 14.sp,
                color = AdvisorUiColors.TextGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onMessageClick,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2B2B2B)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.message),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onEvaluateClick,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdvisorUiColors.YellowAccent
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Grade,
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

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────────
// EVALUATIONS TAB
// ──────────────────────────────────────────────────

@Composable
private fun AdvisorEvaluationsTab(
    evaluation: AdvisorEvaluationDto?,
    onEvaluateClick: () -> Unit
) {
    if (evaluation == null || evaluation.grade == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.evaluation_not_submitted),
                fontSize = 15.sp,
                color = AdvisorUiColors.TextGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEvaluateClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdvisorUiColors.YellowAccent
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Grade,
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            DetailSectionCard(title = stringResource(R.string.evaluations)) {
                evaluation.grade?.let { grade ->
                    DetailRow(label = "Nota", value = grade.toString())
                }
                evaluation.comments?.takeIf { it.isNotBlank() }?.let { comments ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = comments, fontSize = 14.sp, color = AdvisorUiColors.TextDarkGray)
                }
                evaluation.submittedAt?.takeIf { it.isNotBlank() }?.let { date ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = date, fontSize = 12.sp, color = AdvisorUiColors.TextGray)
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────
// DOCUMENTS TAB
// ──────────────────────────────────────────────────

@Composable
private fun AdvisorDocumentsTab(documents: List<AdvisorDocumentDto>) {
    if (documents.isEmpty()) {
        EmptyState(text = stringResource(R.string.no_documents))
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            documents.forEach { doc ->
                AdvisorDocumentCard(document = doc)
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdvisorDocumentCard(document: AdvisorDocumentDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { /* TODO: open document */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (document.type?.lowercase() == "pdf") Icons.Filled.PictureAsPdf else Icons.Filled.Description,
            contentDescription = null,
            tint = Color(0xFF333333),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = document.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            document.type?.takeIf { it.isNotBlank() }?.let { type ->
                Text(
                    text = type.uppercase(),
                    fontSize = 11.sp,
                    color = AdvisorUiColors.TextGray
                )
            }
        }
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
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        content()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
